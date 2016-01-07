import java.io.* ;
import java.net.* ;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

class Client implements Runnable{
    final static String CRLF = "\r\n";
    Socket socket;
    String log = "   ********   ";
    DataOutputStream os;
    InputStream instream;
	BufferedReader buff;
    int maxThreads = WebServer.maxThreads;
    String rootDir = WebServer.rootDir;
    String defaultPage = WebServer.defaultPage;
    String line = null;
    Boolean requestHasBody;
    int contentLength;
    String postParameters;
    Boolean keepAlive = true;	
    public Client(Socket socket , String rootDir , String defaultPage) throws Exception
	{
		this.socket = socket;
		this.rootDir = rootDir;
		this.defaultPage = defaultPage;
    }

	// Implement the run() method of the Runnable interface.
    public void run()
	{
		try
		{
		    processRequest();
		}
		catch (Exception e)
		{
		    System.out.println(e);
		}
	}

    // read the input data and generate the proper response.
    // embedded objects take care of it
    private void processRequest() throws Exception {

    	try {
    		//Get a reference to the socket's streams
    		os = new DataOutputStream(socket.getOutputStream());
    		instream = socket.getInputStream();
    		//reads the input data
    		buff = new BufferedReader(new InputStreamReader(instream));
    		if (buff.ready()) {	
    			//request line first line
    			line = buff.readLine();

    			if (line != null) {
    				StringBuilder req = new StringBuilder();
    				requestHasBody = false;

    				// read all the request to string
    				while (!(line.length() == 0)){
    					req.append(line + "\n");
    					line = buff.readLine();

    					// Check if the request has body
    					if (line.startsWith("Content-Length")) {
    						checkLengthOfBody();
    						requestHasBody = true;
    					}
    				}

    				// read the request body
    				if (requestHasBody) {
    					char[] body = new char[contentLength];
    					buff.read(body, 0, contentLength);
    					
    					postParameters = String.valueOf(body);
    					requestHasBody = false;
    				}

    				// make new HTTP request object
    				HttpRequest httpRequest = new HttpRequest(req.toString(), postParameters, os);

    				// determine the keep alive value according to the request parameter
    				keepAlive = httpRequest.keepAlive();
    			}
    		}

    	}catch (Exception e) {
    		System.out.println();
    		System.out.println("HTTP response:");
    		System.out.println("HTTP/1.1 500 Internal Server Error\r\n");
    		try {
    			os.writeBytes("HTTP/1.1 500 Internal Server Error\r\n");
    		} catch (IOException e1) {
    			System.err.println("Warning - Unable to write to Output Stream");
    		}		
    	}
    	try {
    		kill();
    	} catch (IOException e) {
    		System.err.println("Warning - Unable to close connection");
    		System.out.println();
    		System.out.println(log + "HTTP response:" + log);
    		System.out.println("HTTP/1.1 500 Internal Server Error\r\n");
    		try {
    			os.writeBytes("HTTP/1.1 500 Internal Server Error\r\n");
    		} catch (IOException e1) {
    			System.err.println("Warning - Unable to write to Output Stream");
    		}	
    	} finally {
    		MyThreadPool.numberOfCnnections --;
    		System.out.println(log +"Number of Threads: "+ MyThreadPool.numberOfCnnections + log);
    		System.out.println(log +"Session thread was killed" + log);
    	}
    } 

    private Map<String, String> parsePostParameters(String i_postParameters) {
    	Map<String, String> result = new HashMap<String, String>();
		
		return result;
    	
    }

	private void checkLengthOfBody() {
		try {
			String input[] = line.split(": ");
			contentLength = Integer.parseInt(input[1]);
		} catch (NumberFormatException e) {
			System.err.println("Warning - Can't read content length");
		}
	}
	
	public void kill() throws IOException {
		os.close();
		buff.close();
		socket.close();
		


	}
}
	




