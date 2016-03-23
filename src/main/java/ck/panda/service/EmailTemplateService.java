package ck.panda.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Document;
import ck.panda.domain.entity.EmailTemplate;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for email template entity.
 *
 */
@Service
public interface EmailTemplateService extends CRUDService<EmailTemplate> {

    EmailTemplate saves(Document document) throws FileNotFoundException, IOException;

}
