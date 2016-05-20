package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.SecondaryStorage;

/**
 * Jpa Repository for Secondary storage entity.
 *
 */
@Service
public interface SecondaryStoageRepository extends PagingAndSortingRepository<SecondaryStorage, Long> {

}