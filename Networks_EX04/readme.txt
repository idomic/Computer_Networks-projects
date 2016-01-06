



We implemented 4 classes: WebServer, MyThreadPool, HttpRequest and Client.

WebServer - is the “main” class. in this class we parse the config file to variable, open server socket that listen for TCP connection and send(enqueue) to MyThreadPool client to execute. 

MyThreadPool - has 2 important fields: 1. array that hold runnable variable(WorkerThreads) 2. LinkedList that hold the requests from the clients(requestsQueue). while the queue is not empty each runnable variable take responsibility of each request.

Client - has one major method “processRequest”. this method open socket and read line by line the request from the client. if the request is not empty the Client class send to HttpRequest class the information.

HttpRequest - responsible to parse the request and find the correct response according to the request.
the class has 2 Hashmaps: 1.requestParam - hold key and value of the request , 2.parameters - hold key and value with parameters from the URI.
At the beginning we parse the request and save the data in hashmaps.if the request is valid and supported then we build the response according to the type request(GET, POST, OPTIONS, HEAD, TRACE).
else we build response according to the information that miss(response404,response400) from the request or from us(response501)
 