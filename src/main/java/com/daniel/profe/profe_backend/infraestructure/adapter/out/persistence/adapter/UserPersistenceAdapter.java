package com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.adapter;

import com.daniel.profe.profe_backend.domain.model.User;
import com.daniel.profe.profe_backend.domain.port.out.UserRepositoryPort;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.repository.UserJpaRepository;
import com.daniel.profe.profe_backend.infraestructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository repository;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        return userMapper.toDomain(repository.save(userMapper.toEntity(user)));

        /*UserEntity entity = userMapper.toEntity(user);

        UserEntity savedEntity = repository.save(entity);

        User result = userMapper.toDomain(savedEntity);

        return result;*/
    }

    @Override
    public Optional<User> findByEmail(String email) {
        //.map(userMapper::toDomain) Busca un usuario por email y lo convierte a modelo de dominio si existe.
        return repository.findByEmail(email).map(userMapper::toDomain);

        /*
        Optional<UserEntity> entityOptional = repository.findByEmail(email);

        if (entityOptional.isPresent()) {
            UserEntity entity = entityOptional.get();
            User user = userMapper.toDomain(entity);
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
         */
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username).map(userMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }
}
