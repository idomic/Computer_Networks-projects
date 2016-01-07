
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;


/**
 * WebCrawler class in the Runnable class that is executed by each crawler thread.
 *
 */
public class Downloader implements Runnable {

  
   // The id associated to the crawler thread running this instance
  protected int m_Id;
  
   // The thread within which this crawler instance is running. 
  private Thread myThread;



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
  private long m_startTime;
  private long m_endTime;
  public SynchronousQueue<String> m_downloadedQueue;

  /**
   * Initializes the current instance of the crawler
   *
   * @param i_Id
   *            the id of this crawler instance
   */
  public Downloader(int i_Id, Map<String, String> i_postParameters) {
    this.m_Id = i_Id;
    this.m_Domain = i_postParameters.get("domain");
    this.m_RobotsTxt = shouldUseRobots(i_postParameters.get("robotsTxt"));
    this.m_portScan = shouldPerformPortScan(i_postParameters.get("portScan"));
    this.isWaitingForNewURLs = false;
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
	//	Deal with robots/port scan.
	// Get method to domain.
	
	// Save response as String
	m_startTime = System.currentTimeMillis();
	generateGetRequest();
	m_endTime = System.currentTimeMillis();
	
	// save the result inside downloads queue for analyzer class.
	String parsedSite = "";
	m_downloadedQueue.add(parsedSite);
	m_downloadedQueue.notifyAll();
	// shutdown.
	
}

private void generateGetRequest() {
	
}

private void generateHeadRequest() {
	
}


//
//    public PageFetcher(CrawlConfig config) {
//      super(config);
//
//      RequestConfig requestConfig =
//          RequestConfig.custom().setExpectContinueEnabled(false).setCookieSpec(CookieSpecs.DEFAULT)
//                       .setRedirectsEnabled(false).setSocketTimeout(config.getSocketTimeout())
//                       .setConnectTimeout(config.getConnectionTimeout()).build();
//
//      RegistryBuilder<ConnectionSocketFactory> connRegistryBuilder = RegistryBuilder.create();
//      connRegistryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE);
//      if (config.isIncludeHttpsPages()) {
//        try { // Fixing: https://code.google.com/p/crawler4j/issues/detail?id=174
//          // By always trusting the ssl certificate
//          SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
//            @Override
//            public boolean isTrusted(final X509Certificate[] chain, String authType) {
//              return true;
//            }
//          }).build();
//          SSLConnectionSocketFactory sslsf =
//              new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//          connRegistryBuilder.register("https", sslsf);
//        } catch (Exception e) {
//          logger.warn("Exception thrown while trying to register https");
//          logger.debug("Stacktrace", e);
//        }
//      }
//
//      Registry<ConnectionSocketFactory> connRegistry = connRegistryBuilder.build();
//      connectionManager = new PoolingHttpClientConnectionManager(connRegistry);
//      connectionManager.setMaxTotal(config.getMaxTotalConnections());
//      connectionManager.setDefaultMaxPerRoute(config.getMaxConnectionsPerHost());
//
//      HttpClientBuilder clientBuilder = HttpClientBuilder.create();
//      clientBuilder.setDefaultRequestConfig(requestConfig);
//      clientBuilder.setConnectionManager(connectionManager);
//      clientBuilder.setUserAgent(config.getUserAgentString());
//      clientBuilder.setDefaultHeaders(config.getDefaultHeaders());
//
//      if (config.getProxyHost() != null) {
//        if (config.getProxyUsername() != null) {
//          BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//          credentialsProvider.setCredentials(new AuthScope(config.getProxyHost(), config.getProxyPort()),
//                                             new UsernamePasswordCredentials(config.getProxyUsername(),
//                                                                             config.getProxyPassword()));
//          clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
//        }
//
//        HttpHost proxy = new HttpHost(config.getProxyHost(), config.getProxyPort());
//        clientBuilder.setProxy(proxy);
//        logger.debug("Working through Proxy: {}", proxy.getHostName());
//      }
//
//      httpClient = clientBuilder.build();
//      if ((config.getAuthInfos() != null) && !config.getAuthInfos().isEmpty()) {
//        doAuthetication(config.getAuthInfos());
//      }
//
//      if (connectionMonitorThread == null) {
//        connectionMonitorThread = new IdleConnectionMonitorThread(connectionManager);
//      }
//      connectionMonitorThread.start();
//    }
//    
//
//    private void doAuthetication(List<AuthInfo> authInfos) {
//      for (AuthInfo authInfo : authInfos) {
//        if (authInfo.getAuthenticationType() == AuthInfo.AuthenticationType.BASIC_AUTHENTICATION) {
//          doBasicLogin((BasicAuthInfo) authInfo);
//        } else if (authInfo.getAuthenticationType() == AuthInfo.AuthenticationType.NT_AUTHENTICATION) {
//          doNtLogin((NtAuthInfo) authInfo);
//        } else {
//          doFormLogin((FormAuthInfo) authInfo);
//        }
//      }
//    }
//
//    /**
//     * BASIC authentication<br/>
//     * Official Example: https://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org/apache/http/examples
//     * /client/ClientAuthentication.java
//     * */
//    private void doBasicLogin(BasicAuthInfo authInfo) {
//      logger.info("BASIC authentication for: " + authInfo.getLoginTarget());
//      HttpHost targetHost = new HttpHost(authInfo.getHost(), authInfo.getPort(), authInfo.getProtocol());
//      CredentialsProvider credsProvider = new BasicCredentialsProvider();
//      credsProvider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()),
//                                   new UsernamePasswordCredentials(authInfo.getUsername(), authInfo.getPassword()));
//      httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
//    }
//
//    /**
//     * Do NT auth for Microsoft AD sites.
//     */
//    private void doNtLogin(NtAuthInfo authInfo) {
//      logger.info("NT authentication for: " + authInfo.getLoginTarget());
//      HttpHost targetHost = new HttpHost(authInfo.getHost(), authInfo.getPort(), authInfo.getProtocol());
//      CredentialsProvider credsProvider = new BasicCredentialsProvider();
//      try {
//        credsProvider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()),
//                new NTCredentials(authInfo.getUsername(), authInfo.getPassword(),
//                        InetAddress.getLocalHost().getHostName(), authInfo.getDomain()));
//      } catch (UnknownHostException e) {
//        logger.error("Error creating NT credentials", e);
//      }
//      httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
//    }
//
//    /**
//     * FORM authentication<br/>
//     * Official Example:
//     *  https://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org/apache/http/examples/client/ClientFormLogin.java
//     */
//    private void doFormLogin(FormAuthInfo authInfo) {
//      logger.info("FORM authentication for: " + authInfo.getLoginTarget());
//      String fullUri =
//          authInfo.getProtocol() + "://" + authInfo.getHost() + ":" + authInfo.getPort() + authInfo.getLoginTarget();
//      HttpPost httpPost = new HttpPost(fullUri);
//      List<NameValuePair> formParams = new ArrayList<>();
//      formParams.add(new BasicNameValuePair(authInfo.getUsernameFormStr(), authInfo.getUsername()));
//      formParams.add(new BasicNameValuePair(authInfo.getPasswordFormStr(), authInfo.getPassword()));
//
//      try {
//        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, "UTF-8");
//        httpPost.setEntity(entity);
//        httpClient.execute(httpPost);
//        logger.debug("Successfully Logged in with user: " + authInfo.getUsername() + " to: " + authInfo.getHost());
//      } catch (UnsupportedEncodingException e) {
//        logger.error("Encountered a non supported encoding while trying to login to: " + authInfo.getHost(), e);
//      } catch (ClientProtocolException e) {
//        logger.error("While trying to login to: " + authInfo.getHost() + " - Client protocol not supported", e);
//      } catch (IOException e) {
//        logger.error("While trying to login to: " + authInfo.getHost() + " - Error making request", e);
//      }
//    }

//    public PageFetchResult fetchPage(WebURL webUrl)
//        throws InterruptedException, IOException {
//      
//    	// Getting URL, setting headers & content
//      PageFetchResult fetchResult = new PageFetchResult();
//      String toFetchURL = webUrl.getURL();
//      HttpUriRequest request = null;
//      try {
//        request = newHttpUriRequest(toFetchURL);
//        // Applying Politeness delay
//        synchronized (mutex) {
//          long now = (new Date()).getTime();
//          if ((now - lastFetchTime) < config.getPolitenessDelay()) {
//            Thread.sleep(config.getPolitenessDelay() - (now - lastFetchTime));
//          }
//          lastFetchTime = (new Date()).getTime();
//        }
//
//        CloseableHttpResponse response = httpClient.execute(request);
//        fetchResult.setEntity(response.getEntity());
//        fetchResult.setResponseHeaders(response.getAllHeaders());
//
//        // Setting HttpStatus
//        int statusCode = response.getStatusLine().getStatusCode();
//
//        // If Redirect ( 3xx )
//        if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY ||
//            statusCode == HttpStatus.SC_MULTIPLE_CHOICES || statusCode == HttpStatus.SC_SEE_OTHER ||
//            statusCode == HttpStatus.SC_TEMPORARY_REDIRECT ||
//            statusCode == 308) { // todo follow https://issues.apache.org/jira/browse/HTTPCORE-389
//
//          Header header = response.getFirstHeader("Location");
//          if (header != null) {
//            String movedToUrl = URLCanonicalizer.getCanonicalURL(header.getValue(), toFetchURL);
//            fetchResult.setMovedToUrl(movedToUrl);
//          }
//        } else if (statusCode >= 200 && statusCode <= 299) { // is 2XX, everything looks ok
//          fetchResult.setFetchedUrl(toFetchURL);
//          String uri = request.getURI().toString();
//          if (!uri.equals(toFetchURL)) {
//            if (!URLCanonicalizer.getCanonicalURL(uri).equals(toFetchURL)) {
//              fetchResult.setFetchedUrl(uri);
//            }
//          }
//
//          // Checking maximum size
//          if (fetchResult.getEntity() != null) {
//            long size = fetchResult.getEntity().getContentLength();
//            if (size == -1) {
//              Header length = response.getLastHeader("Content-Length");
//              if (length == null) {
//                length = response.getLastHeader("Content-length");
//              }
//              if (length != null) {
//                size = Integer.parseInt(length.getValue());
//              }
//            }
//            if (size > config.getMaxDownloadSize()) {
//              //fix issue #52 - consume entity
//              response.close();
//              throw new PageBiggerThanMaxSizeException(size);
//            }
//          }
//        }
//
//        fetchResult.setStatusCode(statusCode);
//        return fetchResult;
//
//      } finally { // occurs also with thrown exceptions
//        if ((fetchResult.getEntity() == null) && (request != null)) {
//          request.abort();
//        }
//      }
//    }

//    public synchronized void shutDown() {
//      if (connectionMonitorThread != null) {
//        connectionManager.shutdown();
//        connectionMonitorThread.shutdown();
//      }
//    }

    

    
    
    
    
    
    
    

