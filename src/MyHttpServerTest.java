import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MyHttpServerTest {
    @Test(expected = AssertionError.class)
    public void testNoDirectory() {
        String[] a = {"--port", "8080"};
        MyHttpServer.parseArgs(a);
    }

    @Test(expected = AssertionError.class)
    public void testNoPort() {
        String[] a = {"--directory", "/tmp"};
        MyHttpServer.parseArgs(a);
    }
    @Test
    public void identifyArguments(){
        String[] a ={"--port","8080","--responses","10","--directory","/tmp","--threads","10"};
        Arguments arguments = MyHttpServer.parseArgs(a);

        assertEquals(arguments.getPort(),8080);
        assertEquals(arguments.getResponses(),10);
        assertEquals(arguments.getDirectory(),"/tmp");
        assertEquals(arguments.getNoofThreads(),10);
    }

    @Test
    public void identifyArguments2(){
        String[] b ={"--directory","/tmp","--port","8080","--threads","10","--responses","10"};
        Arguments arguments = MyHttpServer.parseArgs(b);

        assertEquals(arguments.getPort(),8080);
        assertEquals(arguments.getResponses(),10);
        assertEquals(arguments.getDirectory(),"/tmp");
        assertEquals(arguments.getNoofThreads(),10);
    }

    @Test(expected = AssertionError.class)
    public void invalidPathInServerArguments(){
        HttpServer server = null;
        Arguments arguments = new Arguments(8080,"foo",10, 10); //unreasonable directory path
        MyHttpServer.serverInit(arguments);
    }
}
