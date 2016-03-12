package ck.panda.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.Event;
import ck.panda.domain.entity.Event.EventType;
import ck.panda.domain.entity.Event.Status;

@Service
public class WebsocketServiceImpl implements WebsocketService {

	/** Simple messaging template for send and receive messages.*/
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	/** Event notification service for tracking.*/
	@Autowired
	private EventNotificationService eventNotificationService;

	@Override
	public void handleEventAction(Event event) throws Exception {
		event.setIsActive(true);
		event.setIsArchive(false);
		Event persistevent = eventNotificationService.save(event);
		if (persistevent != null) {
			if (persistevent.getEvent() != null) {
				if (persistevent.getEventType().equals(Event.EventType.ACTION)) {
					messagingTemplate.convertAndSend(
							CloudStackConstants.CS_ACTION_MAP + persistevent.getEventOwnerId(),persistevent.getMessage());
					if (persistevent.getResourceUuid() != null) {
						messagingTemplate.convertAndSend(
								CloudStackConstants.CS_ACTION_MAP + persistevent.getEvent() + CloudStackConstants.CS_SEPERATOR + persistevent.getEventOwnerId()
										+ CloudStackConstants.CS_SEPERATOR + persistevent.getResourceUuid(),
								persistevent.getMessage());
					} else {
						messagingTemplate.convertAndSend(
								CloudStackConstants.CS_ACTION_MAP + persistevent.getEvent() + CloudStackConstants.CS_SEPERATOR + persistevent.getEventOwnerId(), persistevent.getMessage());
					}
				} else if (persistevent.getEventType().equals(Event.EventType.ASYNC)) {
					if (persistevent.getMessage() != null && persistevent.getStatus().equals(Event.Status.FAILED)) {
						messagingTemplate.convertAndSend(CloudStackConstants.CS_ERROR_MAP + persistevent.getEvent() + CloudStackConstants.CS_SEPERATOR + persistevent.getEventOwnerId()
								+ CloudStackConstants.CS_SEPERATOR + persistevent.getResourceUuid(), persistevent.getMessage());
					} else {
						if (persistevent.getStatus().equals(Event.Status.SUCCEEDED)) {
							messagingTemplate.convertAndSend(CloudStackConstants.CS_ASYNC_MAP + persistevent.getEvent() + CloudStackConstants.CS_SEPERATOR + persistevent.getEventOwnerId()
									+ CloudStackConstants.CS_SEPERATOR + persistevent.getResourceUuid(), persistevent.getStatus());
						}
					}
				}  else {
					messagingTemplate.convertAndSend(CloudStackConstants.CS_ALERT_MAP + persistevent.getEvent(), persistevent.getMessage());
				}
			} else {
					messagingTemplate.convertAndSend(
							CloudStackConstants.CS_RESOURCE_MAP + persistevent.getResourceUuid(),
							persistevent.getMessage());
			}
		}
	}

}
