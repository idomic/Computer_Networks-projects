import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;


public class HttpRequest {
	
	String req;
	DataOutputStream os; 
	Scanner scanner;
	String requestMethod;
	String requestPage;
	String paramSectionHeader=null;
	public String HttpVersion;							
	String line = "";
	String log = "   ********   ";
	String CRLF = "\r\n";
	String defaultPage = WebServer.defaultPage;
 	String rootDir = WebServer.rootDir;
	String htmlPage = "";
	String fileContent="";
	String response="";
	String postParameters;														
	String pramHtmlPageName = "params_info.html";								
	boolean isChunked;															
	boolean isAlive;															
	boolean badRequest = false;
	final int CHUNKED_SIZE = 30;
	private HashMap<String,String> requestParam = new HashMap<String,String>();	
	private HashMap<String,String> parameters = new HashMap<String,String>();	 
	
	// hash set of all known Http methods
		static HashSet<String> HttpMethods = new HashSet<String>();

		//static initialize
		static {
			HttpMethods.add("GET");
			HttpMethods.add("POST");
			HttpMethods.add("TRACE");
			HttpMethods.add("HEAD");
			HttpMethods.add("OPTIONS");
		}
		
		// hash set of the reserved character 
			static HashSet<String> reservedChar = new HashSet<String>();

		//static initialize
		static {
			reservedChar.add("*");
			reservedChar.add("&");
			reservedChar.add(":");
			reservedChar.add("?");
			reservedChar.add("[");
			reservedChar.add("]");
			reservedChar.add("@");
			reservedChar.add("!");
			reservedChar.add("#");
			reservedChar.add("$");
			reservedChar.add("(");
			reservedChar.add(")");
			reservedChar.add("+");
			reservedChar.add(",");
			reservedChar.add(";");
			reservedChar.add("=");
				
			}

	/**
	* constructor, for a new request, it parses the request and send a response, 
	* token it and puts all the tokens in a queue
	* @param -req - the request string, postParameters- the parameters in the request body, os- socket outputStream 
	*/		
	public HttpRequest(String req,String postParameters, DataOutputStream os) {
		this.req = req;
		this.os = os;
		this.postParameters = postParameters;
		scanner = new Scanner(req);
		isChunked = false;
		isAlive = true;
		parseRequest();
		closeOs();
	}
	
	/**
	 * parse from the request the HTTP method, the requested page, http version 
	 * all the other parameters are hold in a HashMap
	 */	
	private void parseRequest() {
		String input[];
		try {
			// print the request to console
			System.out.println();
			System.out.println(log +"HTTP request:" + log);
			System.out.println(req.toString());
			line = scanner.nextLine();
			
			input = line.split(" ");
				
			//parse the header first line;
			requestMethod = input[0];
			requestPage = input[1];
			HttpVersion = input[2];
			splitURI();
			// parse the rest of the http request
			while(scanner.hasNextLine()) {
				line =scanner.nextLine();
				input = line.split(": ");
				requestParam.put(input[0], input[1]);
			}
	
			if (checkRequsetIsValid()) {
			
				if (requestParam.containsKey("chunked") && requestParam.get("chunked").equals("yes")) {
					isChunked = true;
				}
			
				// if the header does not contain the connection field or it is not equal to keep-alive
				if (!requestParam.containsKey("Connection") || !requestParam.get("Connection").equals("keep-alive")) {
					isAlive = false;
				}
		
				parseParam();
				if (!badRequest) {
					handleResponse();
				}
			}
		} catch (Exception e) {
			response400();
		}
	}
	
	/**
	 *parse the parameters in the request URI and body if exists
	 * hold all the parameters in parameter HashMap
	 */	
	private void parseParam() {
		String rawParam[];
		String temp[];
		String param;
		String value;
		try {
			// parse parameters in the URI 
			if (paramSectionHeader != null) { 
		
				// split between each parameter
				if (paramSectionHeader.contains("&")) {
					rawParam = paramSectionHeader.split("&");
					for (int i=0; i < rawParam.length; i++) {
						
						// split between the parameter in his value
						if ((rawParam[i].split("=").length>1) && !parameters.containsKey(rawParam[i].split("=")[0])) {
						parameters.put(rawParam[i].split("=")[0], rawParam[i].split("=")[1]); 
						}
					}
				}
				else {
					parameters.put(paramSectionHeader.split("=")[0], paramSectionHeader.split("=")[1]); 
				}
			}
			
			
			// parse parameters in the request body
			if (postParameters != null) {
				if (postParameters.contains("&")) {
					rawParam= postParameters.split("&");
					for (int i=0; i < rawParam.length; i++) {
						if ((rawParam[i].split("=").length>1) && !parameters.containsKey(rawParam[i].split("=")[0])) {
							param = rawParam[i].split("=")[0];
							value = rawParam[i].split("=")[1];
							if (value.contains("+")) {
								temp =value.split("\\+");
								StringBuilder sb = new StringBuilder();
								for (int j=0; j< (temp.length-1); j++) {
									sb.append(temp[j]+ " ");
								}
								sb.append(temp[temp.length-1]);
								parameters.put(param, sb.toString()); 
							}
							else {
								parameters.put(param, value); 
							}
						}
					}
				}
				
				// if there only one parameter
				else {
					if ((postParameters.split("=").length>1) && !parameters.containsKey(postParameters.split("=")[0])) {
						if (postParameters.split("=")[1].contains("+")) {
							temp = postParameters.split("=")[1].split("\\+");
							StringBuilder sb = new StringBuilder();
							for (int j=0; j< (temp.length-1); j++) {
								sb.append(temp[j]+ " ");
							}
							sb.append(temp[temp.length-1]);
							parameters.put(postParameters.split("=")[0], sb.toString()); 
						}
						else {
							parameters.put(postParameters.split("=")[0], postParameters.split("=")[1]); 
						}		
					}
				}
			}
		} catch (Exception e) {
			response400();
		}
	}
	
