package vsue.rpc;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.sun.org.apache.bcel.internal.generic.NEW;

import vsue.communication.VSObjectConnection;
import vsue.communication.VSTestMessage;
import vsue.rmi.VSBoard;
import vsue.rmi.VSBoardImpl;
import vsue.rmi.VSBoardMessage;


public class VSClient {
	public static void main(String args[]) throws UnknownHostException, IOException, ClassNotFoundException {
		   Socket socket=new Socket("localhost",7777);
		    VSObjectConnection  connection =  new  VSObjectConnection(socket);
		    Object[] a=new Object[1];
		    VSTestMessage message = new VSTestMessage(0,"creat object",a);
			connection.sendObject(message);
			socket.close();
			
			socket=new Socket("localhost",7777);
			 connection =  new  VSObjectConnection(socket);
		    String[] msg = new String[3];
			msg[0] = "42";
			msg[1] = "Hallo Board";
			msg[2] = "Dies ist eine lange Nachricht!";
			VSBoardMessage VM = new VSBoardMessage(Integer.parseInt(msg[0]),msg[1],msg[2]);
			a[0]=VM;
			VSTestMessage message1 = new VSTestMessage(0,"post",a);
			//VSTestMessage message1 = new VSTestMessage(0,"listen",null);
			connection.sendObject(message1);
			connection.receiveObject();
			socket.close();
			
			socket=new Socket("localhost",7777);
			 connection =  new  VSObjectConnection(socket);
			msg[0] = "4222";
			msg[1] = "Hallo Board2222";
			msg[2] = "Dies ist eine lange Nachricht!222222";
           VM = new VSBoardMessage(Integer.parseInt(msg[0]),msg[1],msg[2]);
			a[0]=VM;
           message1 = new VSTestMessage(0,"post",a);
			//VSTestMessage message1 = new VSTestMessage(0,"listen",null);
			connection.sendObject(message1);
			connection.receiveObject();
			socket.close();
			
			socket=new Socket("localhost",7777);
			 connection =  new  VSObjectConnection(socket);
			a[0]= new Integer(3);
			VSTestMessage message2 = new VSTestMessage(0,"get",a);
			connection.sendObject(message2);
			 VSBoardMessage[] messages = ( VSBoardMessage[]) connection.receiveObject();
			System.out.println(messages.length);
			for(int i=0;i<messages.length;i++ ){
				 System.out.println("client recive"+"  uid:"+messages[i].getUid()+"  title:"+messages[i].getTitle()+" message:"+messages[i].getMessage());
			socket.close();
		   }
			//VSBoard bbs = (VSBoardImpl)connection.receiveObject();
			//bbs.post(VM);
			
   }
}
