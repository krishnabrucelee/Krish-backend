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
import ck.panda.domain.entity.MiscellaneousCost;
import ck.panda.service.MiscellaneousCostService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * MiscellaneousCost controller.
 *
 */
@RestController
@RequestMapping("/api/miscellaneous")
@Api(value = "Miscellaneouscost", description = "Operations with miscellneous cost", produces = "application/json")
public class MiscellaneousController extends CRUDController<MiscellaneousCost> implements ApiController {

    /** Service reference to MiscellaneousCost. */
    @Autowired
    private MiscellaneousCostService costService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new cost.", response = MiscellaneousCost.class)
    @Override
    public MiscellaneousCost create(@RequestBody MiscellaneousCost cost) throws Exception {
        return costService.save(cost);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing MiscellaneousCost.", response = MiscellaneousCost.class)
    @Override
    public MiscellaneousCost read(@PathVariable(PATH_ID) Long id) throws Exception {
        return costService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing MiscellaneousCost.", response = MiscellaneousCost.class)
    @Override
    public MiscellaneousCost update(@RequestBody MiscellaneousCost cost, @PathVariable(PATH_ID) Long id) throws Exception {
        return costService.update(cost);
    }

    @Override
    public List<MiscellaneousCost> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, MiscellaneousCost.class);
        Page<MiscellaneousCost> pageResponse = costService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * list all cost.
     *
     * @return projects
     * @throws Exception error
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<MiscellaneousCost> getSearch() throws Exception {
        return costService.findAllByIsActive(true);
    }

    /**
     * Delete the MiscellaneousCost.
     *
     * @param cost id reference of the cost.
     * @param id cost id.
     * @throws Exception error occurs.
     */
    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing MiscellaneousCost.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@RequestBody MiscellaneousCost cost, @PathVariable(PATH_ID) Long id) throws Exception {
        /** Doing Soft delete from the department table. */
        costService.delete(cost);
    }
}
