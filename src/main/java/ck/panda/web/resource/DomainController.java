package ck.panda.web.resource;

import java.util.ArrayList;
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
import ck.panda.domain.entity.User;
import ck.panda.domain.repository.jpa.DomainRepository;
import ck.panda.service.DomainService;
import ck.panda.util.TokenDetails;
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

    /** Inject domain service business logic. */
    @Autowired
    private DomainRepository domainRepository;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new domain.", response = Domain.class)
    @Override
    public Domain create(@RequestBody Domain domain) throws Exception {
        domain.setSyncFlag(true);
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
        domain.setSyncFlag(true);
        return domainService.update(domain);
    }

    @Override
    public List<Domain> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Domain.class);
        Page<Domain> pageResponse = domainService.findAllByActive(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * list all domain.
     *
     * @return projects
     * @throws Exception error
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<Domain> getSearch() throws Exception {
        Domain domain = domainService.find(Long.parseLong(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && !domain.getName().equals("ROOT")) {
            List<Domain> domainList = new ArrayList<Domain>();
            domainList.add(domain);
            return domainList;
        }
        return domainService.findAll();
    }

    /**
     * Delete the Domain.
     *
     * @param domain id reference of the domain.
     * @param id domain id.
     * @throws Exception error occurs.
     */
    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Domain.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@RequestBody Domain domain, @PathVariable(PATH_ID) Long id) throws Exception {
        /** Doing Soft delete from the department table. */
        domain.setSyncFlag(true);
        domainService.softDelete(domain);
    }

    /**
     * Get all domain list by quick search.
     *
     * @param sortBy asc/desc
     * @param searchText search text.
     * @param range pagination range.
     * @param limit per page limit.
     * @param request page request.
     * @param response response content.
     * @return domain list.
     * @throws Exception unhandled exception.
     */
    @RequestMapping(value = "/listByFilter", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Domain> listBySearchFilter(@RequestParam String sortBy, @RequestParam String searchText,
            @RequestHeader(value = RANGE) String range, @RequestParam(required = false) Integer limit,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Domain.class);
        Page<Domain> pageResponse = domainService.findDomainBySearchText(page, searchText);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }
    
    
    /**
     * Update domain to suspended state and deactive all the resources associated to the users.
     *
     * @param user reference of the user.
     * @param id user id.
     * @return user reference.
     * @throws Exception if error.
     */
    @RequestMapping(value = "suspend/{id}", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Domain updateSuspended(@RequestBody Domain domain, @PathVariable(PATH_ID) Long id) throws Exception {
        return domainService.updateSuspended(domain);
    }
}
