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
import ck.panda.domain.entity.Hypervisor;
import ck.panda.service.HypervisorService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Hypervisor controller.
 *
 */
@RestController
@RequestMapping("/api/hypervisors")
@Api(value = "Hypervisors", description = "Operations with hypervisors", produces = "application/json")
public class HypervisorController extends CRUDController<Hypervisor> implements ApiController {

    /** Service reference to Hypervisor. */
    @Autowired
    private HypervisorService hypervisorService;

    @Override
    public List<Hypervisor> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Hypervisor.class);
        Page<Hypervisor> pageResponse = hypervisorService.findAll(page);
        System.out.println(pageResponse);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }
}
