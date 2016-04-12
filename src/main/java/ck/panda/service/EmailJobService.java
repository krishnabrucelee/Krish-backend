package ck.panda.service;

import org.springframework.stereotype.Service;
import ck.panda.rabbitmq.util.EmailEvent;

/**
 * Email job service is used to listen and parse email template from email message from RabbitMQ and send it to email service .
 */
@Service
public interface EmailJobService {

    /**
     * Email message listener and send mail message to email service.
     *
     * @param eventObject response json event object.
     * @throws Exception unhandled errors
     */
    void sendEmail(String eventObject) throws Exception;

    /**
     * Send message to email Queue.
     *
     * @param emailEvent email event.
     * @throws Exception unhandled errors
     */
    void sendMessageToQueue(EmailEvent emailEvent) throws Exception;

}
