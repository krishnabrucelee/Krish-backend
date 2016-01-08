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
import ck.panda.domain.entity.Pod;
import ck.panda.domain.repository.jpa.PodRepository;
import ck.panda.util.CloudStackPodService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Pod service implementation class.
 *
 */
@Service
public class PodServiceImpl implements PodService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainServiceImpl.class);

    /** Department repository reference. */
    @Autowired
    private PodRepository podRepo;

    /** CloudStack Domain service for connectivity with cloudstack. */
    @Autowired
    private CloudStackPodService podService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    @Override
    public Pod save(Pod pod) throws Exception {
        LOGGER.debug(pod.getUuid());
        return podRepo.save(pod);
    }

    @Override
    public Pod update(Pod pod) throws Exception {
        LOGGER.debug(pod.getUuid());
        return podRepo.save(pod);
    }

    @Override
    public void delete(Pod pod) throws Exception {
        podRepo.delete(pod);
    }

    @Override
    public void delete(Long id) throws Exception {
        podRepo.delete(id);
    }

    @Override
    public Pod find(Long id) throws Exception {
        Pod pod = podRepo.findOne(id);
        return pod;
    }

    @Override
    public Page<Pod> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return podRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Pod> findAll() throws Exception {
        return (List<Pod>) podRepo.findAll();
    }

    @Override
    public List<Pod> findAllFromCSServer() throws Exception {
        List<Pod> podList = new ArrayList<Pod>();
        HashMap<String, String> podMap = new HashMap<String, String>();

        // 1. Get the list of pods from CS server using CS connector
        String response = podService.listPods("json", podMap);

        JSONArray podListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listpodsresponse");
        if (responseObject.has("pod")) {
            podListJSON = responseObject.getJSONArray("pod");
            // 2. Iterate the json list, convert the single json entity to pod
            for (int i = 0, size = podListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to Domain entity and
                // Add
                // the converted pod entity to list
                Pod pod = Pod.convert(podListJSON.getJSONObject(i));
                pod.setZoneId(convertEntityService.getZoneId(pod.getTransZoneId()));
                podList.add(pod);
            }
        }
        return podList;
    }

    @Override
    public Pod findByUUID(String uuid) throws Exception {
        return podRepo.findByUUID(uuid);
    }

    @Override
    public Pod softDelete(Pod pod) throws Exception {
        pod.setIsActive(false);
        pod.setStatus(Pod.Status.DISABLED);
        return podRepo.save(pod);
    }
}
