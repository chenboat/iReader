package http;

import junit.framework.TestCase;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * User: ting
 * Date: 1/23/2015
 * Time: 10:11 PM
 */
public class TestJsonPost extends TestCase {
    public void testPostJson(){
        HttpClient httpClient = new DefaultHttpClient();
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("3BRZRMTN5U34AL9RCEJ0EKS2K:M+J8NBySQUxcTlfQDURKL6u/oVZ1l4XjS2DylSNxJ7M"));
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setCredentialsProvider(credentialsProvider);

        try {
            HttpPost request = new HttpPost("https://api.stormpath.com/v1/applications/2tziUQ6OKW9GGFQ8lIgHmB/accounts");
            StringEntity params =new StringEntity("{\"givenName\":\"myname\"," +
                                                    "\"surname\":\"hi\",\"username\":\"hiting\"," +
                                                    "\"email\":\"hi@121.com\",\"password\":\"Changeme1\"" +
                                                    "} ");
            request.addHeader("content-type", "application/json");
            request.addHeader("Accept", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request,localContext);
            System.out.println(response.getStatusLine());
            // handle response here...
        }catch (Exception ex) {
            // handle exception here
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }
}
