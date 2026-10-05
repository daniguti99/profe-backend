package com.daniel.profe.profe_backend.infraestructure.adapter.in.rest;

import com.daniel.profe.profe_backend.application.dto.cycle.CycleDTO;
import com.daniel.profe.profe_backend.domain.port.in.GetCyclesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/cycles")
@RequiredArgsConstructor
public class CycleController {

    private final GetCyclesUseCase getCyclesUseCase;

    @GetMapping
    public ResponseEntity<List<CycleDTO>> getAllCycles() {
        List<CycleDTO> cycles = getCyclesUseCase.getAllCycles();
        return ResponseEntity.ok(cycles);
    }
}
