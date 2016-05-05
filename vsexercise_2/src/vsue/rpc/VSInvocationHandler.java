package vsue.rpc;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;

import vsue.communication.VSObjectConnection;
import vsue.communication.VSTestMessage;

public class VSInvocationHandler implements InvocationHandler , Serializable {
	VSRemoteReference remoteReference;
	public VSInvocationHandler(VSRemoteReference remoteReference) throws UnknownHostException, IOException {
		// TODO Auto-generated constructor stub
		this.remoteReference = remoteReference;
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// TODO Auto-generated method stub
		System.out.println(method.getName());
		/*if(args != null)
		{
			System.out.println("not kong");
		}
		else
		{
			System.out.println("kong");
		}*/
		Socket socket=new Socket(this.remoteReference.getHost(),this. remoteReference.getPort());
	    VSObjectConnection  connection =  new  VSObjectConnection(socket);
	    VSTestMessage message = new VSTestMessage(this.remoteReference.getObjectID(), method.getName(), args);
	    connection.sendObject(message);
	    return connection.receiveObject();
	}

}
