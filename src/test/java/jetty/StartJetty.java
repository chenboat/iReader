package jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * User: ting
 * Date: 11/16/2014
 * Time: 4:00 PM
 */
public class StartJetty {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8088);
        WebAppContext context = new WebAppContext();
        context.setDescriptor("src/test/resources/test/web/WEB-INF/web.xml");
        context.setResourceBase("src/test/resources/test/web");
        context.setContextPath("/");
        context.setParentLoaderPriority(true);
        server.setHandler(context);
        server.start();
        server.join();

    }
}
