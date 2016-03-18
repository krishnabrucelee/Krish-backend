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
import ck.panda.domain.entity.EmailTemplate;
import ck.panda.service.EmailTemplateService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * EmailTemplate controller.
 *
 */
@RestController
@RequestMapping("/api/emails")
@Api(value = "Emails", description = "Operations with domains", produces = "application/json")
public class EmailTemplateController extends CRUDController<EmailTemplate> implements ApiController {

    /** Service reference to EmailTemplate. */
    @Autowired
    private EmailTemplateService emailService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new domain.", response = EmailTemplate.class)
    @Override
    public EmailTemplate create(@RequestBody EmailTemplate email) throws Exception {
        return emailService.save(email);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing EmailTemplate.", response = EmailTemplate.class)
    @Override
    public EmailTemplate read(@PathVariable(PATH_ID) Long id) throws Exception {
        return emailService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing EmailTemplate.", response = EmailTemplate.class)
    @Override
    public EmailTemplate update(@RequestBody EmailTemplate domain, @PathVariable(PATH_ID) Long id) throws Exception {
        return emailService.update(domain);
    }

    @Override
    public List<EmailTemplate> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, EmailTemplate.class);
        Page<EmailTemplate> pageResponse = emailService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }
 }
