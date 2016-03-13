package ck.panda.web.resource;

import java.util.HashMap;
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
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.Template.TemplateType;
import ck.panda.service.TemplateService;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/** Template controller. */
@RestController
@RequestMapping("/api/templates")
@Api(value = "Templates", description = "Operations with templates", produces = "application/json")
public class TemplateController extends CRUDController<Template> implements ApiController {

    /** Service reference to Template. */
    @Autowired
    private TemplateService templateService;

    /** Token details reference. */
    @Autowired
    private TokenDetails tokenDetails;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Template.", response = Template.class)
    @Override
    public Template create(@RequestBody Template template) throws Exception {
        template.setSyncFlag(true);
        template.setTemplateOwnerId(Long.valueOf(tokenDetails.getTokenDetails(CloudStackConstants.CS_ID)));
        return templateService.save(template, Long.parseLong(tokenDetails.getTokenDetails("id")));
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Template.", response = Template.class)
    @Override
    public Template read(@PathVariable(PATH_ID) Long id) throws Exception {
        return templateService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Template.", response = Template.class)
    @Override
    public Template update(@RequestBody Template template, @PathVariable(PATH_ID) Long id) throws Exception {
        template.setSyncFlag(true);
        return templateService.update(template, Long.parseLong(tokenDetails.getTokenDetails("id")));
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Template.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        Template template = templateService.find(id);
        template.setSyncFlag(true);
        templateService.softDelete(template);
    }

    /**
     * List all the templates with pagination.
     *
     * @param sortBy ASC
     * @param type iso/template
     * @param range 1-10
     * @param limit page limit min 10
     * @param request http request
     * @param response http response
     * @return Iso-Templates and Templates
     * @throws Exception error
     */
    @RequestMapping(value = "/listall", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Template> list(@RequestParam String sortBy, @RequestParam String type, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Template.class);
        if (type.contains(CloudStackConstants.TEMPLATE_NAME)) {
            Page<Template> pageResponse = templateService.findAll(page);
            response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
            return pageResponse.getContent();
        } else if (type.contains(CloudStackConstants.ISO_TEMPLATE_NAME)) {
            Page<Template> pageResponse = templateService.findAllIso(page);
            response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
            return pageResponse.getContent();
        } else if(type.contains("user")) {
             Page<Template> pageResponse = templateService.findAllByUserIdAndType(page, type, Long.parseLong(tokenDetails.getTokenDetails("id")));
              response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
             return pageResponse.getContent();
        }
        else {
             Page<Template> pageResponse = templateService.findAllByType(page, type, true, true);
             response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
             return pageResponse.getContent();
        }
    }

    /**
     * Get all the templates.
     *
     * @return template list from server
     * @throws Exception raise if error
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Template> templateList() throws Exception {
        return templateService.findByTemplate(Long.valueOf(tokenDetails.getTokenDetails(CloudStackConstants.CS_ID)));
    }

    /**
     * Find the list of templates by filters.
     *
     * @param template the template object.
     * @return template list from server
     * @throws Exception raise if error
     */
    @RequestMapping(value = "/searchtemplate", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Template> findTemplateByFilters(@RequestBody Template template) throws Exception {
        return templateService.findTemplateByFilters(template, Long.valueOf(tokenDetails.getTokenDetails(CloudStackConstants.CS_ID)));
    }

    /**
     * Find the list of ISO by filters.
     *
     * @param templateIso the template iso object.
     * @return template list from server
     * @throws Exception raise if error
     */
    @RequestMapping(value = "/searchiso", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Template> findIsoByFilters(@RequestBody Template templateIso) throws Exception {
        return templateService.findIsoByFilters(templateIso, Long.valueOf(tokenDetails.getTokenDetails(CloudStackConstants.CS_ID)));
    }

    /**
     * Get the template counts for linux, windows and total count.
     *
     * @param request page request.
     * @param response page response content.
     * @return template count.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "templateCounts", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getTemplateCounts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HashMap<String, Integer> templateCount = templateService.findTemplateCounts();
        return "{\"windowsCount\":" + templateCount.get("windowsCount") + ",\"linuxCount\":" + templateCount.get("linuxCount") + ",\"totalCount\":"
                + templateCount.get("totalCount") + ",\"windowsIsoCount\":" + templateCount.get("windowsIsoCount") + ",\"linuxIsoCount\":"
                + templateCount.get("linuxIsoCount") + ",\"totalIsoCount\":" + templateCount.get("totalIsoCount") + "}";
    }

    @RequestMapping(value = "/listalltemplate", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Template> listAllTemplate() throws Exception {
        return templateService.findAllTemplatesByIsActiveAndType(true);
    }
}
