package httpclient;

import domain.Language;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpUtil {

    private final static Logger logger = LogManager.getLogger(HttpUtil.class.getName());

    private static final String AFF_HOST = "factfinder.census.gov";
    private static final String DEFAULT_DATA_PATH = "/service/data/v1/";
    private static final String DEFAULT_MAP_PATH = "/service/map/v1/";

    private static HttpGet buildRequest(String path, Language language, String apiKey) throws URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost(AFF_HOST)
                .setPath(DEFAULT_DATA_PATH + language + path)
                .setParameter("key", apiKey)
                .setParameter("maxResults", "91")
                .build();

        HttpGet httpget = new HttpGet(uri);
        Header[] headers = {
                new BasicHeader("ContentType", "APPLICATION/JSON"),
                new BasicHeader("Accept", "APPLICATION/JSON")
        };
        httpget.setHeaders(headers);
        return httpget;

    }

    public static String makeRequest(String path, String apiKey) throws Exception {
        logger.debug("Making HTTP request for: {}", path);
        long requestStartTime = System.currentTimeMillis();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpget = buildRequest(path, Language.EN, apiKey);
        String response = httpClient.execute(httpget, new StringResponseHandler());
        logger.debug("Request time: {} secs", ((System.currentTimeMillis() - requestStartTime) / 1000.0));
        return response;

    }
}
