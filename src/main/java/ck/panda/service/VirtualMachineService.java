package ck.panda.service;

import org.springframework.stereotype.Service;

import ck.panda.domain.entity.VmInstance;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Virtual Machine.
 *
 * This service provides basic CRUD and essential api's for Virtual Machine related business actions.
 */
@Service
public interface VirtualMachineService extends CRUDService<VmInstance> {

	/**
	 * Find vm instance by uuid.
	 * @param uuid instance uuid.
	 * @return
	 */
	VmInstance findByUUID(String uuid);
}
