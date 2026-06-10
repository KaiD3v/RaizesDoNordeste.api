package br.com.raizes.application.service;

import java.util.UUID;

import br.com.raizes.application.dto.unidade.UnidadeRequest;
import br.com.raizes.application.dto.unidade.UnidadeResponse;
import br.com.raizes.application.mapper.UnidadeMapper;
import br.com.raizes.domain.entity.Unidade;
import br.com.raizes.domain.exception.RecursoNaoEncontradoException;
import br.com.raizes.infrastructure.persistence.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;
    private final UnidadeMapper unidadeMapper;

    public List<UnidadeResponse> listar() {
        return unidadeRepository.findAll().stream()
                .map(unidadeMapper::toResponse)
                .toList();
    }

    public UnidadeResponse buscarPorId(UUID id) {
        return unidadeMapper.toResponse(findById(id));
    }

    @Transactional
    public UnidadeResponse criar(UnidadeRequest request) {
        Unidade unidade = Unidade.builder()
                .nome(request.getNome())
                .endereco(request.getEndereco())
                .build();
        return unidadeMapper.toResponse(unidadeRepository.save(unidade));
    }

    @Transactional
    public UnidadeResponse atualizar(UUID id, UnidadeRequest request) {
        Unidade unidade = findById(id);
        unidade.setNome(request.getNome());
        unidade.setEndereco(request.getEndereco());
        return unidadeMapper.toResponse(unidadeRepository.save(unidade));
    }

    @Transactional
    public void deletar(UUID id) {
        Unidade unidade = findById(id);
        unidadeRepository.delete(unidade);
    }

    public Unidade findById(UUID id) {
        return unidadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade não encontrada: " + id));
    }
}
