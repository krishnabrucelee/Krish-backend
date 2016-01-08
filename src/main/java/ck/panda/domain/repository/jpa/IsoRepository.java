package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Iso;

/**
 * Jpa Repository for Iso entity.
 */
@Service
public interface IsoRepository extends PagingAndSortingRepository<Iso, Long> {

    /**
     * Find iso by uuid.
     *
     * @param uuid of iso.
     * @param bootable true/false.
     * @return iso object.
     */
    @Query(value = "select iso from Iso iso where iso.uuid = :uuid and iso.isBootable = :bootable")
    Iso findByUUID(@Param("uuid") String uuid, @Param("bootable") Boolean bootable);

    /**
     * Find all iso by bootable.
     *
     * @param bootable bootable iso.
     * @return list of iso objects.
     */
    @Query(value = "select iso from Iso iso where iso.isBootable = :bootable")
    List<Iso> findByBootable(@Param("bootable") Boolean bootable);

}
