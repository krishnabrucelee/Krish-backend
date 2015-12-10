package ck.panda.web.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.VmInstance;
import ck.panda.service.VirtualMachineService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Virtual machine controller.
 */
@RestController
@RequestMapping("/api/virtualmachine")
@Api(value = "VirtualMachines", description = "Operations with Virtual Machine", produces = "application/json")
public class VirtualMachineController extends CRUDController<VmInstance>implements ApiController {
    /** Service reference to Virtual Machine. */
    @Autowired
    private VirtualMachineService virtualmachineservice;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Virtual Machine.", response = VmInstance.class)
    @Override
    public VmInstance create(@RequestBody VmInstance vminstance) throws Exception {
        if (vminstance.getProject() != null) {
            vminstance.setProjectId(vminstance.getProject().getId());
        }
        vminstance.setInstanceOwnerId(vminstance.getInstanceOwner().getId());
        vminstance.setDomainId(vminstance.getDomain().getId());
        vminstance.setDepartmentId(vminstance.getDepartment().getId());
        vminstance.setSyncFlag(true);
        return virtualmachineservice.save(vminstance);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Virtual Machine.", response = VmInstance.class)
    @Override
    public VmInstance read(@PathVariable(PATH_ID) Long id) throws Exception {
        System.out.println("In real read ");
        return virtualmachineservice.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Virtual Machine.", response = VmInstance.class)
    @Override
    public VmInstance update(@RequestBody VmInstance vminstance, @PathVariable(PATH_ID) Long id) throws Exception {
        System.out.println("In real update");
        return virtualmachineservice.update(vminstance);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Virtual Machine.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        System.out.println("In real long");
        virtualmachineservice.delete(id);
    }

    @Override
    public List<VmInstance> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {

        System.out.println("In real long");
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, VmInstance.class);
        Page<VmInstance> pageResponse = virtualmachineservice.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * get all instances.
     *
     * @throws Exception if error occurs.
     * @return list of instances.
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<VmInstance> getSearch() throws Exception {
        return virtualmachineservice.findAll();
    }

    /**
     * get instance with latest state update.
     *
     * @param vm vm uuid.
     * @param event vm event type.
     * @throws Exception if error occurs.
     * @return list of instances.
     */
    @RequestMapping(value = "event", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public VmInstance handleVmEvent(@RequestParam("vm") String vm, @RequestParam("event") String event)
            throws Exception {
        return virtualmachineservice.vmEventHandle(vm, event);
    }

    /**
     * get instance with latest state update.
     *
     * @param vminstance vm object.
     * @throws Exception if error occurs.
     * @return list of instances.
     */
    @RequestMapping(value = "/vm", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public VmInstance handleVmEventWithInstance(@RequestBody VmInstance vminstance) throws Exception {
        String event = vminstance.getEvent();
        return virtualmachineservice.vmEventHandleWithVM(vminstance, event);
    }

    /**
     * get all instances.
     *
     * @param vminstance instance name.
     * @throws Exception if error occurs.
     * @return list of instances.
     */
    @RequestMapping(value = "/console", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getVNC(@RequestBody VmInstance vminstance) throws Exception {
        System.out.println(" in side console ");
        String token = null;
        String host = "192.168.1.221"; // test the host's IP address
        String instance = "i-2-8-VM"; // test virtual machine instance name
        String display = "sibin1"; // Novnc display
        String str = host + "|" + instance + "|" + display;
        token = Base64.encodeBase64String(str.getBytes());
        return "{\"success\":" + "\"http://192.168.1.221/console/?token=" + token + "\"}";
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

}
