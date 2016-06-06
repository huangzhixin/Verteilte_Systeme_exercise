package vsue.faults;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import vsue.property;
import vsue.communication.VSReplyMessage;
import vsue.communication.VSTestMessage;
import vsue.rpc.VSRemoteReference;

public class VSLomSemantics extends VSAbstractRPCSemantics{

	private VSTestMessage message;
	private VSReplyMessage reMessage;
	private int count = 0;
	private CountDownLatch latch;
	
	public VSLomSemantics(VSRemoteReference Reference) {
		super(Reference);
		
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
			message = generateMessage(method, args);
			latch= new CountDownLatch(1);
			Timer timer = new Timer();
			TimerTask handler = new VSTimeoutHandler();
			timer.schedule(handler, 0);
			latch.await();
			handler.cancel();
			timer.cancel();
			//System.out.println("time out !!!");
			System.out.println("client receive:" + reMessage.getMajorID() +" " + reMessage.getMinorID()+" "+count+" "+"client receive object："+" "+reMessage.getObject());
			return reMessage.getObject();
			
	}
	
	
	private void sendAndStartTimer() throws Throwable
	{
		if(count >=property.MaxCallCount)
		{
			reMessage = new VSReplyMessage(new RemoteException("over MaxCallCount"),message.getMajorID(),message.getMinorID());
			latch.countDown();
			return; 
		}
		else
		{
			count++;
		}
		//System.out.println("client :" + message.getMajorID() +" " + message.getMinorID()+" "+count);
		Timer timer = new Timer();
		TimerTask handler = new VSTimeoutHandler();
		timer.schedule(handler, property.waitingTime);	
		System.out.println("client send:" + message.getMajorID() +" " + message.getMinorID()+" "+count);
		VSReplyMessage tmpMessage = (VSReplyMessage)sendAndReceive(message);
		if(tmpMessage.getMajorID() == message.getMajorID() && tmpMessage.getMinorID() == message.getMinorID())
		{
			
			reMessage = tmpMessage;
			latch.countDown();
			handler.cancel();
			timer.cancel();
			
		}
		
		
	
		
	}
	
	
	private class VSTimeoutHandler extends TimerTask{

		@Override
		public void run() {
			message.increaseMinorID();
			try {
				sendAndStartTimer();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
	}
	
}



