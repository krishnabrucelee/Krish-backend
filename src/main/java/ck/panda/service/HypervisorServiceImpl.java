package ck.panda.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import ck.panda.domain.entity.Hypervisor;
import ck.panda.domain.repository.jpa.HypervisorRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Hypervisor service implementation used to get list of hypervisor and save the hypervisor from
 * cloudstack server.
 *
 */
public class HypervisorServiceImpl implements HypervisorService {

    /** Hypervisor repository reference. */
    @Autowired
    private HypervisorRepository hypervisorRepo;

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
        return null;
    }

    @Override
    public List<Hypervisor> findAll() throws Exception {
       return (List<Hypervisor>) hypervisorRepo.findAll();
    }
}
