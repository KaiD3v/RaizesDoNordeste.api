package br.com.raizes.application.mapper;

import br.com.raizes.application.dto.unidade.UnidadeResponse;
import br.com.raizes.domain.entity.Unidade;
import org.springframework.stereotype.Component;

@Component
public class UnidadeMapper {

    public UnidadeResponse toResponse(Unidade unidade) {
        if (unidade == null) {
            return null;
        }

        return UnidadeResponse.builder()
                .id(unidade.getId())
                .nome(unidade.getNome())
                .endereco(unidade.getEndereco())
                .build();
    }
}
