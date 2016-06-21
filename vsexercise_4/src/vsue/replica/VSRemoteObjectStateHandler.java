package vsue.replica;

import java.io.InputStream;
import java.io.OutputStream;

public interface VSRemoteObjectStateHandler {
	public void getState(OutputStream output);
	public void setState(InputStream input);
}