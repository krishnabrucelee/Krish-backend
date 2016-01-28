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
import ck.panda.domain.entity.Item;
import ck.panda.service.ItemService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Item controller.
 *
 */
@RestController
@RequestMapping("/api/billableItems")
@Api(value = "Domains", description = "Operations with items", produces = "application/json")
public class ItemController extends CRUDController<Item> implements ApiController {

    /** Service reference to Domain. */
    @Autowired
    private ItemService itemService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new item.", response = Item.class)
    @Override
    public Item create(@RequestBody Item item) throws Exception {
        item.setIsActive(true);
        return itemService.save(item);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing items.", response = Item.class)
    @Override
    public Item read(@PathVariable(PATH_ID) Long id) throws Exception {
        return itemService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing items.", response = Item.class)
    @Override
    public Item update(@RequestBody Item item, @PathVariable(PATH_ID) Long id) throws Exception {
        return itemService.update(item);
    }

    @Override
    public List<Item> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Item.class);
        Page<Item> pageResponse = itemService.findAllByIsActive(page, true);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * List all items with active status.
     *
     * @return projects
     * @throws Exception error
     */
      @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
      @ResponseStatus(HttpStatus.OK)
      @ResponseBody
      protected List<Item> getSearch() throws Exception {
          return itemService.findAllByIsActive(true);
      }

      /**
       * Delete the item.
       *
       * @param item reference of the item.
       * @param id item id.
       * @throws Exception error occurs.
       */
      @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Tax.")
      @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = { MediaType.APPLICATION_JSON_VALUE })
      @ResponseStatus(HttpStatus.NO_CONTENT)
      public void softDelete(@RequestBody Item item, @PathVariable(PATH_ID) Long id) throws Exception {
          /** Doing Soft delete from the item table. */
          item.setIsActive(false);
          itemService.update(item);
      }
}
