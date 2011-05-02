package edu.j4f.aprog.utils;

public class PersonalMessageWebWrapper<MessaeType> {

	private MessaeType message;
	private String url;

	public MessaeType getMessage() {
		return message;
	}

	public void setMessage(MessaeType personalMessageRecipient) {
		this.message = personalMessageRecipient;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
