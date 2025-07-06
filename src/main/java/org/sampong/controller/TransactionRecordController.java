package org.sampong.controller;

import org.sampong.dto.TrxRequest;
import org.sampong.models.TransactionRecord;
import org.sampong.services.TransactionRecordService;
import org.sampong.services.implement.TransactionRecordServiceImpl;

import java.util.List;

public class TransactionRecordController {
    private final TransactionRecordService trxRecordService = new TransactionRecordServiceImpl();

    public TransactionRecordController() {
    }

    public List<TransactionRecord> getAllTransactionRecords() {
        return trxRecordService.getAll();
    }

    public void transferTransaction(TrxRequest trxRecord) {
        trxRecordService.transferBalance(trxRecord);
    }

    public void withdrawTransaction(TrxRequest trxRecord) {
        trxRecordService.withdrawBalance(trxRecord);
    }

    public void depositTransaction(TrxRequest trxRecord) {
        trxRecordService.depositBalance(trxRecord);
    }
}
