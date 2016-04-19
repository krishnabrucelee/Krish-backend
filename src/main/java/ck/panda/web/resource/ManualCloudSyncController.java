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
import ck.panda.domain.entity.ManualCloudSync;
import ck.panda.service.ManualCloudSyncService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/** Manual cloud sync controller. */
@RestController
@RequestMapping("/api/manualCloudSync")
@Api(value = "manualCloudSync", description = "Operations with manual cloud sync", produces = "application/json")
public class ManualCloudSyncController extends CRUDController<ManualCloudSync> implements ApiController {

    /** Service reference to manual cloud sync. */
    @Autowired
    private ManualCloudSyncService manualCloudSyncService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new manual cloud sync.", response = ManualCloudSync.class)
    @Override
    public ManualCloudSync create(@RequestBody ManualCloudSync manualCloudSync) throws Exception {
        return manualCloudSyncService.save(manualCloudSync);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing manual cloud sync.", response = ManualCloudSync.class)
    @Override
    public ManualCloudSync read(@PathVariable(PATH_ID) Long id) throws Exception {
        return manualCloudSyncService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing manual cloud sync.", response = ManualCloudSync.class)
    @Override
    public ManualCloudSync update(@RequestBody ManualCloudSync manualCloudSync, @PathVariable(PATH_ID) Long id) throws Exception {
        return manualCloudSyncService.update(manualCloudSync);
    }

    @Override
    public List<ManualCloudSync> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, ManualCloudSync.class);
        Page<ManualCloudSync> pageResponse = manualCloudSyncService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

}
