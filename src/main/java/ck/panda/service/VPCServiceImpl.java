package ck.panda.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;

import ck.panda.domain.entity.VPC;
import ck.panda.util.CloudStackResourceCapacity;
import ck.panda.util.domain.vo.PagingAndSorting;

public class VPCServiceImpl implements VPCService {
	  /** Convert Entity service references. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Domain service reference. */
    @Autowired
    private DomainService domainService;

    /** Department service reference. */
    @Autowired
    private DepartmentService departmentService;

    /** Resource Limit Department service reference. */
    @Autowired
    private ResourceLimitDepartmentService resourceLimitDepartmentService;

    /** Resource Limit Project service reference. */
    @Autowired
    private ResourceLimitProjectService resourceLimitProjectService;

    /** CloudStack connector reference for resource capacity. */
    @Autowired
    private CloudStackResourceCapacity cloudStackResourceCapacity;

    /** Update Resource Count service reference. */
    @Autowired
    private UpdateResourceCountService updateResourceCountService;

	@Override
	public VPC save(VPC t) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VPC update(VPC t) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(VPC t) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Long id) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public VPC find(Long id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<VPC> findAll(PagingAndSorting pagingAndSorting) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VPC> findAll() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<VPC> findAllFromCSServerByDomain() throws Exception {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VPC findByUUID(String uuid) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VPC findById(Long id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VPC> findByDepartmentAndVpcIsActive(Long department, Boolean isActive) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VPC softDelete(VPC vpc) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<VPC> findAllByActive(PagingAndSorting page, Long userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VPC> findByProjectAndVpcIsActive(Long projectId, Boolean isActive) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VPC> findAllByActive(Boolean isActive) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@PreAuthorize("hasPermission(#network.getSyncFlag(), 'ADD_VPC')")
	public VPC save(VPC vpc, Long userId) throws Exception {
		if (vpc.getSyncFlag()) {

		}
		return vpc;
	}

	@Override
	public VPC restartNetwork(VPC vpc) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VPC ipRelease(VPC vpc) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<VPC> findAllByDomainId(Long domainId, PagingAndSorting page) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VPC> findAllByDomainAndIsActive(Long domainId, Boolean isActive) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VPC> findAllByUserId(Long userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VPC> findAllByDomainId(Long domainId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
