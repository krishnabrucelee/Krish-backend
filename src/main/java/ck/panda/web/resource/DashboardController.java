package ck.panda.web.resource;

import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import ck.panda.domain.entity.Application;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.service.DashboardService;
import ck.panda.util.web.ApiController;


/**
 * Dashboard controller.
 */
@RestController
@RequestMapping("/api/dashboard")
@Api(value = "Dashboard", description = "Operations with Dashboard", produces = "application/json")
public class DashboardController implements ApiController {
    
    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);
    
    @Autowired
    private DashboardService dashboardService;
    
    /**
     * Get the dashboard infrastructure.
     *
     * @param request page request.
     * @param response page response content.
     * @return vm count.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "/infrastructure", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getInfrastructure(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	return dashboardService.getInfrastructure().toString();
    }

    /**
     * Get the quota limit for domain.
     * 
     * @return quota limit list
     * @throws Exception if error
     */
    @RequestMapping(value = "/quota", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ResourceLimitDomain> findByDomainQuota()  throws Exception {
    	return dashboardService.findByDomainQuota();
    }
    
    /**
     * Get the departments.
     * 
     * @return department list
     * @throws Exception if error
     */
    @RequestMapping(value = "/departmentByDomain", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Department> findAllDepartmentByDomain()  throws Exception {
    	return dashboardService.findAllDepartmentByDomain();
    }
    
    /**
     * Get the applicaitons.
     * 
     * @return applicaiton list
     * @throws Exception if error
     */
    @RequestMapping(value = "/applicationByDomain", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Application> findAllApplicationByDomain()  throws Exception {
    	return dashboardService.findAllApplicationByDomain();
    }
}
