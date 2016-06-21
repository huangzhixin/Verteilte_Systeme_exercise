package vsue.replica;

import java.io.Serializable;

import vsue.rpc.VSRemoteReference;

public class VSRemoteGroupReference implements Serializable {
		private VSRemoteReference[] references;
		
		public void setReferences(VSRemoteReference[] references) {
			this.references = references;
		}

		public VSRemoteReference[] getReferences() {
			return references;
		}
		
}