package vsue.replica;

import java.io.Serializable;
import java.util.concurrent.Semaphore;
import org.jgroups.JChannel;
import org.jgroups.Message;
import vsue.communication.VSTestMessage;
import vsue.communication.VSReplyMessage;
import vsue.communication.VSObjectConnection;

public class VSWorkerThread implements Runnable {
	private VSObjectConnection objConnection;
	private JChannel channel;
	private Object returnMessage;
	private Semaphore sem;
	private VSTestMessage message;

	public VSWorkerThread(VSObjectConnection con, JChannel channel, VSTestMessage message) {
		setObjConnection(con);
		setChannel(channel);
		sem = new Semaphore(0);
		this.message = message;
	}
	
	@Override
	public void run() {
		VSReplyMessage reply = null;

		try {
			System.out.println("---> WorkerThread started");
			
			Message jGroupMessage = new Message(null, null, message);
			channel.send(jGroupMessage);
			
			sem.acquireUninterruptibly();
		    reply=(VSReplyMessage)returnMessage;
			
			System.out.println("Reply to " + reply);
			getObjConnection().sendObject((Serializable) reply);
			getObjConnection().close();
			System.out.println("---> Client finished");
			System.out.println(channel.getViewAsString());
		} catch (Exception e) {
				e.printStackTrace();
		}
	}
	
	public void setReturnMessage(Object returnValue) {
		this.returnMessage = returnValue;
		sem.release();
	}

	public void setChannel(JChannel channel) {
		this.channel = channel;
	}

	public JChannel getChannel() {
		return channel;
	}
	
	protected void setObjConnection(VSObjectConnection objConnection) {
		this.objConnection = objConnection;
	}

	protected VSObjectConnection getObjConnection() {
		return objConnection;
	}	
}