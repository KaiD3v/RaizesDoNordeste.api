package br.com.raizes.application.service;

import br.com.raizes.domain.entity.Promocao;
import br.com.raizes.domain.enums.TipoPromocao;
import br.com.raizes.infrastructure.persistence.PromocaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromocaoService {

    private final PromocaoRepository promocaoRepository;

    public BigDecimal calcularDesconto(BigDecimal subtotal) {
        int diaSemana = mapDayOfWeek(LocalDate.now().getDayOfWeek());
        List<Promocao> promocoes = promocaoRepository.findByAtivaTrueAndDiaSemana(diaSemana);

        BigDecimal descontoTotal = BigDecimal.ZERO;
        for (Promocao promocao : promocoes) {
            if (promocao.getTipo() == TipoPromocao.PERCENTUAL_DESCONTO) {
                BigDecimal percentual = promocao.getValorDesconto()
                        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                descontoTotal = descontoTotal.add(subtotal.multiply(percentual));
            }
        }

        return descontoTotal.setScale(2, RoundingMode.HALF_UP);
    }

    private int mapDayOfWeek(DayOfWeek dayOfWeek) {
        return dayOfWeek.getValue();
    }
}
