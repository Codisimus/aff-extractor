import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import domain.CountyData;
import domain.StateDataResponse;
import domain.USDataResponse;
import domain.aff.AffResponse;
import httpclient.HttpUtil;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.AffUtil;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class App {

    private final static Logger logger = LogManager.getLogger(App.class.getName());

    private static final String BASE_PATH = "/programs/ACS/datasets";
    private static final String PROGRAM = "ACS";
    private static final String JSON_OUTPUT_FILENAME = "aff_data.json";

    //TODO: output to CSV
    private static final String CSV_OUTPUT_FILENAME = "aff_data.csv";

    //TODO: Add support for the DEC dataset to pull P2 Table
    //private static final String P2_PATH = "/data/v1/en/programs/DEC/datasets/10_SF1/tables/P2/data/0400000US36.05000";

    public static void main(String [] args) {
        long startTime = System.currentTimeMillis();

        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        Gson gson = gsonBuilder.create();

        CommandLine cmd = parseCommandLineArgs(args);

        String year = cmd.getOptionValue("y");
        String apiKey = cmd.getOptionValue("k");
        String dataset = year.substring(2) + "_5YR"; //ACS dataset

        logger.info("Starting data extraction for: {} {}...", PROGRAM, dataset);

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

        long totalEndTime = System.currentTimeMillis();
        logger.info("Total time: {} secs", ((totalEndTime-startTime)/1000.0));
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
        }
        return null;
    }
}
