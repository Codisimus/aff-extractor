import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVWriter;
import domain.CountyData;
import domain.StateDataResponse;
import domain.USDataResponse;
import domain.aff.AffResponse;
import httpclient.HttpUtil;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.AffUtil;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class App {

    private final static Logger logger = LogManager.getLogger(App.class.getName());

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final String BASE_PATH = "/programs/ACS/datasets";
    private static final String PROGRAM = "ACS";
    private static final String JSON_OUTPUT_FILENAME = "aff_data.json";

    private static final String CSV_OUTPUT_FILENAME = "aff_data.csv";

    //TODO: Add support for the DEC dataset to pull P2 Table
    //private static final String P2_PATH = "/data/v1/en/programs/DEC/datasets/10_SF1/tables/P2/data/0400000US36.05000";

    public static void main(String [] args) {
        long startTime = System.currentTimeMillis();

        CommandLine cmd = parseCommandLineArgs(args);

        String year = cmd.getOptionValue("y");
        String apiKey = cmd.getOptionValue("k");
        String dataset = year.substring(2) + "_5YR"; //ACS dataset

        logger.info("Starting data extraction for: {} {}...", PROGRAM, dataset);

        //generateJson(year, dataset, apiKey);
        generateCsv(year, dataset, apiKey);

        long totalEndTime = System.currentTimeMillis();
        logger.info("Total time: {} secs", ((totalEndTime-startTime)/1000.0));
    }

    private static void generateJson(String year, String dataset, String apiKey) {
        USDataResponse usDataResponse = new USDataResponse(year, new ArrayList<>());

        logger.info("---------------------------");
        for (Map.Entry<String, String> stateMap : DataMaps.stateMap.entrySet()) {
            String state = stateMap.getKey();
            String stateGeoId = stateMap.getValue();

            StateDataResponse sdr = new StateDataResponse();
            sdr.setState(state);

            logger.debug("Fetching data for state: {}", state);
            //iterate for all tables/columns in the acsTableMap
            for (Map.Entry<String, List<String>> dataMapEntry : DataMaps.acsTableMap.entrySet()) {
                String tableName = dataMapEntry.getKey();
                List<String> columnsToPullFromTable = dataMapEntry.getValue();

                //fetch data table from AFF
                String fullPath = BASE_PATH + "/" + dataset + "/tables/" + tableName + "/data/" + stateGeoId;
                String affResponseStr;
                try {
                    affResponseStr = HttpUtil.makeRequest(fullPath, apiKey);
                } catch (Exception e) {
                    logger.warn("Could not fetch table: {} \n {}", tableName, e);
                    continue;
                }

                //deserialize String response to POJO
                AffResponse affResponse = gson.fromJson(affResponseStr, AffResponse.class);

                //extract desired columns from data table
                List<CountyData> countyDataList = AffUtil.extractDataFromResponse(tableName, affResponse, columnsToPullFromTable);

                //add data from current table to "master" output object
                AffUtil.mergeCountyData(sdr, countyDataList);
                logger.info("Table: {} done", tableName);
                logger.info("---------------------------");
            }
            usDataResponse.getStates().add(sdr);
        }

        String extractedData = gson.toJson(usDataResponse);
        logger.info("Writing data to: {}", JSON_OUTPUT_FILENAME);

        try (FileWriter fw = new FileWriter(JSON_OUTPUT_FILENAME)){
            fw.write(extractedData);
            fw.flush();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private static void generateCsv(String year, String dataset, String apiKey) {
        Map<String, CountyData> counties = new HashMap<>();
        logger.info("---------------------------");
        //iterate for all tables/columns in the acsTableMap
        for (Map.Entry<String, List<String>> dataMapEntry : DataMaps.acsTableElementMap.entrySet()) {
            String tableName = dataMapEntry.getKey();
            List<String> columnsToPullFromTable = dataMapEntry.getValue();

            //fetch data table from AFF
            String fullPath = BASE_PATH + "/" + dataset + "/tables/" + tableName + "/data/"
                              + StringUtils.join(DataMaps.stateMap.values(), ",") + "/"
                              + StringUtils.join(columnsToPullFromTable, ",");
            String affResponseStr;
            try {
                affResponseStr = HttpUtil.makeRequest(fullPath, apiKey);
            } catch (Exception e) {
                logger.warn("Could not fetch table: {} \n {}", tableName, e);
                continue;
            }

            //deserialize String response to POJO
            AffResponse affResponse = gson.fromJson(affResponseStr, AffResponse.class);

            //extract desired columns from data table
            Map<String, CountyData> results = AffUtil.extractDataFromResponse(tableName, affResponse);

            //add data from current table to "master" output object
            results.forEach((k, v) -> counties.merge(k, v, (master, supplemental) -> {
                master.getDataPoints().putAll(supplemental.getDataPoints());
                return master;
            }));
            logger.info("Table: {} done", tableName);
            logger.info("---------------------------");
        }

        List<CountyData> countyDataList = new ArrayList<>(counties.values());
        countyDataList.sort(Comparator.comparing(CountyData::getGeoId));

        File file = new File(year + "_" + CSV_OUTPUT_FILENAME);
        logger.info("Writing data to: {}", file.getPath());

        List<String> columnHeaders = new LinkedList<>();
        List<String> columnHeaderLabels = new LinkedList<>();
        countyDataList.forEach(county -> county.getDataPoints().forEach((key, value) -> {
            if (!columnHeaders.contains(key)) {
                columnHeaders.add(key);
                columnHeaderLabels.add(value.getDescription());
            }
        }));

        List<String[]> rows = new LinkedList<>();
        for (CountyData county : countyDataList) {
            List<String> values = new LinkedList<>();
            values.add(county.getGeoId());
            values.add(county.getLabel());
            for (String column : columnHeaders) {
                try {
                    values.add(county.getDataPoints().get(column).getValue());
                } catch (Exception e) {
                    values.add("");
                }
            }
            rows.add(values.toArray(new String[0]));
        }

        columnHeaders.addAll(0, Arrays.asList("Geo ID", "Geography"));
        columnHeaderLabels.addAll(0, Arrays.asList("County ID", "County"));

        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeNext(columnHeaders.toArray(new String[0]));
            writer.writeNext(columnHeaderLabels.toArray(new String[0]));
            writer.writeAll(rows);
            writer.flushQuietly();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * Parse command line args using Apache Commons CLI
     * https://commons.apache.org/proper/commons-cli/index.html
     *
     * @param args program args
     * @return CommandLine obj containing commandLine args
     */
    private static CommandLine parseCommandLineArgs(String[] args){
        Options options = new Options();

        Option yearOption = new Option("y", true, "year");
        yearOption.setRequired(true);
        options.addOption(yearOption);

        Option keyOption = new Option("k", true, "API Key for American Fact Finder API");
        keyOption.setRequired(true);
        options.addOption(keyOption);

        try {
            CommandLineParser parser = new DefaultParser();
            return parser.parse(options, args, true);
        } catch (ParseException pe){
            logger.error(pe);
            System.exit(-1);
            throw new RuntimeException("Unreachable Statement");
        }
    }
}
