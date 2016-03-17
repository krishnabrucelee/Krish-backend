package ck.panda.web.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ck.panda.util.web.ApiController;

/**
 * Authentication controller.
 *
 */
@RestController
public class AuthenticationController implements ApiController {
	/** Simple message template reference. */
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Authenticate method.
     *
     * @return string
     */
    @RequestMapping(value = AUTHENTICATE_URL, method = RequestMethod.POST)
    public String authenticate() {
        return "This is just for in-code-documentation purposes and Rest API reference documentation."
                + "Servlet will never get to this point as Http requests are processed by AuthenticationFilter."
                + "Nonetheless to authenticate Domain User POST request with X-Auth-Username and X-Auth-Password headers "
                + "is mandatory to this URL. If username and password are correct valid token will be returned (just json string in response) "
                + "This token must be present in X-Auth-Token header in all requests for all other URLs, including logout."
                + "Authentication can be issued multiple times and each call results in new ßticket.";
    }

    @MessageMapping("/cloud/event.message")
	@SendTo("/topic/action.event/")
	public String eventMessage(@Payload String message) {
		return message;
	}

	@MessageMapping("/socket/ws")
	public void filtereventMessage() {
		simpMessagingTemplate.convertAndSend("/topic/action.event/", "hi");
	}
}
