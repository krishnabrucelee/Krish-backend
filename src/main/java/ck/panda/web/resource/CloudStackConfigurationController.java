package ck.panda.web.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.CloudStackConfiguration;
import ck.panda.service.CloudStackConfigurationService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * CloudStack Configuration Controller which acts as front controller and service for crud methods.
 *
 *
 */
@RestController
@RequestMapping("/api/cloudconfiguration")
@Api(value = "CloudStackConfiguration", description = "URL formation for cloudstack", produces = "application/json")
@PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
public class CloudStackConfigurationController extends CRUDController<CloudStackConfiguration> implements ApiController {

    /** Service reference to CloudStackConfiguration. */
    @Autowired
    private CloudStackConfigurationService configService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new CloudStackConfiguration.", response = CloudStackConfiguration.class)
    @Override
    public CloudStackConfiguration create(@RequestBody CloudStackConfiguration config) throws Exception {
        return configService.save(config);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing CloudStackConfiguration.", response = CloudStackConfiguration.class)
    @Override
    public CloudStackConfiguration read(@PathVariable(PATH_ID) Long id) throws Exception {
        return configService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing CloudStackConfiguration.", response = CloudStackConfiguration.class)
    @Override
    public CloudStackConfiguration update(@RequestBody CloudStackConfiguration config, @PathVariable(PATH_ID) Long id) throws Exception {
        return configService.update(config);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing CloudStackConfiguration.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        configService.delete(id);
    }

    @Override
    public List<CloudStackConfiguration> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, CloudStackConfiguration.class);
        Page<CloudStackConfiguration> pageResponse = configService.findAll(page);

        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }
}