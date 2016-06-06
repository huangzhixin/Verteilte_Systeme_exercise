package vsue.communication;
import java.io.*;

import java.net.*;

import java.applet.Applet;

import vsue.property;
import vsue.faults.VSBuggyObjectConnection;

public class VSServer {
	
   public static void main(String args[]) {
   try{
      ServerSocket server=null;
      try{
        server=new ServerSocket(property.serverport);
       }catch(Exception e) {
          System.out.println("can not listen to:"+e);
       }
	  Socket socket=null;
      try{
         socket=server.accept();
        // VSObjectConnection  connection =  new  VSObjectConnection(socket);
         VSBuggyObjectConnection  connection =  new  VSBuggyObjectConnection(socket);
         Serializable object=connection.receiveObject();
         connection.sendObject(object);
      }catch(Exception e) {
          System.out.println("Error."+e);
      }
	socket.close(); 
	//server.close(); 
	}catch(Exception e){
	    System.out.println("Error:"+e);
    }
   }
}
