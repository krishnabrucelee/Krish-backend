package ck.panda.email.util;

import java.util.HashMap;

/** Simple email class **/
public class Email {
	/** Sender email address. **/
	private String from;
	/** Recipient email address. **/
	private String to;
	/** Recipient email addresses. **/
	private String cc;
	/** Recipient email addresses. **/
	private String bcc;
	/** Email subject. **/
	private String subject;
	/** Email body. **/
	private String body;
	/** Email attachments. **/
	private HashMap<String, byte[]> attachments;

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * @return the cc
	 */
	public String getCc() {
		return cc;
	}

	/**
	 * @param cc the cc to set
	 */
	public void setCc(String cc) {
		this.cc = cc;
	}

	/**
	 * @return the bcc
	 */
	public String getBcc() {
		return bcc;
	}

	/**
	 * @param bcc the bcc to set
	 */
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the attachments
	 */
	public HashMap<String, byte[]> getAttachments() {
		return attachments;
	}

	/**
	 * @param attachments the attachments to set
	 */
	public void setAttachments(HashMap<String, byte[]> attachments) {
		this.attachments = attachments;
	}
}
