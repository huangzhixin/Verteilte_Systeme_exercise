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
	public void sendObject(Serializable object) throws IOException{
		ByteArrayOutputStream BAoutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream (BAoutputStream);
		objectOutputStream.writeObject(object);
		byte[] byteOfObject = BAoutputStream.toByteArray();
		//System.out.println("die Größe der serialisierten Daten ist    " + byteOfObject.length);
		sendChunk(byteOfObject);
	}
	public Serializable  receiveObject() throws IOException, ClassNotFoundException{
		byte[] byteOfObject = receiveChunk();
		ByteArrayInputStream BAinputStream = new ByteArrayInputStream(byteOfObject);
		ObjectInputStream objectInputStream = new ObjectInputStream (BAinputStream);
		Serializable object = (Serializable) objectInputStream.readObject();
		
		return object;
	}

}
