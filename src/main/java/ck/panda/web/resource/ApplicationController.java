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
import ck.panda.domain.entity.Application;
import ck.panda.service.ApplicationService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/** Application controller. */
@RestController
@RequestMapping("/api/applications")
@Api(value = "Applications", description = "Operations with applications", produces = "application/json")
public class ApplicationController extends CRUDController<Application>implements ApiController {

    /** Service reference to Application. */
    @Autowired
    private ApplicationService applicationService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new application.", response = Application.class)
    @Override
    public Application create(@RequestBody Application application) throws Exception {
        return applicationService.save(application);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing application.", response = Application.class)
    @Override
    public Application read(@PathVariable(PATH_ID) Long id) throws Exception {
        return applicationService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing application.", response = Application.class)
    @Override
    public Application update(@RequestBody Application application, @PathVariable(PATH_ID) Long id) throws Exception {
        return applicationService.update(application);
    }

    /**
     * Delete the application.
     *
     * @param application reference of the application.
     * @param id application id.
     * @throws Exception error occurs.
     */
    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Application Type.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@RequestBody Application application, @PathVariable(PATH_ID) Long id) throws Exception {
        /** Doing Soft delete from the application table. */
        applicationService.softDelete(application);
    }

    @Override
    public List<Application> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Application.class);
        Page<Application> pageResponse = applicationService.findAllByActive(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Find the list of active applications.
     *
     * @return projects project list.
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<Application> getSearch() throws Exception {
        return applicationService.findAllByIsActive(true);
    }

    @Override
    public void testMethod() throws Exception {
        applicationService.findAll();
    }
}