  /**
   * Get the id of the current crawler instance
   *
   * @return the id of the current crawler instance
   */
  public int getMyId() {
    return m_Id;
  }


  /**
   * This function is called just before starting the crawl by this crawler
   * instance. It can be used for setting up the data structures or
   * initializations needed by this crawler instance.
   */
  public void onStart() {
    // Do nothing by default
    // Sub-classed can override this to add their custom functionality
  }

  /**
   * This function is called just before the termination of the current
   * crawler instance. It can be used for persisting in-memory data or other
   * finalization tasks.
   */
  public void onBeforeExit() {
    // Do nothing by default
    // Sub-classed can override this to add their custom functionality
  }

  /**
   * This function is called once the header of a page is fetched. It can be
   * overridden by sub-classes to perform custom logic for different status
   * codes. For example, 404 pages can be logged, etc.
   *
   * @param webUrl WebUrl containing the statusCode
   * @param statusCode Html Status Code number
   * @param statusDescription Html Status COde description
   */
//  protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
//    // Do nothing by default
//    // Sub-classed can override this to add their custom functionality
//  }

  /**
   * This function is called before processing of the page's URL
   * It can be overridden by subclasses for tweaking of the url before processing it.
   * For example, http://abc.com/def?a=123 - http://abc.com/def
   *
   * @param curURL current URL which can be tweaked before processing
   * @return tweaked WebURL
   */
//  protected WebURL handleUrlBeforeProcess(WebURL curURL) {
//    return curURL;
//  }

