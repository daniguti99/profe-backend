package com.daniel.profe.profe_backend.application.dto.session;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSessionResponse {

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
    private String warmUpObservations;
    private String mainPartTime;
    private String mainPartDescription;
    private String mainPartObservations;
    private String coolDownTime;
    private String coolDownDescription;
    private String coolDownObservations;

    private Boolean active;
}
