package vsue.communication;
import java.io.*;
import java.net.*;
import java.util.Arrays;
public class VSClient {
	
  public static void main(String args[]) {
  try{
      Socket socket=new Socket("127.0.0.1",4700);
      VSObjectConnection  connection =  new  VSObjectConnection(socket);
      //exercise 1.2.3
      Serializable object1 =  (Serializable)new Integer(0);
      connection.sendObject(object1);
      Serializable object2 = connection.receiveObject();
      Integer sb =  (Integer) object2;
      System.out.println(sb);
      if(object1.equals(object2)){
    	  System.out.println("Original-Object entspricht!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      }
      //exercise 1.2.4----------------1
      String[] ss=new String[2];
     ss[0]= "huangzhixin";
     ss[1]="baijuntao";  		  
      VSTestMessage testMessage11 = new VSTestMessage(0,"s",ss);
      VSTestMessage testMessage12 = new VSTestMessage(123456789,"asdfghjkl",ss);
      connection.sendObject( testMessage11);
      connection.sendObject( testMessage12);
      //exercise 1.2.4 ----------------2
       VSTestMessage testMessage2 = new VSTestMessage(0,null,null);
       connection.sendObject( testMessage2);
     //exercise 1.2.4 ----------------3
       VSTestMessage testMessage31 = new VSTestMessage(0,"asdfghjkl",ss);
       VSTestMessage testMessage32 = new VSTestMessage(123456789,null,ss);
       VSTestMessage testMessage33 = new VSTestMessage(123456789,"asdfghjkl",null);
       connection.sendObject( testMessage31);
       connection.sendObject( testMessage32);
       connection.sendObject( testMessage33);
     //exercise 1.2.4 ----------------4
       VSTestMessage testMessage4 = new VSTestMessage(123456789,null,null);
       connection.sendObject( testMessage4);
       //exercise 1.2.4 ----------------5
       VSTestMessage testMessage5 = new VSTestMessage(0,"a",null);
       connection.sendObject( testMessage4);
     //exercise 1.2.4 ----------------6
       VSTestMessage testMessage6 = new VSTestMessage(0,null,ss);
       connection.sendObject( testMessage4);
      socket.close();
  }catch(Exception e) {
      System.out.println("Error"+e); 
    }
  }
}

