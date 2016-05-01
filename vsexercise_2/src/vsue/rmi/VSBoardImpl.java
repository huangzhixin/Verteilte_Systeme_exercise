package vsue.rmi;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;



public class VSBoardImpl  implements VSBoard,Serializable {
     public VSBoardImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	private List<VSBoardMessage> listOfMessage = new ArrayList<VSBoardMessage>();
     private Set<VSBoardListener> setOfListener = new HashSet<VSBoardListener>();
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
		//System.out.println("+++++");
		 setOfListener.add(listener);
		// System.out.println(setOfListener.size() );
		 System.out.println("new user!");
	}
	
     
}
