package com.daniel.profe.profe_backend.domain.port.out;

import com.daniel.profe.profe_backend.domain.model.Course;

import java.util.List;

public interface CourseRepositoryPort {
    List<Course> findAll();
}