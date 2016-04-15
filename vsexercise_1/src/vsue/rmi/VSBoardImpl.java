package vsue.rmi;

import java.rmi.RemoteException;
import java.util.*;



public class VSBoardImpl implements VSBoard {
     private List<VSBoardMessage> listOfMessage = new ArrayList<VSBoardMessage>();
     private Set<VSBoardListener> setOfListener = new HashSet<VSBoardListener>();
	 public void post(VSBoardMessage message) throws RemoteException {
		 // TODO Auto-generated method stub
		 listOfMessage.add(message);
		 for(VSBoardListener listener : setOfListener ){
			 listener.newMessage(message);
	 	 }
 	 }

  public VSBoardMessage[] get(int n) throws IllegalArgumentException,RemoteException {
		// TODO Auto-generated method stub
	    int size = listOfMessage.size();
		if (n<0){
			System.out.println("Input ist nicht gütig, n musst größer als 0");
		}
		VSBoardMessage[] message = new VSBoardMessage[n];
       if(n>size){
			n=size;
		}
       
	   for(int i=0;i<n;i++ ){
			message[i]=listOfMessage.get(size-i-1);
		    //System.out.print( listOfMessage.get(i));
	   }
		return message;
	}

	public void listen(VSBoardListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		 setOfListener.add(listener);
	}
     
}
