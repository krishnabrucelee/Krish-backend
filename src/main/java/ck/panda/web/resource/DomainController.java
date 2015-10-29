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
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.GuestNetwork;
import ck.panda.service.DomainService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Domain controller.
 *
 */
@RestController
@RequestMapping("/api/domains")
@Api(value = "Domains", description = "Operations with domains", produces = "application/json")
public class DomainController extends CRUDController<Domain> implements ApiController {

    /** Service reference to Domain. */
    @Autowired
    private DomainService domainService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new domain.", response = Domain.class)
    @Override
    public Domain create(@RequestBody Domain domain) throws Exception {
        return domainService.save(domain);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Domain.", response = Domain.class)
    @Override
    public Domain read(@PathVariable(PATH_ID) Long id) throws Exception {
        return domainService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Domain.", response = Domain.class)
    @Override
    public Domain update(@RequestBody Domain domain, @PathVariable(PATH_ID) Long id) throws Exception {
        return domainService.update(domain);
    }

    @Override
    public List<Domain> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Domain.class);
        Page<Domain> pageResponse = domainService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * list all domain.
     * @return projects
     * @throws Exception
     */
  	@RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
  	@ResponseStatus(HttpStatus.OK)
  	@ResponseBody
  	protected List<Domain> getSearch() throws Exception {
  		return domainService.findAll();
  	}

    @Override
    public void testMethod() throws Exception {
        // TODO Auto-generated method stub

    }

}