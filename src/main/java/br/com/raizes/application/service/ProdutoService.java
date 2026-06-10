package br.com.raizes.application.service;

import java.util.UUID;

import br.com.raizes.application.dto.produto.ProdutoRequest;
import br.com.raizes.application.dto.produto.ProdutoResponse;
import br.com.raizes.application.mapper.ProdutoMapper;
import br.com.raizes.domain.entity.Produto;
import br.com.raizes.domain.entity.Unidade;
import br.com.raizes.domain.exception.RecursoNaoEncontradoException;
import br.com.raizes.infrastructure.persistence.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;
    private final UnidadeService unidadeService;

    public List<ProdutoResponse> listarPorUnidade(UUID unidadeId) {
        return produtoRepository.findByUnidadeId(unidadeId).stream()
                .map(produtoMapper::toResponse)
                .toList();
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        Unidade unidade = unidadeService.findById(request.getUnidadeId());

        Produto produto = Produto.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .preco(request.getPreco())
                .categoria(request.getCategoria())
                .unidade(unidade)
                .build();

        return produtoMapper.toResponse(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponse atualizar(UUID id, ProdutoRequest request) {
        Produto produto = findById(id);
        Unidade unidade = unidadeService.findById(request.getUnidadeId());

        produto.setNome(request.getNome());
        produto.setDescricao(request.getDescricao());
        produto.setPreco(request.getPreco());
        produto.setCategoria(request.getCategoria());
        produto.setUnidade(unidade);

        return produtoMapper.toResponse(produtoRepository.save(produto));
    }

    @Transactional
    public void deletar(UUID id) {
        Produto produto = findById(id);
        produtoRepository.delete(produto);
    }

    public Produto findById(UUID id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + id));
    }
}
