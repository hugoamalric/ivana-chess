package dev.gleroy.ivanachess.api.user

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
    fun convert(user: User): UserDto
}
