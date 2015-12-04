package ck.panda.domain.repository.mongo;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import ck.panda.domain.entity.DomainUsage;

/**
 * JPA repository for DomainUsage entity.
 */
public interface DomainUsageRepository extends MongoRepository<DomainUsage, String> {

}

