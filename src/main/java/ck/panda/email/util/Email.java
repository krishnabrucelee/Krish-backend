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
	 * Set sender email.
	 *
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * Set sender email.
	 *
	 * @param from the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * Set recipient email.
	 *
	 * @return the to
	 */
	public String getTo() {
		return to;
	}

	/**
	 * Get recipient email.
	 *
	 * @param to the to to set
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * Get recipients email.
	 *
	 * @return the cc
	 */
	public String getCc() {
		return cc;
	}

	/**
	 * Set recipients email.
	 *
	 * @param cc the cc to set
	 */
	public void setCc(String cc) {
		this.cc = cc;
	}

	/**
	 * Get recipients email.
	 *
	 * @return the bcc
	 */
	public String getBcc() {
		return bcc;
	}

	/**
	 * Set recipients email.
	 *
	 * @param bcc the bcc to set
	 */
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	/**
	 * Get subject for email.
	 *
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Set subject of the email.
	 *
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Get body of the email.
	 *
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Set subject for email.
	 *
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * Get attachments for email.
	 *
	 * @return the attachments
	 */
	public HashMap<String, byte[]> getAttachments() {
		return attachments;
	}

	/**
	 * Set attachments for email.
	 *
	 * @param attachments the attachments to set
	 */
	public void setAttachments(HashMap<String, byte[]> attachments) {
		this.attachments = attachments;
	}
}
