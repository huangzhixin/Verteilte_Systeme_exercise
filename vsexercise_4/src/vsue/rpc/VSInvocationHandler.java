package vsue.rpc;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;

import vsue.communication.VSObjectConnection;
import vsue.communication.VSTestMessage;
import vsue.faults.VSAbstractRPCSemantics;
import vsue.faults.VSBuggyObjectConnection;
import vsue.faults.VSMaybeSemantics;
import vsue.replica.VSRemoteGroupReference;

public class VSInvocationHandler implements InvocationHandler , Serializable {
	protected VSRemoteGroupReference remoteGroupReference;
	
	//这个方法是相当于为客户端负责的客服，客户端发送了调用消息remoteReference，该类自动调用invoke方法
	public VSInvocationHandler(VSRemoteGroupReference remoteGroupReference) throws UnknownHostException, IOException {
		// TODO Auto-generated constructor stub
		this.remoteGroupReference = remoteGroupReference;
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		/*
		// TODO Auto-generated method stub
		System.out.println(method.getName());
		Socket socket=new Socket(this.remoteReference.getHost(),this. remoteReference.getPort());
		VSBuggyObjectConnection  connection =  new  VSBuggyObjectConnection(socket);
		//VSObjectConnection  connection =  new  VSObjectConnection(socket);
	    VSTestMessage message = new VSTestMessage(this.remoteReference.getObjectID(), method.getName(), args);
	   System.out.println(this.remoteReference.getObjectID()+"      "+ method.getName());
	    connection.sendObject(message);
	    return connection.receiveObject();
	    */
		VSMaybeSemantics rpcSemantics = new VSMaybeSemantics(remoteGroupReference);
		return rpcSemantics.invoke(proxy, method, args);
	}

}
