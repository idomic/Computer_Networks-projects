
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Properties;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;


public class WebServer {
	static Object lock = new Object();
    static Properties config = new Properties();
    static InputStream input = null;
    static int port;
    static int maxThreads;
    static int maxDownloaders;
    static int maxAnalyzers;
    static String rootDir = "";
    static String[] imageExtensions;
    static String[] videoExtensions;
    static String[] documentExtensions;
    static String defaultPage = "";
    static String unavailable503 = "HTTP/1.1 503 Service Unavailable\r\n";
    static DataOutputStream os;
    
	public static void main(String[] args) throws Exception {
		 ServerSocket welcomeSocket = null;
        
		 //parse configure file
		parseConfig();
//		Analyzer anz = new Analyzer(1);
//		anz.run();
//		anz.m_downloadedQueue.add("<a href='http://www.w3schools.com/html/'>Visit our HTML tutorial</a>");
        
        try {
        	// TODO Check with ori when it closes the socket?

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
			// TODO change to Downloaders and analyzers
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
	        	maxDownloaders = Integer.parseInt(config.getProperty("maxDownloaders"));
	        	maxAnalyzers = Integer.parseInt(config.getProperty("maxAnalyzers"));
	        	imageExtensions = parseExtension(config.getProperty("imageExtensions"));
	        	videoExtensions = parseExtension(config.getProperty("videoExtensions"));
	        	documentExtensions = parseExtension(config.getProperty("documentExtensions"));
	        	rootDir = config.getProperty("root");
	        	defaultPage = config.getProperty("defaultPage");
	            	            
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
	public static String[] parseExtension(String i_toParse) {
		
		return i_toParse.split(",");
	}
}

