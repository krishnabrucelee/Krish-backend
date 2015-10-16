package ck.panda.web.resource;

import java.util.List;

import ck.panda.domain.entity.DomainUser;
import ck.panda.domain.entity.Stuff;

/**
 * Service gateway interface.
 */
public interface ServiceGateway {

    /**
     * Get some stuff.
     * @return stuff.
     */
    List<Stuff> getSomeStuff();

    /**
     * Create stuff.
     * @param newStuff to set
     * @param domainUser to set
     */
    void createStuff(Stuff newStuff, DomainUser domainUser);
}
