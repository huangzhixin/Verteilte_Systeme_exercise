package vsue.rmi;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import vsue.replica.VSRemoteObjectStateHandler;



public class VSBoardImpl  implements VSBoard,Serializable, VSRemoteObjectStateHandler {
     public VSBoardImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	private List<VSBoardMessage> listOfMessage = new ArrayList<VSBoardMessage>();
     private List<VSBoardListener> setOfListener = new ArrayList<VSBoardListener>();
     //private List<VSBoardListener> setOfListener= Collections.synchronizedList(new LinkedList<VSBoardListener>());
     
	 public void post(VSBoardMessage message) throws RemoteException {
		 // TODO Auto-generated method stub
		 listOfMessage.add(message);
		 System.out.println("test"+"  uid:"+message.getUid()+"  title:"+message.getTitle()+" message:"+message.getMessage());
		 for(VSBoardListener listener : setOfListener ){
			 listener.newMessage(message);
	 	 }
 	 }

  public VSBoardMessage[] get(int n) throws IllegalArgumentException,RemoteException {
		// TODO Auto-generated method stub
	    int size = listOfMessage.size();
	    System.out.println(size);
		if (n<0){
			System.out.println("Input ist nicht gütig, n musst größer als 0!!!!!!!!!!!!!!!!!");
		}	
       if(n>size){	   
			n=size;
			//System.out.println(n);
		}
       VSBoardMessage[] message = new VSBoardMessage[n];
	   for(int i=0;i<n;i++ ){
			message[i]=listOfMessage.get(size-i-1);
		    //System.out.print( listOfMessage.get(i));
	   }
		return message;
	}

	public void listen(VSBoardListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("listen!!!");
		System.out.println(setOfListener.size() );
		if(listener != null)
		{
			System.out.println("listener not leer");
			//System.out.println(listener);
		}
		 setOfListener.add(listener);
		System.out.println(setOfListener.size() );
		// System.out.println(setOfListener.size() );
		 System.out.println("new user!");
	}

	@Override
	public void getState(OutputStream output) {
		// TODO Auto-generated method stub
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(output);
			oos.writeObject(listOfMessage);
			oos.writeObject( setOfListener);
			oos.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
	}

	@Override
	public void setState(InputStream input) {
		// TODO Auto-generated method stub
		ObjectInputStream ois;
		
		try {
			ois = new ObjectInputStream(input);
			listOfMessage = (List<VSBoardMessage>) ois.readObject();
			 setOfListener = (List<VSBoardListener>) ois.readObject();	
			 System.err.println("now size of list of message is  :   "+listOfMessage.size());
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
     
}
