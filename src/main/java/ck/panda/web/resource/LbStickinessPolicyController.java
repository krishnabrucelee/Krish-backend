package ck.panda.web.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import ck.panda.domain.entity.LbStickinessPolicy;
import ck.panda.service.LbStickinessPolicyService;
import ck.panda.util.TokenDetails;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Lb Stickiness Policy Controller.
 *
 */
@RestController
@RequestMapping("/api/LbStickinessPolicy")
@Api(value = "LbStickinessPolicy", description = "Operations with LbStickinessPolicy", produces = "application/json")
public class LbStickinessPolicyController extends CRUDController<LbStickinessPolicy> implements ApiController {

    /** Service reference to Lb Stickiness Policy. */
    @Autowired
    private LbStickinessPolicyService lbStickinessPolicyService;

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Lb Stickiness Policy.", response = LbStickinessPolicy.class)
    @Override
    public LbStickinessPolicy update(@RequestBody LbStickinessPolicy lbStickinessPolicy, @PathVariable(PATH_ID) Long id)
            throws Exception {
    	lbStickinessPolicy.setSyncFlag(true);
        return lbStickinessPolicyService.update(lbStickinessPolicy);
    }
}
