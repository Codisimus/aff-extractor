import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
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
    private static Properties tableColumns;

    /**
     * Loads all configurations associated with the app.
     *
     * @param program The ID of the program for which to load a configuration file
     * @throws IOException if any required resource cannot be loaded
     */
    public static void load(String program) throws IOException {
        settings = loadPropertiesResource("config.properties");
        geoIdMap = loadPropertiesResource("geo_ids.properties");
        tableColumns = loadPropertiesResource(program + ".properties");
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

    public static Set<String> getTableNames() {
        return tableColumns.stringPropertyNames();
    }

    public static String[] getTableColumns(String table) {
        return tableColumns.getProperty(table).split(",");
    }
}
