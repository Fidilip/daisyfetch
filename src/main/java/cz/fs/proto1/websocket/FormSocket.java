package cz.fs.proto1.websocket;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

@ServerEndpoint("/formSocket")
public class FormSocket {
	
    @OnOpen
    public void onOpen(Session session) {
    	System.out.println("Websocket open");
    }
 
    @OnMessage
    public String onMessage(String message, Session session) {
    	int midpoint = message.indexOf(' ');
    	String command = message.substring(0, midpoint);
    	String arg = message.substring(midpoint+1);
    	
    	try {
    		return dispatch(command, arg);
    	}catch(Exception e) {
    		e.printStackTrace();
    		return String.format("error %s %s", e.getClass().getSimpleName(), e.getMessage());
    	}
    }
 
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
    	System.out.println("Websocket closed because: " + closeReason.getReasonPhrase());
    }
    
    protected String dispatch(String command, String args) {
    	switch(command) {
			case "status": return status(args);
			default: 
				String msg = String.format("Unknown commnad '%s' with args: %s", command, args);
				System.out.println(msg);
				return "error unknownCommand "+msg;
    	}
    }
    
    protected String status(String url) {
    	Client client = ClientBuilder.newClient();
		WebTarget target = client.target(url);
		return "status "+String.valueOf(target.request().head().getStatus());
    }
    
}
