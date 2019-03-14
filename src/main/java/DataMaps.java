import java.util.*;

/**
 * Static maps for data to pull from Census tables and geographic identifier mappings
 */
class DataMaps {
    static final Map<String, List<String>> acsTableMap;
    static {
        Map<String, List<String>> acsMap = new HashMap<>();
        acsMap.put("DP03", Arrays.asList("HC01_VC36", "HC01_VC85", "HC03_VC171"));
        acsMap.put("DP02", Arrays.asList("HC03_VC03", "HC03_VC11"));
        acsMap.put("B05001", Arrays.asList("HD01_VD01", "HD01_VD06"));
        acsMap.put("DP05", Arrays.asList("HC03_VC88", "HC03_VC94", "HC03_VC95", "HC03_VC96", "HC03_VC97", "HC03_VC98", "HC03_VC99", "HC03_VC100"));
        acsMap.put("B01001", Arrays.asList("HD01_VD01", "HD01_VD06", "HD01_VD07", "HD01_VD08", "HD01_VD09", "HD01_VD10"));

        //TODO: add support to parse these IDs in AffUtil.findColumnKeys()
//        aMap.put("S0701", Arrays.asList("HC02_EST_VC01", "HC03_EST_VC01", "HC04_EST_VC01", "HC05_EST_VC01"));
//        aMap.put("S2301", Arrays.asList("HC04_EST_VC01"));
//        aMap.put("S1501", Arrays.asList("HC01_EST_VC16"));
        acsTableMap = Collections.unmodifiableMap(acsMap);
    }

    //NOT USED YET
    /*static final Map<String, List<String>> decennialTableMap;
    static {
        Map<String, List<String>> map = new HashMap<>();
        map.put("P2", Arrays.asList("D001", "D002", "D005"));
        decennialTableMap = Collections.unmodifiableMap(map);
    }*/

    //State codes pulled from https://www2.census.gov/programs-surveys/acs/tech_docs/code_lists/2013_ACS_Code_Lists.pdf#
    static final Map<String, String> stateMap;
    static {
        Map<String, String> map = new HashMap<>();
        map.put("AL", "0400000US01.05000");
        map.put("NY", "0400000US36.05000");
        //TODO: add the rest of the states
//        001 Alabama
//        002 Alaska
//        003 Not Used
//        004 Arizona
//        005 Arkansas
//        006 California
//        007 Not Used
//        008 Colorado
//        009 Connecticut
//        010 Delaware
//        011 District of Columbia
//        012 Florida
//        013 Georgia
//        014 Not Used
//        015 Hawaii
//        016 Idaho
//        017 Illinois
//        018 Indiana
//        019 Iowa
//        020 Kansas
//        021 Kentucky
//        022 Louisiana
//        023 Maine
//        024 Maryland
//        025 Massachusetts
//        026 Michigan
//        027 Minnesota
//        028 Mississippi
//        029 Missouri
//        030 Montana
//        031 Nebraska
//        032 Nevada
//        033 New Hampshire
//        034 New Jersey
//        035 New Mexico
//        036 New York
//        037 North Carolina
//        038 North Dakota
//        039 Ohio
//        040 Oklahoma
//        041 Oregon
//        042 Pennsylvania
//        043 Not Used
//        044 Rhode Island
//        045 South Carolina
//        046 South Dakota
//        047 Tennessee
//        048 Texas
//        049 Utah
//        050 Vermont
//        051 Virginia
//        052 Not Used
//        053 Washington
//        054 West Virginia
//        055 Wisconsin
//        056 Wyoming
//        057-059 Not Used

        stateMap = Collections.unmodifiableMap(map);
    }
}
