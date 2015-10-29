package ck.panda;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ck.panda.rabbitmq.util.*;
import ck.panda.service.VirtualMachineService;
import ck.panda.service.VirtualMachineServiceImpl;

/**
 * RabbitMQ configuration class.
 *
 */
@Configuration
public class RabbitConfig {

    /**
     * Initialize the hostname.
     */
    @Value(value = "${spring.rabbit.host}")
    private String hostName;

    /**
     * Initialize the virtual hostname.
     */
    @Value(value = "${spring.rabbit.vhost}")
    private String vhost;

    /**
     * Initialize the rabbitmq login user name.
     */
    @Value(value = "${spring.rabbit.username}")
    private String username;

    /**
     * Initialize rabbitmq login password.
     */
    @Value(value = "${spring.rabbit.password}")
    private String password;

    /**
     * Initialize cs server action pattern.
     */
    @Value(value = "${spring.rabbit.server.action.pattern}")
    private String csActionPattern;

    /**
     * Initialize cs server alert pattern.
     */
    @Value(value = "${spring.rabbit.server.alert.pattern}")
    private String csAlertPattern;

    /**
     * Initialize cs server uasge pattern.
     */
    @Value(value = "${spring.rabbit.server.usage.pattern}")
    private String csUsagePattern;

    /**
     * Initialize cs server action pattern.
     */
    @Value(value = "${spring.rabbit.server.asynchJob.pattern}")
    private String csAsynchJobPattern;


    /**
     * Initialize exchange name.
     */
    @Value(value = "${spring.rabbit.exchange.name}")
    private String exchangeName;

    /**
     * Initialize server cloudstack alert queue name.
     */
    @Value(value = "${spring.rabbit.server.alert.queue}")
    private String csAlertQueueName;

    /**
     * Initialize server cloudstack usage queue name.
     */
    @Value(value = "${spring.rabbit.server.usage.queue}")
    private String csUsageQueueName;


    /**
     * Initialize server cloudstack asyncJob queue name.
     */
    @Value(value = "${spring.rabbit.server.asynchJob.queue}")
    private String csAsynchJobQueueName;


    /**
     * Initialize server cloudstack action queue name.
     */
    @Value(value = "${spring.rabbit.server.action.queue}")
    private String csActionQueueName;

    @Autowired
	private ApplicationContext applicationContext;


    /**
     * @return queue name
     */
    @Bean
    Queue queue() {
        return new Queue(csActionQueueName, true);
    }
    /**
     * @return queue name
     */
    @Bean
    Queue queue1() {
        return new Queue(csAsynchJobQueueName, true);
    }
    /**
     * @return queue name
     */
    @Bean
    Queue queue2() {
        return new Queue(csUsageQueueName, true);
    }
    /**
     * @return queue name
     */
    @Bean
    Queue queue3() {
        return new Queue(csAlertQueueName, true);
    }

    /**
     * @return exchange name
     */
    @Bean
    TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    /**
     * @param queue name to set
     * @param exchange name to set
     * @return binding object
     */
    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(csActionPattern);
    }

    /**
     * @param queue name to set
     * @param exchange name to set
     * @return binding object
     */
    @Bean
    Binding binding1(Queue queue1, TopicExchange exchange) {
        return BindingBuilder.bind(queue1).to(exchange).with(csAsynchJobPattern);
    }

    /**
     * @param queue name to set
     * @param exchange name to set
     * @return binding object
     */
    @Bean
    Binding binding2(Queue queue2, TopicExchange exchange) {
        return BindingBuilder.bind(queue2).to(exchange).with(csUsagePattern);
    }

    /**
     * @param queue name to set
     * @param exchange name to set
     * @return binding object
     */
    @Bean
    Binding binding3(Queue queue3, TopicExchange exchange) {
        return BindingBuilder.bind(queue3).to(exchange).with(csAlertPattern);
    }

    /**
     * @return connection factory
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(hostName);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vhost);
        return connectionFactory;
    }

    /**
     * @return rabbit template setup
     */
    @Bean
    public RabbitTemplate template() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setExchange(exchangeName);
        rabbitTemplate.setConnectionFactory(connectionFactory());
        return rabbitTemplate;
    }

    /**
     * @return message action event listener.
     */
    @Bean
    MessageListenerAdapter actionListenerAdapter() {
		VirtualMachineService virtualmachineservice = applicationContext.getBean(VirtualMachineService.class);
        return new MessageListenerAdapter(new ActionListener(virtualmachineservice));
    }

    /**
     * @return message asynchronous job listener.
     */
    @Bean
    MessageListenerAdapter AsynchJobListenerAdapter() {
        return new MessageListenerAdapter(new AsynchronousJobListener());
    }


    /**
     * @return message Alert listener.
     */
    @Bean
    MessageListenerAdapter AlertListenerAdapter() {
        return new MessageListenerAdapter(new AlertEventListener());
    }

    /**
     * @return message usage listener.
     */
    @Bean
    MessageListenerAdapter UsageListenerAdapter() {
        return new MessageListenerAdapter(new UsageEventListener());
    }

    /**
     * Set Action Event Queue and Listener.
     *
     * @param listenerAdapter class to set.
     * @return message listener actionContainer.
     */
    @Bean
    SimpleMessageListenerContainer actionContainer(Queue queue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueues(queue);
        container.setMessageListener(actionListenerAdapter());
        return container;
    }


    /**
     * Set Alert Queue and Listener.
     *
     * @param listenerAdapter class to set.
     * @return message listener alertContainer.
     */
    @Bean
    SimpleMessageListenerContainer alertContainer(Queue queue3) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueues(queue3);
        container.setMessageListener(AlertListenerAdapter());
        return container;
    }

    /**
     * Set Usage Queue and Listener.
     *
     * @param listenerAdapter class to set.
     * @return message listener usageContainer.
     */
    @Bean
    SimpleMessageListenerContainer usageContainer(Queue queue2) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueues(queue2);
        container.setMessageListener(UsageListenerAdapter());
        return container;
    }


    /**
     * Set AsynchronousJob Queue and Listener.
     *
     * @param listenerAdapter class to set.
     * @return message listener asynchJobContainer.
     */
    @Bean
    SimpleMessageListenerContainer asynchJobContainer(Queue queue1) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueues(queue1);
        container.setMessageListener(AsynchJobListenerAdapter());
        return container;
    }

}
