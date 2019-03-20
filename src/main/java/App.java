import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVWriter;
import domain.CountyData;
import domain.StateDataResponse;
import domain.USDataResponse;
import domain.aff.AffResponse;
import httpclient.HttpUtil;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
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

    private static final String JSON_OUTPUT_FILENAME = "aff_data.json";
    private static final String CSV_OUTPUT_FILENAME = "aff_data.csv";

    public static void main(String [] args) {
        long startTime = System.currentTimeMillis();

        CommandLine cmd = parseCommandLineArgs(args);

        String program = cmd.getOptionValue("p");
        String year = cmd.getOptionValue("y");
        String dataset = year.substring(2) + "_" + cmd.getOptionValue("d");

        try {
            DataConfigurations.load(program);
        } catch (IOException e) {
            logger.error("Failed to load configuration files", e);
            System.exit(-1);
            throw new RuntimeException("Unreachable Statement");
        }

        logger.info("Starting data extraction for: {}...", dataset);

        switch (DataConfigurations.getOutputFormat()) {
            case "json":
                generateJson(year, dataset);
                break;
            case "csv":
                generateCsv(program, dataset);
                break;
            default:
                throw new UnsupportedOperationException(DataConfigurations.getOutputFormat()
                                                        + " is not a supported output format");
        }

        long totalEndTime = System.currentTimeMillis();
        logger.info("Total time: {} secs", ((totalEndTime-startTime)/1000.0));
    }

    private static void generateJson(String year, String dataset) {
        USDataResponse usDataResponse = new USDataResponse(year, new ArrayList<>());

        logger.info("---------------------------");
        for (Entry<Object, Object> stateMap : DataConfigurations.getGeoIdMap().entrySet()) {
            String state = (String) stateMap.getKey();
            String stateGeoId = (String) stateMap.getValue();

            StateDataResponse sdr = new StateDataResponse();
            sdr.setState(state);

            logger.debug("Fetching data for state: {}", state);
            //iterate for all tables/columns in the acsTableMap
            for (Map.Entry<String, List<String>> dataMapEntry : DataMaps.acsTableMap.entrySet()) {
                String tableName = dataMapEntry.getKey();
                List<String> columnsToPullFromTable = dataMapEntry.getValue();

                //fetch data table from AFF
                String fullPath = "/programs/acs/datasets/" + dataset + "/tables/" + tableName + "/data/" + stateGeoId;
                String affResponseStr;
                try {
                    affResponseStr = HttpUtil.makeRequest(fullPath, DataConfigurations.getApiKey());
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

    private static void generateCsv(String program, String dataset) {
        Map<String, CountyData> counties = new HashMap<>();
        logger.info("---------------------------");
        for (String tableName : DataConfigurations.getTableNames()) {
            String[] columnsToPullFromTable = DataConfigurations.getTableColumns(tableName);

            //fetch data table from AFF
            String fullPath = "/programs/" + program + "/datasets/" + dataset + "/tables/" + tableName + "/data/"
                              + StringUtils.join(DataConfigurations.getGeoIdMap().values(), ",")
                              + "/" + StringUtils.join(columnsToPullFromTable, ",");
            String affResponseStr;
            try {
                affResponseStr = HttpUtil.makeRequest(fullPath, DataConfigurations.getApiKey());
            } catch (Exception e) {
                logger.warn("Could not fetch table: {} \n {}", tableName, e);
                continue;
            }

            //deserialize String response to POJO
            AffResponse affResponse = gson.fromJson(affResponseStr, AffResponse.class);

            //extract desired columns from data table
            Map<String, CountyData> results = AffUtil
                .extractDataFromResponse(tableName, affResponse);

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

        File file = new File(program + "_" + dataset + "_" + CSV_OUTPUT_FILENAME);
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

        Option programOption = new Option("p", true, "program");
        programOption.setRequired(true);
        options.addOption(programOption);

        Option yearOption = new Option("y", true, "year");
        yearOption.setRequired(true);
        options.addOption(yearOption);

        Option datasetOption = new Option("d", true, "dataset");
        datasetOption.setRequired(true);
        options.addOption(datasetOption);

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
