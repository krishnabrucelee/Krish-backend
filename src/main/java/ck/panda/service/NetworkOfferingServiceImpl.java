package ck.panda.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.NetworkOffering;
import ck.panda.domain.repository.jpa.NetworkOfferingRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service implementation for NetworkOffering entity.
 *
 */
@Service
public class NetworkOfferingServiceImpl implements NetworkOfferingService {

    /** NetworkOffering repository reference. */
    @Autowired
    private NetworkOfferingRepository networkRepo;

    @Override
    public NetworkOffering save(NetworkOffering network) throws Exception {
        return networkRepo.save(network);
    }

    @Override
    public NetworkOffering update(NetworkOffering network) throws Exception {
        return networkRepo.save(network);
    }

    @Override
    public void delete(NetworkOffering id) throws Exception {
        networkRepo.delete(id);
    }

    @Override
    public void delete(Long id) throws Exception {
        networkRepo.delete(id);
    }

    @Override
    public NetworkOffering find(Long id) throws Exception {
        return networkRepo.findOne(id);
    }

    @Override
    public Page<NetworkOffering> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return null;
    }

    @Override
    public List<NetworkOffering> findAll() throws Exception {
        return (List<NetworkOffering>) networkRepo.findAll();
    }

}
