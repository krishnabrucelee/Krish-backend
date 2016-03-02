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
import ck.panda.domain.entity.VmSnapshot;
import ck.panda.service.VmSnapshotService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Virtual machine snapshot controller.
 */
@RestController
@RequestMapping("/api/vmsnapshot")
@Api(value = "vmsnapshots", description = "Operations with vmsnapshot", produces = "application/json")
public class VMSnapshotController extends CRUDController<VmSnapshot> implements ApiController {

    /** Service reference to Snapshot. */
    @Autowired
    private VmSnapshotService snapshotService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new vmsnapshot.", response = VmSnapshot.class)
    @Override
    public VmSnapshot create(@RequestBody VmSnapshot snapshot) throws Exception {
        snapshot.setSyncFlag(true);
        return snapshotService.save(snapshot);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing vmsnapshot.", response = VmSnapshot.class)
    @Override
    public VmSnapshot read(@PathVariable(PATH_ID) Long id) throws Exception {
        return snapshotService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing vmsnapshot.", response = VmSnapshot.class)
    @Override
    public VmSnapshot update(@RequestBody VmSnapshot snapshot, @PathVariable(PATH_ID) Long id) throws Exception {
        return snapshotService.update(snapshot);
    }

    @Override
    public List<VmSnapshot> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, VmSnapshot.class);
        Page<VmSnapshot> pageResponse = snapshotService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * get all instances snapshot.
     *
     * @throws Exception if error occurs.
     * @return list of snapshots.
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<VmSnapshot> getSearch() throws Exception {
        return snapshotService.findAll();
    }

    /**
     * get all instance's snapshot.
     *
     * @param vmid vm id.
     * @throws Exception if error occurs.
     * @return list of snapshots.
     */
    @RequestMapping(value = "instance/list", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<VmSnapshot> getSearch(@RequestParam("vmId") Long vmid) throws Exception {
        return snapshotService.findByVmInstance(vmid, false);
    }

    /**
     * get instance snapshot with latest state update.
     *
     * @param vm vm snapshot uuid.
     * @param event vm event type.
     * @throws Exception if error occurs.
     * @return list of snapshots.
     */
    @RequestMapping(value = "event", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected VmSnapshot handleVmSnapshotEvent(@RequestParam("vm") String vm, @RequestParam("event") String event)
            throws Exception {
        return snapshotService.vmSnapshotEventHandle(vm, event);
    }
}
