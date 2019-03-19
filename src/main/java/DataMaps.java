import java.util.*;

/**
 * Static maps for data to pull from Census tables and geographic identifier mappings
 */
class DataMaps {
    //Defines requested Table columns
    static final Map<String, List<String>> acsTableMap;
    static {
        Map<String, List<String>> acsMap = new TreeMap<>();
        acsMap.put("DP03", Arrays.asList("HC01_VC36", "HC01_VC85", "HC03_VC171"));
        acsMap.put("DP02", Arrays.asList("HC03_VC03", "HC03_VC11"));
        acsMap.put("B05001", Arrays.asList("HD01_VD01", "HD01_VD06"));
        acsMap.put("DP05", Arrays.asList("HC03_VC88", "HC03_VC94", "HC03_VC95", "HC03_VC96", "HC03_VC97", "HC03_VC98", "HC03_VC99", "HC03_VC100"));
        acsMap.put("B01001", Arrays.asList("HD01_VD01", "HD01_VD06", "HD01_VD07", "HD01_VD08", "HD01_VD09", "HD01_VD10"));
        acsMap.put("S0701", Arrays.asList("HC02_EST_VC01", "HC03_EST_VC01", "HC04_EST_VC01", "HC05_EST_VC01"));
        acsMap.put("S2301", Arrays.asList("HC04_EST_VC01"));
        acsMap.put("S1501", Arrays.asList("HC01_EST_VC16"));
        acsTableMap = Collections.unmodifiableMap(acsMap);
    }
}
