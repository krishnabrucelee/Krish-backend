package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Hypervisor;
import ck.panda.domain.repository.jpa.HypervisorRepository;
import ck.panda.util.CloudStackHypervisorsService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Hypervisor service implementation used to get list of hypervisor and save the hypervisor from
 * cloudstack server.
 *
 */
@Service
public class HypervisorServiceImpl implements HypervisorService {

    /** Hypervisor repository reference. */
    @Autowired
    private HypervisorRepository hypervisorRepo;

    /** Cloudstack hypervisor service for connectivity with cloud Stack server hypervisor. */
    @Autowired
    private CloudStackHypervisorsService hypervisorService;

    @Override
    public Hypervisor save(Hypervisor hypervisor) throws Exception {
        return hypervisorRepo.save(hypervisor);
    }

    @Override
    public Hypervisor update(Hypervisor hypervisor) throws Exception {
        return hypervisorRepo.save(hypervisor);
    }

    @Override
    public void delete(Hypervisor hypervisor) throws Exception {
        hypervisorRepo.delete(hypervisor);
    }

    @Override
    public void delete(Long id) throws Exception {
        hypervisorRepo.delete(id);
    }

    @Override
    public Hypervisor find(Long id) throws Exception {
        return hypervisorRepo.findOne(id);
    }

    @Override
    public Page<Hypervisor> findAll(PagingAndSorting pagingAndSorting) throws Exception {
    	return hypervisorRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Hypervisor> findAll() throws Exception {
       return (List<Hypervisor>) hypervisorRepo.findAll();
    }

    @Override
    public List<Hypervisor> findAllFromCSServer() throws Exception {

        List<Hypervisor> hypervisorList = new ArrayList<Hypervisor>();
        HashMap<String, String> hypervisorMap = new HashMap<String, String>();

        // 1. Get the list of Zones from CS server using CS connector
        String response = hypervisorService.listHypervisors("json", hypervisorMap);
        JSONArray hypervisorListJSON = new JSONObject(response).getJSONObject("listhypervisorsresponse")
                .getJSONArray("hypervisor");

        // 2. Iterate the json list, convert the single json entity to Zone
        for (int i = 0, size = hypervisorListJSON.length(); i < size; i++) {
            // 2.1 Call convert by passing JSONObject to Zone entity and Add
            // the converted Zone entity to list
            hypervisorList.add(Hypervisor.convert(hypervisorListJSON.getJSONObject(i)));
        }
        return hypervisorList;
    }

}

