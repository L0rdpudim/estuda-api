package br.com.estuda.apoioestudos.conteudo.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_etapa_ensino")
public class EtapaEnsino extends EntidadeCatalogo {

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "ordem", nullable = false)
    private int ordem;

    @OneToMany(
        mappedBy = "etapaEnsino",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @OrderBy("ordem ASC")
    private List<AnoEscolar> anosEscolares = new ArrayList<>();

    protected EtapaEnsino() {
    }

    public EtapaEnsino(String nome, int ordem) {
        this.nome = textoObrigatorio(nome, "O nome da etapa é obrigatório.");
        this.ordem = validarOrdem(ordem);
    }

    public AnoEscolar adicionarAnoEscolar(String nome, int ordem) {
        AnoEscolar anoEscolar = new AnoEscolar(nome, ordem);
        anoEscolar.associarEtapaEnsino(this);
        anosEscolares.add(anoEscolar);
        return anoEscolar;
    }

    static String textoObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
        return valor.trim();
    }

    static int validarOrdem(int ordem) {
        if (ordem < 1) {
            throw new IllegalArgumentException("A ordem deve ser maior que zero.");
        }
        return ordem;
    }

    public String getNome() {
        return nome;
    }

    public int getOrdem() {
        return ordem;
    }

    public List<AnoEscolar> getAnosEscolares() {
        return List.copyOf(anosEscolares);
    }
}
