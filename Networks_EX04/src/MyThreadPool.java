
import java.util.LinkedList;

public class MyThreadPool {
	private WorkerThread[] threads;
	private LinkedList<Runnable> requestsQueue;
	public static int numberOfCnnections = 0;
	String log = "   ********   ";
	
	public MyThreadPool(int threadNumber) {
		requestsQueue = new LinkedList<Runnable>();
		threads = new WorkerThread[threadNumber];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new WorkerThread();
			threads[i].start();
			//System.out.println("");
		}
	}
	
	public void enqueue(Runnable r) {
		synchronized (requestsQueue) {
			requestsQueue.addLast(r);
			requestsQueue.notify();
			numberOfCnnections ++;
			System.out.println(log +"Number of Threads:" + numberOfCnnections + log);
			System.out.println(log +"Thread was created" + log);
		}
	}
	
	public class WorkerThread extends Thread {
		public void run() {
			Runnable r;
			while (true) {
				synchronized (requestsQueue) {
					while (requestsQueue.isEmpty()) {
						try {
							requestsQueue.wait();
						} catch (InterruptedException e) {
							// ignore
						}
					}
					r = (Runnable) requestsQueue.removeFirst();					
				}
				try {
					r.run();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}
}