package ck.panda.service;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.VmIpaddress;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for VmIpaddress entity.
 *
 */
@Service
public interface VmIpaddressService extends CRUDService<VmIpaddress> {
}