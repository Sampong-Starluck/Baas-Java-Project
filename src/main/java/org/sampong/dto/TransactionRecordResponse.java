package org.sampong.dto;

public record TransactionRecordResponse(
        Long id,
        Long fromUserId,
        String transactionId,
        Double amount,
        Long fromAccountId,
        String fromAccountNumber,
        String toAccountNumber,
        Long toUserId,
        String toAccountId
) {
}