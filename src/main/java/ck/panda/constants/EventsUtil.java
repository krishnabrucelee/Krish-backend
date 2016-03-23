package ck.panda.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import ck.panda.domain.entity.EventLiterals;
import ck.panda.domain.entity.Permission.Module;

/**
 *
 * Prepare permission list.
 */
public final class EventsUtil {

    /**
     * PermissionUtil constructor.
     */
    private EventsUtil() {

    }



    @SuppressWarnings("rawtypes")
    public static List<EventLiterals> createEventsList(String user, String instance) {
        List<String> stringList = new ArrayList<String>();
        List<EventLiterals> moduleList = new ArrayList<EventLiterals>();
        stringList.add(user);
        for (String string : stringList) {
            List<String> actionList = new ArrayList<String>();
            String[] stringArray = string.split("-");
            String[] actions = stringArray[1].split(",");
            for (String action : actions) {
                EventLiterals events = new EventLiterals();
                events.setEventLiterals(action);
                events.setEventName(stringArray[0]);
                events.setEventLiteralsKey(action.toUpperCase());
                events.setIsActive(true);
                moduleList.add(events);
            }
            }


        return moduleList;
    }
}
