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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ck.panda.rabbitmq.util.ActionListener;
import ck.panda.rabbitmq.util.AlertEventListener;
import ck.panda.rabbitmq.util.AsynchronousJobListener;
import ck.panda.rabbitmq.util.ResourceStateListener;
import ck.panda.rabbitmq.util.UsageEventListener;
import ck.panda.service.SyncService;
import ck.panda.service.VirtualMachineService;
import ck.panda.util.CloudStackServer;

/**
 * RabbitMQ configuration to publish/consume messages from CS server via RabbitMQ server with specified
 * Exchange name. All CS server events are tracked and update the status of resources in APP DB, update usage
 * of resource in APP DB, sync APP DB while action directly handled at CS server, CS server Alert.
 */
@Configuration
public class RabbitConfig {
    /** The hostname. */
    @Value(value = "${spring.rabbit.host}")
    private String hostName;

    /** The virtual hostname. */
    @Value(value = "${spring.rabbit.vhost}")
    private String vhost;

    /** RabbitMQ login user name. */
    @Value(value = "${spring.rabbit.username}")
    private String username;

    /** RabbitMQ login password. */
    @Value(value = "${spring.rabbit.password}")
    private String password;

    /** CS server action routing key pattern. */
    @Value(value = "${spring.rabbit.server.action.pattern}")
    private String csActionPattern;

    /** CS server alert routing key pattern. */
    @Value(value = "${spring.rabbit.server.alert.pattern}")
    private String csAlertPattern;

    /** CS server usage routing key pattern. */
    @Value(value = "${spring.rabbit.server.usage.pattern}")
    private String csUsagePattern;

    /** CS server asynchronous routing key pattern. */
    @Value(value = "${spring.rabbit.server.asynchJob.pattern}")
    private String csAsynchJobPattern;

    /** CS server usage routing key pattern. */
    @Value(value = "${spring.rabbit.server.resource.pattern}")
    private String csResourcePattern;

    /** CS server exchange name. */
    @Value(value = "${spring.rabbit.exchange.name}")
    private String exchangeName;

    /** CS server alert queue name. */
    @Value(value = "${spring.rabbit.server.alert.queue}")
    private String csAlertQueueName;

    /** CS server usage queue name. */
    @Value(value = "${spring.rabbit.server.usage.queue}")
    private String csUsageQueueName;

    /** CS server asyncJob queue name. */
    @Value(value = "${spring.rabbit.server.asynchJob.queue}")
    private String csAsynchJobQueueName;

    /** CS server action queue name. */
    @Value(value = "${spring.rabbit.server.action.queue}")
    private String csActionQueueName;

    /** CS server action queue name. */
    @Value(value = "${spring.rabbit.server.resource.queue}")
    private String csResourceQueueName;

    /** Application context reference. */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Queue for action event messages.
     *
     * @return queue name
     */
    @Bean
    Queue queue() {
        return new Queue(csActionQueueName, true);
    }

    /**
     * Queue for asynchronous job messages.
     *
     * @return queue name
     */
    @Bean
    Queue queue1() {
        return new Queue(csAsynchJobQueueName, true);
    }

    /**
     * Queue for usage messages.
     *
     * @return queue name
     */
    @Bean
    Queue queue2() {
        return new Queue(csUsageQueueName, true);
    }

    /**
     * Queue for alert messages.
     *
     * @return queue name
     */
    @Bean
    Queue queue4() {
        return new Queue(csResourceQueueName, true);
    }

    /**
     * Queue for resource's state messages.
     *
     * @return queue name
     */
    @Bean
    Queue queue3() {
        return new Queue(csAlertQueueName, true);
    }

