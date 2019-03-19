package utils;

import domain.ColumnInfo;
import domain.CountyData;
import domain.DataPoint;
import domain.StateDataResponse;
import domain.aff.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AffUtil {
    private final static Logger logger = LogManager.getLogger(AffUtil.class.getName());

    /**
     * Iterate through the Map of cells and pull out the column keys that match the data columns we're interested in.
     *
     * @param cells cells from AFF header. Describes data contained within that column of the table
     * @param columns columns to pull eg. 'HC03_VC171'
     * @return list of ColumnInfo objects
     */
    private static List<ColumnInfo> findColumnKeys(Map<String, AffCell> cells, List<String> columns){
        List<ColumnInfo> columnInfoList = new ArrayList<>();

        for(String columnCode: columns) {
            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setColumnCode(columnCode);
            String prefix = columnCode.substring(0, columnCode.indexOf("_"));
            String mid = "";
            String suffix = "";
            if(columnCode.contains("_EST_")){
                mid = columnCode.substring(columnCode.indexOf("_")+1, columnCode.lastIndexOf("_"));
                suffix = columnCode.substring(columnCode.lastIndexOf("_")+1);
            } else {
                suffix = columnCode.substring(columnCode.indexOf("_")+1);
            }

            for (Map.Entry<String, AffCell> entry : cells.entrySet()) {
                AffCell cell = entry.getValue();
                boolean foundPrefixMatch = false;
                boolean foundSuffixMatch = false;
                boolean foundMidMatch = false;
                String prefixLabel = "";
                String suffixLabel = "";
                String midLabel = "";
                if(mid.length() == 0){
                    foundMidMatch = true;
                }
                for (Map.Entry<String, AffCategory> category : cell.getCategories().entrySet()) {
                    if (prefix.equals(category.getValue().getId())){
                        foundPrefixMatch = true;
                        prefixLabel = category.getValue().getLabel();
                    }
                    if (mid.equals(category.getValue().getId())){
                        foundMidMatch = true;
                        midLabel = category.getValue().getLabel();
                    }
                    if (suffix.equals(category.getValue().getId())){
                        suffixLabel = category.getValue().getLabel();
                        foundSuffixMatch = true;
                    }
                }
                if(foundPrefixMatch && foundSuffixMatch && foundMidMatch){
                    columnInfo.setColumnId(entry.getKey());
                    if(mid.length() > 0){
                        columnInfo.setColumnDescription(prefixLabel + ": " + midLabel + ": " + suffixLabel);
                    } else {
                        columnInfo.setColumnDescription(prefixLabel + ": " + suffixLabel);
                    }
                }
            }
            columnInfoList.add(columnInfo);
        }
        return columnInfoList;
    }

    /**
     * Given the "column codes" within a Census table, pull data for only those columns.
     *
     * @param tableName Census table name
     * @param affResponse response object to pull from
     * @param columnsToPullFromTable "column codes" to pull
     * @return
     */
    public static List<CountyData> extractDataFromResponse(String tableName, AffResponse affResponse, List<String> columnsToPullFromTable) {
        List<ColumnInfo> columnInfoList = AffUtil.findColumnKeys(affResponse.getData().getHeader().getCells(), columnsToPullFromTable);

        List<AffRow> rows = affResponse.getData().getRows();

        List<CountyData> countyDataList = new ArrayList<>();
        for(AffRow affRow : rows){
            CountyData countyData = new CountyData();
            for (Map.Entry<String, AffCategory> category : affRow.getCategories().entrySet()){
                if("GEO".equals(category.getKey())){
                    countyData.setGeoId(category.getValue().getId());
                    countyData.setLabel(category.getValue().getLabel());
                }
            }

            outerfor: for (Map.Entry<String, AffRowData> affRowDataEntry : affRow.getCells().entrySet()){
                for(ColumnInfo columnInfo: columnInfoList){
                    if(columnInfo.getColumnId() != null) {
                        if (columnInfo.getColumnId().equals(affRowDataEntry.getKey())) {
                            DataPoint dataPoint = new DataPoint();
                            dataPoint.setDescription(columnInfo.getColumnDescription());
                            dataPoint.setValue(affRowDataEntry.getValue().getValue());
                            countyData.getDataPoints().put(tableName + "_" + columnInfo.getColumnCode(), dataPoint);
                        }
                    } else {
                        logger.warn("No column: {} in table: {} for: {}", columnInfo.getColumnCode(), tableName, countyData.getLabel());
                        break outerfor;
                    }
                }
            }
            countyDataList.add(countyData);
        }
        return countyDataList;
    }

    /**
     * Helper method to merge CountyData from one table to the main output response object.
     *
     * @param sdr main output response object
     * @param countyDataList CountyData to be merged
     */
    public static void mergeCountyData(StateDataResponse sdr, List<CountyData> countyDataList){
        for(CountyData countyData: countyDataList){
            String countyGeoId = countyData.getGeoId();
            if(sdr.getCounties().isEmpty()){
                sdr.setCounties(countyDataList);
            }
            else {
                for (CountyData masterCd : sdr.getCounties()) {
                    if (masterCd.getGeoId().equals(countyGeoId)) {
                        masterCd.getDataPoints().putAll(countyData.getDataPoints());
                    }
                }
            }
        }
    }

    /**
     * Maps the provided AffResponse to CountyData. Multiple rows are joined together by geo ID.
     *
     * @param tableName Census table name
     * @param affResponse response object to pull from
     * @return A Map of Geo ID to CountyData
     */
    public static Map<String, CountyData> extractDataFromResponse(String tableName, AffResponse affResponse) {
        Map<String, AffCell> headerCells = affResponse.getData().getHeader().getCells();

        Map<String, CountyData> counties = new HashMap<>();
        for (AffRow affRow : affResponse.getData().getRows()){
            AffCategory category = affRow.getCategories().get("GEO");
            String geoId = category.getId();
            CountyData countyData;
            if (counties.containsKey(geoId)) {
                countyData = counties.get(geoId);
            } else {
                countyData = new CountyData();
                countyData.setGeoId(geoId);
                countyData.setLabel(category.getLabel());
                counties.put(geoId, countyData);
            }

            for (Entry<String, AffRowData> affRowDataEntry : affRow.getCells().entrySet()){
                String columnId = affRowDataEntry.getKey();
                if (headerCells.containsKey(columnId)) {
                    DataPoint dataPoint = new DataPoint();
                    dataPoint.setDescription(getColumnDescription(headerCells.get(columnId)));
                    dataPoint.setValue(affRowDataEntry.getValue().getValue());
                    countyData.getDataPoints().put(tableName + "_" + columnId, dataPoint);
                } else {
                    logger.warn("No column: {} in table: {} for: {}", columnId, tableName, countyData.getLabel());
                }
            }
        }
        return counties;
    }

    /**
     * Contructs a description using data pulled from the provided AffCell
     *
     * @param cell cell from AFF header. Describes data contained within that column of the table
     * @return description pulled from the AffCell
     */
    private static String getColumnDescription(AffCell cell) {
        StringBuilder sb = new StringBuilder();
        Iterator<AffCategory> itr = cell.getCategories().values().iterator();
        while (itr.hasNext()) {
            sb.append(itr.next().getLabel());
            if (itr.hasNext()) {
                sb.append(": ");
            }
        }
        return sb.toString();
    }
}
