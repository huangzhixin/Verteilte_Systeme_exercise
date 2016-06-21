package vsue;

public class property {
	public static enum RPC_SEMANTICS_ENUM { LAST_OF_MANY, AT_MOST_ONCE, MAYBE };
	public static RPC_SEMANTICS_ENUM RPC_SEMANTICS = RPC_SEMANTICS_ENUM.LAST_OF_MANY;	
	public static int serverport = 7601; //打开client的时候修改port
	public static int serverport1 = 7605;
	public static int serverport2 = 7606;
	public static String  host1="localhost"; 
	public static String  host2="localhost"; 
	public static String  host3="localhost"; 
	
	public static int MaxCallCount = 5;
	public static int waitingTime = 400;
	public static int clearGabageFrequence= 10000;
	public static String GROUP_NAME = "gruppe10";
}