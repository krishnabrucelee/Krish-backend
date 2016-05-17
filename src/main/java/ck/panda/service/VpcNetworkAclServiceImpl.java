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
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.VpcAcl;
import ck.panda.domain.entity.VpcNetworkAcl;
import ck.panda.domain.repository.jpa.VpcNetworkAclRepository;
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
public class VpcNetworkAclServiceImpl implements VpcNetworkAclService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VpcNetworkAclServiceImpl.class);

    /** VPC ACL repository reference. */
    @Autowired
    private VpcNetworkAclRepository vpcNetworkAclRepo;

    /** VPC ACL cloudstack service reference. */
    @Autowired
    private CloudStackVPCService cloudStackVPCService;

    /** VPC Acl Service reference. */
    @Autowired
    private VpcAclService vpcAclService;

    /** Configuration Utilities. */
    @Autowired
    private ConfigUtil config;

    @Override
    public VpcNetworkAcl save(VpcNetworkAcl vpcNetworkAcl) throws Exception {
        if (!vpcNetworkAcl.getSyncFlag()) {
        return vpcNetworkAclRepo.save(vpcNetworkAcl);
        }
        return vpcNetworkAcl;
    }

    @Override
    public VpcNetworkAcl update(VpcNetworkAcl vpcNetworkAcl) throws Exception {
        if (vpcNetworkAcl.getSyncFlag()) {
            HashMap<String, String> vpcAclMap = new HashMap<String, String>();
            config.setServer(1L);
            vpcAclMap.put("action", vpcNetworkAcl.getAction());
            vpcAclMap.put("cidrlist", vpcNetworkAcl.getCidrList());
            vpcAclMap.put("number", vpcNetworkAcl.getRuleNumber());
            vpcAclMap.put("traffictype", vpcNetworkAcl.getTrafficType());
            if (vpcNetworkAcl.getProtocolNumber() != null) {
                vpcAclMap.put("protocol", vpcNetworkAcl.getProtocolNumber());
            } else {
                vpcAclMap.put("protocol", vpcNetworkAcl.getProtocol());
            }
            if (vpcNetworkAcl.getStartPort() != null) {
                vpcAclMap.put("startport", vpcNetworkAcl.getStartPort());
            }
            if (vpcNetworkAcl.getEndPort() != null) {
                vpcAclMap.put("endport", vpcNetworkAcl.getEndPort());
            }
            if (vpcNetworkAcl.getIcmpCode() != null) {
                vpcAclMap.put("icmpcode", vpcNetworkAcl.getIcmpCode());
            }
            if (vpcNetworkAcl.getIcmpType() != null) {
                vpcAclMap.put("icmptype", vpcNetworkAcl.getIcmpType());
            }
            try {
                String vpcAclResponse = cloudStackVPCService.updateNetworkACL(vpcNetworkAcl.getUuid(), "json",
                        vpcAclMap);
                JSONObject jobId = new JSONObject(vpcAclResponse).getJSONObject("createnetworkaclresponse");
                if (jobId.has(CloudStackConstants.CS_ERROR_CODE)) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED,
                            jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
                }
                JSONObject createVpcResponseJSON = new JSONObject(vpcAclResponse)
                        .getJSONObject("createnetworkaclresponse");

                // Checking job id.
                if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                    String jobResponse = cloudStackVPCService
                            .vpcJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
                    JSONObject jobresult = new JSONObject(jobResponse)
                            .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                    if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS)
                            .equals(GenericConstants.ERROR_JOB_STATUS)) {
                        JSONObject errorText = jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT);
                        throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED,
                                errorText.getString(CloudStackConstants.CS_ERROR_TEXT));
                    }
                }
                vpcNetworkAcl.setForDisplay(true);
                vpcNetworkAcl.setIsActive(true);
            } catch (ApplicationException e) {
                LOGGER.error("ERROR AT VPC ACL UPDATION", e);
                throw new ApplicationException(e.getErrors());
            }
        }
        return vpcNetworkAclRepo.save(vpcNetworkAcl);
    }

    @Override
    public void delete(VpcNetworkAcl vpcNetworkAcl) throws Exception {
        if (!vpcNetworkAcl.getSyncFlag()) {
            vpcNetworkAcl.setIsActive(false);
        }
        vpcNetworkAclRepo.delete(vpcNetworkAcl);

    }

    @Override
    public void delete(Long id) throws Exception {
        vpcNetworkAclRepo.delete(id);
    }

    @Override
    public VpcNetworkAcl find(Long id) throws Exception {
        return vpcNetworkAclRepo.findOne(id);
    }

    @Override
    public Page<VpcNetworkAcl> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return vpcNetworkAclRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<VpcNetworkAcl> findAll() throws Exception {
        return (List<VpcNetworkAcl>) vpcNetworkAclRepo.findAll();
    }

    @Override
    public List<VpcNetworkAcl> findByAclIdAndIsActive(Long aclId) {
        return vpcNetworkAclRepo.findByAclIdAndIsActive(aclId, true);
    }

    @Override
    public VpcNetworkAcl addVpcAcl(VpcNetworkAcl vpcNetworkAcl, Long aclId) throws Exception {
        HashMap<String, String> vpcAclMap = new HashMap<String, String>();
        vpcAclMap.put("aclid", vpcAclService.find(aclId).getUuid());
        vpcAclMap.put("action", vpcNetworkAcl.getAction());
        vpcAclMap.put("cidrlist", vpcNetworkAcl.getCidrList());
        vpcAclMap.put("number", vpcNetworkAcl.getRuleNumber());
        vpcAclMap.put("traffictype", vpcNetworkAcl.getTrafficType());
        if (vpcNetworkAcl.getProtocolNumber() != null) {
            vpcAclMap.put("protocol", vpcNetworkAcl.getProtocolNumber());
        } else {
            vpcAclMap.put("protocol", vpcNetworkAcl.getProtocol());
        }
        if (vpcNetworkAcl.getStartPort() != null) {
            vpcAclMap.put("startport", vpcNetworkAcl.getStartPort());
        }
        if (vpcNetworkAcl.getEndPort() != null) {
            vpcAclMap.put("endport", vpcNetworkAcl.getEndPort());
        }
        if (vpcNetworkAcl.getIcmpCode() != null) {
            vpcAclMap.put("icmpcode", vpcNetworkAcl.getIcmpCode());
        }
        if (vpcNetworkAcl.getIcmpType() != null) {
            vpcAclMap.put("icmptype", vpcNetworkAcl.getIcmpType());
        }

        try {
        config.setUserServer();
        String vpcAclResponse= cloudStackVPCService.createNetworkACL(CloudStackConstants.JSON, vpcAclMap);
        JSONObject jobId = new JSONObject(vpcAclResponse).getJSONObject("createnetworkaclresponse");
        if (jobId.has(CloudStackConstants.CS_ERROR_CODE)) {
            throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
        }
        JSONObject createVpcResponseJSON = new JSONObject(vpcAclResponse)
                .getJSONObject("createnetworkaclresponse");

        // Checking job id.
        if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
            String jobResponse = cloudStackVPCService.vpcJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID),
                    CloudStackConstants.JSON);
            JSONObject jobresult = new JSONObject(jobResponse)
                    .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
            if(jobresult.getString(CloudStackConstants.CS_JOB_STATUS)
                    .equals(GenericConstants.ERROR_JOB_STATUS)){
                throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, jobresult.getString(CloudStackConstants.CS_ERROR_TEXT));
            }
        }
        vpcNetworkAcl.setUuid(createVpcResponseJSON.getString(CloudStackConstants.CS_ID));
        vpcNetworkAcl.setVpcAclId(aclId);
        vpcNetworkAcl.setForDisplay(true);
        vpcNetworkAcl.setIsActive(true);
        } catch (ApplicationException e) {
            LOGGER.error("ERROR AT VPC ACL CREATION", e);
            throw new ApplicationException(e.getErrors());
        }
        return vpcNetworkAclRepo.save(vpcNetworkAcl);
    }

    @Override
    public VpcNetworkAcl softDelete(VpcNetworkAcl vpcNetworkAcl) throws Exception {
        HashMap<String, String> vpcAclMap = new HashMap<String, String>();
        try {
            config.setUserServer();
            String vpcAclResponse= cloudStackVPCService.deleteNetworkACL(vpcNetworkAcl.getUuid(), CloudStackConstants.JSON, vpcAclMap);
            JSONObject jobId = new JSONObject(vpcAclResponse).getJSONObject("deletenetworkaclresponse");
            // Checking job id.
            if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                String jobResponse = cloudStackVPCService.vpcJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID),
                        CloudStackConstants.JSON);
                JSONObject jobresult = new JSONObject(jobResponse)
                        .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                if(jobresult.getString(CloudStackConstants.CS_JOB_STATUS)
                        .equals(GenericConstants.ERROR_JOB_STATUS)){
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, jobresult.getString(CloudStackConstants.CS_ERROR_TEXT));
                }
            }
            vpcNetworkAcl.setIsActive(false);
            } catch (ApplicationException e) {
                LOGGER.error("ERROR AT VPC ACL DELETION", e);
                throw new ApplicationException(e.getErrors());
            }
        return vpcNetworkAclRepo.save(vpcNetworkAcl);
    }

    @Override
    public List<VpcNetworkAcl> findAllFromCSServer() throws Exception {
        List<VpcAcl> aclList = vpcAclService.findAllByIsActive(true);
        List<VpcNetworkAcl> aclElementsList = new ArrayList<VpcNetworkAcl>();
        for(VpcAcl vpcACL : aclList) {
        HashMap<String, String> aclMap = new HashMap<String, String>();
        aclMap.put("listall", "true");
        aclMap.put("aclid", vpcACL.getUuid());
        config.setServer(1L);
        // 1. Get the list of domains from CS server using CS connector
        String response = cloudStackVPCService.listNetworkACLItems(aclMap, "json");
        JSONObject domainListJSON = new JSONObject(response).getJSONObject("listnetworkaclsresponse");
        if (domainListJSON != null && domainListJSON.has("networkacl")) {
            JSONArray ingressListJSON = domainListJSON.getJSONArray("networkacl");
        // 2. Iterate the json list, convert the single json entity to domain
        for (int i = 0, size = ingressListJSON.length(); i < size; i++) {
            // 2.1 Call convert by passing JSONObject to Domain entity and Add
            // the converted Domain entity to list
            VpcNetworkAcl vpcAcl = VpcNetworkAcl.convert(ingressListJSON.getJSONObject(i));
            vpcAcl.setVpcAclId(vpcAclService.findbyUUID(vpcAcl.getAclId()).getId());
            aclElementsList.add(vpcAcl);
            }
          }
        }
        return aclElementsList;
    }


}
