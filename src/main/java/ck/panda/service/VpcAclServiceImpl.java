package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.VPC;
import ck.panda.domain.entity.VpcAcl;
import ck.panda.domain.repository.jpa.VpcAclRepository;
import ck.panda.util.CloudStackVPCService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.CustomGenericException;

/**
 * VPC ACL service implementation used to get list of VPC ACL and save the VPC ACL from cloudstack server.
 *
 */
@Service
public class VpcAclServiceImpl implements VpcAclService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VpcAclServiceImpl.class);

    /** VPC ACL repository reference. */
    @Autowired
    private VpcAclRepository vpcAclRepo;

    /** VPC Service reference. */
    @Autowired
    private VPCService vpcService;

    /** VPC ACL cloudstack service reference. */
    @Autowired
    private CloudStackVPCService cloudStackVPCService;

    /** Configuration Utilities. */
    @Autowired
    private ConfigUtil config;

    /** Constant for list network ACL list response. */
    public static final String CS_LIST_NETWORK_ACL_LIST_RESPONSE = "listnetworkacllistsresponse";

    /** Constant for network ACL list. */
    public static final String CS_NETWORK_ACL_LIST = "networkacllist";

    @Override
    public VpcAcl save(VpcAcl vpcAcl) throws Exception {
        return vpcAclRepo.save(vpcAcl);
    }

    @Override
    public VpcAcl update(VpcAcl vpcAcl) throws Exception {
        return vpcAclRepo.save(vpcAcl);
    }

    @Override
    public void delete(VpcAcl vpcAcl) throws Exception {
        vpcAcl.setIsActive(false);
        vpcAclRepo.save(vpcAcl);
    }

    @Override
    public void delete(Long id) throws Exception {
        vpcAclRepo.delete(id);
    }

    @Override
    public VpcAcl find(Long id) throws Exception {
        return vpcAclRepo.findOne(id);
    }

    @Override
    public VpcAcl findbyUUID(String uuid) throws Exception {
        return vpcAclRepo.findByUuid(uuid, true);
    }

    @Override
    public Page<VpcAcl> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return vpcAclRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<VpcAcl> findAll() throws Exception {
        return (List<VpcAcl>) vpcAclRepo.findAll();
    }

    @Override
    public List<VpcAcl> findAllByIsActive(Boolean isActive) throws Exception {
        return (List<VpcAcl>) vpcAclRepo.findAllByIsActive(true);
    }

    @Override
    public List<VpcAcl> findAllFromCSServer() throws Exception {

        List<VpcAcl> vpcAclList = new ArrayList<VpcAcl>();
        HashMap<String, String> vpcAclMap = new HashMap<String, String>();
        vpcAclMap.put("listall", "true");
        JSONArray vpcAclListJSON = null;
        // 1. Get the list of VPC ACL from CS server using CS connector
        String response = cloudStackVPCService.listNetworkACLLists(vpcAclMap, CloudStackConstants.JSON);
        JSONObject responseObject = new JSONObject(response).getJSONObject(CS_LIST_NETWORK_ACL_LIST_RESPONSE);
        if (responseObject.has(CS_NETWORK_ACL_LIST)) {
            vpcAclListJSON = responseObject.getJSONArray(CS_NETWORK_ACL_LIST);
            // 2. Iterate the json list, convert the single json entity to VPC ACL
            for (int i = 0, size = vpcAclListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to VPC ACL entity and Add
                // the converted VPC ACL entity to list
                VpcAcl vpcAcl = VpcAcl.convert(vpcAclListJSON.getJSONObject(i));
                VPC vpc = vpcService.findByUUID(vpcAcl.getTransvpcId());
                if (vpc != null) {
                    vpcAcl.setVpcId(vpc.getId());
                }
                vpcAcl.setIsActive(true);
                vpcAclList.add(vpcAcl);
            }
        }
        return vpcAclList;
    }

    @Override
    public VpcAcl findVpcAclById(Long id) throws Exception {
        return vpcAclRepo.findOne(id);
    }

    @Override
    public VpcAcl addVpcAcl(VpcAcl vpcAcl, Long vpcId) throws Exception {
        HashMap<String, String> vpcAclMap = new HashMap<String, String>();
        vpcAclMap.put("vpcid", vpcService.find(vpcId).getUuid());
        try {
            config.setUserServer();
            String vpcAclResponse = cloudStackVPCService.createNetworkACLList(vpcAcl.getName(), vpcAcl.getDescription(),
                    CloudStackConstants.JSON, vpcAclMap);
            JSONObject createVpcResponseJSON = new JSONObject(vpcAclResponse)
                    .getJSONObject("createnetworkacllistresponse");
            JSONObject jobId = new JSONObject(vpcAclResponse).getJSONObject("createnetworkacllistresponse");
            // Checking job id.
            if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                String jobResponse = cloudStackVPCService.vpcJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID),
                        CloudStackConstants.JSON);
                JSONObject jobresult = new JSONObject(jobResponse)
                        .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS).equals(GenericConstants.ERROR_JOB_STATUS)) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED,
                            jobresult.getString(CloudStackConstants.CS_ERROR_TEXT));
                }
            }
            vpcAcl.setUuid(createVpcResponseJSON.getString(CloudStackConstants.CS_ID));
            vpcAcl.setVpcId(vpcId);
            vpcAcl.setForDisplay(true);
            vpcAcl.setIsActive(true);
        } catch (ApplicationException e) {
            LOGGER.error("ERROR AT VPC ACL CREATION", e);
            throw new ApplicationException(e.getErrors());
        }
        return vpcAclRepo.save(vpcAcl);

    }

    @Override
    public List<VpcAcl> findByVpcIdAndIsActive(Long vpcId) throws Exception {
        return vpcAclRepo.findByVpcIdAndIsActive(vpcId, true);
    }

    @Override
    public VpcAcl softDelete(VpcAcl vpcAcl) throws Exception {
        HashMap<String, String> vpcAclMap = new HashMap<String, String>();
        try {
            config.setUserServer();
            String vpcAclResponse = cloudStackVPCService.deleteNetworkACLList(vpcAcl.getUuid(),
                    CloudStackConstants.JSON, vpcAclMap);
            JSONObject jobId = new JSONObject(vpcAclResponse).getJSONObject("deletenetworkacllistresponse");
            // Checking job id.
            if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                String jobResponse = cloudStackVPCService.vpcJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID),
                        CloudStackConstants.JSON);
                JSONObject jobresult = new JSONObject(jobResponse)
                        .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS).equals(GenericConstants.ERROR_JOB_STATUS)) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED,
                            jobresult.getString(CloudStackConstants.CS_ERROR_TEXT));
                }
            }
            vpcAcl.setIsActive(false);
        } catch (ApplicationException e) {
            LOGGER.error("ERROR AT VPC ACL DELETION", e);
            throw new ApplicationException(e.getErrors());
        }
        return vpcAclRepo.save(vpcAcl);
    }

    @Override
    public VpcAcl findByUUID(String uuid) throws Exception {
        return vpcAclRepo.findByUUID(uuid);
    }

}
