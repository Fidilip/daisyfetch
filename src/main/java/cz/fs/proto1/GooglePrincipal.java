package cz.fs.proto1;

import java.io.Serializable;
import java.security.Principal;

public class GooglePrincipal implements Serializable, Principal {

	private static final long serialVersionUID = -2548544374439590331L;
	protected String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return email;
	}

}
