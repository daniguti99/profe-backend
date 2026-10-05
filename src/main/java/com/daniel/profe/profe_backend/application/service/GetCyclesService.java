package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.cycle.CycleDTO;
import com.daniel.profe.profe_backend.domain.model.Cycle;
import com.daniel.profe.profe_backend.domain.port.in.GetCyclesUseCase;
import com.daniel.profe.profe_backend.domain.port.out.CycleRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GetCyclesService implements GetCyclesUseCase {

    private final CycleRepositoryPort cycleRepository;

    @Override
    public List<CycleDTO> getAllCycles() {
        return cycleRepository.findAll().stream()
                .map(cycle -> CycleDTO.builder()
                        .id(cycle.getId())
                        .name(cycle.getName())
                        .build())
                .toList();
    }
}
