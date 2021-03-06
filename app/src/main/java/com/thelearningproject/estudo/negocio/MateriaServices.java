package com.thelearningproject.estudo.negocio;

import android.content.Context;

import com.thelearningproject.estudo.dominio.Materia;
import com.thelearningproject.estudo.persistencia.MateriaDAO;

import java.util.ArrayList;

/**
 * Criado por Nicollas on 26/07/2017.
 */

public class MateriaServices {
    private static MateriaServices instancia;
    private MateriaDAO persistencia;

    public MateriaServices(Context context) {
        this.persistencia = MateriaDAO.getInstancia(context);
    }

    public static MateriaServices getInstancia(Context context) {
        if (instancia == null) {
            instancia = new MateriaServices(context);
        }
        return instancia;
    }

    private Materia inserirMateria(Materia materia) {
        persistencia.inserir(materia);
        return persistencia.consultaNome(materia.getNome());
    }

    public Materia cadastraMateria(Materia materia) {
        Materia novamateria = persistencia.consultaNome(materia.getNome());
        if (novamateria == null) {
            novamateria = inserirMateria(materia);
        }
        return novamateria;
    }

    public Materia consultar(int id) {
        return persistencia.consultar(id);
    }

    public Materia consultarNome(String nome) {
        return persistencia.consultaNome(nome);
    }

    public ArrayList<String> retornaLista(String nome) {
        return persistencia.retornaMateriasNome(nome);
    }


}
