package com.intelliReader.jetty;

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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * User: ting
 * Date: 1/31/2015
 * Time: 7:29 PM
 */
@Path("/register")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class RegistrationResource {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void newTodo(@FormParam("givenName") String givenName,
                        @FormParam("surname") String surname,
                        @FormParam("username") String username,
                        @FormParam("email") String email,
                        @FormParam("password") String password) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("3BRZRMTN5U34AL9RCEJ0EKS2K:M+J8NBySQUxcTlfQDURKL6u/oVZ1l4XjS2DylSNxJ7M"));
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setCredentialsProvider(credentialsProvider);

        try {
            HttpPost request = new HttpPost("https://api.stormpath.com/v1/applications/2tziUQ6OKW9GGFQ8lIgHmB/accounts");
            StringEntity params =new StringEntity("{\"givenName\":\"" + givenName +"\"," +
                    "\"surname\":\""+surname+"\",\"username\":\""+username+"\"," +
                    "\"email\":\""+email+"\",\"password\":\""+password+"\"" +
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
