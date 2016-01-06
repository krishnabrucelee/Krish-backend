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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.Network.Status;
import ck.panda.domain.entity.NetworkOffering;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Zone;
import ck.panda.domain.repository.jpa.DepartmentReposiory;
import ck.panda.domain.repository.jpa.DomainRepository;
import ck.panda.domain.repository.jpa.NetworkOfferingRepository;
import ck.panda.domain.repository.jpa.NetworkRepository;
import ck.panda.domain.repository.jpa.UserRepository;
import ck.panda.domain.repository.jpa.ZoneRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackNetworkService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Service implementation for Network entity.
 *
 */
@Service
@SuppressWarnings("PMD.CyclomaticComplexity")
public class NetworkServiceImpl implements NetworkService {

    /** Network repository reference. */
    @Autowired
    private NetworkRepository networkRepo;

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Service implementation for Cloudstack Network . */
    @Autowired
    private CloudStackNetworkService csNetwork;

    /** Configuration Utilities. */
    @Autowired
    private ConfigUtil config;

    /** Token Detail Utilities. */
    @Autowired
    private TokenDetails tokenDetails;

    /** Token Detail Utilities. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Project service reference. */
    @Autowired
    private ProjectService projectService;

    /** Zone service reference. */
    @Autowired
    private ZoneService zoneService;

    /** NetworkOffering service reference. */
    @Autowired
    private NetworkOfferingService networkOfferingService;

    /** Domain service reference. */
    @Autowired
    private DomainService domainService;

    /** Department service reference. */
    @Autowired
    private DepartmentService departmentService;

     /** Virtual Machine service reference. */
    @Autowired
    private VirtualMachineService vmService;

    @Override
    @PreAuthorize("hasPermission(#network.getSyncFlag(), 'ADD_ISOLATED_NETWORK')")
    public Network save(Network network) throws Exception {

        if (network.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("Network", network);
            errors = validator.validateEntity(network, errors);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                config.setUserServer();
                Zone zoneObject = convertEntityService.getZoneById(network.getZoneId());
                String networkOfferings = csNetwork.createNetwork(zoneObject.getUuid(), "json", optional(network));
                JSONObject createNetworkResponseJSON = new JSONObject(networkOfferings)
                        .getJSONObject("createnetworkresponse");
                if (createNetworkResponseJSON.has("errorcode")) {
                    errors = this.validateEvent(errors, createNetworkResponseJSON.getString("errortext"));
                    throw new ApplicationException(errors);
                }
                JSONObject networkResponse = createNetworkResponseJSON.getJSONObject("network");
                network.setUuid(networkResponse.getString("id"));
                network.setNetworkType(network.getNetworkType().valueOf(networkResponse.getString("type")));
                network.setDisplayText(networkResponse.getString("displaytext"));
                network.setcIDR(networkResponse.getString("cidr"));
                network.setDomainId(domainService.findbyUUID(networkResponse.getString("domainid")).getId());
                network.setZoneId(zoneService.findByUUID(networkResponse.getString("zoneid")).getId());
                network.setNetworkOfferingId(
                        networkOfferingService.findByUUID(networkResponse.getString("networkofferingid")).getId());
                network.setStatus(network.getStatus().valueOf(networkResponse.getString("state")));
                if (network.getProject() != null) {
                    network.setProjectId(convertEntityService.getProjectId(networkResponse.getString("projectid")));
                    network.setDepartmentId(null);
                } else {
                    if (network.getDepartmentId() != null) {
                        network.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(
                                departmentService.find(network.getDepartmentId()).getUserName(),
                                domainService.find(network.getDomainId())));
                    } else {
                        network.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(departmentService
                                .find(Long.parseLong(tokenDetails.getTokenDetails("departmentid"))).getUserName(),
                                domainService.find(network.getDomainId())));
                    }
                }
                network.setGateway(networkResponse.getString("gateway"));

