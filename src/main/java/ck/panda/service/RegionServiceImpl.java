package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Region;
import ck.panda.domain.repository.jpa.RegionRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Region service implementation class.
 */
@Service
public class RegionServiceImpl implements RegionService {

    /** Region repository reference. */
    @Autowired
    private RegionRepository regionRepo;

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
    	return null;
    }
}
