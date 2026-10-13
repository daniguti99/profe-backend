package com.daniel.profe.profe_backend.application.dto.teachingunit;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTeachingUnitResponse {

    private Integer id;
    private Integer userId;
    private Integer courseId;
    private String title;
    private String description;
    private String schedule;
    private String notes;
}