    /**
     * Create Topic Exchange from given exchange name specified in CS server event-bus configuration.
     *
     * @return exchange name
     */
    @Bean
    TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    /**
     * Binding Queue with exchange to get action related event message from CS server.
     *
     * @param queue name of the queue.
     * @param exchange name of the exchange.
     * @return binding object
     */
    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(csActionPattern);
    }

    /**
     * Binding Queue with exchange to get asynchronous job related event message from CS server.
     *
     * @param queue1 name of the queue1.
     * @param exchange name of the exchange.
     * @return binding object
     */
    @Bean
    Binding binding1(Queue queue1, TopicExchange exchange) {
        return BindingBuilder.bind(queue1).to(exchange).with(csAsynchJobPattern);
    }

    /**
     * Binding Queue with exchange to get usage related event message from CS server.
     *
     * @param queue2 name of the queue2.
     * @param exchange name of the exchange.
     * @return binding object
     */
    @Bean
    Binding binding2(Queue queue2, TopicExchange exchange) {
        return BindingBuilder.bind(queue2).to(exchange).with(csUsagePattern);
    }

    /**
     * Binding Queue with exchange to get alert related event message from CS server.
     *
     * @param queue3 name of the queue2.
     * @param exchange name of the exchange.
     * @return binding object
     */
    @Bean
    Binding binding3(Queue queue3, TopicExchange exchange) {
        return BindingBuilder.bind(queue3).to(exchange).with(csAlertPattern);
    }

    /**
     * Binding Queue with exchange to get resources state event message from CS server.
     *
     * @param queue4 name of the queue4.
     * @param exchange name of the exchange.
     * @return binding object
     */
    @Bean
    Binding binding4(Queue queue4, TopicExchange exchange) {
        return BindingBuilder.bind(queue4).to(exchange).with(csResourcePattern);
    }

    /**
     * Convenience "factory" to facilitate opening a link Connection to an AMQP broker.
     *
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
     * It simplifies synchronous RabbitMQ access (sending and receiving messages).
     *
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
     * Message listener adapter that delegates the handling of action event messages to target listener
     * methods via reflection, with flexible type conversion.
     *
     * @return message action event listener.
     */
    @Bean
    MessageListenerAdapter actionListenerAdapter() {
        SyncService syncService = applicationContext.getBean(SyncService.class);
        CloudStackServer cloudStackServer = applicationContext.getBean(CloudStackServer.class);
        return new MessageListenerAdapter(new ActionListener(syncService, cloudStackServer));
    }

    /**
     * Message listener adapter that delegates the handling of asynchronous job messages to target listener
     * methods via reflection, with flexible type conversion.
     *
     * @return message asynchronous job listener.
     */
    @Bean
    MessageListenerAdapter asynchJobListenerAdapter() {
        SyncService syncService = applicationContext.getBean(SyncService.class);
        CloudStackServer cloudStackServer = applicationContext.getBean(CloudStackServer.class);
        return new MessageListenerAdapter(new AsynchronousJobListener(syncService, cloudStackServer));
    }

    /**
     * Message listener adapter that delegates the handling of resource state event messages to target
     * listener methods via reflection, with flexible type conversion.
     *
     * @return message asynchronous job listener.
     */
    @Bean
    MessageListenerAdapter resourceStateListenerAdapter() {
        VirtualMachineService virtualMachineService = applicationContext.getBean(VirtualMachineService.class);
        return new MessageListenerAdapter(new ResourceStateListener(virtualMachineService));
    }

    /**
     * Message listener adapter that delegates the handling of alert messages to target listener methods via
     * reflection, with flexible type conversion.
     *
     * @return message alert listener.
     */
    @Bean
    MessageListenerAdapter alertListenerAdapter() {
        return new MessageListenerAdapter(new AlertEventListener());
    }

    /**
     * Message listener adapter that delegates the handling of usage messages to target listener methods via
     * reflection, with flexible type conversion.
     *
     * @return message usage listener.
     */
    @Bean
    MessageListenerAdapter usageListenerAdapter() {
        return new MessageListenerAdapter(new UsageEventListener());
    }

    /**
     * Add message listerner to message listener container with specified queue to listen/consume action event
     * message.
     *
     * @param queue queue for action event.
     * @return message listener container for action event messages.
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
     * Add message listerner to message listener container with specified queue to listen/consume alert
     * message.
     *
     * @param queue3 queue for alert event.
     * @return message listener container for alert message.
     */
    @Bean
    SimpleMessageListenerContainer alertContainer(Queue queue3) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueues(queue3);
        container.setMessageListener(alertListenerAdapter());
        return container;
    }

    /**
     * Add message listerner to message listener container with specified queue to listen/consume usage
     * message.
     *
     * @param queue2 queue for usage event.
     * @return message listener container for usage message.
     */
    @Bean
    SimpleMessageListenerContainer usageContainer(Queue queue2) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueues(queue2);
        container.setMessageListener(usageListenerAdapter());
        return container;
    }

    /**
     * Add message listerner to message listener container with specified queue to listen/consume asynchronous
     * job message.
     *
     * @param queue1 queue for asynchronous event.
     * @return message listener container for asynchronous message.
     */
    @Bean
    SimpleMessageListenerContainer asynchJobContainer(Queue queue1) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueues(queue1);
        container.setMessageListener(asynchJobListenerAdapter());
        return container;
    }

    /**
     * Add message listerner to message listener container with specified queue to listen/consume resource's
     * state message.
     *
     * @param queue4 queue for asynchronous event.
     * @return message listener container for resource's state message.
     */
    @Bean
    SimpleMessageListenerContainer resourceStateContainer(Queue queue4) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueues(queue4);
        container.setMessageListener(resourceStateListenerAdapter());
        return container;
    }
}
