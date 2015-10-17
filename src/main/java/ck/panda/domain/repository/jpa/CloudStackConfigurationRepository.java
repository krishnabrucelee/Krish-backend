package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.CloudStackConfiguration;

/**
 * JPA repository for CloudStackConfiguration entity.
 */
public interface CloudStackConfigurationRepository extends PagingAndSortingRepository<CloudStackConfiguration, Long> {

// TODO for validation
//@Query(value = "select config from CloudStackConfiguration config where config.apiKey=:apiKey ")
//CloudStackConfiguration findByKeys(@Param("apiKey") String apiKey);
}
