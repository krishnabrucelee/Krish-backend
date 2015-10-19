package ck.panda.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Zone;
import ck.panda.domain.repository.jpa.ZoneRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service implementation for Zone entity.
 *
 */
@Service
public class ZoneServiceImpl implements ZoneService {

    /** Zone repository reference. */
    @Autowired
    private ZoneRepository zoneRepo;

    @Override
    public Zone save(Zone zone) throws Exception {
        return zoneRepo.save(zone);
    }

    @Override
    public Zone update(Zone zone) throws Exception {
        return zoneRepo.save(zone);
    }

    @Override
    public void delete(Zone zone) throws Exception {
        zoneRepo.delete(zone);

    }

    @Override
    public void delete(Long id) throws Exception {
        zoneRepo.delete(id);
    }

    @Override
    public Zone find(Long id) throws Exception {
        return zoneRepo.findOne(id);
    }

    @Override
    public List<Zone> findAll() throws Exception {
        return (List<Zone>) zoneRepo.findAll();
    }

    @Override
    public Page<Zone> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return null;
    }
}
