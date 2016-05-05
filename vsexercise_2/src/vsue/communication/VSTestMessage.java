package vsue.communication;

import java.io.Serializable;

public class VSTestMessage implements Serializable {
       private int integer;
       private String string;
       private Object[] objects;
       public VSTestMessage( int integer,String string,Object[] objects){
    	     this.integer=integer;
    	     this.string = string;
    	     this.objects =objects;
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
}
