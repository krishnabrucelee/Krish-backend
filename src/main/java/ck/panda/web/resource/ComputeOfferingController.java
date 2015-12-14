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
import ck.panda.domain.entity.ComputeOffering;
import ck.panda.service.ComputeOfferingService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 *
 * Compute Offering Controller.
 */

@RestController
@RequestMapping("/api/computes")
@Api(value = "ComputerOffering", description = "Operations with compute offerings", produces = "application/json")
public class ComputeOfferingController extends CRUDController<ComputeOffering> implements ApiController {

    /** Service reference to ComputeOffering. */
    @Autowired
    private ComputeOfferingService computeService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new ComputeOffering.", response = ComputeOffering.class)
    @Override
    public ComputeOffering create(@RequestBody ComputeOffering compute) throws Exception {
        compute.setIsSyncFlag(true);
        return computeService.save(compute);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing ComputeOffering.", response = ComputeOffering.class)
    @Override
    public ComputeOffering read(@PathVariable(PATH_ID) Long id) throws Exception {
        System.out.println(id+"======================================");
        return computeService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing ComputeOffering.", response = ComputeOffering.class)
    @Override
    public ComputeOffering update(@RequestBody ComputeOffering compute, @PathVariable(PATH_ID) Long id) throws Exception {
        compute.setIsSyncFlag(true);
        return computeService.update(compute);
    }

    /**
     * Delete the compute offering.
     *
     * @param compute reference of the compute offering.
     * @param id compute offering id.
     * @throws Exception error occurs.
     */
    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing compute Offering.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@RequestBody ComputeOffering compute, @PathVariable(PATH_ID) Long id) throws Exception {
        /** Doing Soft delete from the compute offering table. */
         compute = computeService.find(id);
         compute.setIsSyncFlag(true);
         computeService.softDelete(compute);
    }

    /**
     * list all compute offing and offer Id for instance.
     * @return compute offing.
     * @throws Exception
     */
      @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
      @ResponseStatus(HttpStatus.OK)
      @ResponseBody
      protected List<ComputeOffering> getSearch() throws Exception {
          return computeService.findByIsActive(true);
      }

    @Override
    public List<ComputeOffering> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, ComputeOffering.class);
        Page<ComputeOffering> pageResponse = computeService.findAllByActive(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }
}
