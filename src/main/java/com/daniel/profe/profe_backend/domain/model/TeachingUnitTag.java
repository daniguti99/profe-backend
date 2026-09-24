package com.daniel.profe.profe_backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeachingUnitTag {

    private Integer id;
    private Integer teachingUnitId;
    private Integer tagId;
}