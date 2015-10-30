package ck.panda.rabbitmq.util;

import java.util.List;

import org.json.JSONObject;
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
			//eventResponse = mapper.readValue(new String(message.getBody()), ResponseEvent.class);
			System.out.println("==event response=====");
			this.handleActionEvent(new String(message.getBody()));
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
	public void handleActionEvent(String eventObject) throws Exception {
		System.out.println(eventObject);
		// VM Event Update.
			handleVmEvent(eventObject);
	}

	private void handleVmEvent(String event) throws Exception {
		JSONObject instance = new JSONObject(event);
		if (instance.has("id")) {
			VmInstance vmInstance = virtualmachineservice.findByUUID(instance.getString("id"));
			if(instance.getString("new-state").equals("Error")){
				vmInstance.setStatus(instance.getString("new-state"));
				vmInstance.setEventMessage(instance.getString("new-state") + "occured");
			}
			vmInstance.setStatus(instance.getString("new-state"));
			vmInstance.setEventMessage("");
			virtualmachineservice.update(vmInstance);

	    }
	}
}
