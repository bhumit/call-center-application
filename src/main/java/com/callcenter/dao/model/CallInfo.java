package com.callcenter.dao.model;

import java.time.Instant;

import com.callcenter.dao.DataModelConstants;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * DAO for call info
 */
@Data
@Document(collection = DataModelConstants.CALL)
public class CallInfo {

    private String id;

    private String callerId;

    private String callerName;

    private String employeeId;

    private Instant startedAt;

    private Instant endedAt;

    private Instant receivedAt;

    private boolean inProgress;

    private boolean isQueued;
}
