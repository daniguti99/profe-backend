package com.daniel.profe.profe_backend.domain.port.in;

import com.daniel.profe.profe_backend.application.dto.cycle.CycleDTO;

import java.util.List;

public interface GetCyclesUseCase {
    List<CycleDTO> getAllCycles();
}
