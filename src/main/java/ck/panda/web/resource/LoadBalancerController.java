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
import ck.panda.domain.entity.FirewallRules;
import ck.panda.domain.entity.LoadBalancerRule;
import ck.panda.domain.entity.Volume;
import ck.panda.service.LoadBalancerService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Load Balancer Controller.
 *
 */
@RestController
@RequestMapping("/api/loadBalancer")
@Api(value = "LoadBalancer", description = "Operations with LoadBalancer", produces = "application/json")
public class LoadBalancerController extends CRUDController<LoadBalancerRule> implements ApiController {

    /** Service reference to Load Balancer. */
    @Autowired
    private LoadBalancerService loadBalancerService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Load Balancer.", response = LoadBalancerRule.class)
    @Override
    public LoadBalancerRule create(@RequestBody LoadBalancerRule loadBalancer) throws Exception {
        loadBalancer.setSyncFlag(true);
        return loadBalancerService.save(loadBalancer);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Load Balancer.", response = LoadBalancerRule.class)
    @Override
    public LoadBalancerRule read(@PathVariable(PATH_ID) Long id) throws Exception {
        return loadBalancerService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Load Balancer.", response = LoadBalancerRule.class)
    @Override
    public LoadBalancerRule update(@RequestBody LoadBalancerRule loadBalancer, @PathVariable(PATH_ID) Long id)
            throws Exception {
        loadBalancer.setSyncFlag(true);
        return loadBalancerService.update(loadBalancer);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Load Balancer.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        LoadBalancerRule loadBalancer = loadBalancerService.find(id);
        loadBalancer.setSyncFlag(true);
        loadBalancerService.softDelete(loadBalancer);
    }

    @Override
    public List<LoadBalancerRule> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, FirewallRules.class);
        Page<LoadBalancerRule> pageResponse = loadBalancerService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

   /**
    * list all Volumes for instance.
    *
    * @return Volume service
     * @throws Exception error
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
   @ResponseBody
    protected List<LoadBalancerRule> getSearch(@RequestParam("ipAddressId") Long ipAddressId) throws Exception {
        return loadBalancerService.findByIpaddress(ipAddressId, true);
    }

}

