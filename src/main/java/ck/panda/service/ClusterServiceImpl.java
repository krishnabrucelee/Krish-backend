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
import ck.panda.domain.entity.Cluster;
import ck.panda.domain.repository.jpa.ClusterRepository;
import ck.panda.util.CloudStackClusterService;
import ck.panda.util.ConvertUtil;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Cluster service implementation class.
 *
 */
@Service
public class ClusterServiceImpl implements ClusterService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainServiceImpl.class);

    /** Cluster repository reference. */
    @Autowired
    private ClusterRepository clusterRepo;

    /** CloudStack Cluster service for connectivity with cloudstack. */
    @Autowired
    private CloudStackClusterService clusterService;

    /** Convert entity repository reference. */
    @Autowired
    private ConvertUtil entity;

    @Override
    public Cluster save(Cluster cluster) throws Exception {
        LOGGER.debug(cluster.getUuid());
        return clusterRepo.save(cluster);
    }

    @Override
    public Cluster update(Cluster cluster) throws Exception {
        LOGGER.debug(cluster.getUuid());
        return clusterRepo.save(cluster);
    }

    @Override
    public void delete(Cluster cluster) throws Exception {
        clusterRepo.delete(cluster);
    }

    @Override
    public void delete(Long id) throws Exception {
        clusterRepo.delete(id);
    }

    @Override
    public Cluster find(Long id) throws Exception {
        Cluster cluster = clusterRepo.findOne(id);
        return cluster;
    }

    @Override
    public Page<Cluster> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return clusterRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Cluster> findAll() throws Exception {
        return (List<Cluster>) clusterRepo.findAll();
    }

    @Override
    public List<Cluster> findAllFromCSServer() throws Exception {
        List<Cluster> clusterList = new ArrayList<Cluster>();
        HashMap<String, String> clusterMap = new HashMap<String, String>();

        // 1. Get the list of cluster from CS server using CS connector
        String response = clusterService.listClusters("json", clusterMap);
        JSONArray clusterListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listclustersresponse");
        if (responseObject.has("cluster")) {
            clusterListJSON = responseObject.getJSONArray("cluster");
            // 2. Iterate the json list, convert the single json entity to pod
            for (int i = 0, size = clusterListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to cluster entity and
                // Add
                // the converted cluster entity to list
                clusterList.add(Cluster.convert(clusterListJSON.getJSONObject(i), entity));
            }
        }
        return clusterList;
    }

    @Override
    public Cluster findByUUID(String uuid) throws Exception {
        return clusterRepo.findByUUID(uuid);
    }
}



