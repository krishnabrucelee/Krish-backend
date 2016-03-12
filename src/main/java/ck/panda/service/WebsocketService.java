package ck.panda.service;

import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Event;
import ck.panda.domain.entity.Event.EventType;
import ck.panda.domain.entity.Event.Status;

@Service
public interface WebsocketService {

	/**
	 * Save the event from async and action listener.
	 *
	 * @param event event object.
	 * @throws Exception unhandled error.
	 */
	void handleEventAction(Event event) throws Exception;
}
