package vsue.rpc;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import vsue.property;
import vsue.communication.VSObjectConnection;
import vsue.communication.VSReplyMessage;
import vsue.communication.VSTestMessage;
import vsue.faults.VSBuggyObjectConnection;
import vsue.property.RPC_SEMANTICS_ENUM;
import vsue.rmi.VSBoard;
import vsue.rmi.VSBoardImpl;

public class VSServerWorkerThread implements Runnable {
	private static VSServerWorkerThread instance;
	static int port;
	//VSReplyMessage reMessageCache = new VSReplyMessage(null,-1,-1);
	private HashMap<String, VSReplyMessage> map = new HashMap();
	
	
	public VSServerWorkerThread (){
		this.port = property.serverport;
		Timer timer = new Timer();
		TimerTask handler = new VSTimeoutHandler();
		timer.schedule(handler, property.clearGabageFrequence,property.clearGabageFrequence);
	}
	
	
	static synchronized VSServerWorkerThread getInstance() throws IOException {
	    if (VSServerWorkerThread.instance == null)
	      VSServerWorkerThread.instance = new VSServerWorkerThread();
	    return VSServerWorkerThread.instance;
	  }
	
	public class VSRequestWorker extends Thread{
		private Socket clientSocket;
		
		
		public VSRequestWorker(Socket clientSocket) {
			this.clientSocket = clientSocket;

			start();
		}
		
		public void run(){
			
			VSObjectConnection  connection =  new  VSBuggyObjectConnection(this.clientSocket);
			//VSObjectConnection  connection =  new  VSObjectConnection(this.clientSocket);
			while(true)
			{
				VSTestMessage message;
				Serializable retVal = null;
				try {
					message = (VSTestMessage) connection.receiveObject();
					//System.out.println("get message to call "+message.getString()+":  "  + message);
					if(!map.containsKey(message.getClient()))
					{
						VSReplyMessage reMessageCache = new VSReplyMessage(null,-1,-1);
						map.put(message.getClient(), reMessageCache);
					}
					
					
					if(property.RPC_SEMANTICS == RPC_SEMANTICS_ENUM.LAST_OF_MANY)
					{
						System.out.println("messageMajorID："+message.getMajorID()+"messageMinorID:"+message.getMinorID());
						System.out.println("cacheMajorID："+map.get(message.getClient()).getMajorID()+"cacheMinorID:"+map.get(message.getClient()).getMinorID());
						//retVal = (Serializable)lomReply(message);
						VSReplyMessage retmp = lomReply(message);
						if(retmp != null)
						{
							retVal = (Serializable)retmp;
						}
					}
					else if(property.RPC_SEMANTICS == RPC_SEMANTICS_ENUM.AT_MOST_ONCE)
					{
						VSReplyMessage retmp = amoReply(message);
						if(retmp != null)
						{
							retVal = (Serializable)retmp;
						}
					}
					else if(property.RPC_SEMANTICS == RPC_SEMANTICS_ENUM.MAYBE)
					{
						retVal = (Serializable)maybeReply(message);
					}
					
					if(retVal == null)
					{
						continue;
					}
					
					connection.sendObject(retVal);
				
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					System.err.println("error");
					e.printStackTrace();
				}
			}
		}
		
		
		private VSReplyMessage lomReply(VSTestMessage message) throws IOException{
			VSReplyMessage reMessage = null;
			
			if(message==null){
				System.out.println("message is null");
			}
			int objectID = message.getInteger();
			String genericMethodName = message.getString();
			Object[] argss = message.getObjects();
			//System.out.println("server Cache first:" + reMessageCache.getMajorID() +" " + reMessageCache.getMinorID());
			if(map.get(message.getClient()).getMajorID() == message.getMajorID())
			{
				if(map.get(message.getClient()).getMinorID() < message.getMinorID())
				{
					System.out.println("reMessageCache.getMinorID() < message.getMinorID()" );
					VSRemoteObjectManager manager = VSRemoteObjectManager.getInstance();
					Serializable retVal = null;
					retVal = (Serializable) manager.invokeMethod(objectID, genericMethodName,argss);
					reMessage = new VSReplyMessage(retVal,message.getMajorID(),message.getMinorID());
					map.put(message.getClient(), reMessage);
					//reMessageCache = reMessage;
					
				}
				else if(map.get(message.getClient()).getMinorID() >= message.getMinorID())
				{
					System.out.println("reMessageCache.getMinorID()："+map.get(message.getClient()).getMinorID()+" >= message.getMinorID(): "+message.getMinorID());
					//reMessage = new VSReplyMessage(null,0,0);
					reMessage = null;
				}
			}
			else if(map.get(message.getClient()).getMajorID() != message.getMajorID())
			{
				System.out.println("reMessageCache.getMajorID() != message.getMajorID()" );
				VSRemoteObjectManager manager = VSRemoteObjectManager.getInstance();
				Serializable retVal = null;
				retVal = (Serializable) manager.invokeMethod(objectID, genericMethodName,argss);
				reMessage = new VSReplyMessage(retVal,message.getMajorID(),message.getMinorID());
				map.put(message.getClient(), reMessage);
				//reMessageCache = reMessage;
				
			}
			//System.out.println("server Cache second:" + reMessageCache.getMajorID() +" " + reMessageCache.getMinorID());
			System.out.println("server :" + reMessage.getMajorID() +" " + reMessage.getMinorID());
			return reMessage;
		}
		
