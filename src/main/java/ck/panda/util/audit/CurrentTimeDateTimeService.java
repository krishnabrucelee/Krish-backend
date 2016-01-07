package ck.panda.util.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;

/** This class returns the current time. */
@Service
public class CurrentTimeDateTimeService implements DateTimeService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentTimeDateTimeService.class);

    /**
     * Returns the current date-time using system clock.
     *
     * @return
     */
    @Override
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public ZonedDateTime getCurrentDateAndTime() {
        ZonedDateTime currentDateAndTime = ZonedDateTime.now();

        LOGGER.info("Returning current date and time: {}", currentDateAndTime);

        return currentDateAndTime;
    }
}
