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
import ck.panda.domain.entity.Template;
import ck.panda.service.TemplateService;
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

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Template.", response = Template.class)
    @Override
    public Template create(@RequestBody Template template) throws Exception {
        template.setSyncFlag(true);
        return templateService.save(template);
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
        return templateService.update(template);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Template.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        Template template = templateService.find(id);
        template.setSyncFlag(true);
        templateService.softDelete(template);
    }

    /**
     * List all Iso-Templates and Templates.
     *
     * @param type iso/template
     * @param sortBy ASC
     * @param range 1-10
     * @param limit page limit min 10
     * @param request http request
     * @param response http response
     * @return Iso-Templates and Templates
     * @throws Exception error
     */
    @RequestMapping(value = "/category", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Template> list(@RequestParam("type") String type, @RequestParam("sortBy") String sortBy,
            @RequestHeader(value = RANGE) String range, @RequestParam(required = false) Integer limit,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Template.class);
        String template = "template?lang=en";
        if (type.equals(template)) {
            Page<Template> pageResponse = templateService.findAll(page);
            response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
            return pageResponse.getContent();
        } else {
            Page<Template> pageResponse = templateService.findAllIso(page);
            response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
            return pageResponse.getContent();
        }
    }

    /**
     * Get the list of templates.
     *
     * @return template list from server
     * @throws Exception raise if error
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Template> templateList() throws Exception {
        return templateService.findByTemplate();
    }

    /**
     * Find the list of templates by filters.
     *
     * @param template the template object.
     * @return template list from server
     * @throws Exception raise if error
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Template> findByFilters(@RequestBody Template template) throws Exception {
        return templateService.findByFilters(template);
    }

}
