package org.sampong.dto;

import org.sampong.models.enumerate.TransactionType;

public record TrxRequest(
        Long userId,
        Long accountId,
        String accountNumber,
        Double balance,
        Long desUserId,
        Long desAccountId,
        String desAccountNumber,
        TransactionType type
) {
}
