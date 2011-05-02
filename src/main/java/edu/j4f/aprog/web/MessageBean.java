package edu.j4f.aprog.web;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.j4f.aprog.entity.PersonalMessage;
import edu.j4f.aprog.entity.PersonalMessageRecipient;
import edu.j4f.aprog.entity.User;
import edu.j4f.aprog.service.MessageService;
import edu.j4f.aprog.utils.GetParameter;
import edu.j4f.aprog.utils.PersonalMessageWebWrapper;
import edu.j4f.aprog.utils.UserManager;

@ManagedBean(name = "message")
public class MessageBean {

	private static final Logger logger = LoggerFactory.getLogger(MessageBean.class);

	private String title;
	private String message;
	private boolean isProcessed;
	private String messageAnswerText;

	private MessageService messageService;

	public MessageBean() {
		messageService = new MessageService();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean getIsProcessed() {
		return isProcessed;
	}

	public void setMessageAnswerText(String messageAnswerText) {
		this.messageAnswerText = messageAnswerText;
	}

	public String getMessageAnswerText() {
		return messageAnswerText;
	}

	public void sendClaimMessage() {
		User currentUser = UserManager.getInstance().getCurrentUser();
		FacesContext context = FacesContext.getCurrentInstance();
		if (currentUser != null) {
			if (currentUser.getRole().canClaim()) {
				int documentId = GetParameter.getIntegerValue("id");
				if (documentId != -1) {
					PersonalMessage claimMessage = new PersonalMessage();
					claimMessage.setBody("Claim for document id: " + documentId + "\n" + getMessage());
					claimMessage.setCreationTime(new Timestamp(System.currentTimeMillis()));
					claimMessage.setSubject(getTitle());

					messageService.sendMessageToAdmins(currentUser, claimMessage);
					isProcessed = true;
				} else {
					logger.error("Document id is undefined");
					FacesMessage errorMessage = new FacesMessage(context.getApplication().getResourceBundle(context, "i18n").getString("claim.undefineddocument"));
					errorMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
					context.addMessage("", errorMessage);
					isProcessed = false;
				}
			} else {
				logger.error("User has no permission to claim");
				FacesMessage errorMessage = new FacesMessage(context.getApplication().getResourceBundle(context, "i18n").getString("claim.nopermissions"));
				errorMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				context.addMessage("", errorMessage);
				isProcessed = false;
			}
		} else {
			logger.error("User must be logged in to send messages");
			FacesMessage errorMessage = new FacesMessage(context.getApplication().getResourceBundle(context, "i18n").getString("claim.loginrequired"));
			errorMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage("", errorMessage);
			isProcessed = false;
		}
	}

	public int getNewMessageCount() {
		User currentUser = UserManager.getInstance().getCurrentUser();
		return messageService.getNewMessagesCount(currentUser);
	}

	public List<PersonalMessageWebWrapper<PersonalMessageRecipient>> getIncomingMessages() {
		User currentUser = UserManager.getInstance().getCurrentUser();
		List<PersonalMessageWebWrapper<PersonalMessageRecipient>> pmrsWrapped = new ArrayList<PersonalMessageWebWrapper<PersonalMessageRecipient>>();

		if (currentUser != null) {
			List<PersonalMessageRecipient> pmrs = messageService.getPersonalMessagesRecipient(currentUser);
			for (PersonalMessageRecipient pmr : pmrs) {
				PersonalMessageWebWrapper<PersonalMessageRecipient> pmrw = new PersonalMessageWebWrapper<PersonalMessageRecipient>();
				pmrw.setMessage(pmr);
				pmrw.setMessage(pmr);
				String url = FacesContext.getCurrentInstance().getExternalContext().encodeResourceURL("/pmanswer.jsf?id=" + pmr.getId());
				pmrw.setUrl(url);
				pmrsWrapped.add(pmrw);
			}
		}
		return pmrsWrapped;
	}

	public PersonalMessageRecipient getPersonalMessageRecipient() {
		int id = GetParameter.getIntegerValue("id");
		if (id == -1) {
			logger.error("Undefined id");
			return null;
		}
		User currentUser = UserManager.getInstance().getCurrentUser();
		if (currentUser != null) {
			PersonalMessageRecipient pmr = messageService.getPersonalMessageRecipient(currentUser, new Long(id));
			messageService.setRecipientMessageRead(currentUser, pmr.getId());
			return pmr;
		}
		return null;
	}

	public void answerMessage() {
		PersonalMessageRecipient originalMessage = getPersonalMessageRecipient();

		User currentUser = UserManager.getInstance().getCurrentUser();
		FacesContext context = FacesContext.getCurrentInstance();
		if (currentUser != null) {
			PersonalMessage message = new PersonalMessage();
			message.setBody(getMessageAnswerText());
			message.setCreationTime(new Timestamp(System.currentTimeMillis()));
			String subject = originalMessage.getPersonalMessage().getSubject();
			message.setSubject(subject);
			User owner = originalMessage.getPersonalMessage().getUser();
			messageService.sendMessage(currentUser, owner, message);
			isProcessed = true;
		} else {
			logger.error("User must be logged in to send messages");
			FacesMessage errorMessage = new FacesMessage(context.getApplication().getResourceBundle(context, "i18n").getString("claim.loginrequired"));
			errorMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage("", errorMessage);
			isProcessed = false;
		}
	}

	public void deletePMRMessage() {
		int messageId = GetParameter.getIntegerValue("id");

		FacesContext context = FacesContext.getCurrentInstance();
		try {
			User currentUser = UserManager.getInstance().getCurrentUser();
			if (currentUser != null) {
				messageService.deleteRecipientMessage(currentUser, new Long(messageId));
				String pmUrl = context.getExternalContext().encodeResourceURL("/pmincoming.jsf");
				FacesContext.getCurrentInstance().getExternalContext().redirect(pmUrl);
			}
		} catch (Exception e) {
			logger.error("Can not delete personal incoming message");
			FacesMessage errorMessage = new FacesMessage(context.getApplication().getResourceBundle(context, "i18n").getString("claim.deletemessagefailed"));
			errorMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage("", errorMessage);			
		}
	}

	public List<PersonalMessageWebWrapper<PersonalMessage>> getOutgoingMessages() {
		User currentUser = UserManager.getInstance().getCurrentUser();
		FacesContext context = FacesContext.getCurrentInstance();
		List<PersonalMessageWebWrapper<PersonalMessage>> personalMessagesWrapped = new ArrayList<PersonalMessageWebWrapper<PersonalMessage>>();
		if (currentUser != null) {
			List<PersonalMessage> personalMessages = messageService.getPersonalMessages(currentUser);
			for (PersonalMessage pm : personalMessages) {
				PersonalMessageWebWrapper<PersonalMessage> pmw = new PersonalMessageWebWrapper<PersonalMessage>();
				pmw.setMessage(pm);
				String url = context.getExternalContext().encodeResourceURL("/pmoutgoingview.jsf?id=" + pm.getId());
				pmw.setUrl(url);
				personalMessagesWrapped.add(pmw);
			}
		}
		return personalMessagesWrapped;
	}

	public PersonalMessage getPersonalMessage() {
		int id = GetParameter.getIntegerValue("id");
		if (id == -1) {
			logger.error("Undefined id");
			return null;
		}
		User currentUser = UserManager.getInstance().getCurrentUser();
		if (currentUser != null) {
			PersonalMessage pm = messageService.getPersonalMessage(currentUser, new Long(id));
			return pm;
		}
		return null;
	}

	public void deletePersonalMessage() {
		int messageId = GetParameter.getIntegerValue("id");

		FacesContext context = FacesContext.getCurrentInstance();
		try {
			User currentUser = UserManager.getInstance().getCurrentUser();
			if (currentUser != null) {
				messageService.deletePersonalMessage(currentUser, new Long(messageId));
				String pmUrl = context.getExternalContext().encodeResourceURL("/pmoutgoing.jsf");
				FacesContext.getCurrentInstance().getExternalContext().redirect(pmUrl);
			}
		} catch (Exception e) {
			logger.error("Can not delete personal outgoing message");
			FacesMessage errorMessage = new FacesMessage(context.getApplication().getResourceBundle(context, "i18n").getString("claim.deletemessagefailed"));
			errorMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage("", errorMessage);			
		}
	}

	public void sendFeedbackMessage() {
		User currentUser = UserManager.getInstance().getCurrentUser();
		FacesContext context = FacesContext.getCurrentInstance();
		if (currentUser != null) {
			PersonalMessage claimMessage = new PersonalMessage();
			claimMessage.setBody(getMessage());
			claimMessage.setCreationTime(new Timestamp(System.currentTimeMillis()));
			claimMessage.setSubject(getTitle());

			messageService.sendMessageToAdmins(currentUser, claimMessage);
			isProcessed = true;
		} else {
			logger.error("User must be logged in to send messages");
			FacesMessage errorMessage = new FacesMessage(
					context.getApplication().getResourceBundle(context, "i18n").getString("claim.loginrequired"));
			errorMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage("", errorMessage);
			isProcessed = false;
		}
	}

}
