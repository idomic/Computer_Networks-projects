
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * WebCrawler class in the Runnable class that is executed by each crawler thread.
 *
 */
public class Analyzer implements Runnable {


	// The id associated to the crawler thread running this instance
	protected int m_Id;
	private int m_numOfimgs;
	private int m_numOfvids;
	private int m_numOfdocs;

	/**
	 * The RobotstxtServer instance that is used by this crawler instance to
	 * determine whether the crawler is allowed to crawl the content of each page.
	 */
	//private Robotstxt robotsTxt;


	/**
	 * Is the current crawler instance waiting for new URLs? This field is
	 * mainly used by the controller to detect whether all of the crawler
	 * instances are waiting for new URLs and therefore there is no more work
	 * and crawling can be stopped.
	 */
	private boolean isWaitingForNewURLs;
	private String m_Domain;
	private boolean m_RobotsTxt;
	private boolean m_portScan;
	static String[] m_imageExtensions;
	static String[] m_videoExtensions;
	static String[] m_documentExtensions;
	public SynchronousQueue<String> m_downloadedQueue;

	/**
	 * Initializes the current instance of the crawler
	 *
	 * @param i_Id
	 *            the id of this crawler instance
	 */
	public Analyzer(int i_Id) {
		m_downloadedQueue = new SynchronousQueue<String>();
		m_Id = i_Id;
		m_imageExtensions = WebServer.imageExtensions;
		m_videoExtensions = WebServer.videoExtensions;
		m_documentExtensions = WebServer.documentExtensions;
	}

	// Check if an URL is within the domain
	private boolean isWithinDomain(String i_Url) {

		if((i_Url.equals(m_Domain)) || (i_Url.matches("/.*.htm.*"))) {
			return true;
		} else {
			return false;
		}
	}

	// Check if Robots.txt needed.
	private boolean shouldUseRobots(String i_Robots) {

		return i_Robots.equals("true");
	}

	// Check if port scan required.
	private boolean shouldPerformPortScan(String i_portScan) {

		return i_portScan.equals("true");
	}


	@Override
	public void run() {
		//String currentWebsite = m_downloadedQueue.remove();
		//parseHTML(currentWebsite); 
		parseHTML("<a href='http://www.w3schools.com/html/'>Visit our HTML tutorial</a>");


		// save the result inside downloads queue for analyzer class.
		// shutdown.

	}

	private void parseHTML(String i_toParse) {
		ArrayList<String> tags = new ArrayList<String>();
		ArrayList<String> Links = new ArrayList<String>();
		ArrayList<String> Images = new ArrayList<String>();
		ArrayList<String> Videos = new ArrayList<String>();
		ArrayList<String> Documents = new ArrayList<String>();
		String currTag;
		//Insert all tags to array
		Matcher m = Pattern.compile("/<.*a.*>/g | /<.*img.*>/g").matcher(i_toParse);
		while (m.find()) {
			tags.add(m.group());
		}
		for (String tag : tags) {
			if(tag.matches("/<*a.*?>/g")) {
				boolean isFound = false;
				for (String extension : m_videoExtensions) {
					if(tag.matches(".*" + extension +".*")) {
						Videos.add(tag);
						isFound = true;
						break;
					}
				}
				if(!isFound) {
					for (String extension : m_documentExtensions) {
						if(tag.matches(".*" + extension +".*")) {
							Documents.add(tag);
							isFound = true;
							break;
						}
					}
				} else {
					Links.add(tag);
				}
				
			} else if(tag.matches("/<*img.*?>/g")) {
				Images.add(tag);
			}
		}



	}

	private void parseLinks(String[] i_toParse) {
		String[] tags;
		//Insert all tags to array

	}

	private void parseVideos(String[] i_toParse) {
		String[][] tags;
		//Insert all tags to array

	}

	private void parseImages(String[] i_toParse) {
		String[][] tags;
		//Insert all tags to array

	}

	private void parseDocuments(String[] i_toParse) {
		String[][] tags;
		//Insert all tags to array

	}

	public boolean isNotWaitingForNewURLs() {
		return !isWaitingForNewURLs;
	}
}