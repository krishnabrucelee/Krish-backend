package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Host;
import ck.panda.domain.repository.jpa.HostRepository;
import ck.panda.util.CloudStackHostService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Host service implementation class.
 *
 */
@Service
public class HostServiceImpl implements HostService {

  /** Logger attribute. */
  private static final Logger LOGGER = LoggerFactory.getLogger(DomainServiceImpl.class);

  /** Department repository reference. */
  @Autowired
  private HostRepository hostRepo;

  /** CloudStack Domain service for connectivity with cloudstack. */
  @Autowired
  private CloudStackHostService hostService;

  @Override
  public Host save(Host host) throws Exception {
      LOGGER.debug(host.getUuid());
    return hostRepo.save(host);
  }

  @Override
  public Host update(Host host) throws Exception {
      LOGGER.debug(host.getUuid());
    return hostRepo.save(host);
  }

  @Override
  public void delete(Host host) throws Exception {
      hostRepo.delete(host);
  }

  @Override
  public void delete(Long id) throws Exception {
      hostRepo.delete(id);
  }

  @Override
  public Host find(Long id) throws Exception {
      Host host = hostRepo.findOne(id);
    return host;
  }

  @Override
  public Page<Host> findAll(PagingAndSorting pagingAndSorting) throws Exception {
    return hostRepo.findAll(pagingAndSorting.toPageRequest());
  }

  @Override
  public List<Host> findAll() throws Exception {
      return (List<Host>) hostRepo.findAll();
  }

@Override
public List<Host> findAllFromCSServer() throws Exception {
      List<Host> hostList = new ArrayList<Host>();
      HashMap<String, String> hostMap = new HashMap<String, String>();

      // 1. Get the list of hosts from CS server using CS connector
      String response = hostService.listHosts("json", hostMap);

      JSONArray hostListJSON = new JSONObject(response).getJSONObject("listhostsresponse")
              .getJSONArray("host");
      // 2. Iterate the json list, convert the single json entity to domain
      for (int i = 0, size = hostListJSON.length(); i < size; i++) {
          // 2.1 Call convert by passing JSONObject to host entity and Add
          // the converted host entity to list
          hostList.add(Host.convert(hostListJSON.getJSONObject(i)));
      }
      return hostList;
    }
  }