                network.setIsActive(true);
                return networkRepo.save(network);
            }
        } else {
            LOGGER.debug(network.getUuid());
            return networkRepo.save(network);
        }
    }

    @Override
    @PreAuthorize("hasPermission(#network.getSyncFlag(), 'EDIT_NETWORK')")
    public Network update(Network network) throws Exception {
        if (network.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("networks", network);
            errors = validator.validateEntity(network, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                HashMap<String, String> optional = new HashMap<String, String>();
                if (network.getName() != null && network.getName().trim() != "") {
                    optional.put("name", network.getName());
                }
                if (network.getDisplayText() != null && network.getDisplayText().trim() != "") {
                    optional.put("displaytext", network.getDisplayText());
                }
                if (network.getcIDR() != null && network.getcIDR().trim() != "") {
                    Network networkcidr = networkRepo.findOne(network.getId());
                    if (network.getcIDR().equals(networkcidr.getcIDR())) {
                    } else {
                        optional.put("guestvmcidr", network.getcIDR());
                    }
                }
                if (network.getNetworkOfferingId() != null) {
                    NetworkOffering networkOffer = convertEntityService
                            .getNetworkOfferingById(network.getNetworkOfferingId());
                    optional.put("networkofferingid", networkOffer.getUuid());
                }
                if (network.getNetworkDomain() != null && network.getNetworkDomain().trim() != "") {
                    optional.put("networkdomain", network.getNetworkDomain());
                }
                config.setUserServer();
                String updateNetworkResponse = csNetwork.updateNetwork(network.getUuid(), optional, "json");
                JSONObject jobId = new JSONObject(updateNetworkResponse).getJSONObject("updatenetworkresponse");
                Thread.sleep(5000);
                if (jobId.has("jobid")) {
                    String jobResponse = csNetwork.networkJobResult(jobId.getString("jobid"), "json");
                    Thread.sleep(2000);
                    JSONObject jobresults = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                    if (jobresults.getString("jobstatus").equals("0")) {
                        network.setStatus(Status.Allocated);
                        network.setIsActive(true);
                        network.setName(network.getName());
                        network.setDisplayText(network.getDisplayText());
                        network.setNetworkOfferingId(network.getNetworkOfferingId());
                        network.setGateway(network.getGateway());
                        network.setcIDR(network.getcIDR());
                        network.setNetMask(network.getNetMask());
                        network.setNetworkDomain(network.getNetworkDomain());
                    } else {
                        JSONObject jobresponse = jobresults.getJSONObject("jobresult");
                        if (jobresults.getString("jobstatus").equals("2")) {
                            // networkRepo.save(network);
                            if (jobresponse.has("errorcode")) {
                                errors = this.validateEvent(errors, jobresponse.getString("errortext"));
                                throw new ApplicationException(errors);
                            }
                        }

                    }
                }
            }
        }
        return networkRepo.save(network);

    }

    @Override
    public void delete(Network network) throws Exception {
        networkRepo.delete(network);
    }

    @Override
    public void delete(Long id) throws Exception {
        networkRepo.delete(id);
    }

    @Override
    @PreAuthorize("hasPermission(#network.getSyncFlag(), 'DELETE_NETWORK')")
    public Network softDelete(Network network) throws Exception {
        Errors errors = validator.rejectIfNullEntity("networks", network);
        errors = validator.validateEntity(network, errors);
        network.setIsActive(false);
        if (network.getSyncFlag()) {
            List<VmInstance> vmResponse = vmService.findByNetworkAndVmStatus(network.getId(), VmInstance.Status.Expunging);
            if (vmResponse.size() != 0) {
                errors.addGlobalError("Network is associated with Vm instances. You cannot delete this network");
            }
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }  else {
            network.setIsActive(false);
            network.setStatus(Network.Status.Destroy);
            if (network.getSyncFlag()) {
                String networkResponse = csNetwork.deleteNetwork(network.getUuid(), "json");
                JSONObject jobId = new JSONObject(networkResponse).getJSONObject("deletenetworkresponse");
                if (jobId.has("jobid")) {
                    String jobResponse = csNetwork.networkJobResult(jobId.getString("jobid"), "json");
                    JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                }
            }
        }
        return networkRepo.save(network);
    }

    @Override
    public Network find(Long id) throws Exception {
        return networkRepo.findOne(id);
    }

    @Override
    public Page<Network> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return networkRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Network> findAll() throws Exception {
        return (List<Network>) networkRepo.findAll();
    }

    @Override
    public List<Network> findAllFromCSServerByDomain() throws Exception {

        List<Project> project = projectService.findAllByActive(true);
        List<Network> networkList = new ArrayList<Network>();
        for (int j = 0; j <= project.size(); j++) {
            HashMap<String, String> networkMap = new HashMap<String, String>();
            if (j == project.size()) {
                networkMap.put("listall", "true");
            } else {
                networkMap.put("projectid", project.get(j).getUuid());
            }
            // 1. Get the list of domains from CS server using CS connector
            String response = csNetwork.listNetworks("json", networkMap);
            JSONArray networkListJSON = null;
            JSONObject responseObject = new JSONObject(response).getJSONObject("listnetworksresponse");
            if (responseObject.has("network")) {
                networkListJSON = responseObject.getJSONArray("network");
                // 2. Iterate the json list, convert the single json entity to
                // domain
                for (int i = 0, size = networkListJSON.length(); i < size; i++) {
                    // 2.1 Call convert by passing JSONObject to Domain entity
                    // and
                    // Add
                    // the converted Domain entity to list
                    Network network = Network.convert(networkListJSON.getJSONObject(i));
                    network.setDomainId(convertEntityService.getDomainId(network.getTransDomainId()));
                    network.setZoneId(convertEntityService.getZoneId(network.getTransZoneId()));
                    network.setNetworkOfferingId(
                            convertEntityService.getNetworkOfferingId(network.getTransNetworkOfferingId()));
                    network.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(
                            network.getTransDepartmentId(), domainService.find(network.getDomainId())));
                    network.setProjectId(convertEntityService.getProjectId(network.getTransProjectId()));
                    networkList.add(network);
                }
            }
        }
        return networkList;
    }

    @Override
    public Network findByUUID(String uuid) throws Exception {
        return networkRepo.findByUUID(uuid);
    }

    /**
     * Check the compute offering CS error handling.
     *
     * @param errors error creating status.
     * @param errmessage error message.
     * @return errors.
     * @throws Exception if error occurs.
     */
    private Errors validateEvent(Errors errors, String errmessage) throws Exception {
        errors.addGlobalError(errmessage);
        return errors;
    }

    /**
     * Hash Map to map the optional values to cloudstack.
     *
     * @return optional
     * @param network Network
     * @throws Exception Exception
     */
    public HashMap<String, String> optional(Network network) throws Exception {

        HashMap<String, String> optional = new HashMap<String, String>();
        if (network.getNetMask() != null && network.getNetMask().trim() != "") {
            optional.put("netmask", network.getNetMask());
        }
        if (network.getGateway() != null && network.getGateway().trim() != "") {
            optional.put("gateway", network.getGateway());
        }
        if (network.getNetworkDomain() != null && network.getNetworkDomain().trim() != "") {
            optional.put("networkdomain", network.getNetworkDomain());
        }
        if (network.getDomainId() != null) {
            optional.put("domainid", convertEntityService.getDomainById(network.getDomainId()).getUuid());
        } else {
            optional.put("domainid",
                    domainService.find(Long.parseLong(tokenDetails.getTokenDetails("domainid"))).getUuid());
        }
        if (network.getName() != null && network.getName().trim() != "") {
            optional.put("name", network.getName());
        }
        if (network.getDisplayText() != null && network.getDisplayText().trim() != "") {
            optional.put("displaytext", network.getDisplayText());
        }
        if (network.getNetworkOfferingId() != null) {
            optional.put("networkofferingid",
                    convertEntityService.getNetworkOfferingById(network.getNetworkOfferingId()).getUuid());
        }
        if (network.getProjectId() != null) {
            optional.put("projectid", convertEntityService.getProjectById(network.getProjectId()).getUuid());

        } else {
            if (network.getDepartmentId() != null) {
                optional.put("account", departmentService.find(network.getDepartmentId()).getUserName());
            } else {
                optional.put("account", departmentService
                        .find(Long.parseLong(tokenDetails.getTokenDetails("departmentid"))).getUserName());
            }
        }

        return optional;
    }

    @Override
    public List<Network> findByDepartmentAndNetworkIsActive(Long department, Boolean isActive) throws Exception {
        return networkRepo.findByDepartmentAndNetworkIsActive(department, true);
    }

    @Override
    public List<Network> findByProjectAndNetworkIsActive(Long projectId, Boolean isActive) throws Exception {
        return networkRepo.findByProjectAndNetworkIsActive(projectId, true);
    }

    /**
     * Find all the compute offering with pagination.
     *
     * @throws Exception  application errors.
     * @param pagingAndSorting do pagination with sorting for computeofferings.
     * @return list of compute offerings.
     */
    public Page<Network> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        if (tokenDetails.getTokenDetails("domainname").equals("/")) {
            return networkRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
        } else {
            return networkRepo.findByDomainIsActive(pagingAndSorting.toPageRequest(), true,
                    Long.parseLong(tokenDetails.getTokenDetails("domainid")));
        }
    }

    /**
     * Validate the department.
     *
     * @param network reference of the department.
     * @throws Exception error occurs
     */
    private void validateNetwork(Network network) throws Exception {
        Errors errors = validator.rejectIfNullEntity("computes", network);
        errors = validator.validateEntity(network, errors);
        Network validNetwork = networkRepo.findName(network.getName());
        if (validNetwork != null && network.getId() != validNetwork.getId()) {
            errors.addFieldError("name", "network.already.exist");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
    }

    @Override
    public Network findById(Long Id) throws Exception {
        return networkRepo.findById(Id);
    }
}
