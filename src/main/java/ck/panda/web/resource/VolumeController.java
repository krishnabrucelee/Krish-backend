/**
 *
 */
package ck.panda.web.resource;

import java.util.ArrayList;
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
import ck.panda.domain.entity.Volume;
import ck.panda.domain.entity.Volume.VolumeType;
import ck.panda.service.VolumeService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Volume Controller.
 */
@RestController
@RequestMapping("/api/volumes")
@Api(value = "Volumes", description = "Operations with Volumes", produces = "application/json")
public class VolumeController extends CRUDController<Volume> implements ApiController {

    /** Service reference to Volume. */
    @Autowired
    private VolumeService volumeService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Volume.", response = Volume.class)
    @Override
    public Volume create(@RequestBody Volume volume) throws Exception {
        volume.setIsSyncFlag(true);
        return volumeService.save(volume);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Volume.", response = Volume.class)
    @Override
    public Volume read(@PathVariable(PATH_ID) Long id) throws Exception {
        return volumeService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Volume.", response = Volume.class)
    @Override
    public Volume update(@RequestBody Volume volume, @PathVariable(PATH_ID) Long id) throws Exception {
        volume.setIsSyncFlag(true);
        return volumeService.update(volume);
    }

    /**
     * Soft delete for volume.
     *
     * @param volume volume
     * @param id volume id
     * @throws Exception error
     */
    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Volume.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@RequestBody Volume volume, @PathVariable(PATH_ID) Long id) throws Exception {
        /** Doing Soft delete from the department table. */
        volume.setIsSyncFlag(true);
        volumeService.softDelete(volume);
    }

    @Override
    public List<Volume> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Volume.class);
        Page<Volume> pageResponse = volumeService.findAllByIsActive(page);
        System.out.println(pageResponse);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));

        return pageResponse.getContent();
    }

    /**
     * list all Volumes for instance.
     *
     * @return Volume service
     * @throws Exception error
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<Volume> getSearch() throws Exception {
        return volumeService.findAll();
    }

    /**
     * Attaches volume to the Instance.
     *
     * @param volume Volume
     * @param id Id
     * @return attachVolume
     * @throws Exception exception
     */
    @RequestMapping(value = "attach/{id}", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected Volume attachVolume(@RequestBody Volume volume, @PathVariable(PATH_ID) Long id) throws Exception {
        volume.setIsSyncFlag(true);
        return volumeService.attachVolume(volume);
    }

    /**
     * Datach volume from the Instance.
     *
     * @param volume Volume
     * @param id Id
     * @return attachVolume
     * @throws Exception exception
     */
    @RequestMapping(value = "detach/{id}", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected Volume detachVolume(@RequestBody Volume volume, @PathVariable(PATH_ID) Long id) throws Exception {
        volume.setIsSyncFlag(true);
        return volumeService.detachVolume(volume);
    }

    /**
     * Resize volume from created volume.
     *
     * @param volume Volume
     * @param id Id
     * @return Resize Volume
     * @throws Exception exception
     */
    @RequestMapping(value = "resize/{id}", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected Volume resizeVolume(@RequestBody Volume volume, @PathVariable(PATH_ID) Long id) throws Exception {
        volume.setIsSyncFlag(true);
        return volumeService.resizeVolume(volume);
    }

    /**
     * Upload volume from URL.
     *
     * @param volume Volume
     * @return Upload Volume
     * @throws Exception exception
     */
    @RequestMapping(value = "upload", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected Volume uploadVolume(@RequestBody Volume volume) throws Exception {
        volume.setIsSyncFlag(true);
        return volumeService.uploadVolume(volume);
    }

    /**
     * list by instance attached to volume.
     *
     * @param instanceId Volume
     * @return volume Volume by instances
     * @throws Exception exception
     */
    @RequestMapping(value = "listbyinstances", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Volume> listByInstance(@RequestParam("instanceid") Long instanceId) throws Exception {
        System.out.println(instanceId);
        return volumeService.findByInstanceAndIsActive(instanceId);
    }

    /**
     * list by instance attached to volume.
     *
     * @param instanceId Volume
     * @return volume Volume by instances
     * @throws Exception exception
     */
    @RequestMapping(value = "listbyinstancesandvolumetype", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Volume> listByInstanceAndVolumeType(@RequestParam("instanceid") Long instanceId) throws Exception {
        return volumeService.findByInstanceAndVolumeTypeAndIsActive(instanceId);
    }

    /**
     * instance attached to volume.
     *
     * @param instanceId Volume
     * @return volume Volume by instances
     * @throws Exception exception
     */
    @RequestMapping(value = "instance", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Volume getByInstanceAndVolumeType(@RequestParam("instanceid") Long instanceId) throws Exception {
        return volumeService.findByInstanceAndVolumeType(instanceId);
    }

    /**
     * list all Volumes for instance.
     *
     * @return Volume service
     * @throws Exception error
     */
    @RequestMapping(value = "listbyvolumetype", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<Volume> listbyvolumetype() throws Exception {
        return volumeService.findByVolumeTypeAndIsActive();
    }

    /**
     * Get the volumes based on project.
     *
     * @param projectId project id.
     * @return project
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "/instance/project/{id}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public List<Volume> findByProjectAndStatus(@PathVariable(PATH_ID) Long projectId) throws Exception {
        List<Volume.VolumeType> volumeType = new ArrayList<Volume.VolumeType>();
        volumeType.add(VolumeType.DATADISK);
        return volumeService.findByProjectAndVolumeType(projectId, volumeType);
    }

    /**
     * Get the volumes based on department.
     *
     * @param departmentId department id.
     * @return department
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "/instance/department/{id}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public List<Volume> findByDepartmentAndStatus(@PathVariable(PATH_ID) Long departmentId) throws Exception {
        List<Volume.VolumeType> volumeType = new ArrayList<Volume.VolumeType>();
        volumeType.add(VolumeType.DATADISK);
        return volumeService.findByDepartmentAndVolumeType(departmentId, volumeType);
    }

    /**
     * Get the volumes based on department.
     *
     * @param departmentId department id.
     * @param projectId project id.
     * @return department
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "/instance/department", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public List<Volume> findByDepartmentAndProjectAndStatus(@RequestParam("departmentId") Long departmentId,
            @RequestParam("projectId") Long projectId) throws Exception {
        List<Volume.VolumeType> volumeType = new ArrayList<Volume.VolumeType>();
        volumeType.add(VolumeType.DATADISK);
        return volumeService.findByDepartmentAndNotProjectAndVolumeType(departmentId, projectId, volumeType);
    }

    /**
     * Get the volume counts for attached, detached and total count.
     *
     * @param request http request
     * @param response http Servlet Response
     * @return attached/detached count
     * @throws Exception error
     */
    @RequestMapping(value = "volumeCounts", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getVolumeCounts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer attachedCount = volumeService.findAttachedCount();
        Integer detachedCount = volumeService.findDetachedCount();
        return "{\"attachedCount\":" + attachedCount + ",\"detachedCount\":" + detachedCount + "}";
    }

}
