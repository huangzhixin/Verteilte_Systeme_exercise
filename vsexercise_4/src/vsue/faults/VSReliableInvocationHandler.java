package vsue.faults;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;

import vsue.property;
import vsue.property.RPC_SEMANTICS_ENUM;
import vsue.replica.VSRemoteGroupReference;
import vsue.rpc.VSInvocationHandler;
import vsue.rpc.VSRemoteReference;

public class VSReliableInvocationHandler extends VSInvocationHandler{

	public VSReliableInvocationHandler(VSRemoteGroupReference remoteGroupReference) throws UnknownHostException, IOException {
		super(remoteGroupReference);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		
		if(property.RPC_SEMANTICS == RPC_SEMANTICS_ENUM.LAST_OF_MANY)
		{
			VSLomSemantics rpcSemantics = new VSLomSemantics(remoteGroupReference);
			return rpcSemantics.invoke(proxy, method, args);
		}
		else if(property.RPC_SEMANTICS == RPC_SEMANTICS_ENUM.AT_MOST_ONCE)
		{
			VSAmoSemantics rpcSemantics = new VSAmoSemantics(remoteGroupReference);
			return rpcSemantics.invoke(proxy, method, args);
		}
		return null;
		
		
	}

}
