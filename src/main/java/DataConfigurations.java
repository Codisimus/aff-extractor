import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * Contains configurations and settings for the application.
 * The load method should be called before attempting to access any content.
 */
public class DataConfigurations {
    private static final FilenameFilter propertiesFileFilter = (dir, name) -> name.toLowerCase().endsWith(".properties");
    private static Properties settings;
    private static Properties geoIdMap;
    private static Map<String, Properties> tableColumns;

    /**
     * Loads all configurations associated with the app.
     *
     * @throws IOException if any required resource cannot be loaded
     */
    public static void load() throws IOException {
        settings = loadPropertiesResource("config.properties");
        geoIdMap = loadPropertiesResource("geo_ids.properties");

        tableColumns = new LinkedHashMap<>();
        URL url = ClassLoader.getSystemResource("program_configurations");
        if (url == null) {
            throw new FileNotFoundException("The directory 'program_configurations' was not found");
        }
        File[] programConfigs = new File(url.getPath()).listFiles(propertiesFileFilter);
        for (File file : Objects.requireNonNull(programConfigs)) {
            String program = file.getName().substring(0, file.getName().lastIndexOf('.'));
            Properties properties = new Properties();
            try (InputStream is = new FileInputStream(file)) {
                properties.load(is);
            }
            tableColumns.put(program, properties);
        }
    }

    /**
     * Loads the specified resource as a Properties file.
     *
     * @param fileName The name of the resource
     * @return A new Properties file with all key value pairs loaded
     * @throws IOException if the resource cannot be loaded
     */
    private static Properties loadPropertiesResource(String fileName) throws IOException {
        Properties properties = new Properties();
        try (InputStream is = ClassLoader.getSystemResourceAsStream(fileName)) {
            if (is == null) {
                throw new FileNotFoundException("The configuration file '" + fileName + "' was not found");
            }
            properties.load(is);
        }
        return properties;
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
