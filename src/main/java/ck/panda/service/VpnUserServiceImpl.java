package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.VpnUser;
import ck.panda.domain.repository.jpa.VpnUserRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CSVPNService;
import ck.panda.util.CloudStackAddressService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;

/**
 * Service implementation for VPN user.
 *
 */
@Service
public class VpnUserServiceImpl implements VpnUserService {

    /** VPN user repository reference. */
    @Autowired
    private VpnUserRepository vpnUserRepository;

    /** Object server created for CloudStackServer. */
    @Autowired
    private ConfigUtil configServer;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** CloudStack VPN service for connectivity with cloudstack. */
    @Autowired
    private CSVPNService csVPNService;

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** CloudStack IP address service for connectivity with cloudstack. */
    @Autowired
    private CloudStackAddressService csipaddressService;

    @Override
    public VpnUser save(VpnUser vpnUser) throws Exception {
        vpnUser.setIsActive(true);
        if (vpnUser.getSyncFlag()) {
            return saveVpnUser(vpnUser);
        } else {
            return vpnUserRepository.save(vpnUser);
        }
    }

    @Override
    public VpnUser update(VpnUser vpnUser) throws Exception {
        return vpnUserRepository.save(vpnUser);
    }

    @Override
    public void delete(VpnUser vpnUser) throws Exception {
        vpnUserRepository.delete(vpnUser);
    }

    @Override
    public void delete(Long id) throws Exception {
        vpnUserRepository.delete(id);
    }

    @Override
    public VpnUser find(Long id) throws Exception {
        return vpnUserRepository.findOne(id);
    }

    @Override
    public Page<VpnUser> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return vpnUserRepository.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<VpnUser> findAll() throws Exception {
        return (List<VpnUser>) vpnUserRepository.findAll();
    }

