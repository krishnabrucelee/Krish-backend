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
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Zone;
import ck.panda.service.ComputeOfferingCostService;
import ck.panda.service.ZoneService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Department controller.
 *
 */
@RestController
@RequestMapping("/api/zones")
@Api(value = "Zones", description = "Operations with zones", produces = "application/json")
public class ZoneController extends CRUDController<Zone> implements ApiController {

    /** Service reference to Department. */
    @Autowired
    private ComputeOfferingCostService computeOfferingcostService;

    /** Service reference to Zone. */
    @Autowired
    private ZoneService zoneService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Department.", response = Department.class)
    @Override
    public Zone create(@RequestBody Zone zone) throws Exception {
        return zoneService.save(zone);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Department.", response = Department.class)
    @Override
    public Zone read(@PathVariable(PATH_ID) Long id) throws Exception {
        return zoneService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Department.", response = Department.class)
    @Override
    public Zone update(@RequestBody Zone zone, @PathVariable(PATH_ID) Long id) throws Exception {
        return zoneService.update(zone);
    }

    /**
     * Soft deleting the department from the table.
     *
     * @param zone zone object.
     * @param id zone's id.
     * @throws Exception unhandled errors.
     */
    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Department.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@RequestBody Zone zone, @PathVariable(PATH_ID) Long id) throws Exception {
        /** Doing Soft delete from the department table. */
        zoneService.delete(zone);
    }

    @Override
    public List<Zone> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Zone.class);
        Page<Zone> pageResponse = zoneService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Get the list of zones.
     *
     * @return zone list.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Zone> zoneList() throws Exception {
        return zoneService.findAll();
    }
}
