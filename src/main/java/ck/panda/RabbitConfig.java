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
import ck.panda.rabbitmq.util.EmailListener;
import ck.panda.rabbitmq.util.ResourceStateListener;
import ck.panda.rabbitmq.util.UsageEventListener;
import ck.panda.service.AsynchronousJobService;
import ck.panda.service.ConvertEntityService;
import ck.panda.service.EmailJobService;
import ck.panda.service.SyncService;
import ck.panda.util.CloudStackServer;
import ck.panda.util.ConfigUtil;

/**
 * RabbitMQ configuration to publish/consume messages from CS server via RabbitMQ server with specified Exchange name.
 * All CS server events are tracked and update the status of resources in APP DB, update usage of resource in APP DB,
 * sync APP DB while action directly handled at CS server, CS server Alert.
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

    /** CS server usage routing key pattern. */
    @Value(value = "${spring.rabbit.server.email.pattern}")
    private String csEmailPattern;

    /** CS server exchange name. */
    @Value(value = "${spring.rabbit.exchange.name}")
    private String exchangeName;

    /** CS server exchange name. */
    @Value(value = "${spring.rabbit.email.name}")
    private String emailExchangeName;

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

    /** CS server email queue name. */
    @Value(value = "${spring.rabbit.server.email.queue}")
    private String csEmailQueueName;

    /** Admin username. */
    @Value("${backend.admin.username}")
    private String backendAdminUsername;

    /** Admin role. */
    @Value("${backend.admin.role}")
    private String backendAdminRole;

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
     * Queue for email messages.
     *
     * @return queue name
     */
    @Bean
    Queue emailQueue() {
        return new Queue(csEmailQueueName, true);
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
     * Create Topic Exchange from given exchange name specified in CS server event-bus configuration.
     *
     * @return exchange name.
     */
    @Bean
    TopicExchange emailExchange() {
        return new TopicExchange(emailExchangeName);
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
     * Binding Queue with exchange to get email message from CS server event.
     *
     * @param emailQueue name of the queue4.
     * @param exchange name of the exchange.
     * @return binding object
     */
    @Bean
    Binding binding5(Queue emailQueue, TopicExchange emailExchange) {
        return BindingBuilder.bind(emailQueue).to(emailExchange).with(csEmailPattern);
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
     * It simplifies synchronous RabbitMQ access for email (sending and receiving messages).
     *
     * @return rabbit template setup
     */
    @Bean
    public RabbitTemplate emailTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setExchange(emailExchangeName);
        rabbitTemplate.setConnectionFactory(connectionFactory());
        return rabbitTemplate;
    }

    /**
     * Message listener adapter that delegates the handling of action event messages to target listener methods via
     * reflection, with flexible type conversion.
     *
     * @return message action event listener.
     */
    @Bean
    MessageListenerAdapter actionListenerAdapter() {
        SyncService syncService = applicationContext.getBean(SyncService.class);
        EmailJobService emailJobService = applicationContext.getBean(EmailJobService.class);
        AsynchronousJobService asyncService = applicationContext.getBean(AsynchronousJobService.class);
        CloudStackServer cloudStackServer = applicationContext.getBean(CloudStackServer.class);
        ConvertEntityService convertEntityService = applicationContext.getBean(ConvertEntityService.class);
        return new MessageListenerAdapter(new ActionListener(syncService, asyncService, convertEntityService,
                cloudStackServer, backendAdminUsername, backendAdminRole, emailJobService));
    }

    /**
     * Message listener adapter that delegates the handling of asynchronous job messages to target listener methods via
     * reflection, with flexible type conversion.
     *
     * @return message asynchronous job listener.
     */
    @Bean
    MessageListenerAdapter asynchJobListenerAdapter() {
        SyncService syncService = applicationContext.getBean(SyncService.class);
        AsynchronousJobService asyncService = applicationContext.getBean(AsynchronousJobService.class);
        CloudStackServer cloudStackServer = applicationContext.getBean(CloudStackServer.class);
        ConvertEntityService convertEntityService = applicationContext.getBean(ConvertEntityService.class);
        ConfigUtil configUtil = applicationContext.getBean(ConfigUtil.class);
        return new MessageListenerAdapter(new AsynchronousJobListener(syncService, asyncService, cloudStackServer, convertEntityService, configUtil,
            backendAdminUsername, backendAdminRole));
    }

    /**
     * Message listener adapter that delegates the handling of resource state event messages to target listener methods
     * via reflection, with flexible type conversion.
     *
     * @return message asynchronous job listener.
     */
    @Bean
    MessageListenerAdapter resourceStateListenerAdapter() {
        ConvertEntityService convertEntityService = applicationContext.getBean(ConvertEntityService.class);
        SyncService sync = applicationContext.getBean(SyncService.class);
        return new MessageListenerAdapter(new ResourceStateListener(convertEntityService,sync));
    }

    /**
     * Message listener adapter that delegates the handling of alert messages to target listener methods via reflection,
     * with flexible type conversion.
     *
     * @return message alert listener.
     */
    @Bean
    MessageListenerAdapter alertListenerAdapter() {
        ConvertEntityService convertEntityService = applicationContext.getBean(ConvertEntityService.class);
        EmailJobService emailJobService = applicationContext.getBean(EmailJobService.class);
        return new MessageListenerAdapter(new AlertEventListener(convertEntityService, emailJobService));
    }

    /**
     * Message listener adapter that delegates the handling of usage messages to target listener methods via reflection,
     * with flexible type conversion.
     *
     * @return message usage listener.
     */
    @Bean
    MessageListenerAdapter usageListenerAdapter() {
        return new MessageListenerAdapter(new UsageEventListener());
    }

    /**
     * Message listener adapter that delegates the handling of email messages to target listener methods via reflection,
     * with flexible type conversion.
     *
     * @return message usage listener.
     */
    @Bean
    MessageListenerAdapter emailListenerAdapter() {
        EmailJobService emailJobService = applicationContext.getBean(EmailJobService.class);
        return new MessageListenerAdapter(new EmailListener(emailJobService, backendAdminUsername, backendAdminRole));
    }

    /**
     * Add message listerner to message listener container with specified queue to listen/consume action event message.
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
     * Add message listerner to message listener container with specified queue to listen/consume alert message.
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
     * Add message listerner to message listener container with specified queue to listen/consume usage message.
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
     * Add message listerner to message listener container with specified queue to listen/consume asynchronous job
     * message.
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
     * Add message listerner to message listener container with specified queue to listen/consume resource's state
     * message.
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

    /**
     * Add message listerner to message listener container with specified queue to listen/consume email
     * message.
     *
     * @param emailQueue queue for asynchronous event.
     * @return message listener container for email message.
     */
    @Bean
    SimpleMessageListenerContainer emailContainer(Queue emailQueue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueues(emailQueue);
        container.setMessageListener(emailListenerAdapter());
        return container;
    }
}