	/**
	 * parse the URI to the resource and parameter section
	 */	
	private void splitURI() {
		int indexOfParam=requestPage.length();
		if (requestPage.contains("?")) {
			indexOfParam = requestPage.indexOf("?"); 
			paramSectionHeader = requestPage.substring((indexOfParam + 1), requestPage.length());
		}
		
		requestPage = requestPage.substring(0,indexOfParam);
	}
	
	/**
	 * check that the request is valid request if not send the propre response according to the reason 
	 */	
	private boolean checkRequsetIsValid() {
		
		
		// check that the http method is supported
		if (!HttpMethods.contains(requestMethod)) {
			response501(requestMethod);
			return false;
			
		}
		
		// check that the http version is http 1.1 or 1.0
		if (!HttpVersion.equals("HTTP/1.0") && !HttpVersion.equals("HTTP/1.1")) {
			System.err.println("Http version does not supported the server support only 1.0 or 1.1");
			response400();	
			return false;
		}
		
		// if the http version is 1.1 check that a host field exists
		if (!requestParam.containsKey("Host") && HttpVersion.equals("HTTP/1.1")) {
			System.err.println("ERROR - In Http 1.1 version request must include Host value");
			response400();
			return false;
		}
		
		if (!requestPage.startsWith("/") && (!requestMethod.equals("OPTIONS"))) {
			response400();
			return false;
		}
		
		// check for illegal characters in the URI 
		for (String s : reservedChar) {
			if (requestPage.contains(s)) {
				if (!requestPage.equals("*") || !requestMethod.equals("OPTIONS")) {
					System.err.println("ERROR - The reasoure contain illegal character");
					System.err.println("illegal char: " +s);
					response400();
					return false;
				}
			}
		}
		
		if (requestPage.contains("/../") || requestPage.contains("/src/"))
        {
            response401();
            return false;
        }
		return true;
	}
	
	/**
	 *  handle the response according the known HTTP methods
	 */	
	private void handleResponse() {	
	
		switch (requestMethod) {
			case "GET":
				getMethod();
				break;
			case "POST":
				postMethod();
				break;
			 case "OPTIONS":
	            optionsMethod();
	            break;
			 case "HEAD":
	            headMethod();
	            break;
	         case "TRACE":
	            traceMethod();
	            break;

	            default:
	                // Handles unsupported methods
	                if (requestMethod.equals("DELETE") || requestMethod.equals("PUT") || requestMethod.equals("CONNECT")) {
	                    response501(requestMethod);
	                }
	                
	                // Handles bad requests
	                else {
	                    response400();
	                    return;
	                }
	                break;		
		}
	}
		
	/**
	 *  Handle response for request TRACE 
	 */		
	private void traceMethod() {
		try {
			String newFilePath = filePath(requestPage);
			fileContent = readFile(newFilePath);		//TODO-do i need to check the URI is correct?
			if (fileContent !=null) {
				response = response200OK(req.toString());
				os.writeBytes(response);
				os.writeBytes(req.toString());
				if (postParameters != null) {
					os.writeBytes(postParameters);
				}
			}
		} catch (IOException e) {
			System.err.println("Warning - Unable to write to OutputStream on traceMethod- Stream Closed..");
    		closeOs();
		}
		
	}
	
	/**
	 *  Handle response for request HEAD 
	 */	
	private void headMethod() {
		try {
			String newFilePath = filePath(requestPage);
			fileContent = readFile(newFilePath);
			if (fileContent !=null) {
				response = response200OK(newFilePath);
				os.writeBytes(response);
			}
		} catch (IOException e) {
			System.err.println("Warning - Unable to write to OutputStream on headMethod- Stream Closed..");
    		closeOs();
		}
		
	}

