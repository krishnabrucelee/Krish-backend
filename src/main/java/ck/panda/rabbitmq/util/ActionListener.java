package ck.panda.rabbitmq.util;

import java.util.List;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ck.panda.ApplicationConfig;
import ck.panda.domain.entity.VmInstance;
import ck.panda.service.VirtualMachineService;

public class ActionListener implements MessageListener {
	/** Response event entity. */
	private ResponseEvent eventResponse = null;

	private VirtualMachineService virtualmachineservice;

	public ActionListener(VirtualMachineService virtualmachineservice) {
		this.virtualmachineservice = virtualmachineservice;
		System.out.println("==============");
		System.out.println(virtualmachineservice.getClass().getName());
	}

	@Override
	public void onMessage(Message message) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			eventResponse = mapper.readValue(new String(message.getBody()), ResponseEvent.class);
			System.out.println("==event response=====");
			System.out.println(eventResponse.toString());
			this.handleActionEvent(eventResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handling VM events and updated those in our application DB according to the type of events.
	 *
	 * @param event
	 *            event type.
	 * @param ResponseEvent
	 *            json object.
	 * @throws Exception
	 */
	public void handleActionEvent(ResponseEvent eventObject) throws Exception {
		System.out.println(eventObject.getEvent());
		// VM Event Update.
		if (eventObject.getEvent().startsWith(EventTypes.EVENT_VM_START)) {
			handleVmEvent(eventObject);
		}
	}

	private void handleVmEvent(ResponseEvent event) throws Exception {
		if (event.getEntityuuid() != null) {
			event.setId(event.getEntityuuid());
		}
		if(EventTypes.EVENT_VM_CREATE == event.getEvent()){
			VmInstance vmInstance = virtualmachineservice.findByUUID(event.getId());
			if(event.getStatus() ==  EventTypes.EVENT_COMPLETED){
				vmInstance.setStatus(EventTypes.EVENT_STATUS_RUNNING);
			}
			else if(event.getStatus() ==  EventTypes.EVENT_ERROR){
				vmInstance.setStatus(EventTypes.EVENT_ERROR);
			}
			else{
				vmInstance.setStatus(EventTypes.EVENT_STATUS_CREATE);
			}
			virtualmachineservice.update(vmInstance);
		}
		if(EventTypes.EVENT_VM_START == event.getEvent()){
			VmInstance vmInstance = virtualmachineservice.findByUUID(event.getId());
			if(event.getStatus() ==  EventTypes.EVENT_COMPLETED){
				vmInstance.setStatus(EventTypes.EVENT_STATUS_RUNNING);
			}
			else if(event.getStatus() ==  EventTypes.EVENT_STARTED){
				vmInstance.setStatus(EventTypes.EVENT_STATUS_CREATE);
			}
			else{
				vmInstance.setStatus(EventTypes.EVENT_ERROR);
			}
			virtualmachineservice.update(vmInstance);
		}
		if(EventTypes.EVENT_VM_STOP == event.getEvent()) {
			VmInstance vmInstance = virtualmachineservice.findByUUID(event.getId());
			if(event.getStatus() ==  EventTypes.EVENT_COMPLETED){
				vmInstance.setStatus(EventTypes.EVENT_STATUS_STOPPED);
			}
			else if(event.getStatus() ==  EventTypes.EVENT_STARTED){
				vmInstance.setStatus(EventTypes.EVENT_STATUS_STOPPING);
			}
			else{
				vmInstance.setStatus(EventTypes.EVENT_ERROR);
			}
			virtualmachineservice.update(vmInstance);
		}
		if(EventTypes.EVENT_VM_DESTROY == event.getEvent()) {
			VmInstance vmInstance = virtualmachineservice.findByUUID(event.getId());
			if(event.getStatus() ==  EventTypes.EVENT_COMPLETED){
				vmInstance.setStatus(EventTypes.EVENT_STATUS_DESTROYED);
			}
			else if(event.getStatus() ==  EventTypes.EVENT_STARTED){
				vmInstance.setStatus(EventTypes.EVENT_STATUS_STOPPING);
			}
			else{
				vmInstance.setStatus(EventTypes.EVENT_ERROR);
			}
			virtualmachineservice.update(vmInstance);
		}
		if(EventTypes.EVENT_VM_REBOOT == event.getEvent()) {
			VmInstance vmInstance = virtualmachineservice.findByUUID(event.getId());
			if(event.getStatus() ==  EventTypes.EVENT_COMPLETED){
				vmInstance.setStatus(EventTypes.EVENT_STATUS_RUNNING);
			}
			else if(event.getStatus() ==  EventTypes.EVENT_STARTED){
				vmInstance.setStatus(EventTypes.EVENT_STATUS_CREATE);
			}
			else{
				vmInstance.setStatus(EventTypes.EVENT_ERROR);
			}
			virtualmachineservice.update(vmInstance);
		}
		if(EventTypes.EVENT_VM_RESTORE == event.getEvent()) {
			VmInstance vmInstance = virtualmachineservice.findByUUID(event.getId());
			if(event.getStatus() ==  EventTypes.EVENT_COMPLETED){
				vmInstance.setStatus(EventTypes.EVENT_STATUS_RUNNING);
			}
			else if(event.getStatus() ==  EventTypes.EVENT_STARTED){
				vmInstance.setStatus(EventTypes.EVENT_STATUS_CREATE);
			}
			else{
				vmInstance.setStatus(EventTypes.EVENT_ERROR);
			}
			virtualmachineservice.update(vmInstance);
		}
		if(EventTypes.EVENT_VM_EXPUNGE == event.getEvent()) {
			VmInstance vmInstance = virtualmachineservice.findByUUID(event.getId());
			if(event.getStatus() ==  EventTypes.EVENT_COMPLETED){
				vmInstance.setStatus(EventTypes.EVENT_STATUS_DESTROYED);
			}
			else if(event.getStatus() ==  EventTypes.EVENT_STARTED){
				vmInstance.setStatus(EventTypes.EVENT_STATUS_STOPPING);
			}
			else{
				vmInstance.setStatus(EventTypes.EVENT_ERROR);
			}
			virtualmachineservice.update(vmInstance);
		}
	}

}
