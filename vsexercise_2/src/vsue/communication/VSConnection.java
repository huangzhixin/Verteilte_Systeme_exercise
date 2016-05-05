package vsue.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class VSConnection {
	 Socket socket = null;
      public VSConnection(Socket socket){
    	   this.socket = socket;
      }
      public void sendChunk(byte[] chunk){
    	  try {
    		  OutputStream outputStream = socket.getOutputStream();
    		 int length = chunk.length;
    		 byte[] bytesOfLength = ByteBuffer.allocate(4).putInt(length).array();
    		 //http://stackoverflow.com/questions/2183240/java-integer-to-byte-array
    		 outputStream.write( bytesOfLength);
    		 outputStream.flush();
    		
    		 outputStream.write(chunk);
    		 outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
      public byte[] receiveChunk(){
    	  byte[] chunk =null;
    	  try {
			InputStream inputstream = socket.getInputStream();
			byte[] byteOfLength = new byte[4]; 
			inputstream.read(byteOfLength);
			int length =  ByteBuffer.wrap(byteOfLength).getInt(); // big-endian by default
			 //http://stackoverflow.com/questions/7619058/convert-a-byte-array-to-integer-in-java-and-vise-versa
			 chunk = new byte[length];
			 inputstream.read(chunk); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 
    	  return chunk;
      }
}
