package com.daniel.profe.profe_backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    private Integer id;

    private Integer teachingUnitId;
    private Integer userId;

    private String title;
    private String description;

    private String materials;
    private String totalDuration;

    private LocalDate date;

    private String warmUpTime;
    private String warmUpDescription;
    private String warmUpGraphicUrl;
    private String warmUpObservations;

    private String mainPartTime;
    private String mainPartDescription;
    private String mainPartGraphicUrl;
    private String mainPartObservations;

    private String coolDownTime;
    private String coolDownDescription;
    private String coolDownGraphicUrl;
    private String coolDownObservations;
}