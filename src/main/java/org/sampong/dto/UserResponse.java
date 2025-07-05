package org.sampong.dto;

/**
 * DTO for {@link org.sampong.models.User}
 */
public record UserResponse(Long id, String username, String phoneNumber, String email,
                           String address) {
}