	private void optionsMethod() {
		StringBuilder sb = new StringBuilder();
		sb.append(HttpVersion + " 200 OK "+ CRLF);
		sb.append("Allow: ");
		for (String s : HttpMethods) {
			sb.append(s+",");
		}
		String optionString = sb.toString();
		optionString = optionString.substring(0, optionString.length() - 1);
		optionString = optionString + CRLF;
		System.out.println(optionString);
		try {
			os.writeBytes(optionString);
		} catch (IOException e) {
			System.out.println("Warning - Unable to write to OuputStream in optionMethod - Stream closed..");
		}
		
	}

	/**
	 *  Handle response for request POST 
	 */		
	private void postMethod() {
		String newFilePath = filePath(requestPage);
		makeNewHtmlPage();	
		fileContent = readFile(newFilePath);
		try {
			if (fileContent !=null) {
				response = response200OK(newFilePath);
				os.writeBytes(response);
				writeFile(fileContent);
			}
		} catch (Exception e) {
			System.out.println("Warning - Unable to write to ouput stream in postMethod - Stream closed..");
		}	
	}

	
	/**
	 *  Handle response for request POST 
	 */	
	private void getMethod() {	
		try {
			String newFilePath = filePath(requestPage);
			fileContent = readFile(newFilePath);
			if (fileContent !=null) {
				response = response200OK(newFilePath);
				os.writeBytes(response);
				writeFile(fileContent);
			}
		} catch (IOException e) {
			System.err.println("Warning - Unable to write to OutputStream on getMethod- Stream Closed..");
    		closeOs();
		}
	}
		
	
	
	/**
	 *  make a 200OK response
	 */	
	public String response200OK(String path) {	
		String pathExtansion = contentType(path);
		StringBuilder sb = new StringBuilder();
		System.out.println();
		System.out.println(log +"HTTP response:" + log);
		sb.append(HttpVersion +" 200 OK"+CRLF);
		sb.append("Content-Type: "+ pathExtansion +CRLF);
		if (isChunked) {
			sb.append("Transfer-Encoding: chunked" + CRLF);
        }
		sb.append("Content-Length:");
		if (requestMethod.equals("TRACE")) {
			sb.append(path.length()+CRLF);
		}
		else {
			sb.append(fileContent.length()+CRLF);
		}
		System.out.println(sb.toString());
		sb.append(CRLF);
		return sb.toString();
	}
	
	/**
	 *  make a 404 NOT FOUND response
	 */	
	private void response404(String path) {
		StringBuilder sb = new StringBuilder();  
		System.out.println();
		System.out.println(log +"HTTP response:" + log);
		sb.append(HttpVersion +" 404 Not Found "+CRLF);
		System.out.println(sb.toString());
		sb.append(CRLF);
		sb.append("<HTML><HEAD><TITLE> 404 Page Not Found </TITLE></HEAD><BODY><H1> 404 Not Found </H1></BODY></HTML>" + CRLF);
		try {
			os.writeBytes(sb.toString());
		} catch (IOException e) {
			System.err.println("Warning - Unable to write response 404 to OutputStream - Stream closed..");
			closeOs();
		}
	}
	
	/**
	 *  make a 401 Unauthorized response
	 */
	private void response401() {
		StringBuilder sb = new StringBuilder();  
		System.out.println();
		System.out.println(log +"HTTP response:" + log);
		sb.append(HttpVersion +" 401 Unauthorized "+CRLF);
		sb.append("Connection: close "+CRLF);
		System.out.println(sb.toString());
		sb.append(CRLF);
		sb.append("<HTML><HEAD><TITLE>  401 Unauthorized </TITLE></HEAD><BODY><H1> 401 Unauthorized </H1></BODY></HTML>" + CRLF);
		try {
			os.writeBytes(sb.toString());
			isAlive = false;
		} catch (IOException e) {
			System.err.println("Warning - Unable to write response 401 to OutputStream - Stream closed..");
			closeOs();
		}
	}
	
