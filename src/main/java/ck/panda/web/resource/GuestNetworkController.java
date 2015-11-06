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
import ck.panda.domain.entity.GuestNetwork;
import ck.panda.service.GuestNetworkService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * GuestNetwork controller.
 *
 */
@RestController
@RequestMapping("/api/guestnetwork")
@Api(value = "GuestNetwork", description = "Operations with GuestNetworks", produces = "application/json")
public class GuestNetworkController extends CRUDController<GuestNetwork> implements ApiController {

    /** Service reference to Guest Network. */
    @Autowired
    private GuestNetworkService guestNetworkService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Network.", response = GuestNetwork.class)
    @Override
    public GuestNetwork create(@RequestBody GuestNetwork network) throws Exception {
        return guestNetworkService.save(network);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Network.", response = GuestNetwork.class)
    @Override
    public GuestNetwork read(@PathVariable(PATH_ID) Long id) throws Exception {
        return guestNetworkService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Network.", response = GuestNetwork.class)
    @Override
    public GuestNetwork update(@RequestBody GuestNetwork network, @PathVariable(PATH_ID) Long id) throws Exception {
        return guestNetworkService.update(network);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Network.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        guestNetworkService.delete(id);
    }

    @Override
    public List<GuestNetwork> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, GuestNetwork.class);
        Page<GuestNetwork> pageResponse = guestNetworkService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * list all network for instance.
     * @return projects
     * @throws Exception Exception
     */
      @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
      @ResponseStatus(HttpStatus.OK)
      @ResponseBody
      protected List<GuestNetwork> getSearch() throws Exception {
          return guestNetworkService.findAll();
      }

    @Override
    public void testMethod() throws Exception {
        guestNetworkService.findAll();
    }
}