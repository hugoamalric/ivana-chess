package dev.gleroy.ivanachess.api.user

import dev.gleroy.ivanachess.dto.UserDto
import org.springframework.stereotype.Component

/**
 * Default implementation of user converter.
 */
@Component
class DefaultUserConverter : UserConverter {
    override fun convert(user: User) = UserDto(
        id = user.id,
        pseudo = user.pseudo,
        creationDate = user.creationDate,
        role = user.role.toDto()
    )

    /**
     * Convert user role to DTO.
     *
     * @return Role DTO.
     */
    private fun User.Role.toDto() = when (this) {
        User.Role.Simple -> UserDto.Role.Simple
        User.Role.Admin -> UserDto.Role.Admin
        User.Role.SuperAdmin -> UserDto.Role.SuperAdmin
    }
}
