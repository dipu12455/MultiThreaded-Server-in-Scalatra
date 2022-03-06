import com.sun.net.httpserver.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import java.net.BindException;
import java.net.InetSocketAddress;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class MyHttpServer{
    static Arguments parseArgs(String[] args) {
        int portIndex=-1;
        int resIndex=-1;
        int dirIndex=-1;

        //the following code will need to successfully return the correct indexes of the 3 strings in the array.
        for (int i = 0; i < args.length; i++){
            if ("--port".equals(args[i])) portIndex = i;
            if ("--responses".equals(args[i])) resIndex = i;
            if ("--directory".equals(args[i])) dirIndex = i;
        }
        //if you come out of that loop and indexes are still -1, then somethings wrong
        if (portIndex == -1 || dirIndex == -1) {
            System.out.println("Missing Arguments");
            throw new AssertionError();
        }

        //retrieve arguments correctly
        int port=-1;
        int res=-1;
        String dir=null;

        //following code retrieves arguments
        port=Integer.parseInt(args[portIndex+1]);
        res=Integer.parseInt(args[resIndex+1]);
        dir=args[dirIndex+1];

        return new Arguments(port,dir,res);
    }
    static void serverInit(HttpServer _server, Arguments _arguments){
        try {
            _server = HttpServer.create(new InetSocketAddress("localhost", _arguments.getPort()), 0);
        }catch(IllegalArgumentException | IOException e) {
            System.out.println("Port number illegal, please try again.");
            System.exit(0);
        }

        _server.createContext(_arguments.getDirectory(), new MyHttpHandler()); //single slash means the context is right in the localhost location
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(_arguments.getResponses());
        _server.setExecutor(threadPoolExecutor);
        _server.start();
    }
    public static void main(String[] args) {
        Arguments arguments = parseArgs(args);

        System.out.println("Parameters provided as: port: "+arguments.getPort()+"\n"
                + "Responses (Threads):" + arguments.getResponses() + "\n"
                +"Directory: " +arguments.getDirectory()+"\n");
        System.out.println("To test server locally, enter the following in your browser window:");
        System.out.println("http://localhost:"+arguments.getPort()+arguments.getDirectory());
        System.out.println("You can only load files under "+arguments.getDirectory());
        System.out.println("For example, if you'd like to load the file 'hello.html' inside /tmp, " +
                "please enter the following in your browser window:");
        System.out.println("http://localhost:"+arguments.getPort()+"/tmp/hello.html");
        System.out.println("To allow access to the whole directory, please enter '/' on the directory argument.");
        System.out.println(" ... ");

        HttpServer server = null;
        serverInit(server,arguments);

        System.out.println("Waiting for http request...");
        System.out.println("To quit, ^C (for Mac/Linux) or Ctrl+Alt+Del (for Windows");
    }

}

class MyHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange _httpEx) throws IOException{
        String requestParameterValue = null;
        String mthGet = "GET";
        if(mthGet.equals(_httpEx.getRequestMethod())){
            requestParameterValue = _httpEx.getRequestURI().toString();
            System.out.println("Client is requesting ..."+requestParameterValue);
        }
        System.out.println("Debug: "+_httpEx);
        handleResponse(_httpEx,requestParameterValue);
    }

    private void handleResponse(HttpExchange _httpEx, String _reqParamVal) throws IOException{

        Headers h = _httpEx.getResponseHeaders();
        OutputStream out = _httpEx.getResponseBody();

        //first get the file that is being asked
        String status="";
        String requestedContent=""; //this variable stores the file that is being asked, later this content is exported to httpEx
        String response="";
        try{
            File file = new File(_reqParamVal.substring(1)); //omitting the first slash
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                requestedContent+=scanner.nextLine() + "\n";
            }
            System.out.println("Debug(requestedContent): "+requestedContent);
            status = "200 OK";

            h.add("Content-Type","text/html"); //this tells the client to open the file and not read it as text file

        }catch (IOException e)
        {
            //if file not found, trigger status, dissolve http request
            status = "404 Not Found";
            System.out.println(status);
            //retrieve the date
            Date date = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("E',' dd MMM yyyy HH:mm:ss zzz");
            //Response message construction start----------
            response = "HTTP/1.1 " + status + "\r\n";
            response += "Date: " + ft.format(date) + "\r\n";
            response += "Server: Pandorian/10.0.52 (DummyOS)\r\n";
            response += "\r\n";
            response += "404 not found, No html method was implemented.\r\n";
            response += "\r\n";
            response += "\r\n";
            response += "\r\n";
            response += "\r\n";
        }
        if (status=="200 OK"){
            _httpEx.sendResponseHeaders(200, requestedContent.length());
            out.write(requestedContent.getBytes());
        }else{
            _httpEx.sendResponseHeaders(404, response.length());
            out.write(response.getBytes());
        }

        out.flush();
        out.close();

    }

}
class Arguments {
    private final int port;
    private final String directory;
    private final int responses;

    Arguments(int _port, String _directory, int _responses){
        if (_port < 0 || _port > 65536) {
            System.out.println("Port number out of range (0-65536)");
            throw new AssertionError();
        }
        if(_directory == null || _directory.equals("") || !_directory.contains("/")) {
            System.out.println("Invalid directory argument. Cannot be empty. Start path with front-slash'/'");
            throw new AssertionError();
        }
        port = _port;
        directory = _directory;
        responses = _responses;
    }

    int getPort(){return port;}
    String getDirectory(){ return directory;}
    int getResponses(){return responses; }
}