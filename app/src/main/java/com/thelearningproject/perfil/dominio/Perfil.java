package com.thelearningproject.perfil.dominio;

import com.thelearningproject.combinacao.dominio.Combinacao;
import com.thelearningproject.estudo.dominio.Materia;
import com.thelearningproject.pessoa.dominio.Pessoa;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Criado por Ebony Marques on 26/07/2017.
 */

public class Perfil implements Serializable {
    private int id;
    private Pessoa pessoa;
    private String descricao;
    private ArrayList<Materia> habilidades = new ArrayList<>();
    private ArrayList<Materia> necessidades = new ArrayList<>();
    private ArrayList<Combinacao> combinacoes = new ArrayList<>();

    public ArrayList<Combinacao> getCombinacoes() {
        return combinacoes;
    }

    public void addCombinacoes(Combinacao combinacao) {
        this.combinacoes.add(combinacao);
    }

    public void setCombinacoes(ArrayList<Combinacao> lista) {
        this.combinacoes = lista;
    }

    public int getId() {
        return id;
    }

    public void setId(int novoId) {
        this.id = novoId;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa novaPessoa) {
        this.pessoa = novaPessoa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String novaDescricao) {
        this.descricao = novaDescricao;
    }

    public ArrayList<Materia> getHabilidades() {
        return habilidades;
    }

    public void addHabilidade(Materia nova) {
        habilidades.add(nova);
    }

    public ArrayList<Materia> getNecessidades() {
        return necessidades;
    }

    public void addNecessidade(Materia nova) {
        necessidades.add(nova);
    }

    public boolean equals(Object o) {
        boolean r = false;
        if (o instanceof Perfil) {
            Perfil that = (Perfil) o;
            r = (this.getId() == that.getId());
        }
        return r;
    }

    public int hashCode() {
        return (Integer.toString(this.getId()).hashCode());
    }
}
