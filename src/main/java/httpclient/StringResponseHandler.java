package httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class StringResponseHandler implements ResponseHandler<String> {
    public String handleResponse(final HttpResponse response) throws ParseException, IOException  {
        int status = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        String content = null == entity ? "" : EntityUtils.toString(entity);
        if (status >= 200 && status < 300) {
            return content;
        } else {
            throw new IOException("Response Status Code: " + status + " - " + content);
        }
    }
}
