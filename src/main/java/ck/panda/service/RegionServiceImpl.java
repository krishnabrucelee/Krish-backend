package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Region;
import ck.panda.domain.repository.jpa.RegionRepository;
import ck.panda.util.CloudStackRegionService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Region service implementation class.
 */
@Service
public class RegionServiceImpl implements RegionService {

    /** Region repository reference. */
    @Autowired
    private RegionRepository regionRepo;

    @Autowired
    private CloudStackRegionService regionService;

    @Override
    public Region save(Region region) throws Exception {
        return regionRepo.save(region);
    }

    @Override
    public Region update(Region region) throws Exception {
        return regionRepo.save(region);
    }

    @Override
    public void delete(Region region) throws Exception {
        regionRepo.delete(region);
    }

    @Override
    public void delete(Long id) throws Exception {
        regionRepo.delete(id);
    }

    @Override
    public Region find(Long id) throws Exception {
        return regionRepo.findOne(id);
    }

    @Override
    public List<Region> findAll() throws Exception {
        return (List<Region>) regionRepo.findAll();
    }

    @Override
    public Page<Region> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return regionRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Region> findAllFromCSServer() throws Exception {
         List<Region> regionList = new ArrayList<Region>();
            HashMap<String, String> regionMap = new HashMap<String, String>();

            // 1. Get the list of Zones from CS server using CS connector
            String response = regionService.listRegions("json", regionMap);
            JSONArray regionListJSON = new JSONObject(response).getJSONObject("listregionsresponse")
                    .getJSONArray("region");

            // 2. Iterate the json list, convert the single json entity to Zone
            for (int i = 0, size = regionListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to Zone entity and Add
                // the converted Zone entity to list
                regionList.add(Region.convert(regionListJSON.getJSONObject(i)));
            }
            return regionList;
        }
    }
