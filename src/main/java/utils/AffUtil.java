package utils;

import domain.ColumnInfo;
import domain.CountyData;
import domain.DataPoint;
import domain.StateDataResponse;
import domain.aff.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AffUtil {

    /**
     * Iterate through the Map of cells and pull out the column keys that match the data columns we're interested in
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
            String suffix = columnCode.substring(columnCode.indexOf("_")+1);
            for (Map.Entry<String, AffCell> entry : cells.entrySet()) {
                AffCell cell = entry.getValue();
                boolean foundPrefixMatch = false;
                boolean foundSuffixMatch = false;
                String prefixLabel = "";
                String suffixLabel = "";
                for (Map.Entry<String, AffCategory> category : cell.getCategories().entrySet()) {
                    if (prefix.equals(category.getValue().getId())){
                        foundPrefixMatch = true;
                        prefixLabel = category.getValue().getLabel();
                    }
                    if (suffix.equals(category.getValue().getId())){
                        suffixLabel = category.getValue().getLabel();
                        foundSuffixMatch = true;
                    }
                }
                if(foundPrefixMatch && foundSuffixMatch){
                    columnInfo.setColumnId(entry.getKey());
                    columnInfo.setColumnDescription(prefixLabel + ": " + suffixLabel);
                }
            }
            columnInfoList.add(columnInfo);
        }
        return columnInfoList;
    }

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
            //TODO: replace with Java .stream() for efficiency sake
            for (Map.Entry<String, AffRowData> affRowDataEntry : affRow.getCells().entrySet()){
                for(ColumnInfo columnInfo: columnInfoList){
                    if(columnInfo.getColumnId().equals(affRowDataEntry.getKey())){
                        DataPoint dataPoint = new DataPoint();
                        dataPoint.setDescription(columnInfo.getColumnDescription());
                        dataPoint.setValue(affRowDataEntry.getValue().getValue());
                        countyData.getDataPoints().put(tableName + "_" + columnInfo.getColumnCode(), dataPoint);
                    }
                }
            }
            countyDataList.add(countyData);
        }
        return countyDataList;
    }

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
}
