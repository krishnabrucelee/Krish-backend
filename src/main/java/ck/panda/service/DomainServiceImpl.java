package ck.panda.service;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.repository.jpa.DomainRepository;
import ck.panda.util.CloudStackDomainService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Domain service implementation class.
 *
 */
@Service
public class DomainServiceImpl implements DomainService {

  /** Department repository reference. */
  @Autowired
  private DomainRepository domainRepo;

  /** CloudStack Domain service for connectivity with cloudstack. */
  @Autowired
  private CloudStackDomainService domainService;

  @Override
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

@Override
public List<Domain> findAllFromCSServer() throws Exception {
      List<Domain> domainList = new ArrayList<Domain>();
      HashMap<String, String> domainMap = new HashMap<String, String>();

      // 1. Get the list of domains from CS server using CS connector
      String response = domainService.listDomains("json", domainMap);

      JSONArray domainListJSON = new JSONObject(response).getJSONObject("listdomainsresponse")
              .getJSONArray("domain");
      // 2. Iterate the json list, convert the single json entity to domain
      for (int i = 0, size = domainListJSON.length(); i < size; i++) {
          // 2.1 Call convert by passing JSONObject to Domain entity and Add
          // the converted Domain entity to list
          domainList.add(Domain.convert(domainListJSON.getJSONObject(i)));
      }
      return domainList;
  }
}

