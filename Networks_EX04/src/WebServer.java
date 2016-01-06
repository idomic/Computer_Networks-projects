
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Properties;


public class WebServer {
	static Object lock = new Object();
    static Properties config = new Properties();
    static InputStream input = null;
    static int port;
    static int maxThreads = 10;
    static String rootDir = "";
    static String defaultPage = "";
    static String unavailable503 = "HTTP/1.1 503 Service Unavailable\r\n";
    static DataOutputStream os;
    
	public static void main(String[] args) throws Exception {
		 ServerSocket welcomeSocket = null;;
        //parse configure file
		parseConfig();
        
        try {
            welcomeSocket = new ServerSocket(port);
            System.out.println("Server is up");
            
            
        } catch (IOException e) {
            System.out.println("Erorr");
            e.printStackTrace();
        }
        while(true) {
        	//listen for TCP connection
        	try {
        	Client client = new Client(welcomeSocket.accept(), rootDir, defaultPage);
			MyThreadPool pool = new MyThreadPool(maxThreads);
			pool.enqueue(client);
        	} catch (Exception e) {
        		System.err.println("Warning - Fail to connect, Number of threads in the server: " + 
						MyThreadPool.numberOfCnnections+" Maximum number of thread is: " + maxThreads );
				try {
					os.writeBytes(unavailable503);
				} catch (Exception e1) {
					System.err.println("Warning - Unable to write to Output Stream");
				}
        	}
        	
        	
        }
	
	}
	static void parseConfig() {
	      try {
	        	input = new FileInputStream("config.ini");
	        	config.load(input);
	        	port = Integer.parseInt(config.getProperty("port"));
	        	maxThreads = Integer.parseInt(config.getProperty("maxThreads"));
	        	rootDir = config.getProperty("root");
	        	defaultPage = config.getProperty("defaultPage");
	            //welcomeSocket = new ServerSocket(port);
	            //System.out.println("Server is up");
	            
	            
	        } catch (IOException e) {
	            System.out.println("Erorr");
	            e.printStackTrace();
	        } finally {
	        	if (input != null) {
	        		try {
	        			input.close();
	        		} catch (IOException e) {
	        			e.printStackTrace();
	        		}
	        	}
	        }
	}

}

