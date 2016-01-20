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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Tax;
import ck.panda.service.TaxService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Tax controller.
 *
 */
@RestController
@RequestMapping("/api/tax")
@Api(value = "Taxes", description = "Operations with taxes", produces = "application/json")
public class TaxController extends CRUDController<Tax> implements ApiController {

    /** Service reference to Tax. */
    @Autowired
    private TaxService taxService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Tax.", response = Tax.class)
    @Override
    public Tax create(@RequestBody Tax tax) throws Exception {
        return taxService.save(tax);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Department.", response = Tax.class)
    @Override
    public Tax read(@PathVariable(PATH_ID) Long id) throws Exception {
        return taxService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Department.", response = Tax.class)
    @Override
    public Tax update(@RequestBody Tax tax, @PathVariable(PATH_ID) Long id) throws Exception {
        return taxService.update(tax);
    }

    /**
     * Delete the tax.
     *
     * @param tax reference of the tax.
     * @param id tax id.
     * @throws Exception error occurs.
     */
    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Tax.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@RequestBody Tax tax, @PathVariable(PATH_ID) Long id) throws Exception {
        /** Doing Soft delete from the tax table. */
        taxService.softDelete(tax);
    }

    @Override
    public List<Tax> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Tax.class);
        Page<Tax> pageResponse = taxService.findAllByActive(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }
}
