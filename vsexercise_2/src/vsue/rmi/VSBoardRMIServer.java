package vsue.rmi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import vsue.rpc.VSRemoteObjectManager;

public class VSBoardRMIServer {
	    public static void main(String[] args) throws UnknownHostException, IOException, AlreadyBoundException {
	    	VSBoard boardImpl = new VSBoardImpl();
	    	//System.setProperty("java.rmi.server.hostname", "192.168.0.112"); //*************
	    	@SuppressWarnings("deprecation")
			VSBoard board = (VSBoard) VSRemoteObjectManager.getInstance().exportObject(boardImpl);
	    	//System.out.println(board.toString());
	    	
	    	Registry registry = LocateRegistry.createRegistry(7777);
			registry.bind("board", board);

	    	
	    	//System.out.println("afdafa");
	    }
}
