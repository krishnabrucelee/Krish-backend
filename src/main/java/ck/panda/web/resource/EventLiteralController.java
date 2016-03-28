package ck.panda.web.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.EventLiterals;
import ck.panda.service.EventLiteralsService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * EventLiterals controller.
 *
 */
@RestController
@RequestMapping("/api/literals")
@Api(value = "EventLiterals", description = "Operations with domains", produces = "application/json")
public class EventLiteralController extends CRUDController<EventLiterals> implements ApiController {

    /** Service reference to EventLiterals. */
    @Autowired
    private EventLiteralsService literalService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new domain.", response = EventLiterals.class)
    @Override
    public EventLiterals create(@RequestBody EventLiterals email) throws Exception {
        return literalService.save(email);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing EventLiterals.", response = EventLiterals.class)
    @Override
    public EventLiterals read(@PathVariable(PATH_ID) Long id) throws Exception {
        return literalService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing EventLiterals.", response = EventLiterals.class)
    @Override
    public EventLiterals update(@RequestBody EventLiterals domain, @PathVariable(PATH_ID) Long id) throws Exception {
        return literalService.update(domain);
    }

    @Override
    public List<EventLiterals> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, EventLiterals.class);
        Page<EventLiterals> pageResponse = literalService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * List all event literals.
     *
     * @return event literals.
     * @throws Exception if error occurs.
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<EventLiterals> listAll() throws Exception {
        return literalService.findByIsActive(true);
    }

    /**
     * List event literals by event name.
     *
     * @param eventName to be choosed.
     * @return event literals.
     * @throws Exception if error occurs.
     */
    @RequestMapping(value = "/listbyevent", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<EventLiterals> findByEventName(@RequestParam String eventName) throws Exception {
        return literalService.findByType(eventName);
    }

 }
