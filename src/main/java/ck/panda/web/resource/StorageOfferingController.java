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
import ck.panda.domain.entity.StorageOffering;
import ck.panda.service.StorageOfferingService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Storage Offering Controller.
 *
 */
@RestController
@RequestMapping("/api/storages")
@Api(value = "StorageOfferings", description = "Operations with StorageOfferings", produces = "application/json")
public class StorageOfferingController extends CRUDController<StorageOffering> implements ApiController {

    /** Service reference to StorageOffering. */
    @Autowired
    private StorageOfferingService storageOfferingService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new StorageOffering.", response = StorageOffering.class)
    @Override
    public StorageOffering create(@RequestBody StorageOffering storage) throws Exception {
        storage.setIsSyncFlag(true);
        return storageOfferingService.save(storage);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing StorageOffering.", response = StorageOffering.class)
    @Override
    public StorageOffering read(@PathVariable(PATH_ID) Long id) throws Exception {
        return storageOfferingService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing StorageOffering.", response = StorageOffering.class)
    @Override
    public StorageOffering update(@RequestBody StorageOffering storage, @PathVariable(PATH_ID) Long id)
            throws Exception {
        storage.setIsSyncFlag(true);
        return storageOfferingService.update(storage);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing StorageOffering.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        storageOfferingService.delete(id);
    }

    @Override
    public List<StorageOffering> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, StorageOffering.class);
        Page<StorageOffering> pageResponse = storageOfferingService.findAll(page);
        System.out.println(pageResponse);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * list all storage service for instance.
     *
     * @return storage service
     * @throws Exception error
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<StorageOffering> getSearch() throws Exception {
        return storageOfferingService.findAll();
    }

    /**
     * list all storage service for instance.
     *
     * @return storage service
     * @throws Exception error
     */
    @RequestMapping(value = "storagesort", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<StorageOffering> getFindByTags(@RequestParam String tags) throws Exception {
        return storageOfferingService.findAllByTags(tags);
    }

    /**
     * list all storage service for instance.
     *
     * @return storage service
     * @throws Exception error
     */
    @RequestMapping(value = "storagetags", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<String> getStorageTags() throws Exception {
        return storageOfferingService.findTags(true);
    }
}
