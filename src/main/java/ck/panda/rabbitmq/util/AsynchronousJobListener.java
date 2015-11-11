package ck.panda.rabbitmq.util;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

/**
 * Asynchronous Job listener will listen and update resource data to our App DB when
 * an event handled directly in CS server.
 *
 */
public class AsynchronousJobListener implements MessageListener {

   @Override
   public void onMessage(Message arg0) {
      // TODO Auto-generated method stub

   }

}
