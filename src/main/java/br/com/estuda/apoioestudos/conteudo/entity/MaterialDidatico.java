package br.com.estuda.apoioestudos.conteudo.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_material_didatico")
public class MaterialDidatico extends EntidadeCatalogo {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aula_id", nullable = false)
    private Aula aula;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoMaterialDidatico tipo;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "conteudo", columnDefinition = "TEXT")
    private String conteudo;

    @Column(name = "url", length = 2048)
    private String url;

    @Column(name = "ordem", nullable = false)
    private int ordem;

    protected MaterialDidatico() {
    }

    MaterialDidatico(
        TipoMaterialDidatico tipo,
        String titulo,
        String conteudo,
        String url,
        int ordem
    ) {
        this.tipo = Objects.requireNonNull(tipo, "O tipo do material é obrigatório.");
        this.titulo = EtapaEnsino.textoObrigatorio(titulo, "O título do material é obrigatório.");
        this.ordem = EtapaEnsino.validarOrdem(ordem);
        this.conteudo = conteudo == null || conteudo.isBlank() ? null : conteudo;
        this.url = url == null || url.isBlank() ? null : url.trim();

        if (tipo == TipoMaterialDidatico.TEXTO && this.conteudo == null) {
            throw new IllegalArgumentException("Material textual exige conteúdo.");
        }
        if (tipo == TipoMaterialDidatico.TEXTO && this.url != null) {
            throw new IllegalArgumentException("Material textual não deve possuir URL.");
        }
        if (tipo == TipoMaterialDidatico.VIDEO && this.url == null) {
            throw new IllegalArgumentException("Material de vídeo exige URL.");
        }
        if (tipo == TipoMaterialDidatico.REFERENCIA
            && this.conteudo == null
            && this.url == null) {
            throw new IllegalArgumentException("Referência exige citação ou URL.");
        }
    }

    void associarAula(Aula aula) {
        this.aula = aula;
    }

    public Aula getAula() {
        return aula;
    }

    public TipoMaterialDidatico getTipo() {
        return tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public String getUrl() {
        return url;
    }

    public int getOrdem() {
        return ordem;
    }
}
