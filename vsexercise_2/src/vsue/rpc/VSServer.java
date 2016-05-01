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

public class VSServer {

	//public static vsue.rmi.VSBoard bbs;
    
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		ServerSocket serverSocket = null;
		VSBoard bbs = new VSBoardImpl();
		try {
			serverSocket = new ServerSocket(7777);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		Socket clientSocket = null;
		while(true) {
			try {
				clientSocket = serverSocket.accept();
				//与客户端连接后，接收到客户的函数请求
			    VSObjectConnection  connection =  new  VSObjectConnection(clientSocket);
			    VSTestMessage message = (VSTestMessage) connection.receiveObject();
				int objectID = message.getInteger();
				String genericMethodName = message.getString();
				Object[] argss = message.getObjects();
				VSRemoteObjectManager manager = VSRemoteObjectManager.getInstance();
				 Serializable retVal = null;
				if(genericMethodName.equals("creat object")){
				      retVal = (Serializable) manager.exportObject(bbs);
				}
				else{
				     retVal = (Serializable) manager.invokeMethod(objectID, genericMethodName,argss);
				}
				connection.sendObject(retVal);
			}
			catch(IOException e) {
				System.err.println("Accept failed on Port 7777");
			}
			
		}

	}
}