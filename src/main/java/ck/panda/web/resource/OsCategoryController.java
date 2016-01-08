package ck.panda.web.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.OsCategory;
import ck.panda.service.OsCategoryService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * OsCategory controller.
 *
 */
@RestController
@RequestMapping("/api/oscategorys")
@Api(value = "OsCategorys", description = "Operations with oscategorys", produces = "application/json")
public class OsCategoryController extends CRUDController<OsCategory> implements ApiController {

    /** Service reference to OsCategory. */
    @Autowired
    private OsCategoryService osCategoryService;

    @Override
    public List<OsCategory> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, OsCategory.class);
        Page<OsCategory> pageResponse = osCategoryService.findAll(page);
        System.out.println(pageResponse);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Get the list of OS category.
     *
     * @return OS category list
     * @throws Exception raise if error
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<OsCategory> osCategoryList() throws Exception {
        return osCategoryService.findAll();
    }

    /**
     * Find the list of Os categories in templates by filters.
     *
     * @return Os categories list from server
     * @throws Exception raise if error
     */
    @RequestMapping(value = "/os", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<OsCategory> findByOsCategoryFilters() throws Exception {
        return osCategoryService.findByOsCategoryFilters();
    }
}
