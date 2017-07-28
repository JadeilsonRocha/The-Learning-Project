package com.thelearningproject.applogin.infraestrutura.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import com.thelearningproject.applogin.R;
import com.thelearningproject.applogin.infraestrutura.utils.Auxiliar;
import com.thelearningproject.applogin.infraestrutura.utils.ControladorSessao;
import com.thelearningproject.applogin.infraestrutura.utils.UsuarioException;
import com.thelearningproject.applogin.usuario.dominio.Usuario;
import com.thelearningproject.applogin.usuario.gui.CadastroActivity;
import com.thelearningproject.applogin.usuario.negocio.UsuarioServices;

/**
 * Created by Ebony Marques on 17/07/2017.
 */

public class LoginActivity extends Activity {
    private ControladorSessao sessao;
    private Auxiliar auxiliar = new Auxiliar();
    private EditText entradaLogin, entradaSenha;
    private Switch switchConectado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessao = ControladorSessao.getInstancia(this.getApplicationContext());
        entradaLogin = (EditText) findViewById(R.id.tLogin);
        entradaSenha = (EditText) findViewById(R.id.tSenha);
        Button botaoLogin = (Button) findViewById(R.id.btLogin);
        Button botaoCadastro = (Button) findViewById(R.id.cadastroID);
        switchConectado = (Switch) findViewById(R.id.manterSwitch);

        botaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processoLogin(view);
            }
        });

        botaoCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(LoginActivity.this, CadastroActivity.class));
            }
        });

    }

    private boolean validaCampos(Usuario usuario){
        boolean validacao = true;
        StringBuilder erro = new StringBuilder();

        if (usuario.getEmail() == null || usuario.getEmail().trim().length() == 0 || !auxiliar.aplicaPattern(usuario.getEmail().toUpperCase())) {
            entradaLogin.setError("Email inválido");
            validacao = false;

        }
        if (usuario.getSenha() == null || usuario.getSenha().trim().length() == 0) {
            entradaSenha.setError("Senha inválida");
            validacao = false;
        }

        String resultado = (erro.toString().trim());

        if (!resultado.equals("")) {
            Toast.makeText(LoginActivity.this, resultado, Toast.LENGTH_LONG).show();
        }

        return validacao;
    }

    private void processoLogin(View view){
        String email = entradaLogin.getText().toString();
        String senha = entradaSenha.getText().toString();

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha(senha);

        try{
            executarLogin(usuario);

        } catch (UsuarioException e){
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void executarLogin(Usuario usuario) throws UsuarioException {
        if (validaCampos(usuario)) {
            UsuarioServices negocio = UsuarioServices.getInstancia(getBaseContext());
            Usuario logado = negocio.logar(usuario);

            if (logado != null) {
                sessao.encerraSessao();
                sessao.setUsuario(logado);
                sessao.iniciaSessao();

                if (switchConectado.isChecked()) {
                    sessao.salvaSessao();
                }

                Intent entidade = new Intent(LoginActivity.this, MainActivity.class);
                entidade.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();

                startActivity(entidade);

            } else {
                Toast.makeText(LoginActivity.this, "Usuário ou senha incorretos", Toast.LENGTH_LONG).show();

            }
        }
    }

}