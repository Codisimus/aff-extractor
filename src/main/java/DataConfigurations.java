import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

public class DataConfigurations {
  private static final FilenameFilter propertiesFileFilter = (dir, name) -> name.toLowerCase().endsWith(".properties");
  private static Properties settings;
  private static Properties geoIdMap;
  private static Map<String, Properties> tableColumns;

  public static void load() throws IOException {
    settings = new Properties();
    try (InputStream is = new FileInputStream("config.properties")) {
      settings.load(is);
    }
    geoIdMap = new Properties();
    try (InputStream is = new FileInputStream("geo_ids.properties")) {
      geoIdMap.load(is);
    }
    tableColumns = new LinkedHashMap<>();
    for (File file : Objects.requireNonNull(new File("program_configurations").listFiles(propertiesFileFilter))) {
      String program = file.getName().substring(0, file.getName().lastIndexOf('.'));
      Properties properties = new Properties();
      try (InputStream is = new FileInputStream(file)) {
        properties.load(is);
      }
      tableColumns.put(program, properties);
    }
  }

  public static Properties getGeoIdMap() {
    return geoIdMap;
  }

  public static String getOutputFormat() {
    return settings.getProperty("outputFormat");
  }

  public static String getApiKey() {
    return settings.getProperty("apiKey");
  }

  public static Set<String> getProgramNames() {
    return tableColumns.keySet();
  }

  public static Set<String> getTableNames(String program) {
    return tableColumns.get(program).stringPropertyNames();
  }

  public static String[] getTableColumns(String program, String table) {
    return tableColumns.get(program).getProperty(table).split(",");
  }
}
