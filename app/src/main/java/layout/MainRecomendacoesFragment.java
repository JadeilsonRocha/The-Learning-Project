package layout;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.thelearningproject.applogin.R;
import com.thelearningproject.applogin.combinacao.dominio.Combinacao;
import com.thelearningproject.applogin.combinacao.dominio.ICriarCombinacao;
import com.thelearningproject.applogin.combinacao.negocio.CombinacaoServices;
import com.thelearningproject.applogin.estudo.dominio.Materia;
import com.thelearningproject.applogin.infraestrutura.utils.Auxiliar;
import com.thelearningproject.applogin.infraestrutura.utils.ControladorSessao;
import com.thelearningproject.applogin.infraestrutura.utils.PerfilAdapter;
import com.thelearningproject.applogin.perfil.dominio.Perfil;
import com.thelearningproject.applogin.perfil.gui.PerfilActivity;
import com.thelearningproject.applogin.perfil.negocio.PerfilServices;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Criado por Gabriel on 03/08/2017
 */
public class MainRecomendacoesFragment extends Fragment implements AdapterView.OnItemClickListener, ICriarCombinacao {
    private FragmentActivity activity;
    private ListView listaRecomendados;
    private ControladorSessao sessao;
    private CombinacaoServices combinacaoServices;

    public MainRecomendacoesFragment() {
        // Requer um construtor publico vazio
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_recomendacoes, container, false);
        activity = getActivity();
        sessao = ControladorSessao.getInstancia(activity);
        combinacaoServices = CombinacaoServices.getInstancia(getContext());
        listaRecomendados = (ListView) view.findViewById(R.id.listViewRecomendaID);
        listaRecomendados.setOnItemClickListener(this);
        listar();

        return view;
    }

    private void listar() {
        PerfilServices perfilServices = PerfilServices.getInstancia(activity);
        Set<Perfil> listaPerfil = new LinkedHashSet<>();
        for (Materia materia:sessao.getPerfil().getNecessidades()){
            listaPerfil.addAll(perfilServices.listarPerfil(materia));
        }
        for (Materia materia : perfilServices.recomendaMateria(sessao.getPerfil())) {
            listaPerfil.addAll(perfilServices.listarPerfil(materia));
        }
        if (listaPerfil.contains(sessao.getPerfil())) {
            listaPerfil.remove(sessao.getPerfil());
        }
        for (Combinacao c: sessao.getPerfil().getCombinacoes()){
            if (c.getPerfil1() == sessao.getPerfil().getId()) {
                listaPerfil.remove(perfilServices.consulta(c.getPerfil2()));
            } else {
                listaPerfil.remove(perfilServices.consulta(c.getPerfil1()));
            }
        }

        ArrayAdapter adaptador = new PerfilAdapter(activity, new ArrayList<>(listaPerfil), null, this, null);


        adaptador.notifyDataSetChanged();
        listaRecomendados.setAdapter(adaptador);
    }

    public void criarCombinacao(Perfil pEstrangeiro) {
        combinacaoServices.inserirCombinacao(sessao.getPerfil(), pEstrangeiro);
        listar();
        Auxiliar.criarToast(getContext(),"Você fez um match");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Perfil p = (Perfil) parent.getAdapter().getItem(position);
        sessao.setPerfilSelecionado(p);
        Intent intent = new Intent(getActivity(), PerfilActivity.class);
        startActivity(intent);
    }
}
