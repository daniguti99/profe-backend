package com.daniel.profe.profe_backend.infraestructure.mapper;

import com.daniel.profe.profe_backend.domain.model.User;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toDomain(UserEntity userEntity);
    UserEntity toEntity(User user);
}
