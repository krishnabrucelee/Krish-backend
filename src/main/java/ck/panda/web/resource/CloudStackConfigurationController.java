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
public class CloudStackConfigurationController extends CRUDController<CloudStackConfiguration>
        implements ApiController {

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
    public CloudStackConfiguration update(@RequestBody CloudStackConfiguration config, @PathVariable(PATH_ID) Long id)
            throws Exception {
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

    /**
     * list all CloudStack configuration .
     *
     * @return configuration values.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "configlist", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<CloudStackConfiguration> getSearch() throws Exception {
        return configService.findAll();
    }

    /**
     * Import individual sync from CS.
     *
     * @param id import id
     * @param type sync type
     * @return configuration values.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "/importData", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected void importCsData(@RequestParam String keyName, @RequestParam String type) throws Exception {
        configService.importCsData(keyName, type);
    }

}
