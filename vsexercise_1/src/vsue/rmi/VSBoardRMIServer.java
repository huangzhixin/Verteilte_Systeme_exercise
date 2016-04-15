package vsue.rmi;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class VSBoardRMIServer {
	    public static void main(String[] args) throws RemoteException, AlreadyBoundException, MalformedURLException {
	    	VSBoard boardImpl = new VSBoardImpl();
	    	
	    	VSBoard board = (VSBoard) UnicastRemoteObject.exportObject(boardImpl,0);
	    	Registry registry = LocateRegistry.createRegistry(7777);
	    	registry.bind("board", board);
	    	System.out.println(" server success!!!!!");
	   
	    }
}
