package cz.fs.proto1.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

@SuppressWarnings("serial")
@JsonAutoDetect
public class Snippet implements Serializable {

	protected String name;
	protected String selector;
	
	public Snippet() {}
	
	public Snippet(String name, String selector) {
		super();
		this.name = name;
		this.selector = selector;
	}
	
	@JsonProperty
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonProperty
	public String getSelector() {
		return selector;
	}
	
	public void setSelector(String selector) {
		this.selector = selector;
	}
	
	
	
}

