package vsue.communication;

import java.io.Serializable;

public class VSReplyMessage implements Serializable{
	 private Object object;
     private long majorID;
     private int minorID;
     private long timeStamp = System.currentTimeMillis();
     
     public VSReplyMessage(Object object, long majorID, int minorID)
     {
    	 this.object = object;
    	 this.majorID = majorID;
    	 this.minorID = minorID;
     }
     
     public Object getObject()
     {
    	 setTimeStamp();
    	 return this.object;
     }
     
     public long getMajorID()
     {   
    	 setTimeStamp();
    	 return this.majorID;
     }
     
     public int getMinorID()
     {
    	 setTimeStamp();
    	 return this.minorID;
     }
     public void setTimeStamp(){
    	 this.timeStamp = System.currentTimeMillis();
     }
     public long getTimeStamp(){
    	 return timeStamp;
     }
}
