package vsue.rpc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import vsue.rmi.VSBoard;
import vsue.rmi.VSBoardImpl;
import vsue.rpc.VSRemoteObjectManager;

public class VSBoardServer {
	    public static void main(String[] args) throws UnknownHostException, IOException, AlreadyBoundException {
	    	VSBoard boardImpl = new VSBoardImpl();
	    	//System.setProperty("java.rmi.server.hostname", "192.168.0.112"); //*************
	    	@SuppressWarnings("deprecation")
			VSBoard board = (VSBoard) VSRemoteObjectManager.getInstance().exportObject(boardImpl);
	    	//当新建这个manager类后会开启服务器线程监听每一个客户来访
	    	//System.out.println(board.toString());
	    	
	    	Registry registry = LocateRegistry.createRegistry(7779);
			registry.bind("board", board);

	    	
	    	//System.out.println("afdafa");
	    }
}
