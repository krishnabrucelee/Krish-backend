package ck.panda.rabbitmq.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.EmailConstants;
import ck.panda.domain.entity.Event;
import ck.panda.domain.entity.Event.EventType;
import ck.panda.domain.entity.Event.Status;
import ck.panda.service.ConvertEntityService;
import ck.panda.service.EmailJobService;

/**
 * Alert event listener will listen and update resource's idle,bound limit,etc., data to our App DB when an event
 * handled directly in CS server.
 */
public class AlertEventListener implements MessageListener {

    /** Reference of the convert entity service. */
    private ConvertEntityService convertEntityService;

    /** Email job service. */
    private EmailJobService emailJobService;

    /**
     * Inject convert entity service.
     *
     * @param convertEntityService convertEntityService object.
     */
    public AlertEventListener(ConvertEntityService convertEntityService, EmailJobService emailJobService){
        this.convertEntityService = convertEntityService;
        this.emailJobService = emailJobService;
    }

    @Override
    public void onMessage(Message message) {
        try {
            JSONObject eventObject = new JSONObject(new String(message.getBody()));
            if (eventObject != null) {
                if (!message.getMessageProperties().getReceivedRoutingKey()
                        .contains(CloudStackConstants.CS_ALERT_USAGE)) {
                    Event alertEvent = new Event();
                    alertEvent.setEventDateTime(convertEntityService.getTimeService()
                            .convertDateAndTime(eventObject.getString(CloudStackConstants.CS_EVENT_DATE_TIME)));
                    alertEvent.setEventType(EventType.ALERT);
                    alertEvent.setEvent(eventObject.getString(CloudStackConstants.CS_EVENT_NAME));
                    alertEvent.setMessage(eventObject.getString(CloudStackConstants.CS_ALERT_MESSAGE));
                    alertEvent.setStatus(Status.FAILED);
                    if(eventObject.getString(CloudStackConstants.CS_ALERT_MESSAGE).trim().equalsIgnoreCase("")) {
                        alertEvent.setMessage(eventObject.getString(CloudStackConstants.CS_ALERT_SUBJECT));
                    }
                    EmailEvent emailEvent = new EmailEvent();
                    emailEvent.setSubject(eventObject.getString(CloudStackConstants.CS_ALERT_SUBJECT));
                    emailEvent.setMessageBody(alertEvent.getMessage());
                    emailEvent.setEventType(EmailConstants.SYSTEM_ERROR);
                    emailEvent.setResourceUuid(eventObject.getString(EmailConstants.EMAIL_dataCenterId));
                    if (eventObject.has(EmailConstants.EMAIL_podId)) {
                        emailEvent.setResourceId(eventObject.getString(EmailConstants.EMAIL_podId));
                    }
                    emailJobService.sendMessageToQueue(emailEvent);
                    try {
                        convertEntityService.getWebsocketService().handleEventAction(alertEvent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
