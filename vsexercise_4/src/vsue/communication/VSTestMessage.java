package vsue.communication;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class VSTestMessage implements Serializable {
       private int integer;
       private String string;
       private Object[] objects;
       private long majorID = System.currentTimeMillis();
       private int minorID = 0;
       private String client;
       
       
       
       
       
       public VSTestMessage(int integer,String string,Object[] objects){
    	     this.integer=integer;
    	     this.string = string;
    	     this.objects = objects;
    	     this.client = ManagementFactory.getRuntimeMXBean().getName();
       }
       
       
       
       public int getInteger(){
    	   return integer;
       }
	   public String getString(){
		   return string;
	   }
		public Object[] getObjects(){
			return objects;
		}
		
		
		public long getMajorID(){
			return majorID;
		}
		
		
		public int getMinorID(){
			return minorID;
		}
		
		public void increaseMinorID()
		{
			this.minorID += 1;
		}
		
		public String getClient()
		{
			return this.client;
		}
}
