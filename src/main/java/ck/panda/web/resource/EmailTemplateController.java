package ck.panda.web.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.EmailTemplate;
import ck.panda.domain.entity.EmailTemplate.RecipientType;
import ck.panda.service.EmailTypeTemplateService;
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
    private EmailTypeTemplateService emailService;

    /** English template directory. */
    @Value("${english.template.dir}")
    private String englishTemplateDir;

    /** Chinese template directory */
    @Value("${chinese.template.dir}")
    private String chineseTemplateDir;

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
    public @ResponseBody String handleFileUpload(
            @RequestParam(value="file") MultipartFile[] files, @RequestParam(value="eventName", required=true) String eventName, @RequestParam(value="englishLanguage", required=true) String englishLanguage, @RequestParam(value="chineseLanguage")String chineseLanguage,@RequestParam(value="subject")String subject,@RequestParam(value="recipientType")String recipientType,@RequestParam(value="hasEnglish")Boolean hasEnglish) throws Exception {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        int i = 0;
        EmailTemplate email = emailService.findByEventAndIsActive(eventName, true);
        if (email == null ) {
            email = new EmailTemplate();
        }
        for (MultipartFile file : files) {
            i++;
            if((hasEnglish == false && files.length == 1) || (files.length > 1) && i == 2) {
                 String fileName = file.getOriginalFilename();
                 File newFile = new File(chineseTemplateDir + "/" + eventName);
                 email.setChineseLanguage(fileName);
                 emailService.save(email);
                     try {
                         inputStream = file.getInputStream();

                         if (!newFile.exists()) {
                             newFile.createNewFile();
                         }
                         outputStream = new FileOutputStream(newFile);
                         int read = 0;
                         byte[] bytes = new byte[1024];

                         while ((read = inputStream.read(bytes)) != -1) {
                             outputStream.write(bytes, 0, read);
                         }
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                    newFile.getAbsolutePath();
        }
            else {
                  String fileName = file.getOriginalFilename();
                  File newFile = new File(englishTemplateDir + "/" + eventName);
                  email.setEventName(eventName);
                  email.setEnglishLanguage(fileName);
                  email.setSubject(subject);
                  email.setRecipientType(RecipientType.valueOf(recipientType));
                  email.setIsActive(true);
                  email.setFilePath(englishTemplateDir);
                  emailService.save(email);

              try {
                  inputStream = file.getInputStream();
                  if (!newFile.exists()) {
                      newFile.createNewFile();
                  }
                  outputStream = new FileOutputStream(newFile);
                  int read = 0;
                  byte[] bytes = new byte[1024];

                  while ((read = inputStream.read(bytes)) != -1) {
                      outputStream.write(bytes, 0, read);
                  }
              } catch (IOException e) {
                  e.printStackTrace();
              }
               newFile.getAbsolutePath();
            }
        }
        return eventName;
    }

    /**
     * List event literals by event name.
     *
     * @param eventName to be choosed.
     * @return event literals.
     * @throws Exception if error occurs.
     */
    @RequestMapping(value = "listbyeventname", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<EmailTemplate> findByEventName(@RequestParam String eventName) throws Exception {
        return emailService.findByEventName(eventName);
    }

 }
