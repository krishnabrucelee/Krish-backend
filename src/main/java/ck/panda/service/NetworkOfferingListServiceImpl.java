package ck.panda.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.NetworkOfferingServiceList;
import ck.panda.domain.repository.jpa.NetworkOfferingServiceListRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service implementation for NetworkOfferingServiceList entity.
 *
 */
@Service
public class NetworkOfferingListServiceImpl implements NetworkOfferingListService {

    /** NetworkOfferingServiceListRepository repository reference. */
    @Autowired
    private NetworkOfferingServiceListRepository networklistRepo;

    @Override
    public NetworkOfferingServiceList save(NetworkOfferingServiceList support) throws Exception {
        return networklistRepo.save(support);
    }

    @Override
    public NetworkOfferingServiceList update(NetworkOfferingServiceList support) throws Exception {
        return networklistRepo.save(support);
    }

    @Override
    public void delete(NetworkOfferingServiceList id) throws Exception {
        networklistRepo.delete(id);
    }

    @Override
    public void delete(Long id) throws Exception {
        networklistRepo.delete(id);

    }

    @Override
    public NetworkOfferingServiceList find(Long id) throws Exception {
        return networklistRepo.findOne(id);
    }

    @Override
    public Page<NetworkOfferingServiceList> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return null;
    }

    @Override
    public List<NetworkOfferingServiceList> findAll() throws Exception {
        return (List<NetworkOfferingServiceList>) networklistRepo.findAll();
    }

}
