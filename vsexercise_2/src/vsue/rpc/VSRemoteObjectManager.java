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
	private String host = "localhost";
	private int port = 7777;
	
    public static VSRemoteObjectManager getInstance(){
    	if(manager == null) {
			manager = new VSRemoteObjectManager();
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
    	Class<?>[] classes = new Class<?>[args.length];   // Class<?> getClass()返回此 Object 的运行时类
    	//?代表〔直接继承Object或者间接继承〕的正在运行的类。
    	for (int i=0; i<args.length;i++){
    		classes[i] = args.getClass(); 
    		if (classes[i].equals(Integer.class)){
    			classes[i] = int.class;
    		}
    	}
    	if(genericMethodName.equalsIgnoreCase("listen"))    //这个方法的参数比较特殊，不是普通的那些类，所以特别提出来
    			classes[0] = VSBoardListener.class;
    	if(genericMethodName.equalsIgnoreCase("post"))    //这个方法的参数比较特殊，不是普通的那些类，所以特别提出来
			   classes[0] = VSBoardMessage.class;
    	if(genericMethodName.equalsIgnoreCase("get"))    //这个方法的参数比较特殊，不是普通的那些类，所以特别提出来
			   classes[0] = int.class;
    	try {
    		
    		//System.out.println(cl.getMethods()[0].getName());
    		//System.out.println(classes.length);
			Method m = cl.getMethod(genericMethodName, classes);  //通过类获得该类的方法，参数为方法名+参数类型
			System.out.println("++++++++++++++++++++++");
			return m.invoke(objects.get(objectID), args);                         //通过objectID在objects里找到存好的对象，外加参数就可以返回调用参数的结果了
			//应为参数的类型和返回结果的类型因客户调用不同的函数而异；
			//所以要使用动态反射机制 ！！！！！！！
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
