package ck.panda.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Domain;
import ck.panda.domain.repository.jpa.DomainRepository;
import ck.panda.util.CloudStackDomainService;

/**
 * Synchronization of zone,domain, region , template with cloudStack.
 *
 */
@Service
public class SyncService {

    /** Domain repository for reference. */
    @Autowired
    private DomainRepository domainRepos;

    /** Domain Service Implementation for listing domains. */
    @Autowired
    private DomainService domainservice;

    /**
     * To save domain list into respository.
     *
     * @param domain
     * @return
     * @throws Exception
     */
    public void save(List<Domain> domain) throws Exception {

        for (int i=0; i<domain.size(); i++){
            domainservice.save(domain.get(i));
        }


    }
}
