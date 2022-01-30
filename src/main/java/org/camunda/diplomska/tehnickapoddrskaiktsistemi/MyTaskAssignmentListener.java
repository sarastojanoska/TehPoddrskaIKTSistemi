package org.camunda.diplomska.tehnickapoddrskaiktsistemi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.context.Context;

public class MyTaskAssignmentListener implements TaskListener{
	
	// TODO: Set Mail Server Properties
		  private static final String HOST = "smtp.gmail.com";
		  private static final String USER = "adm.tehpoddrska@gmail.com";
		  private static final String PWD = "admin123!!";
		  private static final int PORT = 587;

		  private final static Logger LOGGER = Logger.getLogger(TaskAssignmentListener.class.getName());

		  public void notify(DelegateTask delegateTask) {

		    String assignee = delegateTask.getAssignee();
		    //String taskId = delegateTask.getId();

		    if (assignee != null) {
		      
		      // Get User Profile from User Management
		      IdentityService identityService = Context.getProcessEngineConfiguration().getIdentityService();
		      User user = identityService.createUserQuery().userId(assignee).singleResult();

		      if (user != null) {
		        
		        // Get Email Address from User Profile
		        String recipient = user.getEmail();
		        
		        if (recipient != null && !recipient.isEmpty()) {

		          Email email = new SimpleEmail();
		          email.setCharset("utf-8");
		          email.setHostName(HOST);
		          email.setSmtpPort(PORT);
		          email.setTLS(true);
		          email.setAuthentication(USER, PWD);

		          try {
		            email.setFrom("noreply@camunda.org");
		            email.setSubject("Confirmation mail " + delegateTask.getName());
		            email.setMsg("Your problem has been verified and a ticket has been opened");

		            email.addTo(recipient);

		            email.send();
		            LOGGER.info("Confirmation Email successfully sent to user '" + assignee + "' with address '" + recipient + "'.");            

		          } catch (Exception e) {
		            LOGGER.log(Level.WARNING, "Could not send email to assignee", e);
		          }

		        } else {
		          LOGGER.warning("Not sending email to user " + assignee + "', user has no email address.");
		        }

		      } else {
		        LOGGER.warning("Not sending email to user " + assignee + "', user is not enrolled with identity service.");
		      }

		    }

		  }

}
