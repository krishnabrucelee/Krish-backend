package ck.panda.service;

import ck.panda.email.util.Email;

public interface EmailService {

    /**
     * Send an email message.
     * <p/>
     * The send date is set or overridden if any is present.
     *
     * @param mimeEmail an email to be send.
     * @return email status.
     * @throws unhandled exception.
     */
    Boolean sendMail(Email mimeEmail)  throws Exception;

}
