package ck.panda.util.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/** This class returns the current time. */
@Service
public class CurrentTimeDateTimeService implements DateTimeService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentTimeDateTimeService.class);

    /**
     * Returns the current date-time using system clock.
     *
     * @return currentDateAndTime.
     */
    @Override
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public ZonedDateTime getCurrentDateAndTime() {
        ZonedDateTime currentDateAndTime = ZonedDateTime.now();

        LOGGER.info("Returning current date and time: {}", currentDateAndTime);

        return currentDateAndTime;
    }

    /**
     * Returns the current date-time from string date.
     *
     * @param date string date.
     * @return dateAndTime.
     */
	@Override
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	public ZonedDateTime convertDateAndTime(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");
		ZonedDateTime dateAndTime = ZonedDateTime.parse(date, formatter);
		LOGGER.info("Returning current date and time: {}", dateAndTime);
		return dateAndTime;
	}
}
