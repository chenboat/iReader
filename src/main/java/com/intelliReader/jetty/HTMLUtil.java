package com.intelliReader.jetty;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Request;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 8/31/14
 * Time: 3:39 PM
 */
public class HTMLUtil {
    public static final String ACCOUNT_DELIMITER = ":";
    private static final Pattern p = Pattern.compile(
            "https?://graphics..nytimes.com/[^\"]*thumbLarge.jpg|https?://static[^\"]*.nyt.com/[^\"]*.jpg|https?://static[^\"]*.nyt.com/[^\"]*.png"); // the regex to match the picture url

    public static void setHTMLPagePrelude(Request baseRequest,
                                          HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println("<html>\n <body>");
    }

    public static void setHTMLPageEpilogue(HttpServletResponse response) throws IOException {
        response.getWriter().println("</body>\n" + "</html>");
    }

    /**
     *
     * @param url a nytimes link
     * @return a url for the picture in the link
     */
    public static String getPicURLFromNYTimesLink(String url) throws IOException{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(url);
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
            httpclient.close();
        }
        return null;
    }

    /**
     *
     * @param url a url representing a pic
     * @return a string for the picture
     */
    public static byte[] getPicFromUrl(String url) throws IOException {
        URL u = new URL(url);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = u.openStream();
            byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
            int n;

            while ( (n = is.read(byteChunk)) > 0 ) {
                baos.write(byteChunk, 0, n);
            }
        }
        catch (IOException e) {
            System.err.printf ("Failed while reading bytes from %s: %s", u.toExternalForm(), e.getMessage());
            e.printStackTrace ();
            // Perform any other exception handling that's appropriate.
        }
        finally {
            if (is != null) { is.close(); }
        }
        return baos.toByteArray();
    }
}