	/**
	 *  make a 501 NOT supported response
	 */	
	private void response501(String methodType) {
		StringBuilder sb = new StringBuilder();
		System.out.println();
		System.out.println(log +"HTTP response:" + log);
		sb.append(HttpVersion +" 501 Not Implemented "+CRLF);
		System.out.println(sb.toString());
		sb.append(CRLF);
		sb.append("<HTML><HEAD><TITLE> 501 Not Implemented </TITLE></HEAD><BODY><H1> 501 Unsupported Request </H1><H2>Unsupported Request: "+
				methodType +" not supported </H2></BODY></HTML>" + CRLF);
		try {
			os.writeBytes(sb.toString());
		} catch (IOException e) {
			System.err.println("Warning - Unable to write response 501 to OutputStream - Stream closed..");
		}
	}

	
	/**
	 *  make a 400 Bad request response
	 */	
	private void response400() {
		StringBuilder sb = new StringBuilder();
		System.out.println();
		System.out.println(log +"HTTP response:" + log);
		sb.append(HttpVersion +" 400 Bad Request "+CRLF);
		sb.append("Connection: close "+CRLF);
		System.out.println(sb.toString());
		sb.append(CRLF);
		sb.append("<HTML><HEAD><TITLE>  400 Bad Request </TITLE></HEAD><BODY><H1> 400 Bad Request </H1></BODY></HTML>" + CRLF);
		
		try {
			badRequest = true;
			isAlive = false;
			os.writeBytes(sb.toString());
		} catch (IOException e) {
			System.err.println("Warning - Unable to write response 400 to OutputStream");
		}
	}
	
	/**
	 *  Write the file content (String) to outputStream 
	 */	
	private void writeFile(String fileContent) {
		try {
			if (isChunked) {
				byte[] rawData = fileContent.getBytes();
				String rawDataString = new String(rawData);
				for(int currentByte = 0; currentByte < rawData.length; currentByte += CHUNKED_SIZE) {
					int currentWrite;
					StringBuilder sb = new StringBuilder();
					if(currentByte + CHUNKED_SIZE > rawData.length) {
						currentWrite = rawData.length - currentByte;
					} else {
						currentWrite = CHUNKED_SIZE;
					}
					sb.append(Integer.toString(currentWrite,16) + CRLF);
					sb.append(rawDataString.substring(currentByte, currentByte+currentWrite));
					sb.append(CRLF);
					os.writeBytes(sb.toString());
				}
				os.writeBytes("0"+CRLF);
				os.writeBytes(CRLF);
			} else {
					os.writeBytes(fileContent);
					}
		} catch (IOException e) {
	        		System.err.println("Warning - Unable to write to OutputStream - Stream Closed..");
	        		closeOs();
	    }
	} 
		
			
	/**
	 *  Read a file content into a string
	 */		
	private String readFile(String path) {
		StringBuilder sb = new StringBuilder();
		File file = new File(path);
		if (!file.exists()) {
			System.err.println("WARNING - The resource " + requestPage + " does not exist");
			response404(path);
			return null;
		}	
		try {	
			FileInputStream fis = new FileInputStream(file);
			int res = 0;
			while(fis.available() != 0) {
					res = fis.read();
					sb.append((char) res);
			}	
			fis.close();
			} catch(FileNotFoundException e) {
				response404(path);
			} catch(IOException e) {
				System.err.println("Warning - Unable to read from file: " + path); 
			}
			fileContent = sb.toString();
			return sb.toString();
		}
		
	/**
	 *  make a new path according to the root directory
	 */	
	private String filePath(String path) {
		String newPath;
		if (path.equals("/")) {
			newPath = rootDir + defaultPage;
		}
		else {
				//path = path.substring(1, path.length());
				newPath = rootDir +path;
				//newPath = path;
		}
		return newPath;	
	}
	
	/**
	 *  Make a new HTML file in the root directory to show the parameters send in the POST request
	 */		
	private void makeNewHtmlPage() {
		try {
			File postParamHtml = new File(rootDir+pramHtmlPageName);
			BufferedWriter bw = new BufferedWriter(new FileWriter(postParamHtml));
			bw.write("<HTML><HEAD><TITLE> params_info.html </TITLE></HEAD>" + "<BODY>");
			bw.write("<table border=\"1\" style=\"width:400px;\">");
			bw.write( "\t\t<tr>\n" +
	                   "\t\t\t<td>Parameter: </td>\n" +
	                   "\t\t\t<td>Value: </td>\n" +
	                   "\t\t</tr>");
	
			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				bw.write( "\t\t<tr>\n" +
                   "\t\t\t<td>" + entry.getKey() + " </td>\n" +
                   "\t\t\t<td>" + entry.getValue() + "</td>\n" +
                   "\t\t</tr>");
				}
			bw.write("</BODY></HTML>");
			bw.close();
		} catch (Exception e) {
			System.err.println("Warning - Unable to write on the file: "+ pramHtmlPageName);
		}
		}
		
	private static String contentType(String filename) {
		if(filename.endsWith(".htm") || filename.endsWith(".html")) {
			return "text/html";
		}
		if(filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
			return "image/jpeg";
		}
		if(filename.endsWith(".gif")) {
			return "image/gif";
		}
		if(filename.endsWith(".ico")) {
			return "ico";
		}
		return "application/octet-stream";
	}


	public boolean keepAlive() {
		return isAlive;
	}
	
	private void closeOs() {
		try {
			os.close();
		} catch (IOException e) {
			System.err.println("Warning - Unable to close OutputStream");
		}
	}
	
	
}
