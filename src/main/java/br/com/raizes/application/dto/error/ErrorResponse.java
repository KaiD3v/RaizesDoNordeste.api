package br.com.raizes.application.dto.error;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ErrorResponse {

    private String error;
    private String message;
    private List<ErrorDetail> details;
    private Instant timestamp;
    private String path;
}
