package vsue.communication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class VSObjectConnection extends VSConnection{


	public VSObjectConnection(Socket socket) {
		super(socket);
		// TODO Auto-generated constructor stub
	}
	public void sendObject(Serializable object) throws IOException {
		ByteArrayOutputStream BAoutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = null;
	  objectOutputStream = new ObjectOutputStream (BAoutputStream);
		 objectOutputStream.writeObject(object);
		
		byte[] byteOfObject = BAoutputStream.toByteArray();
		//System.out.println("die Größe der serialisierten Daten ist    " + byteOfObject.length);
	   sendChunk(byteOfObject);
	}
	public Serializable  receiveObject() throws ClassNotFoundException{
		byte[] byteOfObject = receiveChunk();
		ByteArrayInputStream BAinputStream = new ByteArrayInputStream(byteOfObject);
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream (BAinputStream);
			Serializable object = (Serializable) objectInputStream.readObject();
			//System.err.println(object);
			return object;
		} catch (IOException e) {

				System.err.println("object connection error");

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}

}
