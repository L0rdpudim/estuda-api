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
@Table(name = "tb_ano_escolar")
public class AnoEscolar extends EntidadeCatalogo {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etapa_ensino_id", nullable = false)
    private EtapaEnsino etapaEnsino;

    @Column(name = "nome", nullable = false, length = 80)
    private String nome;

    @Column(name = "ordem", nullable = false)
    private int ordem;

    @OneToMany(
        mappedBy = "anoEscolar",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @OrderBy("ordem ASC")
    private List<Disciplina> disciplinas = new ArrayList<>();

    protected AnoEscolar() {
    }

    AnoEscolar(String nome, int ordem) {
        this.nome = EtapaEnsino.textoObrigatorio(nome, "O nome do ano escolar é obrigatório.");
        this.ordem = EtapaEnsino.validarOrdem(ordem);
    }

    void associarEtapaEnsino(EtapaEnsino etapaEnsino) {
        this.etapaEnsino = etapaEnsino;
    }

    public Disciplina adicionarDisciplina(String nome, int ordem) {
        Disciplina disciplina = new Disciplina(nome, ordem);
        disciplina.associarAnoEscolar(this);
        disciplinas.add(disciplina);
        return disciplina;
    }

    public EtapaEnsino getEtapaEnsino() {
        return etapaEnsino;
    }

    public String getNome() {
        return nome;
    }

    public int getOrdem() {
        return ordem;
    }

    public List<Disciplina> getDisciplinas() {
        return List.copyOf(disciplinas);
    }
}
