package vsue.replica;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jgroups.ReceiverAdapter;
import org.jgroups.Message;


import vsue.property;
import vsue.communication.VSReplyMessage;
import vsue.communication.VSTestMessage;
import vsue.rpc.VSServerWorkerThread;
import vsue.rpc.VSRemoteObjectManager;

public class VSReplicaReceiver extends ReceiverAdapter {
	private HashMap<String, VSReplyMessage> map = new HashMap();
	private Map<String, VSWorkerThread> worker;

	public VSReplicaReceiver() {
		worker = new HashMap<String, VSWorkerThread>();
	}

	public void put(String id, VSWorkerThread w) {
		synchronized (worker) {
			worker.put(id, w);
		}
	}

	private VSReplyMessage lomReply(VSTestMessage message) throws IOException{
		VSReplyMessage reMessage = null;
		
		if(message==null){
			System.out.println("message is null");
		}
		int objectID = message.getInteger();
		String genericMethodName = message.getString();
		Object[] argss = message.getObjects();
		//System.out.println("server Cache first:" + message.getMajorID() +"  " + message.getMinorID()+"  "+ message.getClient());
		if(map.containsKey(message.getClient())){
				    if(map.get(message.getClient()).getMajorID() == message.getMajorID())
					{
						if(map.get(message.getClient()).getMinorID() < message.getMinorID())
						{
							System.out.println("reMessageCache.getMinorID() < message.getMinorID()" );
							VSRemoteObjectManager manager = VSRemoteObjectManager.getInstance();
							Serializable retVal = null;
							retVal = (Serializable) manager.invokeMethod(objectID, genericMethodName,argss);
							reMessage = new VSReplyMessage(retVal,message.getMajorID(),message.getMinorID());
							map.put(message.getClient(), reMessage);
							//reMessageCache = reMessage;
							
						}
						else if(map.get(message.getClient()).getMinorID() >= message.getMinorID())
						{
							System.out.println("reMessageCache.getMinorID()："+map.get(message.getClient()).getMinorID()+" >= message.getMinorID(): "+message.getMinorID());
							//reMessage = new VSReplyMessage(null,0,0);
							reMessage = null;
						}
					}
					else if(map.get(message.getClient()).getMajorID() != message.getMajorID())
					{
						System.out.println("reMessageCache.getMajorID() != message.getMajorID()" );
						VSRemoteObjectManager manager = VSRemoteObjectManager.getInstance();
						Serializable retVal = null;
						retVal = (Serializable) manager.invokeMethod(objectID, genericMethodName,argss);
						reMessage = new VSReplyMessage(retVal,message.getMajorID(),message.getMinorID());
						map.put(message.getClient(), reMessage);
						//reMessageCache = reMessage;
					}
		}
		else{
			VSRemoteObjectManager manager = VSRemoteObjectManager.getInstance();
			Serializable retVal = null;
			retVal = (Serializable) manager.invokeMethod(objectID, genericMethodName,argss);
			reMessage = new VSReplyMessage(retVal,message.getMajorID(),message.getMinorID());
			map.put(message.getClient(), reMessage);
		}
		//System.out.println("server Cache second:" + reMessageCache.getMajorID() +" " + reMessageCache.getMinorID());
		//System.out.println("server :" + reMessage.getMajorID() +" " + reMessage.getMinorID());
		return reMessage;
	}
	
	private VSReplyMessage amoReply(VSTestMessage message) throws IOException{
		VSReplyMessage reMessage = null;
		
		if(message==null){
			System.out.println("message is null");
		}
		int objectID = message.getInteger();
		String genericMethodName = message.getString();
		Object[] argss = message.getObjects();
		
		if(map.get(message.getClient()).getMajorID() != message.getMajorID())
		{
			System.out.println("reMessageCache.getMajorID() != message.getMajorID()" );
			VSRemoteObjectManager manager = VSRemoteObjectManager.getInstance();
			Serializable retVal = null;
			retVal = (Serializable) manager.invokeMethod(objectID, genericMethodName,argss);
			reMessage = new VSReplyMessage(retVal,message.getMajorID(),message.getMinorID());
			map.put(message.getClient(), reMessage);
			//reMessageCache = reMessage;
		}
		else if(map.get(message.getClient()).getMajorID() == message.getMajorID())
		{
			System.out.println("reMessageCache.getMajorID() == message.getMajorID()" );
			if(map.get(message.getClient()).getMinorID() == message.getMinorID())
			{
				System.out.println("reMessageCache.getMinorID() == message.getMinorID()");
				reMessage = null;
			}
			reMessage = map.get(message.getClient());
			//reMessage = reMessageCache;
		}
		System.out.println("server :" + reMessage.getMajorID() +" " + reMessage.getMinorID());
		return reMessage;
	}
	
	private VSReplyMessage maybeReply(VSTestMessage message) throws IOException{
		if(message==null){
			System.out.println("message is null");
		}
		int objectID = message.getInteger();
		String genericMethodName = message.getString();
		Object[] argss = message.getObjects();
		VSRemoteObjectManager manager = VSRemoteObjectManager.getInstance();
		Serializable retVal = null;
		retVal = (Serializable) manager.invokeMethod(objectID, genericMethodName,argss);
		VSReplyMessage reMessage = new VSReplyMessage(retVal,message.getMajorID(),message.getMinorID());
		System.out.println("server :" + reMessage.getMajorID() +" " + reMessage.getMinorID());
		return reMessage;
	}


	@Override
	public void getState(OutputStream output) {
		try {
			VSRemoteObjectManager.getInstance().getRemoteObjectStates(output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setState(InputStream input) {
		try {
			VSRemoteObjectManager.getInstance().setRemoteObjectStates(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// TODO: Hier die Erkennung rein, wenn neuer Teilnehmer in die Gruppe kommt
	public void receive(Message msg) {
		System.out.println("ReplicaReciver:  I have received a message!!");
		VSTestMessage message = (VSTestMessage) msg.getObject();
		Object returnValue = null;
		
		if (property.RPC_SEMANTICS == property.RPC_SEMANTICS_ENUM.LAST_OF_MANY){
			try {
				returnValue = lomReply(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (property.RPC_SEMANTICS ==property.RPC_SEMANTICS_ENUM.AT_MOST_ONCE) {
			try {
				returnValue =amoReply(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(property.RPC_SEMANTICS == property.RPC_SEMANTICS_ENUM.MAYBE){
			try {
				returnValue = maybeReply(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		synchronized (worker) {
			String key = "" + message.getMajorID()+ message.getMinorID();

			if (worker.containsKey(key)) {
				worker.get(key).setReturnMessage(returnValue);
			}
		}
	}
}