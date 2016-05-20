package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.PrimaryStorage;

/**
 * Jpa Repository for Primary storage entity.
 *
 */
@Service
public interface PrimaryStorageRepository extends PagingAndSortingRepository<PrimaryStorage, Long> {

}