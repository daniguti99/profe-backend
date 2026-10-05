package com.daniel.profe.profe_backend.domain.port.in;

import com.daniel.profe.profe_backend.application.dto.course.CourseDTO;

import java.util.List;

public interface GetCoursesUseCase {
    List<CourseDTO> getAllCourses();
}
