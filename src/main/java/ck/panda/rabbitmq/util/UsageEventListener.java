package ck.panda.rabbitmq.util;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

/**
 * Usage event listener will listen and update resource usage data to our App DB when an event handled
 * directly in CS server.
 */
public class UsageEventListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
    }
}