    @Override
    public Page<VpnUser> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        return vpnUserRepository.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
    }

    @Override
    public List<VpnUser> findAllFromCSServer() throws Exception {
        List<VpnUser> vpnUserList = new ArrayList<VpnUser>();
        //Get all the VPN user details
        configServer.setServer(1L);
        HashMap<String, String> vpnUserOptional = new HashMap<String, String>();
        vpnUserOptional.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
        String vpnUserResponse = csVPNService.listVpnUsers(vpnUserOptional, CloudStackConstants.JSON);
        JSONArray vpnUserRemoteListJSON = null;
        JSONObject responseVpnUserObject = new JSONObject(vpnUserResponse).getJSONObject(CloudStackConstants.CS_LIST_VPN_USERS_RESPONSE);
        if (responseVpnUserObject.has(CloudStackConstants.CS_VPN_USER)) {
            vpnUserRemoteListJSON = responseVpnUserObject.getJSONArray(CloudStackConstants.CS_VPN_USER);
            for (int i = 0; i < vpnUserRemoteListJSON.length(); i++) {
                VpnUser vpnUser = VpnUser.convert(vpnUserRemoteListJSON.getJSONObject(i));
                vpnUser.setDomainId(convertEntityService.getDomainId(vpnUser.getTransDomainId()));
                vpnUser.setDepartmentId(convertEntityService.getDepartmentByUsername(vpnUser.getTransDepartment(), vpnUser.getDomainId()));
                if (vpnUser.getTransProjectId() != null) {
                    vpnUser.setProjectId(convertEntityService.getProjectId(vpnUser.getTransProjectId()));
                }
                vpnUser.setIsActive(true);
                vpnUserList.add(vpnUser);
            }
        }
        return vpnUserList;
    }

    @Override
    public VpnUser softDelete(VpnUser vpnUser) throws Exception {
        if (vpnUser.getSyncFlag()) {
            Errors errors = null;
            try {
                configServer.setUserServer();
                HashMap<String, String> optional = new HashMap<String, String>();
                optional.put(CloudStackConstants.CS_DOMAIN_ID, convertEntityService.getDomainById(vpnUser.getDomainId()).getUuid());
                optional.put(CloudStackConstants.CS_ACCOUNT, convertEntityService.getDepartmentById(vpnUser.getDepartmentId()).getUserName());

                String deleteRemoteAccess = csVPNService.removeVpnUser(vpnUser.getUserName(), optional, CloudStackConstants.JSON);
                JSONObject jobId = new JSONObject(deleteRemoteAccess).getJSONObject(CloudStackConstants.CS_REMOVE_VPN_USER_RESPONSE);
                if (jobId.has(CloudStackConstants.CS_ERROR_CODE)) {
                    errors = validator.sendGlobalError(jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
                    if (errors.hasErrors()) {
                        throw new BadCredentialsException(jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
                    }
                }
                if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                    Thread.sleep(5000);
                    String jobResponse = csipaddressService.associatedJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
                    JSONObject jobresults = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);

                    if (jobresults.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.SUCCEEDED_JOB_STATUS)) {
                        vpnUser.setIsActive(false);
                        return vpnUserRepository.save(vpnUser);
                    } else if (jobresults.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.ERROR_JOB_STATUS)) {
                    	if(jobresults.has(CloudStackConstants.CS_JOB_RESULT)) {
                    		errors = validator.sendGlobalError(jobresults.getJSONObject(CloudStackConstants.CS_JOB_RESULT).getString(CloudStackConstants.CS_ERROR_TEXT));
                            if (errors.hasErrors()) {
                                throw new BadCredentialsException(jobresults.getJSONObject(CloudStackConstants.CS_JOB_RESULT).getString(CloudStackConstants.CS_ERROR_TEXT));
                            }
                    	}
                    }
                }
            } catch (BadCredentialsException e) {
                throw new BadCredentialsException(e.getMessage());
            }
            return vpnUser;
        }
        vpnUser.setIsActive(false);
        return vpnUserRepository.save(vpnUser);
    }

    @Override
    public VpnUser findbyUUID(String uuid) throws Exception {
        return vpnUserRepository.findByUUID(uuid);
    }

    @Override
    public List<VpnUser> findByDomainWithDepartment(Long domainId, Long departmentId) throws Exception {
        return (List<VpnUser>) vpnUserRepository.findByDomainWithDepartment(domainId, departmentId, true);
    }

    @Override
    public VpnUser findbyDomainWithAccountAndUser(String userName, String account, String domainUUid) throws Exception {
        Domain domain = convertEntityService.getDomain(domainUUid);
        return vpnUserRepository.findbyDomainWithAccountAndUser(userName,
            convertEntityService.getDepartmentByUsername(account, domain.getId()), domain.getId(), true);
    }

    /**
     * Add user details for VPN.
     *
     * @param vpnUser entity object
     * @return VPN user list
     * @throws Exception unhandled errors.
     */
    public VpnUser saveVpnUser(VpnUser vpnUser) throws Exception {
        Errors errors = null;
        try {
            configServer.setUserServer();
            HashMap<String, String> optional = new HashMap<String, String>();
            optional.put(CloudStackConstants.CS_DOMAIN_ID, convertEntityService.getDomainById(vpnUser.getDomainId()).getUuid());
            optional.put(CloudStackConstants.CS_ACCOUNT, convertEntityService.getDepartmentById(vpnUser.getDepartmentId()).getUserName());

            String createVpnUser = csVPNService.addVpnUser(vpnUser.getPassword(), vpnUser.getUserName(), optional, CloudStackConstants.JSON);
            JSONObject jobId = new JSONObject(createVpnUser).getJSONObject(CloudStackConstants.CS_ADD_VPN_USER_RESPONSE);
            if (jobId.has(CloudStackConstants.CS_ERROR_CODE)) {
                errors = validator.sendGlobalError(jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
                if (errors.hasErrors()) {
                    throw new BadCredentialsException(jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
                }
            }
            if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                Thread.sleep(5000);
                String jobResponse = csipaddressService.associatedJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
                JSONObject jobresults = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);

                if (jobresults.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.SUCCEEDED_JOB_STATUS)) {
                    JSONObject jobresultReponse = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE)
                            .getJSONObject(CloudStackConstants.CS_JOB_RESULT).getJSONObject(CloudStackConstants.CS_VPN_USER);
                    vpnUser.setUuid(jobresultReponse.getString(CloudStackConstants.CS_ID));
                    if (vpnUserRepository.findByUUID(vpnUser.getUuid()) == null) {
                        vpnUserRepository.save(vpnUser);
                    }
                } else if (jobresults.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.ERROR_JOB_STATUS)) {
                	if(jobresults.has(CloudStackConstants.CS_JOB_RESULT)) {
                		errors = validator.sendGlobalError(jobresults.getJSONObject(CloudStackConstants.CS_JOB_RESULT).getString(CloudStackConstants.CS_ERROR_TEXT));
                        if (errors.hasErrors()) {
                            throw new BadCredentialsException(jobresults.getJSONObject(CloudStackConstants.CS_JOB_RESULT).getString(CloudStackConstants.CS_ERROR_TEXT));
                        }
                	}
                }
            }
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        }

        return vpnUser;
    }
}
