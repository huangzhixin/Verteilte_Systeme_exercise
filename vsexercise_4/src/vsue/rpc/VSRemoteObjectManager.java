package vsue.rpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import vsue.property;
import vsue.property.RPC_SEMANTICS_ENUM;
import vsue.faults.VSReliableInvocationHandler;
import vsue.replica.VSRemoteGroupReference;
import vsue.replica.VSRemoteObjectStateHandler;
import vsue.replica.VSReplicaServer;
import vsue.rmi.VSBoardListener;
import vsue.rmi.VSBoardMessage;

public class VSRemoteObjectManager {
	//private String host = InetAddress.getLocalHost().getHostAddress();
	private static VSReplicaServer server;
	private static VSRemoteObjectManager manager;
	private Map<Integer, Remote> exportedObjects;
	private Map<Integer, Remote> exportedObjectStubs;
	private int numberOfObjects;
	
	public VSRemoteObjectManager() throws IOException{
		numberOfObjects = 0;
		exportedObjects = new HashMap<Integer, Remote>();
		exportedObjectStubs = new HashMap<Integer, Remote>();	
	}
	
    public static VSRemoteObjectManager getInstance() throws IOException{
    	if (manager == null) {
			manager = new VSRemoteObjectManager();
			server =new VSReplicaServer();
			//server.copyState();
			System.out.println("Starting Server");
			(new Thread(server)).start();
		}
		return manager;
    }
    
    public boolean isStubExported(Remote remote) {
		return exportedObjectStubs.containsValue(remote);
	}
    
    public Remote exportObject(Remote object) throws UnknownHostException, IOException{
    	System.out.println("new exported!!!!");
    	Remote  proxy =null;
    	try {
			if (exportedObjects.containsValue(object)) {
				for (Integer i : exportedObjects.keySet()) {
					if (exportedObjects.get(i) == object)
						//System.out.println(i);
						return exportedObjectStubs.get(i);
				}
			}
			exportedObjects.put(numberOfObjects, object);
			
		//host 和 port在服务器端就定义好了，关键是objectID，
		VSRemoteReference[] references = new VSRemoteReference[3];
    	references[0] = new VSRemoteReference(property.host1, property.serverport, numberOfObjects);
    	references[1] = new VSRemoteReference(property.host2, property.serverport1, numberOfObjects);
    	references[2] = new VSRemoteReference(property.host3, property.serverport, numberOfObjects);
    	VSRemoteGroupReference groupReference = new VSRemoteGroupReference();
    	groupReference.setReferences(references);
    	VSInvocationHandler handler = null;
    	if(property.RPC_SEMANTICS == RPC_SEMANTICS_ENUM.MAYBE)
    	{
    		handler = new VSInvocationHandler(groupReference);
    	}
    	else
    	{
    		handler = new VSReliableInvocationHandler(groupReference);
    	}
		ClassLoader ldr = object.getClass().getInterfaces()[0].getClassLoader();
		Class<?>[] interfaces = object.getClass().getInterfaces();
		proxy = (Remote) Proxy.newProxyInstance(ldr, interfaces, handler);
		 
		//远程请求要调用的类的代理，每当调用这个代理就会执行下面的invokeMethod方法，然后返回执行某个方法的结果
		exportedObjectStubs.put(numberOfObjects, proxy);
		numberOfObjects+=1;
		
		server.copyState();
		
    	} catch (Exception e) {
			e.printStackTrace();
		}
		return proxy;
    }
    
    public Object invokeMethod (int objectID, String genericMethodName, Object[] args){
    	Remote object = null;
		Class<?> cl = null;
		/*
		System.err.println(exportedObjects.size());
		 for (Integer i : exportedObjects.keySet()) {
				if (exportedObjects.get(i) instanceof VSRemoteObjectStateHandler) {
					 System.err.println(i);
				}
		 }
		 从这一段我们可以看出每开启一个replic，都只有一个VSBoardImpl （它的父类是VSRemoteObjectStateHandler）
		 如果你开启两个replic，他们的exportobjects虽然会同步，但他们不是共享的，所以一个replic或者说一个exportsobjects里只有一个VSBoardmpl
		 */
		try {
			object = exportedObjects.get(objectID);
			//System.out.println("get object is  "+object);
			cl = object.getClass();
			do {
				for (Class<?> interfaces : cl.getInterfaces()) {
					if (!Remote.class.isAssignableFrom(interfaces))
						continue;
					for (Method m : interfaces.getMethods()) {
						if (m.toGenericString().equals(genericMethodName)) {
							try {
								return m.invoke(object, args);
							} catch (InvocationTargetException e) {
								return e.getTargetException();
							}
						}
					}
				}
			} while ((cl = cl.getSuperclass()) != null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
    public void getRemoteObjectStates(OutputStream output) {
		
		try {

			for (Integer i : exportedObjects.keySet()) {
				if (exportedObjects.get(i) instanceof VSRemoteObjectStateHandler) {
					System.err.println("getRemoteObjectStates");
					VSRemoteObjectStateHandler stateHandler = (VSRemoteObjectStateHandler)exportedObjects.get(i);
					stateHandler.getState(output);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setRemoteObjectStates(InputStream input) {
		
		try {
          for (Integer i : exportedObjects.keySet()) {
				if (exportedObjects.get(i) instanceof VSRemoteObjectStateHandler) {
					System.err.println("setRemoteObjectStates");
					VSRemoteObjectStateHandler stateHandler = (VSRemoteObjectStateHandler) exportedObjects.get(i);
							stateHandler.setState(input);
							
				} 
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
