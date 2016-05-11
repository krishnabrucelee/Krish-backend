package ck.panda.rabbitmq.util;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import ck.panda.constants.CloudStackConstants;
import ck.panda.service.EmailJobService;
import ck.panda.util.infrastructure.AuthenticatedExternalWebService;
import ck.panda.util.infrastructure.externalwebservice.ExternalWebServiceStub;

/**
 * Email listener will listen and send mail to our panda clients and admin when an event handled directly in CS
 * server.
 */
public class EmailListener implements MessageListener {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailListener.class);

    /** Email Job service. */
    private EmailJobService emailJobSerice;

    /** Admin username. */
    private String backendAdminUsername;

    /** Admin role. */
    private String backendAdminRole;

    @Override
    public void onMessage(Message message) {
        try {
            JSONObject emailObject = new JSONObject(new String(message.getBody()));
            this.handleStatusEvent(emailObject, new String(message.getBody()));
        } catch (Exception e) {
            LOGGER.debug("Error on convert action event message", e);
            e.printStackTrace();
        }
    }

    /**
     * Inject email service.
     *
     * @param emailjobservice email service object.
     */
    public EmailListener(EmailJobService emailjobservice, String backendAdminUsername, String backendAdminRole) {
        this.emailJobSerice = emailjobservice;
        this.backendAdminRole = backendAdminRole;
        this.backendAdminUsername = backendAdminUsername;
    }

    /**
     * Handling all the email events.
     *
     * @param eventObject event object.
     * @param eventMessage event message.
     * @throws Exception exception.
     */
    public void handleStatusEvent(JSONObject eventObject, String eventMessage) throws Exception {
            ExternalWebServiceStub externalWebService = new ExternalWebServiceStub();
            AuthenticatedExternalWebService authenticatedExternalWebService = new AuthenticatedExternalWebService(
                    backendAdminUsername, null, AuthorityUtils.commaSeparatedStringToAuthorityList(backendAdminRole));
            authenticatedExternalWebService.setExternalWebService(externalWebService);
            SecurityContextHolder.getContext().setAuthentication(authenticatedExternalWebService);
            emailJobSerice.sendEmail(eventMessage);
    }
}
