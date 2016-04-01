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
    /** Email template recipient type (EmailTemplate entity recipientType field). **/
    private String recipientType;
    /** Email attachments. **/
    private HashMap<String, String> attachments;
    /** Invoice object. **/
    private String invoice;

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
     * Get the recipientType of Email.
     *
     * @return the recipientType
     */
    public String getRecipientType() {
        return recipientType;
    }

    /**
     * Set the recipientType of Email.
     *
     * @param recipientType the recipientType to set
     */
    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }

    /**
     * Get attachments for email.
     *
     * @return the attachments
     */
    public HashMap<String, String> getAttachments() {
        return attachments;
    }

    /**
     * Set attachments for email.
     *
     * @param attachments the attachments to set
     */
    public void setAttachments(HashMap<String, String> attachments) {
        this.attachments = attachments;
    }

    /**
     * Get the invoice of Email.
     *
     * @return the invoice
     */
    public String getInvoice() {
        return invoice;
    }

    /**
     * Set the invoice of Email.
     *
     * @param invoice the invoice to set
     */
    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

}
