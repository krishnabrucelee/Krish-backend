package ck.panda.web.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Template;
import ck.panda.service.TemplateService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Template controller.
 *
 */
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
        templateService.delete(id);
    }

    @Override
    public List<Template> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Template.class);
        Page<Template> pageResponse = templateService.findAll(page);
        System.out.println(pageResponse);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    @Override
    public void testMethod() throws Exception {

    }
}
