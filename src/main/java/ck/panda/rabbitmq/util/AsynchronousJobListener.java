package ck.panda.rabbitmq.util;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import ck.panda.constants.CloudStackConstants;
import ck.panda.service.AsynchronousJobService;
import ck.panda.service.SyncService;
import ck.panda.util.CloudStackServer;
import ck.panda.util.infrastructure.AuthenticatedExternalWebService;
import ck.panda.util.infrastructure.externalwebservice.ExternalWebServiceStub;

/**
 * Asynchronous Job listener will listen and update resource data to our App DB
 * when an event handled directly in CS server.
 *
 */
public class AsynchronousJobListener implements MessageListener {

	/** Logger attribute. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AsynchronousJobListener.class);

	/** Sync service. */
	private SyncService syncService;

	/** Asynchronous service. */
	private AsynchronousJobService asyncService;

	/** Cloud stack server service. */
	private CloudStackServer cloudStackServer;

	/** Admin username. */
	private String backendAdminUsername;

	/** Admin role. */
	private String backendAdminRole;

	/**
	 * Inject SyncService.
	 *
	 * @param syncService
	 *            synchronous service object.
	 * @param asyncService
	 *            asynchronous Service object.
	 * @param cloudStackServer
	 *            cloudStackServer object.
	 * @param backendAdminUsername
	 *            default admin name.
	 * @param backendAdminRole
	 *            default admin role.
	 */
	public AsynchronousJobListener(SyncService syncService, AsynchronousJobService asyncService,
			CloudStackServer cloudStackServer, String backendAdminUsername, String backendAdminRole) {
		this.syncService = syncService;
		this.asyncService = asyncService;
		this.cloudStackServer = cloudStackServer;
		this.backendAdminUsername = backendAdminUsername;
		this.backendAdminRole = backendAdminRole;
	}

	@Override
	public void onMessage(Message message) {
		try {
			JSONObject instance = new JSONObject(new String(message.getBody()));
			this.handleStatusEvent(instance);
		} catch (Exception e) {
			LOGGER.debug("Error on convert action event message", e);
			e.printStackTrace();
		}
	}

	/**
	 * Handling all the CS events and updated those in our application DB
	 * according to the type of events.
	 *
	 * @param eventObject
	 *            event object.
	 * @throws Exception
	 *             exception.
	 */
	public void handleStatusEvent(JSONObject eventObject) throws Exception {
		if (eventObject.has(CloudStackConstants.CS_EVENT_STATUS)) {
			syncService.init(cloudStackServer);
			ExternalWebServiceStub externalWebService = new ExternalWebServiceStub();
			AuthenticatedExternalWebService authenticatedExternalWebService = new AuthenticatedExternalWebService(
					backendAdminUsername, null, AuthorityUtils.commaSeparatedStringToAuthorityList(backendAdminRole));
			authenticatedExternalWebService.setExternalWebService(externalWebService);
			SecurityContextHolder.getContext().setAuthentication(authenticatedExternalWebService);
			asyncService.syncResourceStatus(eventObject);
		}
	}
}
