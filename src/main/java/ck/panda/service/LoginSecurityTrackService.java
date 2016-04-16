package ck.panda.service;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.LoginSecurityTrack;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for login security track entity.
 *
 */
@Service
public interface LoginSecurityTrackService extends CRUDService<LoginSecurityTrack> {

     /**
     * Find login security track by isActive.
     *
     * @param isActive status of the compute offer
     * @return list login security track.
     * @throws Exception if error occurs.
     */
    LoginSecurityTrack findByIsActive(Boolean isActive) throws Exception;

    /**
     * Find login attempt count by host address.
     *
     * @param hostAddress host address
     * @return login security track details
     */
    LoginSecurityTrack findByIpAddress(String hostAddress);

}
