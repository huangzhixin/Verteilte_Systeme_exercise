package vsue.faults;

import java.lang.reflect.Method;
import vsue.communication.VSTestMessage;

import vsue.replica.VSRemoteGroupReference;
import vsue.rpc.VSRemoteReference;

public class VSMaybeSemantics extends VSAbstractRPCSemantics {

	public VSMaybeSemantics(VSRemoteGroupReference Reference) {
		super(Reference);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		VSTestMessage message = null;
		
		System.out.println("Maybe semantics");

		message = generateMessage(method, args);
		//System.out.println("message is "+message);
		return  sendAndReceive(message);
	}
}