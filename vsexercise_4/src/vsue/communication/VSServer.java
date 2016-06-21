package vsue.communication;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.HashMap;

import vsue.property;


public abstract class VSServer implements Runnable {
	private int port;

	public VSServer() {
		port = property.serverport;
	}

	public int getPort() {
		return port;
	}

	private ServerSocket createServerSocket() throws IOException {
		ServerSocket socket = null;
		boolean exception = false;
		int counter = 0;

		do {
			counter++;
			exception = false;
			try {
				socket = new ServerSocket(getPort());
			} catch (BindException e) {
				port++;
				exception = true;
				System.out.println("Default port already used, trying " + port);
			}
		} while (exception && counter < 20);

		return socket;
	}

	public void run() {
		ServerSocket socket = null;
		
		
		try {
			socket = createServerSocket();
			if (socket == null) {
				System.out.println("No unused port");
				return;
			}


			System.out.println("VSServer is running...");
			while (true) {
				VSObjectConnection vsoCon = new VSObjectConnection(socket.accept());
				
				startWork(vsoCon);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public abstract void startWork(VSObjectConnection vsoCon) throws Exception;
}