  /**
   * This function is called if the content of a url is bigger than allowed size.
   *
   * @param urlStr - The URL which it's content is bigger than allowed size
   */
//  protected void onPageBiggerThanMaxSize(String urlStr, long pageSize) {
//    logger.warn("Skipping a URL: {} which was bigger ( {} ) than max allowed size", urlStr, pageSize);
//  }

  /**
   * This function is called if the crawler encountered an unexpected http status code ( a status code other than 3xx)
   *
   * @param urlStr URL in which an unexpected error was encountered while crawling
   * @param statusCode Html StatusCode
   * @param contentType Type of Content
   * @param description Error Description
   */
//  protected void onUnexpectedStatusCode(String urlStr, int statusCode, String contentType, String description) {
//    logger.warn("Skipping URL: {}, StatusCode: {}, {}, {}", urlStr, statusCode, contentType, description);
//    // Do nothing by default (except basic logging)
//    // Sub-classed can override this to add their custom functionality
//  }

  /**
   * This function is called if the content of a url could not be fetched.
   *
   * @param webUrl URL which content failed to be fetched
   */
//  protected void onContentFetchError(WebURL webUrl) {
//    logger.warn("Can't fetch content of: {}", webUrl.getURL());
//    // Do nothing by default (except basic logging)
//    // Sub-classed can override this to add their custom functionality
//  }
  
