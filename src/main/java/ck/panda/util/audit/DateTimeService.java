package ck.panda.util.audit;

import java.text.ParseException;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Service;

/** This interface defines the method used to get the current date and time. */
@Service
public interface DateTimeService {

    /**
     * Returns the current date and time.
     *
     * @return date and time.
     */
    ZonedDateTime getCurrentDateAndTime();

    /**
     * Returns the current date and time from string date and time.
     *
     * @param Date string date and time as string.
     * @return date and time.
     */
    ZonedDateTime convertDateAndTime(String date);
}
