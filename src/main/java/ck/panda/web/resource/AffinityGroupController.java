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
import ck.panda.domain.entity.AffinityGroup;
import ck.panda.service.AffinityGroupService;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/** Affinity group controller. */
@RestController
@RequestMapping("/api/affinityGroup")
@Api(value = "AffinityGroup", description = "Operations with affinity group", produces = "application/json")
public class AffinityGroupController extends CRUDController<AffinityGroup> implements ApiController {

    /** Service reference to affinity group. */
    @Autowired
    private AffinityGroupService affinityGroupService;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new affinity group.", response = AffinityGroup.class)
    @Override
    public AffinityGroup create(@RequestBody AffinityGroup affinityGroup) throws Exception {
        affinityGroup.setIsSyncFlag(true);
        return affinityGroupService.save(affinityGroup, Long.parseLong(tokenDetails.getTokenDetails("id")));
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing affinity group.", response = AffinityGroup.class)
    @Override
    public AffinityGroup read(@PathVariable(PATH_ID) Long id) throws Exception {
        return affinityGroupService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing affinity group.", response = AffinityGroup.class)
    @Override
    public AffinityGroup update(@RequestBody AffinityGroup affinityGroup, @PathVariable(PATH_ID) Long id) throws Exception {
        affinityGroup.setIsSyncFlag(true);
        return affinityGroupService.update(affinityGroup);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing affinity group.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        AffinityGroup affinityGroup = affinityGroupService.find(id);
        affinityGroup.setIsSyncFlag(true);
        affinityGroupService.delete(affinityGroup);
    }

    @Override
    public List<AffinityGroup> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, AffinityGroup.class);
        Page<AffinityGroup> pageResponse = affinityGroupService.findAll(page, Long.parseLong(tokenDetails.getTokenDetails("id")));
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Find the list of active affinity group.
     *
     * @return  affinity group list
     * @throws Exception error occurs
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<AffinityGroup> listall() throws Exception {
        return affinityGroupService.findAll();
    }

    /**
     * Get group list by department id.
     *
     * @param departmentId department id
     * @return affinity group list
     * @throws Exception error occurs
     */
    @RequestMapping(value = "/groupList/{departmentId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<AffinityGroup> groupList(@PathVariable("departmentId") Long departmentId) throws Exception {
         return affinityGroupService.findByDepartment(departmentId);
    }

    /**
     * Get all affinity group list by domain.
     *
     * @param sortBy asc/desc
     * @param domainId domain id of affinity group.
     * @param range pagination range.
     * @param limit per page limit.
     * @param request page request.
     * @param response response content.
     * @return affinity group list.
     * @throws Exception unhandled exception.
     */
    @RequestMapping(value = "/listByDomain", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<AffinityGroup> listAffinityGroupByDomainId(@RequestParam String sortBy, @RequestParam Long domainId,
            @RequestParam String searchText, @RequestHeader(value = RANGE) String range, @RequestParam(required = false) Integer limit,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, AffinityGroup.class);
        Page<AffinityGroup> pageResponse = affinityGroupService.findAllByDomainId(domainId, searchText, page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

}
