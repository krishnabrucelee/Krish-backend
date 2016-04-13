package ck.panda.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.Event;

@Service
public class WebsocketServiceImpl implements WebsocketService {

    /** Simple messaging template for send and receive messages.*/
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

	 /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsynchronousJobServiceImpl.class);

    /** Event notification service for tracking.*/
    @Autowired
    private EventNotificationService eventNotificationService;

    @Scheduled(fixedDelay = 60000)
    public void handleEventTest() throws Exception {
        messagingTemplate.convertAndSend("/topic/test","test web socket");
    }

    @Override
    public void handleEventAction(Event event) throws Exception {
        event.setIsActive(true);
        event.setIsArchive(false);
		LOGGER.info("Event trigger for websocket" + event.getEvent() +" "+ event.getEventType() +" "+ event.getEventOwner());
        Event persistevent = eventNotificationService.save(event);
        if (persistevent != null) {
            if (persistevent.getEvent() != null) {
                if(persistevent.getEventType().equals(Event.EventType.ACTION) && persistevent.getEvent().contains("VPN.USER.")){
                    messagingTemplate.convertAndSend(
                            CloudStackConstants.CS_ACTION_MAP + persistevent.getEventOwnerId(),persistevent.getMessage());
					LOGGER.info("Action Event trigger for websocket for VPN user" + event.getEvent() +" "+ event.getEventType() +" "+ event.getEventOwner());
                }
                if (persistevent.getEventType().equals(Event.EventType.ACTION) && persistevent.getStatus().equals(Event.Status.INFO)) {
                    messagingTemplate.convertAndSend(
                            CloudStackConstants.CS_ACTION_MAP + persistevent.getEventOwnerId(),persistevent.getMessage());
					LOGGER.info("Action Event trigger for websocket" + event.getEvent() +" "+ event.getEventType() +" "+ event.getEventOwner());
                    if (persistevent.getResourceUuid() != null) {
                        messagingTemplate.convertAndSend(
                                CloudStackConstants.CS_ACTION_MAP + persistevent.getEvent() + CloudStackConstants.CS_SEPERATOR + persistevent.getEventOwnerId()
                                        + CloudStackConstants.CS_SEPERATOR + persistevent.getResourceUuid(),
                                persistevent.getMessage());
						LOGGER.info("Action Event trigger for websocket for relevent event" + event.getEvent() +" "+ event.getEventType() +" "+ event.getEventOwner());
                    } else {
                        messagingTemplate.convertAndSend(
                                CloudStackConstants.CS_ACTION_MAP + persistevent.getEvent() + CloudStackConstants.CS_SEPERATOR + persistevent.getEventOwnerId(), persistevent.getMessage());
						LOGGER.info("Action Event trigger for websocket user id" + event.getEvent() +" "+ event.getEventType() +" "+ event.getEventOwner());
                    }
                } else if (persistevent.getEventType().equals(Event.EventType.ASYNC)) {
                    if (persistevent.getMessage() != null && persistevent.getStatus().equals(Event.Status.FAILED)) {
                        messagingTemplate.convertAndSend(CloudStackConstants.CS_ERROR_MAP + persistevent.getEvent() + CloudStackConstants.CS_SEPERATOR + persistevent.getEventOwnerId()
                                + CloudStackConstants.CS_SEPERATOR + persistevent.getResourceUuid(), persistevent.getMessage());
						LOGGER.info("Async Event trigger for websocket failed status" + event.getEvent() +" "+ event.getEventType() +" "+ event.getEventOwner());
                    } else {
                        if (persistevent.getStatus().equals(Event.Status.SUCCEEDED)) {
                            messagingTemplate.convertAndSend(CloudStackConstants.CS_ASYNC_MAP + persistevent.getEvent() + CloudStackConstants.CS_SEPERATOR + persistevent.getEventOwnerId()
                                    + CloudStackConstants.CS_SEPERATOR + persistevent.getResourceUuid(), persistevent.getStatus());
							LOGGER.info("Async Event trigger for websocket success status" + event.getEvent() +" "+ event.getEventType() +" "+ event.getEventOwner());
                        }
                    }
					LOGGER.info("Alert Event trigger for websocket failed status" + event.getEvent() +" "+ event.getEventType() +" "+ event.getEventOwner());
					LOGGER.info("Alert Event trigger for websocket failed status" + event.getEvent() +" "+ event.getEventType() +" "+ event.getEventOwner());
                }
            } else {
                    messagingTemplate.convertAndSend(
                            CloudStackConstants.CS_RESOURCE_MAP + persistevent.getResourceUuid(),
                            persistevent.getMessage());
					LOGGER.info("Resource Event trigger for websocket state changes" + event.getEvent() +" "+ event.getEventType() +" "+ event.getEventOwner());
            }
        }
    }

}
