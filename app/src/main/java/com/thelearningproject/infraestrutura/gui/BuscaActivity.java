package com.thelearningproject.infraestrutura.gui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.thelearningproject.R;
import com.thelearningproject.combinacao.dominio.Combinacao;
import com.thelearningproject.combinacao.dominio.ICriarCombinacao;
import com.thelearningproject.combinacao.negocio.CombinacaoServices;
import com.thelearningproject.estudo.dominio.Materia;
import com.thelearningproject.estudo.negocio.MateriaServices;
import com.thelearningproject.infraestrutura.utils.Auxiliar;
import com.thelearningproject.infraestrutura.utils.ControladorSessao;
import com.thelearningproject.infraestrutura.utils.PerfilAdapter;
import com.thelearningproject.perfil.dominio.Perfil;
import com.thelearningproject.perfil.gui.PerfilActivity;
import com.thelearningproject.perfil.negocio.PerfilServices;
import com.thelearningproject.registrobusca.negocio.DadosServices;

import java.util.ArrayList;

import static android.content.Intent.ACTION_VIEW;

public class BuscaActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, ICriarCombinacao {
    private ListView listaUsuariosEnsinar;
    private ListView listaUsuariosAprender;
    private TextView informacaoResultado;
    private ControladorSessao sessao;
    private CombinacaoServices combinacaoServices;
    private DadosServices dadosServices;
    private TextView tvSemResultados;
    private String titulo = "Resultados para \"";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca);
        sessao = ControladorSessao.getInstancia(this);
        combinacaoServices = CombinacaoServices.getInstancia(this);
        listaUsuariosEnsinar = (ListView) findViewById(R.id.listViewID1);
        listaUsuariosAprender = (ListView) findViewById(R.id.listViewID2);
        dadosServices = DadosServices.getInstancia(this);
        informacaoResultado = (TextView) findViewById(R.id.tv_resultadoID);
        setTitle(this.titulo);
        tvSemResultados = (TextView) findViewById(R.id.tv1);

        handleSearch(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SearchView searchView;
        getMenuInflater().inflate(R.menu.pesquisar_menu, menu);
        MenuItem not = menu.findItem(R.id.notificacoesBtn);
        not.setVisible(false);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem item = menu.findItem(R.id.pesquisarBtn);

        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.pesquisarBtn) {
            return false;
        }
        Auxiliar.esconderTeclado(this);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        startActivity(intent);
        finish();
    }

    public void handleSearch(Intent intent) {

        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
            String s = intent.getStringExtra(SearchManager.QUERY);
            dadosServices.cadastraBusca(sessao.getPerfil(), s);
            listar(s);
            setTitle(titulo + s + "\"");
        } else if (ACTION_VIEW.equalsIgnoreCase(intent.getAction())) {
            String data = intent.getData().getLastPathSegment();
            dadosServices.cadastraBusca(sessao.getPerfil(), data);
            listar(data);
            setTitle(titulo + data + "\"");
        }
    }


    private void listar(String entrada) {
        Auxiliar.esconderTeclado(this);
        informacaoResultado.setText("");

        MateriaServices materiaServices = MateriaServices.getInstancia(this);
        PerfilServices perfilServices = PerfilServices.getInstancia(this);

        dadosServices.cadastraBusca(sessao.getPerfil(), entrada);
        Materia materia = materiaServices.consultarNome(entrada);
        ArrayList<Perfil> listaPerfil = new ArrayList<>();
        ArrayList<Perfil> listaPerfil2 = new ArrayList<>();
        if (materia != null) {
            listaPerfil = perfilServices.listarPerfil(materia);
            listaPerfil2 = perfilServices.listarPerfil2(materia);
        }
        if (listaPerfil.isEmpty() && listaPerfil2.isEmpty()) {
            listaPerfil = dadosServices.recomendaMateria(sessao.getPerfil(), entrada);
            if (!listaPerfil.isEmpty()) {
                informacaoResultado.setText("Sem resultados para " + entrada + "\nMas estes usuarios podem lhe ajudar com algo relacionado:");
                informacaoResultado.getHeight();
            }
        }
        for (Combinacao c : sessao.getPerfil().getCombinacoes()) {
            if (c.getPerfil1() == sessao.getPerfil().getId()) {
                listaPerfil2.remove(perfilServices.consultar(c.getPerfil2()));
                listaPerfil.remove(perfilServices.consultar(c.getPerfil2()));
            } else {
                listaPerfil2.remove(perfilServices.consultar(c.getPerfil1()));
                listaPerfil.remove(perfilServices.consultar(c.getPerfil1()));
            }
        }

        //remove o perfil do usuario ativo da lista de busca
        if (listaPerfil.contains(sessao.getPerfil()) && listaPerfil2.contains(sessao.getPerfil())) {
            listaPerfil.remove(sessao.getPerfil());
            listaPerfil2.remove(sessao.getPerfil());
        }

        ArrayAdapter adaptador = new PerfilAdapter(this, listaPerfil, null, this, null, null, null);
        ArrayAdapter adaptador2 = new PerfilAdapter(this, listaPerfil2, null, this, null, null, null);

        if (listaPerfil.isEmpty() && listaPerfil2.isEmpty()) {
            tvSemResultados.setVisibility(View.VISIBLE);
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.perfisAprender);
            linearLayout.setVisibility(View.GONE);
            adaptador.clear();
            adaptador2.clear();
        }
        adaptador.notifyDataSetChanged();
        listaUsuariosEnsinar.setAdapter(adaptador);
        listaUsuariosEnsinar.setOnItemClickListener(this);

        adaptador2.notifyDataSetChanged();
        listaUsuariosAprender.setAdapter(adaptador2);
        listaUsuariosAprender.setOnItemClickListener(this);
    }

    public void criarCombinacao(Perfil pEstrangeiro) {
        combinacaoServices.requererCombinacao(sessao.getPerfil(), pEstrangeiro);
        handleSearch(getIntent());
        Auxiliar.criarToast(BuscaActivity.this, "Você fez um match");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Perfil p = (Perfil) parent.getAdapter().getItem(position);
        sessao.setPerfilSelecionado(p);
        Intent intent = new Intent(BuscaActivity.this, PerfilActivity.class);
        startActivity(intent);
    }

}
