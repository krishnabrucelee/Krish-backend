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
//TODO Yasin: why unused imports are here?
import ck.panda.domain.entity.Zone;
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

	@ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing application.")
	@Override
	public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
		applicationService.delete(id);
	}

	@Override
	public List<Application> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
			@RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
					throws Exception {
		PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Application.class);
		Page<Application> pageResponse = applicationService.findAll(page);
		response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
		return pageResponse.getContent();
	}

	 /**
     * Get the list of applications.
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Application> applicationList() throws Exception {
        return applicationService.findAll();
    }

	@Override
	public void testMethod() throws Exception {
		applicationService.findAll();
	}
}
