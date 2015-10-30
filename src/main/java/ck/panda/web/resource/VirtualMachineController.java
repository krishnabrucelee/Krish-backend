package ck.panda.web.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.service.VirtualMachineService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

@RestController
@RequestMapping("/api/virtualmachine")
@Api(value = "VirtualMachines", description = "Operations with Virtual Machine", produces = "application/json")
public class VirtualMachineController extends CRUDController<VmInstance> implements ApiController {

    /** Service reference to Virtual Machine. */
    @Autowired
    private VirtualMachineService virtualmachineservice;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Virtual Machine.", response = VmInstance.class)
    @Override
    public VmInstance create(@RequestBody VmInstance vminstance) throws Exception {
        return virtualmachineservice.save(vminstance);
    }

	@ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Virtual Machine.", response = VmInstance.class)
    @Override
    public VmInstance read(@PathVariable(PATH_ID) Long id) throws Exception {
        return virtualmachineservice.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Virtual Machine.", response = VmInstance.class)
    @Override
    public VmInstance update(@RequestBody VmInstance vminstance, @PathVariable(PATH_ID) Long id) throws Exception {
        return virtualmachineservice.update(vminstance);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Virtual Machine.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        virtualmachineservice.delete(id);
    }

    @Override
    public List<VmInstance> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
    		@RequestParam(required = false) Integer limit,HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, VmInstance.class);
        Page<VmInstance> pageResponse = virtualmachineservice.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    @RequestMapping(value = "list",method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
   	@ResponseStatus(HttpStatus.OK)
   	@ResponseBody
   	protected List<VmInstance> getSearch() throws Exception {
   		return virtualmachineservice.findAll();
   	}

    @RequestMapping(value = "event",method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
   	@ResponseStatus(HttpStatus.OK)
   	@ResponseBody
   	protected VmInstance handleVmEvent(@RequestParam("vm") String vm, @RequestParam("event") String event) throws Exception {
    	return virtualmachineservice.vmEventHandle(vm, event);
   	}

	@Override
	public void testMethod() throws Exception {
	}
}
