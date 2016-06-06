package vsue;

public class property {
	public static enum RPC_SEMANTICS_ENUM { LAST_OF_MANY, AT_MOST_ONCE, MAYBE };
	public static RPC_SEMANTICS_ENUM RPC_SEMANTICS = RPC_SEMANTICS_ENUM.LAST_OF_MANY;	
	public static int serverport = 7601;
	public static String  host="localhost"; 
	public static int MaxCallCount = 5;
	public static int waitingTime = 250;
	public static int clearGabageFrequence= 10000;
}