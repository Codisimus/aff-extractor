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
        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            return null == entity ? "" : EntityUtils.toString(entity);

        } else {
            throw new IOException("Response Status Code: " + status);
        }
    }
}
