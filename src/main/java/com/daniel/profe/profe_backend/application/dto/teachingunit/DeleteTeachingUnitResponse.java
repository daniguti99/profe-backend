package com.daniel.profe.profe_backend.application.dto.teachingunit;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteTeachingUnitResponse {

    private String message;
    private TeachingUnitDTO teachingUnit;
}
