package br.com.estuda.apoioestudos.usuario.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_perfil_usuario")
public class PerfilUsuario {

    @Id
    @Column(name = "usuario_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_perfil", nullable = false, length = 20)
    private TipoPerfil tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_validacao_professor", length = 20)
    private StatusValidacaoProfessor statusValidacaoProfessor;

    protected PerfilUsuario() {
        // Construtor exigido pelo JPA.
    }

    PerfilUsuario(Usuario usuario, TipoPerfil tipo) {
        this.usuario = Objects.requireNonNull(usuario, "O usuário é obrigatório.");
        this.tipo = Objects.requireNonNull(tipo, "O tipo de perfil é obrigatório.");

        if (tipo == TipoPerfil.PROFESSOR) {
            this.statusValidacaoProfessor = StatusValidacaoProfessor.PENDENTE;
        }
    }

    public void atualizarStatusValidacaoProfessor(StatusValidacaoProfessor novoStatus) {
        if (tipo != TipoPerfil.PROFESSOR) {
            throw new IllegalStateException(
                "Somente perfis de professor possuem status de validação."
            );
        }

        this.statusValidacaoProfessor = Objects.requireNonNull(
            novoStatus,
            "O status de validação é obrigatório."
        );
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public TipoPerfil getTipo() {
        return tipo;
    }

    public StatusValidacaoProfessor getStatusValidacaoProfessor() {
        return statusValidacaoProfessor;
    }
}
