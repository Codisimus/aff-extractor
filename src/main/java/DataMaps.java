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

    //Defines requested Table columns by Element ID
    static final Map<String, List<String>> acsTableElementMap;
    static {
        Map<String, List<String>> acsMap = new TreeMap<>();
        acsMap.put("DP03", Arrays.asList("C89", "C231", "C438"));
        acsMap.put("DP02", Arrays.asList("C3", "C34"));
        acsMap.put("B05001", Arrays.asList("B05001_1_EST", "B05001_6_EST"));
        acsMap.put("DP05", Arrays.asList("C255", "C279", "C283", "C287", "C291", "C295", "C299", "C303"));
        acsMap.put("B01001", Arrays.asList("B01001_1_EST", "B01001_6_EST", "B01001_7_EST", "B01001_8_EST", "B01001_9_EST", "B01001_10_EST"));
        acsMap.put("S0701", Arrays.asList("C3", "C5", "C7", "C9"));
        acsMap.put("S2301", Arrays.asList("C7"));
        acsMap.put("S1501", Arrays.asList("C79"));
        acsTableElementMap = Collections.unmodifiableMap(acsMap);
    }

    //State codes pulled from https://www2.census.gov/programs-surveys/acs/tech_docs/code_lists/2013_ACS_Code_Lists.pdf#
    static final Map<String, String> stateMap;
    static {
        Map<String, String> map = new TreeMap<>();
        map.put("AL", "0400000US01.05000");
        map.put("AK", "0400000US02.05000");
        map.put("AZ", "0400000US04.05000");
        map.put("AR", "0400000US05.05000");
        map.put("CA", "0400000US06.05000");
        map.put("CO", "0400000US08.05000");
        map.put("CT", "0400000US09.05000");
        map.put("DE", "0400000US10.05000");
        map.put("DC", "0400000US11.05000");
        map.put("FL", "0400000US12.05000");
        map.put("GA", "0400000US13.05000");
        map.put("HI", "0400000US15.05000");
        map.put("ID", "0400000US16.05000");
        map.put("IL", "0400000US17.05000");
        map.put("IN", "0400000US18.05000");
        map.put("IA", "0400000US19.05000");
        map.put("KS", "0400000US20.05000");
        map.put("KY", "0400000US21.05000");
        map.put("LA", "0400000US22.05000");
        map.put("ME", "0400000US23.05000");
        map.put("MD", "0400000US24.05000");
        map.put("MA", "0400000US25.05000");
        map.put("MI", "0400000US26.05000");
        map.put("MN", "0400000US27.05000");
        map.put("MS", "0400000US28.05000");
        map.put("MO", "0400000US29.05000");
        map.put("MT", "0400000US30.05000");
        map.put("NE", "0400000US31.05000");
        map.put("NV", "0400000US32.05000");
        map.put("NH", "0400000US33.05000");
        map.put("NJ", "0400000US34.05000");
        map.put("NM", "0400000US35.05000");
        map.put("NY", "0400000US36.05000");
        map.put("NC", "0400000US37.05000");
        map.put("ND", "0400000US38.05000");
        map.put("OH", "0400000US39.05000");
        map.put("OK", "0400000US40.05000");
        map.put("OR", "0400000US41.05000");
        map.put("PA", "0400000US42.05000");
        map.put("RI", "0400000US44.05000");
        map.put("SC", "0400000US45.05000");
        map.put("SD", "0400000US46.05000");
        map.put("TN", "0400000US47.05000");
        map.put("TX", "0400000US48.05000");
        map.put("UT", "0400000US49.05000");
        map.put("VT", "0400000US50.05000");
        map.put("VA", "0400000US51.05000");
        map.put("WA", "0400000US53.05000");
        map.put("WV", "0400000US54.05000");
        map.put("WI", "0400000US55.05000");
        map.put("WY", "0400000US56.05000");

        stateMap = Collections.unmodifiableMap(map);
    }

    //NOT USED YET
    /*static final Map<String, List<String>> decennialTableMap;
    static {
        Map<String, List<String>> map = new TreeMap<>();
        map.put("P2", Arrays.asList("D001", "D002", "D005"));
        decennialTableMap = Collections.unmodifiableMap(map);
    }*/

}
