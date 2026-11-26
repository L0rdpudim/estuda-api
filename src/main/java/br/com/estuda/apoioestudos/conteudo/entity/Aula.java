package br.com.estuda.apoioestudos.conteudo.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_aula")
public class Aula extends EntidadeCatalogo {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "ordem", nullable = false)
    private int ordem;

    @OneToMany(
        mappedBy = "aula",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @OrderBy("ordem ASC")
    private List<MaterialDidatico> materiais = new ArrayList<>();

    protected Aula() {
    }

    Aula(String titulo, String descricao, int ordem) {
        this.titulo = EtapaEnsino.textoObrigatorio(titulo, "O título da aula é obrigatório.");
        this.descricao = descricao == null || descricao.isBlank() ? null : descricao.trim();
        this.ordem = EtapaEnsino.validarOrdem(ordem);
    }

    void associarDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public MaterialDidatico adicionarMaterialTexto(String titulo, String conteudo, int ordem) {
        return adicionarMaterial(
            new MaterialDidatico(TipoMaterialDidatico.TEXTO, titulo, conteudo, null, ordem)
        );
    }

    public MaterialDidatico adicionarMaterialVideo(String titulo, String url, int ordem) {
        return adicionarMaterial(
            new MaterialDidatico(TipoMaterialDidatico.VIDEO, titulo, null, url, ordem)
        );
    }

    public MaterialDidatico adicionarReferencia(
        String titulo,
        String citacao,
        String url,
        int ordem
    ) {
        return adicionarMaterial(
            new MaterialDidatico(TipoMaterialDidatico.REFERENCIA, titulo, citacao, url, ordem)
        );
    }

    private MaterialDidatico adicionarMaterial(MaterialDidatico material) {
        material.associarAula(this);
        materiais.add(material);
        return material;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getOrdem() {
        return ordem;
    }

    public List<MaterialDidatico> getMateriais() {
        return List.copyOf(materiais);
    }
}
