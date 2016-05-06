package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.VpcAcl;
import ck.panda.domain.repository.jpa.VpcAclRepository;
import ck.panda.util.CloudStackVPCService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * VPC ACL service implementation used to get list of VPC ACL and save the VPC ACL from cloudstack server.
 *
 */
@Service
public class VpcAclServiceImpl implements VpcAclService {

    /** VPC ACL repository reference. */
    @Autowired
    private VpcAclRepository vpcAclRepo;

    /** VPC ACL cloudstack service reference. */
    @Autowired
    private CloudStackVPCService cloudStackVPCService;

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
        vpcAclRepo.delete(vpcAcl);
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
    public Page<VpcAcl> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return vpcAclRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<VpcAcl> findAll() throws Exception {
        return (List<VpcAcl>) vpcAclRepo.findAll();
    }

    @Override
    public List<VpcAcl> findAllFromCSServer() throws Exception {

        List<VpcAcl> vpcAclList = new ArrayList<VpcAcl>();
        HashMap<String, String> vpcAclMap = new HashMap<String, String>();
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
                vpcAclList.add(VpcAcl.convert(vpcAclListJSON.getJSONObject(i)));
            }
        }
        return vpcAclList;
    }

}
