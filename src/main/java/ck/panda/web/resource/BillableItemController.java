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
import ck.panda.domain.entity.BillableItem;
import ck.panda.service.BillableItemService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * BillableItem controller.
 *
 */
@RestController
@RequestMapping("/api/billableItems")
@Api(value = "Domains", description = "Operations with billable items", produces = "application/json")
public class BillableItemController extends CRUDController<BillableItem> implements ApiController {

    /** Service reference to Domain. */
    @Autowired
    private BillableItemService billableItemService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new billable items.", response = BillableItem.class)
    @Override
    public BillableItem create(@RequestBody BillableItem billableItem) throws Exception {
        billableItem.setIsActive(true);
        return billableItemService.save(billableItem);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing billable items.", response = BillableItem.class)
    @Override
    public BillableItem read(@PathVariable(PATH_ID) Long id) throws Exception {
        return billableItemService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing billable items.", response = BillableItem.class)
    @Override
    public BillableItem update(@RequestBody BillableItem billableItem, @PathVariable(PATH_ID) Long id) throws Exception {
        return billableItemService.update(billableItem);
    }

    @Override
    public List<BillableItem> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, BillableItem.class);
        Page<BillableItem> pageResponse = billableItemService.findAllByIsActive(page, true);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * List all billable items with active status.
     *
     * @return projects
     * @throws Exception error
     */
      @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
      @ResponseStatus(HttpStatus.OK)
      @ResponseBody
      protected List<BillableItem> getSearch() throws Exception {
          return billableItemService.findAllByIsActive(true);
      }


      /**
       * Delete the Billable item.
       *
       * @param billableItem reference of the item.
       * @param id billable item id.
       * @throws Exception error occurs.
       */
      @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Tax.")
      @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = { MediaType.APPLICATION_JSON_VALUE })
      @ResponseStatus(HttpStatus.NO_CONTENT)
      public void softDelete(@RequestBody BillableItem billableItem, @PathVariable(PATH_ID) Long id) throws Exception {
          /** Doing Soft delete from the billable_item table. */
          billableItem.setIsActive(false);
          billableItemService.softDelete(billableItem);
      }
}
