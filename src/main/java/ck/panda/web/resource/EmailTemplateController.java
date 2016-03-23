package ck.panda.web.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Document;
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

    /** Logger attribute of the file .*/
    private static final Logger LOGGER = Logger.getLogger(EmailTemplateController.class);

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

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public @ResponseBody EmailTemplate handleFileUpload(
            @RequestParam(value="file", required=true) MultipartFile file) {

        try {
            Document document = new Document(file.getBytes(),file.getName());
            emailService.saves(document);
             return document.getMetadata();
        } catch (RuntimeException e) {
            LOGGER.error("Error while uploading.", e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error while uploading.", e);
            throw new RuntimeException(e);
        }
    }
 }
