package com.daniel.profe.profe_backend.application.dto.session;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteSessionResponse {

    private String message;
    private SessionDTO session;
}
