package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.IpAddress;

/**
 * Jpa Repository for ipaddress entity.
 */
@Service
public interface IpaddressRepository extends PagingAndSortingRepository<IpAddress, Long> {

    /**
     * Find ipaddress by uuid.
     *
     * @param uuid of ipaddress.
     * @return ipaddress object.
     */
    @Query(value = "SELECT ip FROM IpAddress ip WHERE ip.uuid = :uuid")
    IpAddress findByUUID(@Param("uuid") String uuid);

    /**
     * Find all ipaddresses by network Id.
     *
     * @param state get the ipaddress state.
     * @param networkId from ipaddress
     * @return ipaddress list.
     */
    @Query(value = "SELECT ip FROM IpAddress ip WHERE ip.networkId=:networkId AND ip.state =:state")
    List<IpAddress> findByNetwork(@Param("networkId") Long networkId, @Param("state") IpAddress.State state);

    /**
     * Find all ipaddresses by network Id.
     *
     * @param state get the ipaddress state.
     * @param pageable to get the list with pagination.
     * @param networkId from ipaddress
     * @return ipaddress list.
     */
    @Query(value = "SELECT ip FROM IpAddress ip WHERE ip.networkId=:networkId AND ip.state =:state")
    Page<IpAddress> findByNetwork(Pageable pageable, @Param("networkId") Long networkId, @Param("state") IpAddress.State state);

    /**
     * Find all the Allocate or Free ipaddress with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param state get the ipaddress list based on state.
     * @return list of ipaddresses.
     */
    @Query(value = "SELECT ip FROM IpAddress ip WHERE ip.state =:state")
    Page<IpAddress> findAllByIsActive(Pageable pageable, @Param("state") IpAddress.State state);

    /**
     * Find all the active or inactive ipaddress with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the ipaddress list based on active/inactive status.
     * @return list of ipaddress.
     */
    @Query(value = "SELECT ip FROM IpAddress ip WHERE ip.isActive =:isActive")
    Page<IpAddress> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive ipaddresses.
     *
     *.@param state get the ipaddress list based on state.
     * @param isActive get the ipaddress list based on active/inactive status.
     * @return list of ipaddress.
     */
    @Query(value = "SELECT ip FROM IpAddress ip WHERE ip.isActive =:isActive AND ip.state =:state")
    List<IpAddress> findAllByIsActiveAndState(@Param("state") IpAddress.State state, @Param("isActive") Boolean isActive);

}