		private VSReplyMessage amoReply(VSTestMessage message) throws IOException{
			VSReplyMessage reMessage = null;
			
			if(message==null){
				System.out.println("message is null");
			}
			int objectID = message.getInteger();
			String genericMethodName = message.getString();
			Object[] argss = message.getObjects();
			
			if(map.get(message.getClient()).getMajorID() != message.getMajorID())
			{
				System.out.println("reMessageCache.getMajorID() != message.getMajorID()" );
				VSRemoteObjectManager manager = VSRemoteObjectManager.getInstance();
				Serializable retVal = null;
				retVal = (Serializable) manager.invokeMethod(objectID, genericMethodName,argss);
				reMessage = new VSReplyMessage(retVal,message.getMajorID(),message.getMinorID());
				map.put(message.getClient(), reMessage);
				//reMessageCache = reMessage;
			}
			else if(map.get(message.getClient()).getMajorID() == message.getMajorID())
			{
				System.out.println("reMessageCache.getMajorID() == message.getMajorID()" );
				if(map.get(message.getClient()).getMinorID() == message.getMinorID())
				{
					System.out.println("reMessageCache.getMinorID() == message.getMinorID()");
					reMessage = null;
				}
				reMessage = map.get(message.getClient());
				//reMessage = reMessageCache;
			}
			System.out.println("server :" + reMessage.getMajorID() +" " + reMessage.getMinorID());
			return reMessage;
		}
		
		private VSReplyMessage maybeReply(VSTestMessage message) throws IOException{
			if(message==null){
				System.out.println("message is null");
			}
			int objectID = message.getInteger();
			String genericMethodName = message.getString();
			Object[] argss = message.getObjects();
			VSRemoteObjectManager manager = VSRemoteObjectManager.getInstance();
			Serializable retVal = null;
			retVal = (Serializable) manager.invokeMethod(objectID, genericMethodName,argss);
			VSReplyMessage reMessage = new VSReplyMessage(retVal,message.getMajorID(),message.getMinorID());
			System.out.println("server :" + reMessage.getMajorID() +" " + reMessage.getMinorID());
			return reMessage;
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
	private class VSTimeoutHandler extends TimerTask{

		@Override
		public void run() {
			String oldestKey=null;
			long oldestTime=99999999999999L;
			if(map.isEmpty()){
				System.out.println("Gabage Collection Process, cache is clear");
			}
			else{
			for(String key: map.keySet()){
				if(map.get(key).getTimeStamp()<oldestTime){
					
					oldestKey = key;
					oldestTime=map.get(key).getTimeStamp();
				}
			}
			map.remove(oldestKey);
			System.out.println("Gabage Collection Process, now Key size is: "+map.size()+ "    remove the oldest Key:"+oldestKey );
			}
		}
	}
} 

	