/**
 *
 */
package ck.panda.rabbitmq.util;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.google.common.base.Stopwatch;
import ck.panda.domain.entity.Volume;
import ck.panda.util.CloudStackVolumeService;

/**
 * Query Asynchronous Job Result Listener
 *
 */
@Service
public class QueryAsynchronousJobResult {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryAsynchronousJobResult.class);

    /** Lists types of Volumes in cloudstack server. */
    @Autowired
    private CloudStackVolumeService csVolumeService;

    @Async
    public String callAsync(Volume volume) throws Exception {

        Stopwatch stopwatch = Stopwatch.createStarted();

        HashMap<String, String> optional = new HashMap<String, String>();

        if (volume.getUuid() != null) {
            optional.put("id", volume.getUuid());
        }
        csVolumeService.listVolumes("json", optional);

        LOGGER.info("task " + volume + " starting");

        Thread.sleep(25000);

        stopwatch.elapsed(TimeUnit.MILLISECONDS);
        csVolumeService.listVolumes("json", optional);
        LOGGER.info("task " + volume + "completed in " + stopwatch);

        return csVolumeService.listVolumes("json", optional);
    }
}
