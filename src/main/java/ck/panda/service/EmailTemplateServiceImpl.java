package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.EmailTemplate;
import ck.panda.domain.repository.jpa.EmailTemplateRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * EmailTemplate service implementation class.
 *
 */
@Service
public class EmailTemplateServiceImpl implements EmailTypeTemplateService {

    /** Email template repository reference. */
    @Autowired
    private EmailTemplateRepository emailRepo;

    @Override
    public EmailTemplate save(EmailTemplate email) throws Exception {
        return emailRepo.save(email);
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

    @Override
    public EmailTemplate findByEventName(String eventName) {
        // TODO Auto-generated method stub
        return emailRepo.findByEventName(eventName);
    }
}
