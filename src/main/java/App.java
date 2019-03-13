import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import domain.*;
import httpclient.HttpUtil;
import utils.AcsUtil;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class App {

    private final static Logger logger = LogManager.getLogger(App.class.getName());

    private static final String BASE_PATH = "/programs/ACS/datasets";
    private static final String PROGRAM = "ACS";

    public static void main(String [] args) {
        long startTime = System.currentTimeMillis();

        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        Gson gson = gsonBuilder.create();

        CommandLine cmd = null;
        try {
            cmd = parseCommandLineArgs(args);
        } catch (ParseException pe){
            logger.error(pe);
            System.exit(-1);
        }
        String year = cmd.getOptionValue("y");
        String apiKey = cmd.getOptionValue("k");
        String dataset = year.substring(2) + "_5YR";

        logger.info("Starting data extraction for: {} {}...", PROGRAM, dataset);

        List<StateDataResponse> stateDataResponseList = new ArrayList<>();
        USDataResponse usDataResponse = new USDataResponse();
        usDataResponse.setDataset(dataset);
        usDataResponse.setYear(year);
        usDataResponse.setStates(stateDataResponseList);
        usDataResponse.setProgram(PROGRAM);

        for (Map.Entry<String, String> stateMap : DataMaps.stateMap.entrySet()) {
            String state = stateMap.getKey();
            String stateGeoId = stateMap.getValue();

            logger.debug("Fetching data for state: {}", state);
            StateDataResponse sdr = new StateDataResponse();
            sdr.setState(state);

            //iterate for all tables/columns in the dataTableMap
            for (Map.Entry<String, List<String>> dataMapEntry : DataMaps.dataTableMap.entrySet()) {
                String tableName = dataMapEntry.getKey();
                List<String> columnsToPullFromTable = dataMapEntry.getValue();

                logger.debug("Making HTTP Request for year: {} table: {}", year, tableName);
                long requestStartTime = System.currentTimeMillis();

                //fetch data table from AFF
                String fullPath = BASE_PATH + "/" + dataset + "/tables/" + tableName + "/data/" + stateGeoId;
                String acsResponseStr;
                try {
                    acsResponseStr = HttpUtil.makeRequest(fullPath, apiKey);
                    logger.debug("Request time: {} secs", (System.currentTimeMillis() - requestStartTime / 1000.0));
                } catch (Exception e) {
                    logger.warn("Could not fetch table: {} \n {}", tableName, e);
                    continue;
                }

                //deserialize String response to POJO
                AcsResponse acsResponse = gson.fromJson(acsResponseStr, AcsResponse.class);

                //extract desired columns from data table
                List<CountyData> countyDataList = AcsUtil.extractDataFromResponse(tableName, acsResponse, columnsToPullFromTable);

                //add data from current table to "master" output object
                AcsUtil.mergeCountyData(sdr, countyDataList);

                stateDataResponseList.add(sdr);
                logger.debug("Table: {} done", tableName);
            }
        }

        String extractedData = gson.toJson(usDataResponse);
        logger.debug(extractedData);

        try {
            FileWriter fw = new FileWriter("US_data.json");
            fw.write(extractedData);
            fw.flush();
            fw.close();
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
     * @throws ParseException
     */
    private static CommandLine parseCommandLineArgs(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("y", true, "year");
        options.addOption("k", true, "API Key for American Fact Finder API");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args, true);

        if(!cmd.hasOption("y")) {
            logger.error("No year provided");
        }

        if(!cmd.hasOption("k")) {
            logger.error("No API Key provided");
        }

        return cmd;
    }
}
