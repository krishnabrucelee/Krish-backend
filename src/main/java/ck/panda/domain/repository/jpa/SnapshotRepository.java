package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Snapshot;

/**
 * Jpa Repository for Snapshot entity.
 *
 */
@Service
public interface SnapshotRepository extends PagingAndSortingRepository<Snapshot, Long> {

}