  /**
   * This function is called when a unhandled exception was encountered during fetching
   *
   * @param webUrl URL where a unhandled exception occured
   */
//  protected void onUnhandledException(WebURL webUrl, Throwable e) {
//    String urlStr = (webUrl == null ? "NULL" : webUrl.getURL());
//    logger.warn("Unhandled exception while fetching {}: {}", urlStr, e.getMessage());
//    logger.info("Stacktrace: ", e);
//    // Do nothing by default (except basic logging)
//    // Sub-classed can override this to add their custom functionality
//  }
  
  /**
   * This function is called if there has been an error in parsing the content.
   *
   * @param webUrl URL which failed on parsing
   */
//  protected void onParseError(WebURL webUrl) {
//    logger.warn("Parsing error of: {}", webUrl.getURL());
//    // Do nothing by default (Except logging)
//    // Sub-classed can override this to add their custom functionality
//  }

  /**
   * The CrawlController instance that has created this crawler instance will
   * call this function just before terminating this crawler thread. Classes
   * that extend WebCrawler can override this function to pass their local
   * data to their controller. The controller then puts these local data in a
   * List that can then be used for processing the local data of crawlers (if needed).
   *
   * @return currently NULL
   */
  public Object getMyLocalData() {
    return null;
  }

 
  /**
   * Classes that extends WebCrawler should overwrite this function to tell the
   * crawler whether the given url should be crawled or not. The following
   * default implementation indicates that all urls should be included in the crawl.
   *
   * @param url
   *            the url which we are interested to know whether it should be
   *            included in the crawl or not.
   * @param referringPage
   *           The Page in which this url was found.
   * @return if the url should be included in the crawl it returns true,
   *         otherwise false is returned.
   */
//  public boolean shouldVisit(Page referringPage, WebURL url) {
//    // By default allow all urls to be crawled.
//    return true;
//  }

  /**
   * Classes that extends WebCrawler should overwrite this function to process
   * the content of the fetched and parsed page.
   *
   * @param page
   *            the page object that is just fetched and parsed.
   */
 

 
  public Thread getThread() {
    return myThread;
  }

  public void setThread(Thread myThread) {
    this.myThread = myThread;
  }

  public boolean isNotWaitingForNewURLs() {
    return !isWaitingForNewURLs;
  }
}