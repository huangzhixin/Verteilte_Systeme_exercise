package vsue.rmi;

import java.io.Serializable;

public class VSBoardMessage implements Serializable,Cloneable{
	  private static final long serialVersionUID = -1858518369668584532L; 
      private int uid;
      private String title;
      private String message;
      public  VSBoardMessage(int  uid, String title, String message){
    	   this.uid = uid;
    	   this.title = title;
    	   this.message = message;
      }
      
      public  int getUid(){
    	  return this.uid;
      }
      public String getTitle(){
    	  return this.title;
      }
      public String getMessage(){
    	  return this.message;
      }
}
