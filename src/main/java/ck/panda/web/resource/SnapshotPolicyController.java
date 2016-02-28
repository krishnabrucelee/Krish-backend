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
import ck.panda.domain.entity.SnapshotPolicy;
import ck.panda.service.SnapshotPolicyService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * SnapshotPolicyService controller.
 *
 */
@RestController
@RequestMapping("/api/snapshotpolicies")
@Api(value = "Snapshotpolicies", description = "Operations with SnapshotPolicy", produces = "application/json")
public class SnapshotPolicyController extends CRUDController<SnapshotPolicy> implements ApiController {


    /** Service reference to SnapshotPolicy. */
    @Autowired
    private SnapshotPolicyService snapPolicyService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new SnapshotPolicy.", response = Department.class)
    @Override
    public SnapshotPolicy create(@RequestBody SnapshotPolicy snapshot) throws Exception {
        snapshot.setSyncFlag(true);
        return snapPolicyService.save(snapshot);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing SnapshotPolicy.", response = Department.class)
    @Override
    public SnapshotPolicy read(@PathVariable(PATH_ID) Long id) throws Exception {
        return snapPolicyService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing SnapshotPolicy.", response = Department.class)
    @Override
    public SnapshotPolicy update(@RequestBody SnapshotPolicy snapshot, @PathVariable(PATH_ID) Long id) throws Exception {
        return snapPolicyService.update(snapshot);
    }

    /**
     * Soft deleting the department from the table.
     *
     * @param snapshot snapshot object.
     * @param id snapshot's id.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        /** Doing Soft delete from the department table. */
        snapPolicyService.softDelete(id);
    }

    @Override
    public List<SnapshotPolicy> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, SnapshotPolicy.class);
        Page<SnapshotPolicy> pageResponse = snapPolicyService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Get the list of zones.
     *
     * @return snapshot list.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<SnapshotPolicy> snapshotPolicyList() throws Exception {
        return snapPolicyService.findAll();
    }

    @RequestMapping(value = "listbyvolume", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<SnapshotPolicy> listByVolume(@RequestParam("volumeid") Long volumeId) throws Exception {
        return snapPolicyService.findAllByVolumeAndIsActive(volumeId, true);
   }
}
