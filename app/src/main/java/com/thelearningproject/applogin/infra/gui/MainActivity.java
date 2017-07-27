package com.thelearningproject.applogin.infra.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thelearningproject.applogin.R;
import com.thelearningproject.applogin.infra.utils.SessionController;
import com.thelearningproject.applogin.perfil.dominio.Perfil;
import com.thelearningproject.applogin.perfil.negocio.PerfilServices;
import com.thelearningproject.applogin.usuario.dominio.Usuario;
import com.thelearningproject.applogin.usuario.negocio.UsuarioServices;

public class MainActivity extends AppCompatActivity {
    private SessionController sessao;
    private UsuarioServices negociousuario;
    private PerfilServices negocioperfil;

    private Button botaologout;
    private Button botaoconfig;
    private Button botaoBusca;
    private Button botaoInsereMateria;
    private TextView apresentacao;

    private Button btnAbrir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessao = SessionController.getInstance(this.getApplicationContext());

        apresentacao = (TextView) findViewById(R.id.apresentacaoID);
        botaologout = (Button) findViewById(R.id.LogoutID);
        botaoconfig = (Button) findViewById(R.id.configID);
        botaoBusca = (Button) findViewById(R.id.botaoBuscaID);
        botaoInsereMateria = (Button) findViewById(R.id.BotaoCadastraMateriaID);

        btnAbrir = (Button) findViewById(R.id.btnChamar);

        if(sessao.verificaLogin()) {
            finish();
        } else {
            exibir();
        }

        botaologout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessao.encerraSessao();
                finish();
            }
        });

        botaoBusca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BuscaActivity.class));
            }
            });

        botaoconfig.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, ConfiguracaoActivity.class));
            }
        });

        btnAbrir.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
            }
        });

        botaoInsereMateria.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, CadastroMateriaActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void exibir() {
        negociousuario = UsuarioServices.getInstancia(getBaseContext());
        negocioperfil = PerfilServices.getInstancia(getBaseContext());

        Usuario usuario = negociousuario.retornaUsuario(sessao.getEmail());
        Perfil perfil = negocioperfil.retornaPerfil(usuario.getId());
        perfil.setUsuario(usuario);
        sessao.setPerfil(perfil);

        String mensagem = "Oi, " + usuario.getNome() + ".";

        apresentacao.setText(mensagem);


    }
}
