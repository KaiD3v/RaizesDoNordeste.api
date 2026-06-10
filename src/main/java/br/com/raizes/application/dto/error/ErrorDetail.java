package br.com.raizes.application.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDetail {

    private String field;
    private String issue;
}
