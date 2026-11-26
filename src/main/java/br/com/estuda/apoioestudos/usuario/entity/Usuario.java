package br.com.estuda.apoioestudos.usuario.entity;

import java.time.Instant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    // Deve armazenar o hash da senha, nunca a senha em texto puro.
    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em")
    private Instant atualizadoEm;

    @OneToOne(
        mappedBy = "usuario",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY,
        optional = false
    )
    private PerfilUsuario perfil;

    protected Usuario() {
        // Construtor exigido pelo JPA.
    }

    public Usuario(String nome, String email, String senhaHash, TipoPerfil tipoPerfil) {
        this.nome = textoObrigatorio(nome, "O nome é obrigatório.");
        this.email = textoObrigatorio(email, "O e-mail é obrigatório.");
        this.senhaHash = textoObrigatorio(senhaHash, "O hash da senha é obrigatório.");
        this.ativo = true;
        this.perfil = new PerfilUsuario(this, tipoPerfil);
    }

    private static String textoObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }

        return valor.trim();
    }

    @PrePersist
    protected void aoCriar() {
        this.criadoEm = Instant.now();
    }

    @PreUpdate
    protected void aoAtualizar() {
        this.atualizadoEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }
}
