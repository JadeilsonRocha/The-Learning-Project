package com.thelearningproject.applogin.infraestrutura.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.thelearningproject.applogin.R;
import com.thelearningproject.applogin.combinacao.dominio.Combinacao;
import com.thelearningproject.applogin.combinacao.negocio.CombinacaoServices;
import com.thelearningproject.applogin.estudo.dominio.Materia;
import com.thelearningproject.applogin.perfil.dominio.Perfil;

import java.util.ArrayList;

import layout.MainInteracoesFragment;
import layout.MainRecomendacoesFragment;


public class PerfilAdapter extends ArrayAdapter<Perfil> {
    private ArrayList<Perfil> listaPerfil;
    private ControladorSessao sessao;
    private CombinacaoServices combinacaoServices;
    private Context contexto;
    private Fragment frag;
    private Perfil perfil;

    public PerfilAdapter(Context context, ArrayList<Perfil> perfils, Fragment frag) {
        super(context, 0, perfils);
        this.listaPerfil = perfils;
        this.contexto = context;
        this.frag = frag;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        sessao = ControladorSessao.getInstancia(contexto);
        combinacaoServices = CombinacaoServices.getInstancia(contexto);

        LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.lista_perfil, parent, false);

        TextView nomePerfil = (TextView) view.findViewById(R.id.tv_perfil);
        TextView habilidades = (TextView) view.findViewById(R.id.tv_habilidades);
        TextView necessidades = (TextView) view.findViewById(R.id.tv_necessidades);

        ImageButton btnNovaInteracao = (ImageButton) view.findViewById(R.id.criarInteracao);
        ImageButton btnDesfazerInteracao = (ImageButton) view.findViewById(R.id.desfazerInteracao);

        if (this.frag instanceof MainRecomendacoesFragment) {
            btnDesfazerInteracao.setVisibility(View.INVISIBLE);
        } else if (this.frag instanceof MainInteracoesFragment) {
            btnNovaInteracao.setVisibility(View.INVISIBLE);
        }


        perfil = listaPerfil.get(position);
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        ArrayList<Materia> listahabilidades = perfil.getHabilidades();
        ArrayList<Materia> listanecessidades = perfil.getNecessidades();
        String prefix = "";
        for (Materia m : listahabilidades) {
            sb1.append(prefix);
            prefix = ", ";
            sb1.append(m.getNome());
        }
        prefix = "";
        for (Materia m : listanecessidades) {
            sb2.append(prefix);
            prefix = ", ";
            sb2.append(m.getNome());
        }
        nomePerfil.setText(perfil.getPessoa().getNome());
        habilidades.setText(sb1.toString());
        necessidades.setText(sb2.toString());

        btnNovaInteracao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                combinacaoServices.inserirCombinacao(sessao.getPerfil(), listaPerfil.get(position));
                Auxiliar.criarToast(contexto,"Match feito");
            }
        });

        btnDesfazerInteracao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Combinacao com = new Combinacao();
                com.setPerfil1(sessao.getPerfil().getId());
                com.setPerfil2(listaPerfil.get(position).getId());
                combinacaoServices.removerCombinacao(sessao.getPerfil(), com);
                Auxiliar.criarToast(contexto,"Match desfeito");
            }
        });

        return view;
    }
}

