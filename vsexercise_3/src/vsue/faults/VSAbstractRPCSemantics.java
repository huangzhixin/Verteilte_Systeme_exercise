
package vsue.faults;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import vsue.communication.VSConnection;
import vsue.communication.VSTestMessage;
import vsue.communication.VSObjectConnection;
import vsue.rpc.VSRemoteReference;
import vsue.rpc.VSRemoteObjectManager;

public abstract class VSAbstractRPCSemantics {
	private VSRemoteReference Reference;

	public VSAbstractRPCSemantics(VSRemoteReference Reference) {
		this.setRemoteReference(Reference);
	}

	public abstract Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable;

	protected VSObjectConnection createConnection() throws UnknownHostException, IOException {
		Socket socket = null;
		VSBuggyObjectConnection connection = null;
		socket = new Socket(getRemoteReference().getHost(),getRemoteReference().getPort());
		// connection = new VSObjectConnection(socket);
		 connection = new VSBuggyObjectConnection(socket);
		return connection;
	}

	protected Object sendAndReceive(VSTestMessage clone) throws Throwable {
		VSObjectConnection connection = null;
		Object r = null;
		
		connection = createConnection();
		
		// all replicas were not available
		if (connection == null)
		{
			throw new RemoteException("connection is unavailable!");
		}
		
		//System.out.println("Sending: " + clone);
		connection.sendObject(clone);
		r = connection.receiveObject();
		//System.out.println("recive: " + r);
		//connection.close();
		//System.out.println("connection closed");
		return r;
	}

	protected void setRemoteReference(VSRemoteReference newReference) {
		this.Reference = newReference;
	}

	protected VSRemoteReference getRemoteReference() {
		return Reference;
	}

	// VSMessage erzeugen
	protected VSTestMessage generateMessage(Method method, Object[] args)
			throws IOException {

		exportRemoteArgs(args);

		// better safe than sorry
		return new VSTestMessage(
				getRemoteReference().getObjectID(),
				method.toGenericString(), args);
	}
 
	// exportierbare objekte durch deren exportiertes objekt ersetzen
	protected void exportRemoteArgs(Object[] args) throws UnknownHostException, IOException {
		for (int i = 0; i < args.length; ++i) {
			if (Remote.class.isAssignableFrom(args[i].getClass())) {
				if (!VSRemoteObjectManager.getInstance().isStubExported((Remote) args[i])) {
					args[i] = VSRemoteObjectManager.getInstance().exportObject((Remote) args[i]);
				}
			}
		}

	}

}