/* This is an example of a multithreaded server in Java.
It listens for connections on port 1357 (use telnet to make such connections
once you have the server running) and echos any information sent to it to
anyone else who's logged in at the time - a very simple talker application.

See also http://www.wellho.net/solutions/java-a-multithreaded-server-in-java.html
 */

import java.net.*;
import java.io.*;
import java.util.*;

public class Server implements Runnable {

	// A simple talker - Sample written by Graham Ellis, July 2010 (update)
	// No login needed. Connect on port 1357.
	// commands:
	//      .q      quit
	//      .w      who

	// Variables for each thread

	Socket linkto;                  
	PrintWriter outputStream;     
	BufferedReader inputStream;
	static int port = 8080;
	static String root = "c:/serverroot/";
	int connectionID;
	String hostName; 

	// Class Variables
	static Vector connectiontable;                  // TODO in a list or thread pool.
	static int nextid = 1;                          // ID counter

	public static void main(String [] args) {
		
		// Parent thread - create a server socket and listen for a connection
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		connectiontable = new Vector();

		try {
			serverSocket = new ServerSocket(port);
			while ((clientSocket = serverSocket.accept())!= null) {

				// Connection received - create a thread
				Server now;
				Thread current = new Thread(now = new Server(clientSocket));
				current.setDaemon(true);
				connectiontable.addElement(now);  // Save talker into vector ..
				current.start();                  // start the user's thread
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
		
		// TODO Check if finally needs something else.
		finally {
			try {
				serverSocket.close();
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Constructor for a new thread
	Server (Socket connectionFrom) {
		connectionID = nextid++;
		linkto = connectionFrom;
		InetAddress source = linkto.getInetAddress();
		hostName = source.getHostName();
		try {
			outputStream = new PrintWriter(new OutputStreamWriter
					(linkto.getOutputStream()));
			inputStream = new BufferedReader(new InputStreamReader
					(linkto.getInputStream()));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run () {

		String currentMessage = " [An user has just logged in] ";

		while (true) {
			boolean threadIsDone = false;

			// read a line from the client
			if (currentMessage == null) {
				try {
					outputStream.print(">: ");  //prompt,flush,read
					outputStream.flush();
					currentMessage = inputStream.readLine();
				} catch (Exception e) {
					System.out.println(e);
					threadIsDone = true;   // force exit if there's a problem
				}
			}

			// Handle special cases - user input starts with "."
			// You get a NULL back if connection lost and we should
			// pick up that exception!

			try {
				if (currentMessage.startsWith(".q")) {
					threadIsDone = true;
				}
			} catch (Exception e) {   // e.g. null
				threadIsDone = true;
				currentMessage = "[exiting]";
			}

			if (currentMessage.startsWith(".w")) {  // build up list of host names
				int k;
				StringBuffer fred = new StringBuffer("WHO HERE?\n");
				for (k=0;k<connectiontable.size();k++) {
					Server person = (Server)connectiontable.elementAt(k);
					fred.append(person.hostName);
					fred.append(" ");
				}
				currentMessage = fred.toString();
			}

			// echo the line (with a header) to all users
			String outline = hostName+" "+connectionID+": "+currentMessage;
			int k;
			for (k=0;k<connectiontable.size();k++) {
				Server person = (Server)connectiontable.elementAt(k);
				person.outputStream.println(outline);
				person.outputStream.flush();  // Vital - ensure it is sent!
			}

			// clear out the user if they're done
			if (threadIsDone) {
				connectiontable.removeElement(this);
				try {
					outputStream.close();  // closes needed to terminate connection
					inputStream.close();   // otherwise user's window goes mute
					linkto.close();
				} catch (Exception e) {}
				break;
			}
			currentMessage = null;
		}
	}
}


/** Code from the internet.
 * 
 * package servers;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPooledServer implements Runnable{

    protected int          serverPort   = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    protected ExecutorService threadPool =
        Executors.newFixedThreadPool(10);

    public ThreadPooledServer(int port){
        this.serverPort = port;
    }

    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    break;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
            this.threadPool.execute(
                new WorkerRunnable(clientSocket,
                    "Thread Pooled Server"));
        }
        this.threadPool.shutdown();
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }
}
*/

