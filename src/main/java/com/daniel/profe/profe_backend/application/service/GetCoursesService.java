package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.course.CourseDTO;
import com.daniel.profe.profe_backend.domain.port.in.GetCoursesUseCase;
import com.daniel.profe.profe_backend.domain.port.out.CourseRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GetCoursesService implements GetCoursesUseCase {

    private final CourseRepositoryPort courseRepository;

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(course -> CourseDTO.builder()
                        .id(course.getId())
                        .name(course.getName())
                        .cycleId(course.getCycleId())
                        .build())
                .toList();
    }
}
