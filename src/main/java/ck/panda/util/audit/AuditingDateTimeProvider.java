package ck.panda.util.audit;

import org.springframework.data.auditing.DateTimeProvider;

import java.util.Calendar;
import java.util.GregorianCalendar;

/** This class obtains the current date and time. */
public class AuditingDateTimeProvider implements DateTimeProvider {

    /** created an object for DateTimeService. */
    private final DateTimeService dateTimeService;

    /**
     * Audit the date and time.
     *
     * @param dateTimeService DateTimeServiceObject
     */
    public AuditingDateTimeProvider(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Returns the current date and time to be used as modification or creation date.
     *
     * @return
     */
    @Override
    public Calendar getNow() {
        return GregorianCalendar.from(dateTimeService.getCurrentDateAndTime());
    }
}
