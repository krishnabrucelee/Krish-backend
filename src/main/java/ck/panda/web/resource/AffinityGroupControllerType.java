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
import ck.panda.domain.entity.AffinityGroupType;
import ck.panda.service.AffinityGroupTypeService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/** Affinity group type controller. */
@RestController
@RequestMapping("/api/affinityGroupType")
@Api(value = "AffinityGroupType", description = "Operations with affinity group type", produces = "application/json")
public class AffinityGroupControllerType extends CRUDController<AffinityGroupType> implements ApiController {

    /** Service reference to affinity group type. */
    @Autowired
    private AffinityGroupTypeService affinityGroupTypeService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new affinity group type.", response = AffinityGroupType.class)
    @Override
    public AffinityGroupType create(@RequestBody AffinityGroupType affinityGroupType) throws Exception {
        return affinityGroupTypeService.save(affinityGroupType);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing affinity group type.", response = AffinityGroupType.class)
    @Override
    public AffinityGroupType read(@PathVariable(PATH_ID) Long id) throws Exception {
        return affinityGroupTypeService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing affinity group type.", response = AffinityGroupType.class)
    @Override
    public AffinityGroupType update(@RequestBody AffinityGroupType affinityGroupType, @PathVariable(PATH_ID) Long id) throws Exception {
        return affinityGroupTypeService.update(affinityGroupType);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing affinity group type.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        AffinityGroupType affinityGroupType = affinityGroupTypeService.find(id);
        affinityGroupTypeService.delete(affinityGroupType);
    }

    @Override
    public List<AffinityGroupType> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, AffinityGroupType.class);
        Page<AffinityGroupType> pageResponse = affinityGroupTypeService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Find the list of active affinity group type.
     *
     * @return  affinity group type list
     * @throws Exception error occurs
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<AffinityGroupType> listall() throws Exception {
        return affinityGroupTypeService.findAll();
    }

}
