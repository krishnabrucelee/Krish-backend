package ck.panda.util.audit;

import java.time.ZonedDateTime;

/** This interface defines the method used to get the current date and time. */
public interface DateTimeService {

    /**
     * Returns the current date and time.
     *
     * @return date and time.
     */
    ZonedDateTime getCurrentDateAndTime();
}
