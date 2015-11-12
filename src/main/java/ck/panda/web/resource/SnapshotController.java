package ck.panda.web.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Snapshot;
import ck.panda.service.SnapshotService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

@RestController
@RequestMapping("/api/snapshot")
@Api(value = "Domains", description = "Operations with snapshot", produces = "application/json")
public class SnapshotController extends CRUDController<Snapshot> implements ApiController {

    /** Service reference to Snapshot. */
    @Autowired
    private SnapshotService snapshotService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new snapshot.", response = Domain.class)
    @Override
    public Snapshot create(@RequestBody Snapshot snapshot) throws Exception {
        return snapshotService.save(snapshot);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing snapshot.", response = Domain.class)
    @Override
    public Snapshot read(@PathVariable(PATH_ID) Long id) throws Exception {
        return snapshotService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing snapshot.", response = Domain.class)
    @Override
    public Snapshot update(@RequestBody Snapshot snapshot, @PathVariable(PATH_ID) Long id) throws Exception {
        return snapshotService.update(snapshot);
    }

    @Override
    public List<Snapshot> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Domain.class);
        Page<Snapshot> pageResponse = snapshotService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    @Override
    public void testMethod() throws Exception {
        // TODO Auto-generated method stub

    }
}