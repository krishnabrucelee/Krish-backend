/**
 *
 */
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
import ck.panda.domain.entity.Volume;
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
    public Volume update(@RequestBody Volume volume, @PathVariable(PATH_ID) Long id)
            throws Exception {
        return volumeService.update(volume);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Volume.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        volumeService.delete(id);
    }


    @Override
    public List<Volume> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Volume.class);
        Page<Volume> pageResponse = volumeService.findAll(page);
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
        return volumeService.detachVolume(volume);
    }
}
