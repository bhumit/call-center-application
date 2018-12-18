package com.callcenter.dao.repository;

import com.callcenter.dao.model.CallInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Mongo repository for storing call info
 */
public interface CallInfoRepository extends MongoRepository<CallInfo, String> {

    Page<CallInfo> findCallInfoByisQueuedTrueOrderByReceivedAt(Pageable pageable);
}
