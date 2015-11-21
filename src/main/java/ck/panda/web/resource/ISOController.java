package ck.panda.web.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Iso;
import ck.panda.service.IsoService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * ISO controller.
 *
 */
@RestController
@RequestMapping("/api/iso")
@Api(value = "Iso", description = "Operations with iso", produces = "application/json")
public class ISOController extends CRUDController<Iso> implements ApiController {

    /** Service reference to Iso service. */
    @Autowired
    private IsoService isoService;

    @Override
    public List<Iso> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Iso.class);
        Page<Iso> pageResponse = isoService.findAll(page);
        System.out.println(pageResponse);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Get the list of Iso Type.
     *
     * @return Iso type
     * @throws Exception raise if error
     */
    @RequestMapping(value = "/list")
    public List<Iso> osIsolist() throws Exception {
        return isoService.findAll();
    }

}
