package br.com.estuda.apoioestudos.usuario.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.UUID;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import br.com.estuda.apoioestudos.usuario.entity.StatusValidacaoProfessor;
import br.com.estuda.apoioestudos.usuario.entity.TipoPerfil;
import br.com.estuda.apoioestudos.usuario.entity.Usuario;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Persiste conta, perfil e status de validação do professor")
    void persisteUsuarioEPerfil() {
        String sufixo = UUID.randomUUID().toString();
        Usuario estudante = usuarioRepository.saveAndFlush(
            new Usuario(
                "Estudante de teste",
                "estudante-" + sufixo + "@estuda.com",
                "hash-estudante",
                TipoPerfil.ESTUDANTE
            )
        );
        Usuario professor = usuarioRepository.saveAndFlush(
            new Usuario(
                "Professor de teste",
                "professor-" + sufixo + "@estuda.com",
                "hash-professor",
                TipoPerfil.PROFESSOR
            )
        );

        Long estudanteId = estudante.getId();
        Long professorId = professor.getId();
        entityManager.clear();

        Usuario estudantePersistido = usuarioRepository
            .findByEmailIgnoreCase(estudante.getEmail().toUpperCase(Locale.ROOT))
            .orElseThrow();
        Usuario professorPersistido = usuarioRepository.findById(professorId).orElseThrow();

        assertThat(estudantePersistido.getId()).isEqualTo(estudanteId);
        assertThat(estudantePersistido.getNome()).isEqualTo("Estudante de teste");
        assertThat(estudantePersistido.getCriadoEm()).isNotNull();
        assertThat(estudantePersistido.getPerfil().getTipo()).isEqualTo(TipoPerfil.ESTUDANTE);
        assertThat(estudantePersistido.getPerfil().getStatusValidacaoProfessor()).isNull();

        assertThat(professorPersistido.getPerfil().getTipo()).isEqualTo(TipoPerfil.PROFESSOR);
        assertThat(professorPersistido.getPerfil().getStatusValidacaoProfessor())
            .isEqualTo(StatusValidacaoProfessor.PENDENTE);
    }
}
