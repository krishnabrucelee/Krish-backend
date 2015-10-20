package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.OsType;

/**
 * JPA repository for OS type entity.
 */
public interface OsTypeRepository extends PagingAndSortingRepository<OsType, Long> {

}
