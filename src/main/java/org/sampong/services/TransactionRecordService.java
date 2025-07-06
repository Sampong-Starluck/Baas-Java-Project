package org.sampong.services;

import org.sampong.base.BaseService;
import org.sampong.dto.TrxRequest;
import org.sampong.models.TransactionRecord;

public interface TransactionRecordService extends BaseService<TransactionRecord> {
    void transferBalance(TrxRequest request);

    void withdrawBalance(TrxRequest request);

    void depositBalance(TrxRequest request);
}
