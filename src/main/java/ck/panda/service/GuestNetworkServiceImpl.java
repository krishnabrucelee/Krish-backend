package ck.panda.service;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ck.panda.domain.entity.GuestNetwork;
import ck.panda.domain.entity.Zone;
import ck.panda.domain.repository.jpa.GuestNetworkRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackNetworkService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Service implementation for Guest Network entity.
 *
 */
@Service
public class GuestNetworkServiceImpl implements GuestNetworkService {

    /** GuestNetwork repository reference. */
    @Autowired
    private GuestNetworkRepository guestnetworkRepo;

    @Autowired
    private ZoneService zoneService;
    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GuestNetworkServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    @Autowired
    private CloudStackNetworkService csNetwork;

    @Autowired
    private ConfigUtil config;
    @Override
    public GuestNetwork save(GuestNetwork guestNetwork) throws Exception {

        Errors errors = validator.rejectIfNullEntity("guestNetwork", guestNetwork);
        errors = validator.validateEntity(guestNetwork, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            config.setServer(4L);
            String networkOfferings = csNetwork.createNetwork("guest network",guestNetwork.getName(),"json",optional(guestNetwork));
                    JSONObject createComputeResponseJSON = new JSONObject(networkOfferings).getJSONObject("createnetworkresponse")
                        .getJSONObject("network");
                System.out.println(createComputeResponseJSON);
                guestNetwork.setUuid((String) createComputeResponseJSON.get("id"));
                guestNetwork.setName((String)createComputeResponseJSON.get("name"));
                guestNetwork.setDisplayText((String)createComputeResponseJSON.get("displaytext"));
                guestNetwork.setNetworkType((String)createComputeResponseJSON.get("type"));

            return guestnetworkRepo.save(guestNetwork);
        }
    }

    @Override
    public GuestNetwork update(GuestNetwork guestNetwork) throws Exception {
        return guestnetworkRepo.save(guestNetwork);

    }

    @Override
    public void delete(GuestNetwork id) throws Exception {
        guestnetworkRepo.delete(id);
    }

    @Override
    public void delete(Long id) throws Exception {
        guestnetworkRepo.delete(id);

    }

    @Override
    public GuestNetwork find(Long id) throws Exception {
        return guestnetworkRepo.findOne(id);
    }

    @Override
    public Page<GuestNetwork> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return guestnetworkRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<GuestNetwork> findAll() throws Exception {
        return (List<GuestNetwork>) guestnetworkRepo.findAll();

    }

    public HashMap<String, String> optional(GuestNetwork guestNetwork) throws Exception {

        HashMap<String, String> optional = new HashMap<String, String>();

        optional.put("networkofferingid", guestNetwork.getNetworkOffering().getUuid().toString());

        optional.put("zoneid", zoneService.find(1L).getUuid().toString());


        return optional;
    }

}