package ck.panda.web.resource;

import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import ck.panda.domain.entity.EmailConfiguration;
import ck.panda.service.EmailConfigurationService;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

@RestController
@RequestMapping("/api/email-configuration")
@Api(value = "EmailConfiguration", description = "Operations with Email Configuration", produces = "application/json")
public class EmailConfigurationController extends CRUDController<EmailConfiguration> implements ApiController{

    @Autowired
    private EmailConfigurationService emailConfigurationService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new email.", response = EmailConfiguration.class)
    @Override
    public EmailConfiguration create(@RequestBody EmailConfiguration email) throws Exception {
        return emailConfigurationService.save(email);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing email.", response = EmailConfiguration.class)
    @Override
    public EmailConfiguration read(@PathVariable(PATH_ID) Long id) throws Exception {
        return emailConfigurationService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing email.", response = EmailConfiguration.class)
    @Override
    public EmailConfiguration update(@RequestBody EmailConfiguration email, @PathVariable(PATH_ID) Long id) throws Exception {
        return emailConfigurationService.update(email);
    }

    @RequestMapping(value = "/list")
    public EmailConfiguration listEmailConfiguration() throws Exception {
        return emailConfigurationService.findByIsActive(true);
    }

    @RequestMapping(value = "/send-mail")
    public void sendMail() throws Exception {
        emailConfigurationService.sendEmailTo("post2ibrahim@gmail.com", "Test subject", "Test message");
    }
}
