package vsue.faults;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;

import vsue.property;
import vsue.property.RPC_SEMANTICS_ENUM;
import vsue.rpc.VSInvocationHandler;
import vsue.rpc.VSRemoteReference;

public class VSReliableInvocationHandler extends VSInvocationHandler{

	public VSReliableInvocationHandler(VSRemoteReference remoteReference) throws UnknownHostException, IOException {
		super(remoteReference);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		
		if(property.RPC_SEMANTICS == RPC_SEMANTICS_ENUM.LAST_OF_MANY)
		{
			VSLomSemantics rpcSemantics = new VSLomSemantics(remoteReference);
			return rpcSemantics.invoke(proxy, method, args);
		}
		else if(property.RPC_SEMANTICS == RPC_SEMANTICS_ENUM.AT_MOST_ONCE)
		{
			VSAmoSemantics rpcSemantics = new VSAmoSemantics(remoteReference);
			return rpcSemantics.invoke(proxy, method, args);
		}
		return null;
		
		
	}

}
