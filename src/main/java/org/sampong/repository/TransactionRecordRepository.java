package org.sampong.repository;

import org.sampong.base.BaseRepository;
import org.sampong.models.TransactionRecord;

import java.util.List;

public interface TransactionRecordRepository extends BaseRepository<TransactionRecord> {
    List<TransactionRecord> findAllTrxBySenderId(Long userId);

    List<TransactionRecord> findAllTrxByReceiverId(Long userId);
}
