package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Network;

/**
 * JPA repository for Network entity.
 */
public interface NetworkRepository extends PagingAndSortingRepository<Network, Long> {

    /**
     * Find Network by uuid.
     * @param uuid Network uuid.
     * @return uuid
     */
    @Query(value = "select net from Network net where net.uuid LIKE :uuid ")
    Network findByUUID(@Param("uuid") String uuid);

    /**
     * Find Network list by department.
     * @param department department name.
     * @return network list.
     */
    @Query(value = "select net from Network net where net.account=:department ")
   	List<Network> findByDepartment(@Param("department") String department);
}
