package com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.adapter;

import com.daniel.profe.profe_backend.domain.model.Course;
import com.daniel.profe.profe_backend.domain.port.out.CourseRepositoryPort;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.repository.CourseJpaRepository;
import com.daniel.profe.profe_backend.infraestructure.mapper.CourseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CoursePersistenceAdapter implements CourseRepositoryPort {

    private final CourseJpaRepository repository;
    private final CourseMapper courseMapper;

    @Override
    public List<Course> findAll() {
        return repository.findAll().stream()
                .map(courseMapper::toDomain)
                .toList();
    }
}
