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
import ck.panda.domain.entity.NetworkOffering;
import ck.panda.domain.entity.Project;
import ck.panda.service.NetworkOfferingService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * NetworkOffering controller.
 *
 */
@RestController
@RequestMapping("/api/networkoffer")
@Api(value = "NetworkOffer", description = "Operations with NetworkOffer", produces = "application/json")
public class NetworkOfferingController extends CRUDController<NetworkOffering> implements ApiController {

    /** Service reference to Network Offering. */
    @Autowired
    private NetworkOfferingService networkOffer;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Network Offer.", response = NetworkOffering.class)
    @Override
    public NetworkOffering create(@RequestBody NetworkOffering network) throws Exception {
        return networkOffer.save(network);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Network Offer.", response = NetworkOffering.class)
    @Override
    public NetworkOffering read(@PathVariable(PATH_ID) Long id) throws Exception {
        return networkOffer.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Network Offer.", response = NetworkOffering.class)
    @Override
    public NetworkOffering update(@RequestBody NetworkOffering network, @PathVariable(PATH_ID) Long id) throws Exception {
        return networkOffer.update(network);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Network.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        networkOffer.delete(id);
    }

    @Override
    public List<NetworkOffering> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, NetworkOffering.class);
        Page<NetworkOffering> pageResponse = networkOffer.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * list all projects for instance.
     * @return projects
     * @throws Exception Exception
     */
      @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
      @ResponseStatus(HttpStatus.OK)
      @ResponseBody
      protected List<NetworkOffering> getSearch() throws Exception {
          return networkOffer.findAll();
      }

    @Override
    public void testMethod() throws Exception {
        networkOffer.findAll();
    }
}