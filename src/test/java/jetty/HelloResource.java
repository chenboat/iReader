package jetty;

import com.intelliReader.storage.BerkelyDBStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.Calendar;
import java.util.Date;


@Path("/hello")
public class HelloResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello(@PathParam("key") String key) {
        return bdbStore.get(key).toString();
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    public String sayXMLHello() {
        return "<?xml version=\"1.0\"?><hello> Hello World XML, YAY!!!</hello>";
    }

    @GET
    @Path("{key}")
    @Produces(MediaType.TEXT_HTML)
    public String sayHtmlHello(@PathParam("key") String key) {
        return "<html><title>Hello World HTML</title><body><h1>" + bdbStore.get(key).toString()+ "</body></h1></html>";
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getAllDates() throws Exception{
        return "<html><title>Hello World HTML</title><body><h1>" + bdbStore.getKeys().toString()
            + "</body></h1></html>";
    }

    @POST
    @Consumes({"text/xml", "text/plain", MediaType.TEXT_HTML})
    @Produces(MediaType.TEXT_PLAIN)
    public String sayPostHello() {
        return "Hello World Post!";
    }

    private static BerkelyDBStore<String, Date> bdbStore;
    static {
        String projRoot = System.getProperty("user.dir");
        String dbPath = projRoot + "/src/test/resources/test/web/bdb";

        // Clean the directory
        File dir = new File(dbPath);
        if (dir.listFiles() != null) {
            for (File f : dir.listFiles()) {
                f.delete();
            }
        }

        bdbStore = new BerkelyDBStore<String, Date>
                (dbPath, String.class, Date.class, "sampleTable");

        Calendar calendar = Calendar.getInstance();

        calendar.set(104,4,20);
        Date april20th = calendar.getTime();

        calendar.set(104,4,21);
        Date april21st = calendar.getTime();

        try{
            bdbStore.put("good",april20th);
            bdbStore.put("bad",april21st);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}






