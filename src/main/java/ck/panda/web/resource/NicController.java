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
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.Nic;
import ck.panda.service.NicService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Nic Controller.
 *
 */
@RestController
@RequestMapping("/api/nics")
@Api(value = "Nics", description = "Operations with Nics", produces = "application/json")
public class NicController extends CRUDController<Nic> implements ApiController {

    /** Service reference to Nic. */
    @Autowired
    private NicService nicOfferingService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Nic.", response = Nic.class)
    @Override
    public Nic create(@RequestBody Nic nic) throws Exception {
        nic.setSyncFlag(true);
        return nicOfferingService.save(nic);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Nic.", response = Nic.class)
    @Override
    public Nic read(@PathVariable(PATH_ID) Long id) throws Exception {
        return nicOfferingService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Nic.", response = Nic.class)
    @Override
    public Nic update(@RequestBody Nic nic, @PathVariable(PATH_ID) Long id) throws Exception {
        nic.setSyncFlag(true);
        return nicOfferingService.update(nic);
    }

    @Override
    public List<Nic> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Nic.class);
        Page<Nic> pageResponse = nicOfferingService.findAllByActive(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * list all nic service for instance.
     *
     * @return nic service
     * @throws Exception error
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<Nic> getSearch() throws Exception {
        return nicOfferingService.findAll();
    }

    /**
     * Delete the Nic.
     *
     * @param nic reference of the network interface card.
     * @param id the nic.
     * @throws Exception error occurs.
     */
    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing nic.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@RequestBody Nic nic, @PathVariable(PATH_ID) Long id) throws Exception {
        /** Doing Soft delete from the nic table. */
        nic.setSyncFlag(true);
        nicOfferingService.softDelete(nic);
    }

    /**
     * List by instance attached to nic.
     *
     * @param instanceId Nic
     * @return nic by instances
     * @throws Exception exception
     */
    @RequestMapping(value = "listbyinstances", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Nic> listByInstance(@RequestParam("instanceid") Long instanceId) throws Exception {
        System.out.println(instanceId);
        return nicOfferingService.findByInstance(instanceId);
    }

    @RequestMapping(value = "acquire/{id}", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected Nic acquireSecondaryIP(@RequestBody Nic nic, @PathVariable(PATH_ID) Long id) throws Exception {
        return nicOfferingService.acquireSecondaryIP(nic);
    }

    @RequestMapping(value = "/release", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Nic ReleaseSecondaryIp(@RequestBody Nic nic, @RequestParam("VmIpaddress") Long vmIpaddressId) throws Exception {
        return nicOfferingService.releaseSecondaryIP(nic, vmIpaddressId);
    }

 }

