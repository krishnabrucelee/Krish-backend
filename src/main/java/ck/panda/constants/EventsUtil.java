package ck.panda.constants;

import java.util.ArrayList;
import java.util.List;
import ck.panda.domain.entity.EventLiterals;

/**
 * Prepare event list.
 */
public final class EventsUtil {

    /**
     * EventUtil constructor.
     */
    private EventsUtil() {
    }

    @SuppressWarnings("rawtypes")
    public static List<EventLiterals> createEventsList(String account, String users, String accountremoval, String resource, String systemError, String invoice, String usage, String payment) {
        List<String> stringList = new ArrayList<String>();
        List<EventLiterals> moduleList = new ArrayList<EventLiterals>();
        stringList.add(account);
        stringList.add(users);
        stringList.add(accountremoval);
        stringList.add(resource);
        stringList.add(systemError);
        stringList.add(invoice);
        stringList.add(usage);
        stringList.add(payment);
        for (String string : stringList) {
            List<String> actionList = new ArrayList<String>();
            String[] stringArray = string.split("-");
            String[] actions = stringArray[1].split(",");
            for (String action : actions) {
                String[] actionTest = action.split(":");
                EventLiterals events = new EventLiterals();
                events.setEventLiterals(actionTest[0]);
                events.setEventName(stringArray[0]);
                events.setDescription(actionTest[1]);
                events.setEventLiteralsKey(actionTest[0].toUpperCase());
                events.setIsActive(true);
                moduleList.add(events);
                }
            }
        return moduleList;
    }
}
