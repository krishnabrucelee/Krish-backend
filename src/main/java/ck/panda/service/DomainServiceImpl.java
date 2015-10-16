package ck.panda.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.repository.jpa.DomainRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * Domain service implementation class.
 *
 * @author Krishna<krishnakumar@assistanz.com>
 *
 */
@Service
public class DomainServiceImpl implements DomainService {

  /** Logger attribute. */
  private static final Logger LOGGER = LoggerFactory.getLogger(DomainServiceImpl.class);

  /** Validator attribute. */
  @Autowired
  private AppValidator validator;

  /** Department repository reference. */
  @Autowired
  private DomainRepository domainRepo;

  @Override
  @PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
  public Domain save(Domain domain) throws Exception {
    return domainRepo.save(domain);
  }

  @Override
  public Domain update(Domain domain) throws Exception {
    return domainRepo.save(domain);
  }

  @Override
  public void delete(Domain domain) throws Exception {
    domainRepo.delete(domain);
  }

  @Override
  public void delete(Long id) throws Exception {
    domainRepo.delete(id);
  }

  @Override
  @PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
  public Domain find(Long id) throws Exception {
    Domain domain = domainRepo.findOne(id);
    return domain;
  }

  @Override
  public Page<Domain> findAll(PagingAndSorting pagingAndSorting) throws Exception {
    return domainRepo.findAll(pagingAndSorting.toPageRequest());
  }

  @Override
  public List<Domain> findAll() throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

}
