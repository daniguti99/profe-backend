package com.daniel.profe.profe_backend.infraestructure.adapter.in.rest;

import com.daniel.profe.profe_backend.application.dto.course.CourseDTO;
import com.daniel.profe.profe_backend.domain.port.in.GetCoursesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Cursos", description = "Listado de cursos.")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final GetCoursesUseCase getCoursesUseCase;

    @Operation(summary = "Lista todos los cursos")
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courses = getCoursesUseCase.getAllCourses();
        return ResponseEntity.ok(courses);
    }
}
