package com.intelliReader.newsfeed;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: ting
 * Date: 5/7/2017
 * Time: 10:25 PM
 */
public class NYTimesRSSFeedDescriptor implements RSSFeedDescriptor {
    private static final Pattern p = Pattern.compile(
            "https?://graphics..nytimes.com/[^\"]*thumbLarge.jpg|https?://static[^\"]*.nyt.com/[^\"]*.jpg"); // the regex to match the picture url
    private String section_str;
    public NYTimesRSSFeedDescriptor(String section_str) {
        this.section_str = section_str;
    }
    @Override
    public String getCategory() {
        return section_str.substring("NYTimes ".length());
    }

    @Override
    public String getPictureUrl(FeedMessage feedMessage) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(feedMessage.getLink());
            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };

            String str = httpclient.execute(httpget, responseHandler);
            Matcher m = p.matcher(str);

            while(m.find()) return m.group();

        } catch (IOException e) {
            return null; // the best effect delivery

        } finally
        {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
