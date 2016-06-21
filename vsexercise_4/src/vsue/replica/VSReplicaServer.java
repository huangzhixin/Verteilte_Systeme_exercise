package vsue.replica;

import org.jgroups.JChannel;
import org.jgroups.protocols.SEQUENCER;
import org.jgroups.stack.ProtocolStack;

import vsue.property;
import vsue.communication.VSObjectConnection;
import vsue.communication.VSServer;
import vsue.communication.VSTestMessage;

public class VSReplicaServer extends VSServer{
	private JChannel channel;

	private VSReplicaReceiver receiver;

	public VSReplicaServer() {
		super();
			try {
				channel = new JChannel();
				ProtocolStack pStack = channel.getProtocolStack();
				
				pStack.addProtocol(new SEQUENCER());
			
				channel.connect(property.GROUP_NAME);
				receiver = new VSReplicaReceiver();
				channel.setReceiver(receiver);
				System.out.println(channel.getViewAsString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
	}
	public void copyState(){
		try {
			channel.getState(null,0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void startWork(VSObjectConnection vsoCon) throws Exception {
		VSTestMessage message = (VSTestMessage) vsoCon.receiveObject();
		VSWorkerThread w = new VSWorkerThread(vsoCon, channel,message);
		receiver.put("" + message.getMajorID()+ message.getMinorID(), w);
		(new Thread(w)).start();
	}
}
