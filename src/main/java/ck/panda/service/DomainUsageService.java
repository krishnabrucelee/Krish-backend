package ck.panda.service;

import java.util.List;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.DomainUsage;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Usage.
 *
 * This service class used to get the resource usage details for each company.
 *
 */
@Service
public interface DomainUsageService extends CRUDService<DomainUsage>  {

    void updateDomainUsage() throws Exception;
}


