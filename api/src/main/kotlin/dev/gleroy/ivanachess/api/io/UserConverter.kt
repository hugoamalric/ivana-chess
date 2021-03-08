package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.api.user.User
import dev.gleroy.ivanachess.dto.UserDto

/**
 * User converter.
 */
interface UserConverter {
    /**
     * Convert user to DTO.
     *
     * @param user User.
     * @return User DTO.
     */
    fun convertToDto(user: User): UserDto
}
