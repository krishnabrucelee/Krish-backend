package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.OsCategory;

/**
 * JPA repository for OS category entity.
 */
public interface OsCategoryRepository extends PagingAndSortingRepository<OsCategory, Long> {

}
