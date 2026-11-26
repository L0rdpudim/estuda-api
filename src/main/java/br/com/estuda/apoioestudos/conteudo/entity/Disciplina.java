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
@Table(name = "tb_disciplina")
public class Disciplina extends EntidadeCatalogo {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ano_escolar_id", nullable = false)
    private AnoEscolar anoEscolar;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "ordem", nullable = false)
    private int ordem;

    @OneToMany(
        mappedBy = "disciplina",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @OrderBy("ordem ASC")
    private List<Aula> aulas = new ArrayList<>();

    protected Disciplina() {
    }

    Disciplina(String nome, int ordem) {
        this.nome = EtapaEnsino.textoObrigatorio(nome, "O nome da disciplina é obrigatório.");
        this.ordem = EtapaEnsino.validarOrdem(ordem);
    }

    void associarAnoEscolar(AnoEscolar anoEscolar) {
        this.anoEscolar = anoEscolar;
    }

    public Aula adicionarAula(String titulo, String descricao, int ordem) {
        Aula aula = new Aula(titulo, descricao, ordem);
        aula.associarDisciplina(this);
        aulas.add(aula);
        return aula;
    }

    public AnoEscolar getAnoEscolar() {
        return anoEscolar;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getOrdem() {
        return ordem;
    }

    public List<Aula> getAulas() {
        return List.copyOf(aulas);
    }
}
