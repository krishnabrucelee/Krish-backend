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
import ck.panda.domain.entity.StorageOffering;
import ck.panda.service.StorageOfferingService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 *
 * @author Assistanz
 *
 */
@RestController
@RequestMapping("/api/storages")
@Api(value = "StorageOfferings", description = "Operations with StorageOfferings", produces = "application/json")
public class StorageOfferingController extends CRUDController<StorageOffering>
    implements ApiController {

  /** Service reference to StorageOffering. */
  @Autowired
  private StorageOfferingService storageOfferingService;

  @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new StorageOffering.", response = StorageOffering.class)
  @Override
  public StorageOffering create(@RequestBody StorageOffering storage) throws Exception {
    return storageOfferingService.save(storage);
  }

  @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing StorageOffering.", response = StorageOffering.class)
  @Override
  public StorageOffering read(@PathVariable(PATH_ID) Long id) throws Exception {
    return storageOfferingService.find(id);
  }

  @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing StorageOffering.", response = StorageOffering.class)
  @Override
  public StorageOffering update(@RequestBody StorageOffering storage,
      @PathVariable(PATH_ID) Long id) throws Exception {
    return storageOfferingService.update(storage);
  }

  @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing StorageOffering.")
  @Override
  public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
    storageOfferingService.delete(id);
  }

  @Override
  public List<StorageOffering> list(@RequestParam String sortBy,
      @RequestHeader(value = RANGE) String range, @RequestParam Integer limit,
      HttpServletRequest request, HttpServletResponse response) throws Exception {
    PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, StorageOffering.class);
    Page<StorageOffering> pageResponse = storageOfferingService.findAll(page);
    System.out.println(pageResponse);
    response.setHeader(GenericConstants.CONTENT_RANGE_HEADER,
        page.getPageHeaderValue(pageResponse));
    return pageResponse.getContent();
  }

@Override
public void testMethod() throws Exception {
    // TODO Auto-generated method stub

}
}
