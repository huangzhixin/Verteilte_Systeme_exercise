package vsue.rpc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.util.ArrayList;

import vsue.rmi.VSBoardListener;
import vsue.rmi.VSBoardMessage;

public class VSRemoteObjectManager {
	private static VSRemoteObjectManager manager;
	ArrayList<Remote> objects = new ArrayList<Remote>();
	//private String host = "localhost";
	private String host = "192.168.0.105";
	private int port = 7600;
	private VSServer server;
	
	public VSRemoteObjectManager() throws IOException{
		this.server = VSServer.getInstance();
		server.port= this.port;
		Thread thread = new Thread(server);
	    thread.start();
	}
	
    public static VSRemoteObjectManager getInstance() throws IOException{
    	if(manager == null) {
			manager = new VSRemoteObjectManager();
			System.out.println("manager sucessful!");
			System.out.println(manager.host);
		}
		return manager;
    }
    public Remote exportObject(Remote object) throws UnknownHostException, IOException{
    	int ind = objects.indexOf(object);
    	System.out.println("new exportObject !!!!!!!!!!!!!!!!!!! ");
		if(ind != -1)
			return objects.get(ind);
		objects.add(object);
		//System.out.println(objects.size());
		int objectID = objects.size() - 1;
    	VSRemoteReference reference = new VSRemoteReference(host, port, objectID);
		VSInvocationHandler handler = new VSInvocationHandler(reference);
		ClassLoader ldr = object.getClass().getClassLoader();
		Class<?>[] interfaces = object.getClass().getInterfaces();
		Remote  proxy = (Remote) Proxy.newProxyInstance(ldr, interfaces, handler);
		
		return proxy;
    }
    public Object invokeMethod (int objectID, String genericMethodName, Object[] args){
    	Class<?> cl = objects.get(objectID).getClass();
    	//System.out.println(args.length);
    	
    	Class<?>[] classes = null;
    	
    	/*if(args == null)
    	{
    		System.out.println("kong kong kong");
    	}
    	else
    	{
    		System.out.println("not kong kong kong");
    	}*/
	    classes = new Class<?>[args.length];   // Class<?> getClass()杩斿洖姝� Object 鐨勮繍琛屾椂绫�
	    /*	
    	for (int i=0; i<args.length;i++){
    		classes[i] = args.getClass(); 
    		if (classes[i].equals(Integer.class)){
    			classes[i] = int.class;
    		}
    	}
    	*/
    	
    	if(genericMethodName.equalsIgnoreCase("listen"))
    			classes[0] = VSBoardListener.class;
    	if(genericMethodName.equalsIgnoreCase("post"))    //杩欎釜鏂规硶鐨勫弬鏁版瘮杈冪壒娈婏紝涓嶆槸鏅�氱殑閭ｄ簺绫伙紝鎵�浠ョ壒鍒彁鍑烘潵
			   classes[0] = VSBoardMessage.class;
    	if(genericMethodName.equalsIgnoreCase("get"))    //杩欎釜鏂规硶鐨勫弬鏁版瘮杈冪壒娈婏紝涓嶆槸鏅�氱殑閭ｄ簺绫伙紝鎵�浠ョ壒鍒彁鍑烘潵
			   classes[0] = int.class;
    	if(genericMethodName.equalsIgnoreCase("newMessage"))
    			classes[0] = VSBoardMessage.class;
    	try {
    		
    		//System.out.println(cl.getMethods()[0].getName());
    		//System.out.println(classes.length);
			Method m = cl.getMethod(genericMethodName, classes);  //閫氳繃绫昏幏寰楄绫荤殑鏂规硶锛屽弬鏁颁负鏂规硶鍚�+鍙傛暟绫诲瀷
			System.out.println("++++++++++++++++++++++");
			System.out.println("args length:"+args.length);
			return m.invoke(objects.get(objectID), args);                         //閫氳繃objectID鍦╫bjects閲屾壘鍒板瓨濂界殑瀵硅薄锛屽鍔犲弬鏁板氨鍙互杩斿洖璋冪敤鍙傛暟鐨勭粨鏋滀簡
			//搴斾负鍙傛暟鐨勭被鍨嬪拰杩斿洖缁撴灉鐨勭被鍨嬪洜瀹㈡埛璋冪敤涓嶅悓鐨勫嚱鏁拌�屽紓锛�
			//鎵�浠ヨ浣跨敤鍔ㄦ�佸弽灏勬満鍒� 锛侊紒锛侊紒锛侊紒锛�
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
}
