package ck.panda.web.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import ck.panda.domain.entity.SSHKey;
import ck.panda.service.SSHKeyService;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/** SSHKey controller. */
@RestController
@RequestMapping("/api/sshkeys")
@Api(value = "SSHKeys", description = "Operations with sshkeys", produces = "sshkey/json")
public class SSHKeyController extends CRUDController<SSHKey> implements ApiController {

    /** Service reference to SSH Key. */
    @Autowired
    private SSHKeyService sshkeyService;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new sshkey.", response = SSHKey.class)
    @Override
    public SSHKey create(@RequestBody SSHKey sshkey) throws Exception {
        sshkey.setIsSyncFlag(true);
        return sshkeyService.save(sshkey, Long.parseLong(tokenDetails.getTokenDetails("id")));
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing sshkey.", response = SSHKey.class)
    @Override
    public SSHKey read(@PathVariable(PATH_ID) Long id) throws Exception {
        return sshkeyService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing sshkey.", response = SSHKey.class)
    @Override
    public SSHKey update(@RequestBody SSHKey sshkey, @PathVariable(PATH_ID) Long id) throws Exception {
        return sshkeyService.update(sshkey);
    }

    /**
     * Delete the SSH key.
     *
     * @param sshkey reference of the SSH key
     * @param id SSH key id
     * @throws Exception error occurs
     */
    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing SSHKey.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@RequestBody SSHKey sshkey, @PathVariable(PATH_ID) Long id) throws Exception {
        /** Doing Soft delete from the SSH key table. */
        sshkey = sshkeyService.find(id);
        sshkey.setIsSyncFlag(true);
        sshkeyService.softDelete(sshkey, Long.parseLong(tokenDetails.getTokenDetails("id")));
    }

    @Override
    public List<SSHKey> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, SSHKey.class);
        Page<SSHKey> pageResponse = sshkeyService.findAll(page, Long.parseLong(tokenDetails.getTokenDetails("id")));
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Find the list of active SSH keys.
     *
     * @return  SSH Key list
     * @throws Exception error occurs
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<SSHKey> getSearch() throws Exception {
        return sshkeyService.findAll(Long.parseLong(tokenDetails.getTokenDetails("id")));
    }

    /**
     * Find the list of active SSH keys based on the departmentId.
     *
     * @param departmentId department id of the SSH key
     * @return  SSH Key list
     * @throws Exception if error occurs
     */
    @RequestMapping(value = "search/department", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<SSHKey> getSSHKeyListByDepartment(@RequestParam("dept") Long departmentId) throws Exception {
        return sshkeyService.findAllByDepartmentAndIsActive(departmentId, true);
    }

    /**
     * Find the list of active SSH keys based on the projectId.
     *
     * @param projectId project id of the SSH key
     * @return  SSH Key list
     * @throws Exception if error occurs
     */
    @RequestMapping(value = "search/project", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<SSHKey> getSSHKeyListByProject(@RequestParam("project") Long projectId) throws Exception {
        return sshkeyService.findAllByProjectAndIsActive(projectId, true);
    }
}
