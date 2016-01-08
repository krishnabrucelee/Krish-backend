/**
 *
 */
package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.PortForwarding;
import ck.panda.domain.repository.jpa.PortForwardingRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Port Forwarding Service Implementation.
 *
 */
@Service
public class PortForwardingServiceImpl implements PortForwardingService {

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** VolumeRepository repository reference. */
    @Autowired
    private PortForwardingRepository portForwardingRepo;

    @Override
    public PortForwarding save(PortForwarding portForwarding) throws Exception {
        Errors errors = validator.rejectIfNullEntity("portForwarding", portForwarding);
        errors = validator.validateEntity(portForwarding, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {

            return portForwardingRepo.save(portForwarding);
        }
    }

    @Override
    public PortForwarding update(PortForwarding portForwarding) throws Exception {
        return portForwardingRepo.save(portForwarding);
    }

    @Override
    public void delete(PortForwarding portForwarding) throws Exception {
        portForwardingRepo.delete(portForwarding);
    }

    @Override
    public void delete(Long id) throws Exception {
        portForwardingRepo.delete(id);
    }

    @Override
    public PortForwarding find(Long id) throws Exception {
        return portForwardingRepo.findOne(id);
    }

    @Override
    public Page<PortForwarding> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return portForwardingRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<PortForwarding> findAll() throws Exception {
        return (List<PortForwarding>) portForwardingRepo.findAll();
    }
}
