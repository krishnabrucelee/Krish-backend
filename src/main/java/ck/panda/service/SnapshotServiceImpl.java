package ck.panda.service;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Snapshot;
import ck.panda.domain.repository.jpa.SnapshotRepository;
import ck.panda.util.CloudStackSnapshotService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Snapshot service implementation class.
 *
 */
@Service
public class SnapshotServiceImpl implements SnapshotService {

  /** Logger attribute. */
  private static final Logger LOGGER = LoggerFactory.getLogger(SnapshotServiceImpl.class);

  /** Snapshot repository reference. */
  @Autowired
  private SnapshotRepository snapshotRepo;

  /** CloudStack Domain service for connectivity with cloudstack. */
  @Autowired
  private CloudStackSnapshotService snapshotService;

  @Override
  public Snapshot save(Snapshot snapshot) throws Exception {
      LOGGER.debug(snapshot.getUuid());
    return snapshotRepo.save(snapshot);
  }

  @Override
  public Snapshot update(Snapshot snapshot) throws Exception {
      LOGGER.debug(snapshot.getUuid());
    return snapshotRepo.save(snapshot);
  }

  @Override
  public void delete(Snapshot snapshot) throws Exception {
      snapshotRepo.delete(snapshot);
  }

  @Override
  public void delete(Long id) throws Exception {
      snapshotRepo.delete(id);
  }

  @Override
  public  Snapshot find(Long id) throws Exception {
    Snapshot snapshot = snapshotRepo.findOne(id);
    return snapshot;
  }

  @Override
  public Page<Snapshot> findAll(PagingAndSorting pagingAndSorting) throws Exception {
    return snapshotRepo.findAll(pagingAndSorting.toPageRequest());
  }

  @Override
  public List<Snapshot> findAll() throws Exception {
      return (List<Snapshot>) snapshotRepo.findAll();
  }

@Override
public List<Snapshot> findAllFromCSServer() throws Exception {
      List<Snapshot> snapshotList = new ArrayList<Snapshot>();
      HashMap<String, String> snapshotMap = new HashMap<String, String>();

      // 1. Get the list of domains from CS server using CS connector
      String response = snapshotService.listSnapshots(snapshotMap, "json");

      JSONArray snapshotListJSON = new JSONObject(response).getJSONObject("listsnapshotsresponse")
              .getJSONArray("snapshot");
      // 2. Iterate the json list, convert the single json entity to domain
      for (int i = 0, size = snapshotListJSON.length(); i < size; i++) {
          // 2.1 Call convert by passing JSONObject to Domain entity and Add
          // the converted Domain entity to list
          snapshotList.add(Snapshot.convert(snapshotListJSON.getJSONObject(i)));
      }
      return snapshotList;
  }
}



