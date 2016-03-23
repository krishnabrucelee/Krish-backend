package ck.panda.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import ck.panda.domain.entity.Document;
import ck.panda.domain.entity.EmailTemplate;
import ck.panda.domain.repository.jpa.EmailTemplateRepository;
import ck.panda.domain.repository.jpa.FileService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * EmailTemplate service implementation class.
 *
 */
@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    /** Email template repository reference. */
    @Autowired
    private EmailTemplateRepository emailRepo;

    @Autowired
    private FileService fileRepo;

    @Override
    public EmailTemplate save(EmailTemplate email) throws Exception {
        email.setEventName(email.getEventName());
        //Document document = new Document(email);
        //document.setEventName(email.getEventName());
        return emailRepo.save(email);
    }

    @Override
    public EmailTemplate saves(Document document) throws FileNotFoundException, IOException {
         String tempFilePath = writeDocumentToTempFile(document);
         MultiValueMap<String, Object> parts = createMultipartFileParam(tempFilePath);
         fileRepo.insert(document);
        return document.getMetadata();
    }

    @Override
    public EmailTemplate update(EmailTemplate email) throws Exception {
        return emailRepo.save(email);
    }

    @Override
    public void delete(EmailTemplate email) throws Exception {
        emailRepo.delete(email);
    }

    @Override
    public void delete(Long id) throws Exception {
        emailRepo.delete(id);
    }

    @Override
    public EmailTemplate find(Long id) throws Exception {
        return emailRepo.findOne(id);
    }

    @Override
    public Page<EmailTemplate> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return emailRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<EmailTemplate> findAll() throws Exception {
        return (List<EmailTemplate>) emailRepo.findAll();
    }

    private String writeDocumentToTempFile(Document document) throws IOException, FileNotFoundException {
        Path path;
        path = Files.createTempDirectory(document.getFileName());
        String tempDirPath = path.toString();
        File file = new File(tempDirPath,document.getFileName());
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(document.getFileData());
        fo.close();
        return file.getPath();
    }

    private MultiValueMap<String, Object> createMultipartFileParam(String tempFilePath) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
        parts.add("file", new FileSystemResource(tempFilePath));
        return parts;
    }
}
