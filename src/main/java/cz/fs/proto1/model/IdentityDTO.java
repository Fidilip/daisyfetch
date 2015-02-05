package cz.fs.proto1.model;

import org.codehaus.jackson.annotate.JsonProperty;

// TODO: bean validation?
public class IdentityDTO {

	public enum EType {FACEBOOK, GOOGLE, TWITTER};
	
	protected String time;
	protected String email;
	protected EType type;
	
	public String getTime() {
		return time;
	}

	@JsonProperty
	public void setTime(String time) {
		this.time = time;
	}

	public String getEmail() {
		return email;
	}

	@JsonProperty
	public void setEmail(String email) {
		this.email = email;
	}
	
	public EType getType() {
		return type;
	}
	
	@JsonProperty
	public void setType(EType type) {
		this.type = type;
	}
	
}
