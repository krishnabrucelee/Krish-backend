package ck.panda.web.resource;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmInstance.Status;
import ck.panda.rabbitmq.util.ResponseEvent;
import ck.panda.service.ConvertEntityService;
import ck.panda.service.VirtualMachineService;
import ck.panda.util.CloudStackInstanceService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.Instance;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Virtual machine controller.
 */
@RestController
@RequestMapping("/api/virtualmachine")
@Api(value = "VirtualMachines", description = "Operations with Virtual Machine", produces = "application/json")
public class VirtualMachineController extends CRUDController<VmInstance> implements ApiController {
    /** Service reference to Virtual Machine. */
    @Autowired
    private VirtualMachineService virtualmachineservice;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

    /** Cloud stack configuration reference. */
    @Autowired
    private ConfigUtil configUtil;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** console proxy reference. */
    @Value(value = "${console.proxy}")
    private String consoleProxy;

    /** CloudStack connector reference for instance. */
    @Autowired
    private CloudStackInstanceService cloudStackInstanceService;

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualMachineController.class);

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Virtual Machine.", response = VmInstance.class)
    @Override
    public VmInstance create(@RequestBody VmInstance vminstance) throws Exception {
        vminstance.setSyncFlag(true);
        return virtualmachineservice.saveVmInstance(vminstance, Long.valueOf(tokenDetails.getTokenDetails("id")));
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Virtual Machine.", response = VmInstance.class)
    @Override
    public VmInstance read(@PathVariable(PATH_ID) Long id) throws Exception {
        return virtualmachineservice.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Virtual Machine.", response = VmInstance.class)
    @Override
    public VmInstance update(@RequestBody VmInstance vminstance, @PathVariable(PATH_ID) Long id) throws Exception {
        return virtualmachineservice.updateDisplayName(vminstance);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Virtual Machine.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        virtualmachineservice.delete(id);
    }

    @Override
    public List<VmInstance> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, VmInstance.class);
        Page<VmInstance> pageResponse = virtualmachineservice.findAllBySort(page, Status.EXPUNGING);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Get all vm instance list by status.
     *
     * @param sortBy asc/desc
     * @param status status of vm.
     * @param range pagination range.
     * @param limit per page limit.
     * @param request page request.
     * @param response response content.
     * @return vmlist.
     * @throws Exception unhandled exception.
     */
    @RequestMapping(value = "/listByStatus", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<VmInstance> listVmByStatus(@RequestParam String sortBy, @RequestParam String status,
            @RequestHeader(value = RANGE) String range, @RequestParam(required = false) Integer limit,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, VmInstance.class);
        Page<VmInstance> pageResponse = virtualmachineservice.findAllByUser(page, Long.valueOf(tokenDetails.getTokenDetails("id")));
        if (!status.equals("Expunging")) {
            pageResponse = virtualmachineservice.findAllByStatus(page, Status.valueOf(status.toUpperCase()), Long.valueOf(tokenDetails.getTokenDetails("id")));
		}
		response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
		return pageResponse.getContent();
	}

    /**
     * Get the vm counts for stopped, running and total count.
     *
     * @param request page request.
     * @param response page response content.
     * @return vm count.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "/vmCounts", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getVmCounts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer vmCount = virtualmachineservice.findAllByUser(Long.valueOf(tokenDetails.getTokenDetails("id"))).size();
        Integer runningVmCount = virtualmachineservice.findCountByStatus(Status.RUNNING, Long.valueOf(tokenDetails.getTokenDetails("id")));
        Integer stoppedVmCount = virtualmachineservice.findCountByStatus(Status.STOPPED, Long.valueOf(tokenDetails.getTokenDetails("id")));
        return "{\"runningVmCount\":" + runningVmCount + ",\"stoppedVmCount\":" + stoppedVmCount + ",\"totalCount\":"
                + vmCount + "}";
    }

    /**
     * Get all vm instance list.
     *
     * @throws Exception if error occurs.
     * @return list of instances.
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<VmInstance> getVmList() throws Exception {
        List<Status> status = new ArrayList<Status>();
        status.add(Status.EXPUNGING);
        status.add(Status.ERROR);
        status.add(Status.DESTROYED);
        status.add(Status.STARTING);
        status.add(Status.STOPPING);
        return virtualmachineservice.findByVmStatus(status, Long.valueOf(tokenDetails.getTokenDetails("id")));
    }

    /**
     * Get all instance list for network by network id.
     *
     * @param networkId network's id.
     * @throws Exception if error occurs.
     * @return list of instances.
     */
    @RequestMapping(value = "/network", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<VmInstance> getVmListByNetwork(@RequestParam("networkId") Long networkId) throws Exception {
        return virtualmachineservice.findAllByNic(networkId);
    }

    /**
     * Get instance details after vm action.
     *
     * @param vm vm uuid.
     * @param event vm event type.
     * @throws Exception if error occurs.
     * @return instance.
     */
    @RequestMapping(value = "/handlevmevent", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public VmInstance handleVmEvent(@RequestParam("vm") String vm, @RequestParam("event") String event)
            throws Exception {
        return virtualmachineservice.handleAsyncJobByEventName(vm, event);
    }

    /**
     * Get instance details after vm action.
     *
     * @param vminstance vm object.
     * @throws Exception if error occurs.
     * @return instance.
     */
    @RequestMapping(value = "/handleevent/vm", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public VmInstance handleVmEventWithInstance(@RequestBody VmInstance vminstance) throws Exception {
        String event = vminstance.getEvent();
        return virtualmachineservice.handleAsyncJobByVM(vminstance, event, Long.valueOf(tokenDetails.getTokenDetails("id")));
    }

    /**
     * Generate VNC console token and redirect to console proxy server.
     *
     * @param vminstance instance name.
     * @throws Exception if error occurs.
     * @return VNC token.
     */
    @RequestMapping(value = "/console", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getVNC(@RequestBody VmInstance vminstance) throws Exception {
        // TODO optimize/refactor this console code after completion of Kanaka NoVNC configuration.
        String token = null;
        VmInstance persistInstance = virtualmachineservice.find(vminstance.getId());
        if (persistInstance.getHost() != null) {
            String hostUUID = persistInstance.getHost().getUuid(); // VM's the host's UUID.
            String instanceUUID = persistInstance.getUuid(); // virtual machine UUID.
            token = hostUUID + "-" + instanceUUID;
            LOGGER.debug("VNC Token" + token);
        } else {
            // VM console issue fixed. PK-557.
             HashMap<String, String> vmMap = new HashMap<String, String>();
             vmMap.put(CloudStackConstants.CS_ID, persistInstance.getUuid());
             configUtil.setServer(1L);
             String response = cloudStackInstanceService.listVirtualMachines(CloudStackConstants.JSON, vmMap);
             JSONArray vmListJSON = null;
             JSONObject responseObject = new JSONObject(response)
                     .getJSONObject(CloudStackConstants.CS_LIST_VM_RESPONSE);
             if (responseObject.has(CloudStackConstants.CS_VM)) {
                 vmListJSON = responseObject.getJSONArray(CloudStackConstants.CS_VM);
                 // 2. Iterate the json list, convert the single json
                 // entity to vm.
                 for (int i = 0, size = vmListJSON.length(); i < size; i++) {
                     // 2.1 Call convert by passing JSONObject to vm
                     // entity.
                     VmInstance CsVmInstance = VmInstance.convert(vmListJSON.getJSONObject(i));
                     // 2.2 Update vm host by transient variable.
                     persistInstance.setHostId(convertEntityService.getHostId(CsVmInstance.getTransHostId()));
                     persistInstance.setInstanceInternalName(CsVmInstance.getInstanceInternalName());
                 }
                 persistInstance = virtualmachineservice.update(persistInstance);
                 String hostUUID = persistInstance.getHost().getUuid(); // VM's the host's UUID.
                  String instanceUUID = persistInstance.getUuid(); // virtual machine UUID.
                  token = hostUUID + "-" + instanceUUID;
                  LOGGER.debug("VNC Token" + token);
             }
        }
        return "{\"success\":" + "\"" + consoleProxy + "/console/?token=" + token + "\"}";
    }

    /**
     * Upgrade/Downgrade VM from created instance.
     *
     * @param vminstance instance name.
     * @return list of instances
     * @throws Exception if error occurs.
     */
    @RequestMapping(value = "/resize", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected VmInstance upgradeDowngradeVM(@RequestBody VmInstance vminstance) throws Exception {
        return virtualmachineservice.upgradeDowngradeVM(vminstance);
    }

    /**
     * Get all vm instance list for volume by project.
     *
     * @param projectId project id.
     * @return list of instance.
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "/volume/project/{id}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public List<VmInstance> findAllByProjectAndStatus(@PathVariable(PATH_ID) Long projectId) throws Exception {
        List<VmInstance.Status> statusCode = new ArrayList<VmInstance.Status>();
        statusCode.add(Status.RUNNING);
        statusCode.add(Status.STOPPED);
        return virtualmachineservice.findAllByProjectAndStatus(projectId, statusCode);
    }

    /**
     * Get all vm instance list for volume by department.
     *
     * @param derpartmentId department id.
     * @return list of instance.
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "/volume/department/{id}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public List<VmInstance> findAllByDepartmentAndStatus(@PathVariable(PATH_ID) Long derpartmentId) throws Exception {
        List<VmInstance.Status> statusCode = new ArrayList<VmInstance.Status>();
        statusCode.add(Status.RUNNING);
        statusCode.add(Status.STOPPED);
        return virtualmachineservice.findAllByDepartmentAndStatus(derpartmentId, statusCode);
    }

    /**
     * @param id instance id
     * @return instance
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "/getvncpassword/{id}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public VmInstance findByIdWithVncPassword(@PathVariable(PATH_ID) Long id) throws Exception {
        return virtualmachineservice.findByIdWithVncPassword(id);
    }

    /**
     * Get all vm instance list by domain.
     *
     * @param sortBy asc/desc
     * @param domainId domain id of vm.
     * @param searchText search text.
     * @param status status of vm.
     * @param range pagination range.
     * @param limit per page limit.
     * @param request page request.
     * @param response response content.
     * @return vmlist.
     * @throws Exception unhandled exception.
     */
    @RequestMapping(value = "/listByDomain", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<VmInstance> listVmByDomainId(@RequestParam String sortBy, @RequestParam Long domainId, @RequestParam String searchText,
            @RequestParam String status, @RequestHeader(value = RANGE) String range, @RequestParam(required = false) Integer limit,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, VmInstance.class);
        Page<VmInstance> pageResponse = null;
        if (!status.equals("Expunging")) {
            pageResponse = virtualmachineservice.findAllByStatusAndDomain(page, Status.valueOf(status.toUpperCase()), domainId, searchText, Long.valueOf(tokenDetails.getTokenDetails("id")));
        } else {
            pageResponse = virtualmachineservice.findAllByDomainId(domainId, page, searchText, Long.valueOf(tokenDetails.getTokenDetails("id")));
        }
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Get the vm counts for stopped, running and total count based on the domain filter.
     *
     * @param domainId domain id of vm.
     * @param searchText search text.
     * @return vm count.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "/vmCountsByDomain", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getVmCounts(@RequestParam("domainId") Long domainId, @RequestParam String searchText) throws Exception {
        Integer vmCount = virtualmachineservice.findAllByDomain(domainId, Long.valueOf(tokenDetails.getTokenDetails("id")), searchText).size();
        Integer runningVmCount = virtualmachineservice.findCountByStatusAndDomain(Status.RUNNING, domainId, Long.valueOf(tokenDetails.getTokenDetails("id")), searchText);
        Integer stoppedVmCount = virtualmachineservice.findCountByStatusAndDomain(Status.STOPPED, domainId, Long.valueOf(tokenDetails.getTokenDetails("id")), searchText);
        return "{\"runningVmCount\":" + runningVmCount + ",\"stoppedVmCount\":" + stoppedVmCount + ",\"totalCount\":"
        + vmCount + "}";
    }

    /**
     * Reset SSH Key in created instance.
     *
     * @param vminstance object.
     * @return instance
     * @throws Exception if error occurs.
     */
    @RequestMapping(value = "/reset", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected VmInstance resetSSHKey(@RequestBody VmInstance vminstance) throws Exception {
        return virtualmachineservice.resetSSHKey(vminstance);
    }

    /**
     * Update the affinity group for created instance.
     *
     * @param vminstance object.
     * @return instance
     * @throws Exception if error occurs.
     */
    @RequestMapping(value = "/affinityGroup", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected VmInstance affinityGroup(@RequestBody VmInstance vminstance) throws Exception {
        return virtualmachineservice.affinityGroup(vminstance);
    }

    /**
     * Get instance list by affinity group id.
     *
     * @param id group id
     * @return affinity group list
     * @throws Exception error occurs
     */
    @RequestMapping(value = "/affinityGroupInstance/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<VmInstance> affinityGroupInstance(@PathVariable(PATH_ID) Long id) throws Exception {
         return virtualmachineservice.findInstanceByGroup(id);
    }

}
