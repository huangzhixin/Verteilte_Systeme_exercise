package vsue.rpc;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Scanner;

import vsue.communication.VSObjectConnection;
import vsue.communication.VSTestMessage;
import vsue.rmi.VSBoard;
import vsue.rmi.VSBoardImpl;

public class VSServer implements Runnable{
	private static VSServer instance;
	static int port;
	public VSServer (int port){
		this.port = port;
	}
	
	
	static synchronized VSServer getInstance() throws IOException {
	    if (VSServer.instance == null)
	      VSServer.instance = new VSServer(port);
	    return VSServer.instance;
	  }
	
	public class VSRequestWorker extends Thread{
		private Socket clientSocket;
		
		public VSRequestWorker(Socket clientSocket) {
			this.clientSocket = clientSocket;
			
			start();
		}
		
		public void run(){
			VSObjectConnection  connection =  new  VSObjectConnection(this.clientSocket);
			while(true)
			{
				VSTestMessage message;
				try {
					message = (VSTestMessage) connection.receiveObject();
					int objectID = message.getInteger();
					String genericMethodName = message.getString();
					Object[] argss = message.getObjects();
					/*if(argss == null)
					{
						System.out.println("leer");
					}
					else
					{
						System.out.println("nicht leer");
					}*/
					VSRemoteObjectManager manager = VSRemoteObjectManager.getInstance();
					Serializable retVal = null;
					retVal = (Serializable) manager.invokeMethod(objectID, genericMethodName,argss);
					connection.sendObject(retVal);
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void run() {
    	System.out.println("server run");
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Socket clientSocket = null;
		while(true)
		{
			try {
				clientSocket = serverSocket.accept();
				
				new VSRequestWorker(clientSocket);
				 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}
		
	}
} 

	