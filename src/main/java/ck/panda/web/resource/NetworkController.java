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
import ck.panda.domain.entity.Network;
import ck.panda.service.NetworkService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Network controller.
 *
 */
@RestController
@RequestMapping("/api/guestnetwork")
@Api(value = "Network", description = "Operations with Networks", produces = "application/json")
public class NetworkController extends CRUDController<Network> implements ApiController {

    /** Service reference to Network. */
    @Autowired
    private NetworkService networkService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Network.", response = Network.class)
    @Override
    public Network create(@RequestBody Network network) throws Exception {
        network.setSyncFlag(true);
        return networkService.save(network);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Network.", response = Network.class)
    @Override
    public Network read(@PathVariable(PATH_ID) Long id) throws Exception {
        return networkService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Network.", response = Network.class)
    @Override
    public Network update(@RequestBody Network network, @PathVariable(PATH_ID) Long id) throws Exception {
        network.setSyncFlag(true);
        return networkService.update(network);
    }

    /**
     * Delete the Network.
     *
     * @param network reference of the Network.
     * @param id Network id.
     * @throws Exception error occurs.
     */
    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Network.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@RequestBody Network network, @PathVariable(PATH_ID) Long id) throws Exception {
        /** Doing Soft delete from the Network table. */
         network = networkService.find(id);
         network.setSyncFlag(true);
         networkService.softDelete(network);
    }

    @Override
    public List<Network> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Network.class);
        Page<Network> pageResponse = networkService.findAllByActive(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * list all network for instance.
     * @return projects
     * @param deptartment department
     * @throws Exception Exception
     */
      @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
      @ResponseStatus(HttpStatus.OK)
      @ResponseBody
      protected List<Network> findByDepartment(@RequestParam Long deptartment) throws Exception {
          return networkService.findByDepartmentAndNetworkIsActive(deptartment, true);
      }

}
