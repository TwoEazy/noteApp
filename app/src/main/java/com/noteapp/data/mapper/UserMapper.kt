package com.noteapp.data.mapper

import com.noteapp.data.model.User
import com.noteapp.data.remote.dto.UserDto

fun UserDto.toDomainModel(): User {
    return User(
        id = this.id,
        email = this.email,
        name = this.fullName ?: "${this.firstName} ${this.lastName}"
    )
}