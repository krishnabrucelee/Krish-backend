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
import ck.panda.domain.entity.GeneralConfiguration;
import ck.panda.service.GeneralConfigurationService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * General configuration controller.
 *
 */
@RestController
@RequestMapping("/api/generalconfiguration")
@Api(value = "GeneralConfiguration", description = "Operations with general configuration", produces = "application/json")
public class GeneralConfigurationController extends CRUDController<GeneralConfiguration> implements ApiController {

    /** Service reference to general configuration. */
    @Autowired
    private GeneralConfigurationService generalConfigurationService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new general configuration.", response = GeneralConfiguration.class)
    @Override
    public GeneralConfiguration create(@RequestBody GeneralConfiguration generalConfiguration) throws Exception {
        return generalConfigurationService.save(generalConfiguration);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing general configuration.", response = GeneralConfiguration.class)
    @Override
    public GeneralConfiguration read(@PathVariable(PATH_ID) Long id) throws Exception {
        return generalConfigurationService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing general configuration.", response = GeneralConfiguration.class)
    @Override
    public GeneralConfiguration update(@RequestBody GeneralConfiguration generalConfiguration, @PathVariable(PATH_ID) Long id) throws Exception {
        return generalConfigurationService.update(generalConfiguration);
    }

    @Override
    public List<GeneralConfiguration> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, GeneralConfiguration.class);
        Page<GeneralConfiguration> pageResponse = generalConfigurationService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * List all general configuration.
     *
     * @return configuration values.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "configlist", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<GeneralConfiguration> getSearch() throws Exception {
        return generalConfigurationService.findAll();
    }
}
