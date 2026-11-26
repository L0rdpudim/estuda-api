package br.com.estuda.apoioestudos.conteudo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import br.com.estuda.apoioestudos.conteudo.entity.EtapaEnsino;
import br.com.estuda.apoioestudos.conteudo.entity.TipoMaterialDidatico;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CatalogoEducacionalRepositoryTest {

    @Autowired
    private EtapaEnsinoRepository etapaEnsinoRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Persiste hierarquia curricular e materiais didáticos ordenados")
    void persisteCatalogoComConteudosTextuaisVideosEReferencias() {
        EtapaEnsino etapa = new EtapaEnsino("Ensino Médio", 1);
        var ano = etapa.adicionarAnoEscolar("1º ano", 1);
        var disciplina = ano.adicionarDisciplina("Matemática", 1);
        var aula = disciplina.adicionarAula(
            "Funções",
            "Introdução a funções e seus gráficos.",
            1
        );
        aula.adicionarMaterialTexto("Resumo", "Uma função relaciona dois conjuntos.", 1);
        aula.adicionarMaterialVideo("Aula em vídeo", "https://example.com/video", 2);
        aula.adicionarReferencia(
            "Livro didático",
            "Autor. Matemática para o ensino médio.",
            "https://example.com/livro",
            3
        );

        EtapaEnsino salva = etapaEnsinoRepository.saveAndFlush(etapa);
        Long etapaId = salva.getId();
        entityManager.clear();

        EtapaEnsino carregada = etapaEnsinoRepository.findById(etapaId).orElseThrow();
        var anoCarregado = carregada.getAnosEscolares().get(0);
        var disciplinaCarregada = anoCarregado.getDisciplinas().get(0);
        var aulaCarregada = disciplinaCarregada.getAulas().get(0);

        assertThat(carregada.getNome()).isEqualTo("Ensino Médio");
        assertThat(anoCarregado.getNome()).isEqualTo("1º ano");
        assertThat(disciplinaCarregada.getNome()).isEqualTo("Matemática");
        assertThat(aulaCarregada.getTitulo()).isEqualTo("Funções");
        assertThat(aulaCarregada.getMateriais())
            .extracting("tipo")
            .containsExactly(
                TipoMaterialDidatico.TEXTO,
                TipoMaterialDidatico.VIDEO,
                TipoMaterialDidatico.REFERENCIA
            );
        assertThat(aulaCarregada.getMateriais())
            .extracting("ordem")
            .containsExactly(1, 2, 3);
        assertThat(aulaCarregada.getMateriais().get(0).getConteudo())
            .isEqualTo("Uma função relaciona dois conjuntos.");
        assertThat(aulaCarregada.getMateriais().get(1).getUrl())
            .isEqualTo("https://example.com/video");
        assertThat(aulaCarregada.getMateriais().get(2).getConteudo())
            .isEqualTo("Autor. Matemática para o ensino médio.");
    }
}
