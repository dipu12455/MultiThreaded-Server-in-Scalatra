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
    static HttpServer server = null;

    static Arguments parseArgs(String[] args) {
        int portIndex=-1;
        int resIndex=-1;
        int dirIndex=-1;
        int thIndex=-1;

        //the following code will need to successfully return the correct indexes of the 3 strings in the array.
        for (int i = 0; i < args.length; i++){
            if ("--port".equals(args[i])) portIndex = i;
            if ("--responses".equals(args[i])) resIndex = i;
            if ("--directory".equals(args[i])) dirIndex = i;
            if ("--threads".equals(args[i])) thIndex = i;
        }
        //if you come out of that loop and indexes are still -1, then somethings wrong
        if (portIndex == -1 || dirIndex == -1) {
            System.out.println("Missing Arguments");
            throw new AssertionError();
        }
        if(thIndex==-1){
            System.out.println("Threads argument empty, No. of threads set as default (1)");
        }

        //retrieve arguments correctly
        int port=-1;
        int res=-1;
        String dir=null;
        int noofThreads=1; //default amount is 1

        //following code retrieves arguments
        port=Integer.parseInt(args[portIndex+1]);
        res=Integer.parseInt(args[resIndex+1]);
        dir=args[dirIndex+1];
        if(thIndex!=-1) noofThreads=Integer.parseInt(args[thIndex+1]);

        return new Arguments(port,dir,res,noofThreads);
    }
    static void serverInit(Arguments _arguments){
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", _arguments.getPort()), 0);
        }catch(IllegalArgumentException | IOException e) {
            System.out.println("Port number illegal, please try again.");
            System.exit(0);
        }

        server.createContext(_arguments.getDirectory(), new MyHttpHandler()); //single slash means the context is right in the localhost location
        ThreadPoolExecutor threadPoolExecutor =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(_arguments.getNoofThreads());
        server.setExecutor(threadPoolExecutor);
        server.start();
    }
    public static void main(String[] args) {
        Arguments arguments = parseArgs(args);

        System.out.println("Parameters provided as: port: "+arguments.getPort()+"\n"
                + "Responses:" + arguments.getResponses() + "\n"
                +"Directory: " +arguments.getDirectory()+"\n" +
                "No. of Threads: "+arguments.getNoofThreads());
        System.out.println("To test server locally, enter the following in your browser window:");
        System.out.println("http://localhost:"+arguments.getPort()+arguments.getDirectory());
        System.out.println("You can only load files under "+arguments.getDirectory());
        System.out.println("For example, if you'd like to load the file 'hello.html' inside /tmp, " +
                "please enter the following in your browser window:");
        System.out.println("http://localhost:"+arguments.getPort()+"/tmp/hello.html");
        System.out.println("To allow access to the whole directory, please enter '/' on the directory argument.");
        System.out.println(" ... ");

        serverInit(arguments);

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
            //System.out.println("Client is requesting ..."+requestParameterValue);//comment this out so that
            // threads don't bottleneck in System.out
        }
        //System.out.println("Debug: "+_httpEx); //comment this out so that threads don't bottleneck in System.out
        handleResponse(_httpEx,requestParameterValue);
    }

    //this method will get shared by the threads
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
    private final int noofThreads;

    Arguments(int _port, String _directory, int _responses, int _noOfThreads){
        if (_port < 0 || _port > 65536) {
            System.out.println("Port number out of range (0-65536)");
            throw new AssertionError();
        }
        if(_directory == null || _directory.equals("") || !_directory.contains("/")) {
            System.out.println("Invalid directory argument. Cannot be empty. Start path with front-slash'/'");
            throw new AssertionError();
        }
        if(_noOfThreads<=0){
            System.out.println("Invalid no. of threads argument. Value must be more than zero.");
        }
        port = _port;
        directory = _directory;
        responses = _responses;
        noofThreads = _noOfThreads;
    }

    int getPort(){return port;}
    String getDirectory(){ return directory;}
    int getResponses(){return responses; }
    int getNoofThreads(){ return noofThreads;}
}