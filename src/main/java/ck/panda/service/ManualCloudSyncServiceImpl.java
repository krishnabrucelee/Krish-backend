package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.ManualCloudSync;
import ck.panda.domain.repository.jpa.ManualCloudSyncRepository;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.exception.EntityNotFoundException;

/** Manual Cloud Sync service implementation class. */
@Service
public class ManualCloudSyncServiceImpl implements ManualCloudSyncService {

    /** Manual Cloud Sync repository reference. */
    @Autowired
    private ManualCloudSyncRepository manualCloudSyncRepository;

    @Override
    public ManualCloudSync save(ManualCloudSync manualCloudSync) throws Exception {
        manualCloudSync.setIsActive(true);
        return manualCloudSyncRepository.save(manualCloudSync);
    }

    @Override
    public ManualCloudSync update(ManualCloudSync manualCloudSync) throws Exception {
        return manualCloudSyncRepository.save(manualCloudSync);
    }

    @Override
    public void delete(ManualCloudSync manualCloudSync) throws Exception {
        manualCloudSyncRepository.delete(manualCloudSync);
    }

    @Override
    public void delete(Long id) throws Exception {
        manualCloudSyncRepository.delete(id);
    }

    @Override
    public ManualCloudSync find(Long id) throws Exception {
        ManualCloudSync manualCloudSync = manualCloudSyncRepository.findOne(id);
        if (manualCloudSync == null) {
            throw new EntityNotFoundException("error.manual.cloud.sync.not.found");
        }
        return manualCloudSync;
    }

    @Override
    public Page<ManualCloudSync> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return manualCloudSyncRepository.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<ManualCloudSync> findAll() throws Exception {
        return (List<ManualCloudSync>) manualCloudSyncRepository.findAll();
    }

    @Override
    public ManualCloudSync findBySyncName(String keyName) throws Exception {
        return manualCloudSyncRepository.findBySyncName(keyName);
    }

}
