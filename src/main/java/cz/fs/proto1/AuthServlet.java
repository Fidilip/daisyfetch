package cz.fs.proto1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.security.Principal;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picketlink.Identity;
import org.picketlink.Identity.AuthenticationResult;
import org.picketlink.authentication.UserAlreadyLoggedInException;
import org.picketlink.common.constants.GeneralConstants;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.model.basic.Realm;
import org.picketlink.idm.model.basic.User;

import cz.fs.proto1.service.IdentityService;

@SuppressWarnings("serial")
@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

	protected final static String REDIRECT_URL = "client.html";
	
	@Inject
	protected Instance<Identity> identityInstance;

	@Inject
	protected User user;

	@Inject
	protected IdentityManager identityManager;

	@Inject
	protected RelationshipManager relationshipManager;

	@Inject
	protected PartitionManager partitionManager;

	@Inject
	protected IdentityService identityService;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		PrintWriter writer = resp.getWriter();
		resp.setContentType("text/html");
		
		Identity identity = identityInstance.get();

		String code = req.getParameter("code");
		String login = req.getParameter("login");
		
		String logout = req.getParameter("logout");
		if(logout != null) {
			identity.logout();
			req.logout();
			writer.println("User logged out.");
			return;
		}
		
		// if user is already servlet-logged in, redirect him
		// TODO: extract login check into method
		if(req.getUserPrincipal() != null) {
			redirect(writer);
		}
		
		if(login != null && (login.equals("facebook") || login.equals("google")) || code != null) {
			ThreadLocalUtils.currentRequest.set((HttpServletRequest) req);
			ThreadLocalUtils.currentResponse.set((HttpServletResponse) resp);
			
			picketlinkLogin(login, identity);
		}
		
		if (identity.isLoggedIn()) {
			writer.println("Logged in.");
			writer.printf("Account: %s%n", identity.getAccount());
			writer.printf("Acccount attributes: %s<br>", identity.getAccount().getAttributes());
			writer.printf("Reg.userPrincipal: %s<br>", req.getUserPrincipal());
			
			Principal p = (Principal) req.getSession()
					.getAttribute("PRINCIPAL");
			writer.printf("Session principal: %s<br>", p);

			writer.printf("User: %s<br>", user);
			
			String realmName = "myRealm";
			Realm partition = partitionManager.getPartition(Realm.class, realmName);
			if (partition == null) {
				partition = new Realm("myRealm");
				partitionManager.add(partition);
			}
			
			user.setPartition(partition);
			writer.printf("Partition: %s with id: %s%n", user.getPartition(), user.getPartition().getId());
	
			servletLogin(req, writer);
			
			writer.printf("Roles %s%n", req.getSession().getAttribute(GeneralConstants.ROLES_ID));
		} else if(login != null && login.equals("local") && req.getServerName().equals("localhost")) {
			servletLogin(req, writer);
			
			// create fake principal
			GooglePrincipal gp = new GooglePrincipal();
			gp.setEmail("test@example.org");
			
			// set fake principal
			identityService.setPrincipal(req, gp);
		} else {
			writer.println("Not logged in.");
		}
	}
	
	protected void servletLogin(HttpServletRequest req, PrintWriter writer) throws ServletException {
		try {
			req.login("social", "social");
		}catch(ServletException se) {
			if(!se.getMessage().equals("UT010030: User already logged in")) {
				throw se;
			}
		}finally {
			redirect(writer);
		}
	}
	
	/** Performs picketlink login */
	protected void picketlinkLogin(String login, Identity identity) {
		try {
			AuthenticationResult ar = identity.login();
			System.out.println(ar);
		}catch(UserAlreadyLoggedInException ualie) {
			// nevermind
		}catch(Exception ae) {
			if(ae.getCause() instanceof UnknownHostException) {
				throw new RuntimeException("Server has no access to the internet.", ae.getCause());
			}else {
				throw ae;
			}
		}
	}
	
	protected void redirect(PrintWriter writer) {
		// TODO: refactor
		writer.println("<a href=\""+REDIRECT_URL+"\">Probíhá automatické přesměrování</a>");
		writer.println("<meta http-equiv=\"refresh\" content=\"0;url="+REDIRECT_URL+"\">");
	}

}
