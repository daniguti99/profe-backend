package com.daniel.profe.profe_backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeachingUnit {

    private Integer id;
    private Integer userId;
    private Integer courseId;

    private String title;
    private String description;
    private String schedule;
    private String notes;
}