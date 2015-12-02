import java.io.* ;
import java.net.* ;
import java.util.* ;

final class HttpRequest implements Runnable
{
    final static String CRLF = "\r\n";
    Socket socket;
    
    // TODO implement correct Constructor
    /**
     * 
     * Type (GET/POST...)
o Requested Page (/ or /index.html etc.)
o Is Image – if the requested page has an extension of an image (jpg, bmp, gif...)
o Content Length that is written in the request
o Referer – The referer header
o User Agent – the user agent header
o Parameters – the parameters in the request (I used java.util.HashMap<String,String> to hold
the parameters).

     */
    public HttpRequest(Socket socket) throws Exception
	{
		this.socket = socket;
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

    // Parse the request correctly.
    // read the requested file (if it exists), and generate the proper response.
    // embedded objects take care of.
	private void processRequest() throws Exception
	{
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		//Get the source IP
		String sourceIP = socket.getInetAddress().getHostAddress();
		
		// Construct the response message.
		String statusLine = "HTTP/1.0 200 OK" + CRLF;
		String contentTypeLine = "Content-Type: text/html" + CRLF;
		String contentLength = "Content-Length: ";
		
		String entityBody = "<HTML>" + 
		"<HEAD><TITLE>"+ sourceIP +"</TITLE></HEAD>" +
		"<BODY><H1>"+ sourceIP +"</H1></BODY></HTML>";
		
		// Send the status line.
		os.writeBytes(statusLine);
		
		// Send the content type line.
		os.writeBytes(contentTypeLine);
		
		// Send content length.
		os.writeBytes(contentLength + entityBody.length() + CRLF);
		
		// Send a blank line to indicate the end of the header lines.
		os.writeBytes(CRLF);
		
		// Send the content of the HTTP.
		os.writeBytes(entityBody) ;
		
		// Close streams and socket.
		os.close();
		socket.close();
    }
	//TODO copy from directions
	/**
	 * private byte[] readFile(File file) {
try {
}
￼￼FileInputStream fis = new FileInputStream(file); byte[] bFile = new byte[(int)file.length()];
// read until the end of the stream.
while(fis.available() != 0)
{
		fis.read(bFile, 0, bFile.length);
      return bFile;
}
catch(FileNotFoundException e)
{
      // do something
}
catch(IOException e)
{
      // do something
} }
	 * @param file
	 * @return
	 */
	private byte[] readFile(File file) {
		
	}
}


