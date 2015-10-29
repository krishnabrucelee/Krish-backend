package ck.panda.web.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
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
import ck.panda.domain.entity.OsType;
import ck.panda.domain.entity.Template;
import ck.panda.service.OsTypeService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * OsType controller.
 *
 */
@RestController
@RequestMapping("/api/ostypes")
@Api(value = "OsTypes", description = "Operations with ostypes", produces = "application/json")
public class OsTypeController extends CRUDController<OsType> implements ApiController {

    /** Service reference to OsType. */
    @Autowired
    private OsTypeService osTypeService;

    @Override
    public List<OsType> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, OsType.class);
        Page<OsType> pageResponse = osTypeService.findAll(page);
        System.out.println(pageResponse);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Get the list of OS Type.
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/list")
    public List<OsType> osTypelist(@RequestParam String filter, HttpServletRequest request,
    		HttpServletResponse response) throws Exception {
        return osTypeService.findByCategoryName(filter);
    }

    @Override
    public void testMethod() throws Exception {

    }

}
