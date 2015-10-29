package ck.panda.rabbitmq.util;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

public class AlertEventListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		System.out.println(new String(message.getBody()));
	}

}
