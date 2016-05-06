package ck.panda.web.resource;

import java.time.ZonedDateTime;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Event;
import ck.panda.domain.entity.Event.EventType;
import ck.panda.service.EventNotificationService;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

@RestController
@RequestMapping("/api/events")
@Api(value = "events", description = "Operations with event", produces = "application/json")
public class EventController extends CRUDController<Event> implements ApiController  {

    /** Event notification service for tracking.*/
    @Autowired
    private EventNotificationService eventNotificationService;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

    @Override
    public List<Event> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Event.class);
        Page<Event> pageResponse = eventNotificationService.findAllByOwnerIdAndEventTypeAndActiveAndExceptArchive(Long.parseLong(tokenDetails.getTokenDetails("id")), page, EventType.ACTION, true, false);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Get the list of events by user and date range.
     *
     * @param startDate start date for event.
     * @param endDate end date for event.
     * @return event list.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "/list/date", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Event> findEventListByDateRange(@RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate, @RequestParam String sortBy,
            @RequestHeader(value = RANGE) String range, @RequestParam(required = false) Integer limit,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        ZonedDateTime startDateTime = ZonedDateTime.parse(startDate);
        ZonedDateTime endDateTime = ZonedDateTime.parse(endDate);
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Event.class);
        Page<Event> pageResponse = eventNotificationService.findAllByUserAndInBetweenEventDates(
                Long.parseLong(tokenDetails.getTokenDetails("id")), startDateTime, endDateTime, page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Get the list of events by user and date range.
     *
     * @param startDate start date for event.
     * @param endDate end date for event.
     * @return event list.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "/list/event", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Event> findEventListByType(@RequestParam("type") String eventType, @RequestParam String sortBy,
            @RequestHeader(value = RANGE) String range, @RequestParam(required = false) Integer limit,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Event.class);
        Page<Event> pageResponse = eventNotificationService.findAllByEventTypeAndActiveAndExceptArchive( EventType.valueOf(eventType), page, true, false);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Get the list of events by user and date range.
     *
     * @param startDate start date for event.
     * @param endDate end date for event.
     * @return event list.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "/events/rootadmin", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Event> findEventListByTypes(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Event.class);
        Page<Event> pageResponse = eventNotificationService.findEventListByRootAdmin(page, EventType.ACTION, true, false);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }
}
