package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.LoginSecurityTrack;

/** Repository for login security track. */
public interface LoginSecurityTrackRepository extends PagingAndSortingRepository<LoginSecurityTrack, Long> {

     /**
     * Find by is Active login security track.
     *
     * @param isActive status of login security track.
     * @return login security track
     */
    @Query(value = "SELECT securityTrack FROM LoginSecurityTrack securityTrack WHERE securityTrack.isActive = :isActive")
    LoginSecurityTrack findByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Find login attempt count by host address.
     *
     * @param hostAddress host address of login security track.
     * @return login security track
     */
    @Query(value = "SELECT securityTrack FROM LoginSecurityTrack securityTrack WHERE securityTrack.loginIpAddress = :hostAddress")
    LoginSecurityTrack findByIpAddress(@Param("hostAddress") String hostAddress);
}
