package br.com.raizes.application.mapper;

import br.com.raizes.application.dto.produto.ProdutoResponse;
import br.com.raizes.domain.entity.Produto;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {

    public ProdutoResponse toResponse(Produto produto) {
        if (produto == null) {
            return null;
        }

        return ProdutoResponse.builder()
                .id(produto.getId())
                .nome(produto.getNome())
                .descricao(produto.getDescricao())
                .preco(produto.getPreco())
                .categoria(produto.getCategoria())
                .unidadeId(produto.getUnidade() != null ? produto.getUnidade().getId() : null)
                .build();
    }
}
