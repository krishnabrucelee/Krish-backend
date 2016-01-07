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
import ck.panda.domain.entity.Host;
import ck.panda.service.HostService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Host controller.
 *
 */
@RestController
@RequestMapping("/api/host")
@Api(value = "hosts", description = "Operations with host", produces = "application/json")
public class HostController extends CRUDController<Host> implements ApiController {

    /** Service reference to host. */
    @Autowired
    private HostService hostService;

    @Override
    public List<Host> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Host.class);
        Page<Host> pageResponse = hostService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Get the list of hosts.
     *
     * @return host list.
     * @throws Exception raise if error
     */
    @RequestMapping(value = "/list")
    public List<Host> getHostList() throws Exception {
        return hostService.findAll();
    }

}
