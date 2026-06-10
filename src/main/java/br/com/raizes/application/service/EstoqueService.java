package br.com.raizes.application.service;

import java.util.UUID;

import br.com.raizes.application.dto.estoque.EstoqueResponse;
import br.com.raizes.application.dto.estoque.MovimentarEstoqueRequest;
import br.com.raizes.domain.entity.Estoque;
import br.com.raizes.domain.entity.Produto;
import br.com.raizes.domain.entity.Unidade;
import br.com.raizes.domain.enums.TipoMovimentacaoEstoque;
import br.com.raizes.domain.exception.EstoqueInsuficienteException;
import br.com.raizes.domain.exception.NegocioException;
import br.com.raizes.domain.exception.RecursoNaoEncontradoException;
import br.com.raizes.infrastructure.persistence.EstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoService produtoService;
    private final UnidadeService unidadeService;

    public EstoqueResponse consultar(UUID unidadeId, UUID produtoId) {
        Estoque estoque = estoqueRepository.findByProdutoIdAndUnidadeId(produtoId, unidadeId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Estoque não encontrado para produto " + produtoId + " na unidade " + unidadeId));

        return EstoqueResponse.builder()
                .id(estoque.getId())
                .produtoId(estoque.getProduto().getId())
                .produtoNome(estoque.getProduto().getNome())
                .unidadeId(estoque.getUnidade().getId())
                .quantidade(estoque.getQuantidade())
                .build();
    }

    @Transactional
    public EstoqueResponse movimentar(MovimentarEstoqueRequest request) {
        Produto produto = produtoService.findById(request.getProdutoId());
        Unidade unidade = unidadeService.findById(request.getUnidadeId());

        Estoque estoque = estoqueRepository.findByProdutoIdAndUnidadeId(
                request.getProdutoId(), request.getUnidadeId()
        ).orElseGet(() -> Estoque.builder()
                .produto(produto)
                .unidade(unidade)
                .quantidade(0)
                .build());

        if (request.getTipo() == TipoMovimentacaoEstoque.ENTRADA) {
            estoque.setQuantidade(estoque.getQuantidade() + request.getQuantidade());
        } else {
            if (estoque.getQuantidade() < request.getQuantidade()) {
                throw new NegocioException("ESTOQUE_INSUFICIENTE",
                        "Quantidade insuficiente em estoque para saída");
            }
            estoque.setQuantidade(estoque.getQuantidade() - request.getQuantidade());
        }

        estoque = estoqueRepository.save(estoque);

        return EstoqueResponse.builder()
                .id(estoque.getId())
                .produtoId(estoque.getProduto().getId())
                .produtoNome(estoque.getProduto().getNome())
                .unidadeId(estoque.getUnidade().getId())
                .quantidade(estoque.getQuantidade())
                .build();
    }

    public void validarDisponibilidade(UUID produtoId, UUID unidadeId, Integer quantidade) {
        Estoque estoque = estoqueRepository.findByProdutoIdAndUnidadeId(produtoId, unidadeId)
                .orElseThrow(() -> new EstoqueInsuficienteException(
                        "Produto " + produtoId + " sem estoque na unidade " + unidadeId));

        if (estoque.getQuantidade() < quantidade) {
            throw new EstoqueInsuficienteException(
                    "Estoque insuficiente para o produto " + produtoId +
                    ". Disponível: " + estoque.getQuantidade() + ", solicitado: " + quantidade);
        }
    }

    @Transactional
    public void baixarEstoque(UUID produtoId, UUID unidadeId, Integer quantidade) {
        Estoque estoque = estoqueRepository.findByProdutoIdAndUnidadeId(produtoId, unidadeId)
                .orElseThrow(() -> new EstoqueInsuficienteException(
                        "Produto " + produtoId + " sem estoque na unidade " + unidadeId));

        if (estoque.getQuantidade() < quantidade) {
            throw new EstoqueInsuficienteException(
                    "Estoque insuficiente para baixa do produto " + produtoId);
        }

        estoque.setQuantidade(estoque.getQuantidade() - quantidade);
        estoqueRepository.save(estoque);
    }
}
