package ck.panda.rabbitmq.util;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

/**
 * Alert event listener will listen and update resource's idle,bound limit,etc., data to our App DB when an
 * event handled directly in CS server.
 */
public class AlertEventListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
    }
}
