package org.openforis.collect.remoting.service.dataimport;

import java.util.ArrayList;
import java.util.List;

import org.granite.messaging.amf.io.util.externalizer.annotation.ExternalizedProperty;
import org.openforis.collect.Proxy;
import org.openforis.collect.model.CollectRecord.Step;
import org.openforis.collect.persistence.xml.DataHandler.NodeUnmarshallingError;

/**
 * 
 * @author S. Ricci
 *
 */
public class NodeUnmarshallingErrorProxy implements Proxy {

	private transient NodeUnmarshallingError nodeErrorItem;

	public NodeUnmarshallingErrorProxy(NodeUnmarshallingError nodeErrorItem) {
		super();
		this.nodeErrorItem = nodeErrorItem;
	}

	public static List<NodeUnmarshallingErrorProxy> fromList(List<NodeUnmarshallingError> list) {
		List<NodeUnmarshallingErrorProxy> result = new ArrayList<NodeUnmarshallingErrorProxy>();
		if ( list != null ) {
			for (NodeUnmarshallingError nodeErrorItem : list) {
				NodeUnmarshallingErrorProxy proxy = new NodeUnmarshallingErrorProxy(nodeErrorItem);
				result.add(proxy);
			}
		}
		return result;
	}
	
	@ExternalizedProperty
	public Step getStep() {
		return nodeErrorItem.getStep();
	}

	@ExternalizedProperty
	public String getPath() {
		return nodeErrorItem.getPath();
	}

	@ExternalizedProperty
	public String getMessage() {
		return nodeErrorItem.getMessage();
	}
	
	
	
}
