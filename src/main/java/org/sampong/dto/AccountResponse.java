package org.sampong.dto;

/**
 * DTO for {@link org.sampong.models.Account}
 */
public record AccountResponse(Long id, String accountNumber, String accountName, Double balance, String currency,
                              UserResponse user) {
}