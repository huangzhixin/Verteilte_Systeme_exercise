package vsue.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import vsue.rmi.VSBoardListener;
import vsue.rpc.VSRemoteObjectManager;

public class VSBoardRMIClient implements VSBoardListener, Serializable {
	
	
	private  VSBoard vsBoard = null;
	
	public void post(String[] msg){
		if (this.vsBoard == null) {
		      System.err.println("nicht verbunden");
		      return;
		    }
       //System.out.println(msg[0]);
		try {
			VSBoardMessage VM = new VSBoardMessage(Integer.parseInt(msg[0]),msg[1],msg[2]);
			this.vsBoard.post(VM);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void get(int n){
		
		if (this.vsBoard == null) {
		      System.err.println("nicht verbunden");
		      return;
		    }
		
		VSBoardMessage[] VSMs;
		try {
			VSMs = vsBoard.get(n);
			for(VSBoardMessage m : VSMs){
				System.out.println("uid:"+m.getUid()+"title:"+m.getTitle()+"message"+m.getMessage());
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void listen() throws UnknownHostException, IOException{
		try {
			VSBoardListener vsbl = new VSBoardRMIClient();
			//VSBoardListener lister = (VSBoardListener) VSRemoteObjectManager.getInstance().exportObject(vsbl);
			
			VSBoardListener lister = (VSBoardListener) UnicastRemoteObject.exportObject(vsbl,7778);
			vsBoard.listen(lister);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void registry() throws MalformedURLException{
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry("192.168.0.105",7777);
			//registry = LocateRegistry.getRegistry("127.0.0.1",7777);
			
			vsBoard = (VSBoard) registry.lookup("board");
			System.out.println("sdfsdfsfd");
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	@Override
	public void newMessage(VSBoardMessage message) throws RemoteException {
		System.out.println("New Message!\n"+"uid:"+message.getUid()+"title:"+message.getTitle()+"message:"+message.getMessage());
		
	}
	
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		System.setProperty("java.rmi.server.hostname", "192.168.0.112"); //*************
		System.out.println("client success!!!!!");
		
		VSBoardRMIClient VSClient = new VSBoardRMIClient();
		
		
		VSClient.registry();
		//System.out.println("registry success!!!!!");
		
		
		
		
		
		String[] msg = new String[3];
		msg[0] = "42";
		msg[1] = "Hallo Board";
		msg[2] = "Dies ist eine lange Nachricht!";
		VSClient.post(msg);
		
		
		VSClient.listen();
		
		msg[0] = "47";
		msg[1] = "Das ist det Title";
		msg[2] = "Das ist die Botschaft";
		VSClient.post(msg);
		/*
		//VSClient.listen();
		
		msg[0] = "1000";
		msg[1] = "Das ist det Title";
		msg[2] = "Das ist die Botschaft";
		VSClient.post(msg);
		
		msg[0] = "999";
		msg[1] = "Das ist det Title";
		msg[2] = "Das ist die Botschaft";
		VSClient.post(msg);
		VSClient.get(7);*/
		
		
	}
	
	
}
