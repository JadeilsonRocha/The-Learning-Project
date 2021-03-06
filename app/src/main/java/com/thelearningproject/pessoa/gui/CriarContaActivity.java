package com.thelearningproject.pessoa.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.thelearningproject.R;
import com.thelearningproject.infraestrutura.gui.TermosActivity;
import com.thelearningproject.infraestrutura.utils.Auxiliar;
import com.thelearningproject.infraestrutura.utils.ControladorSessao;
import com.thelearningproject.infraestrutura.utils.Status;
import com.thelearningproject.infraestrutura.utils.UsuarioException;
import com.thelearningproject.pessoa.dominio.Pessoa;
import com.thelearningproject.pessoa.negocio.PessoaServices;
import com.thelearningproject.usuario.dominio.Usuario;
import com.thelearningproject.usuario.negocio.UsuarioServices;

public class CriarContaActivity extends Activity {
    private ControladorSessao sessao;
    private Auxiliar auxiliar = new Auxiliar();
    private EditText entradaNome;
    private EditText entradaEmail;
    private EditText entradaSenha;
    private EditText entradaTelefone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        sessao = ControladorSessao.getInstancia(this.getApplicationContext());
        entradaNome = (EditText) findViewById(R.id.nomeEntradaID);
        entradaEmail = (EditText) findViewById(R.id.emailEntradaID);
        entradaSenha = (EditText) findViewById(R.id.senhaEntradaID);
        entradaTelefone = (EditText) findViewById(R.id.telefoneEntradaID);
        Button botaoContinuar = (Button) findViewById(R.id.botaoCadastroID);

        botaoContinuar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                processoCadastro();
            }
        });
    }

    private boolean validaCampos(Pessoa pessoa) {
        boolean validacao = true;

        if (pessoa.getNome() == null || pessoa.getNome().trim().length() == 0) {
            entradaNome.setError("Nome inválido");
            validacao = false;
        }
        if (!auxiliar.aplicaPattern(pessoa.getUsuario().getEmail().toUpperCase())) {
            entradaEmail.setError("Email inválido");
            validacao = false;
        }
        if (pessoa.getUsuario().getSenha() == null || pessoa.getUsuario().getSenha().trim().length() == 0) {
            entradaSenha.setError("Senha inválida");
            validacao = false;
        }
        if (!Auxiliar.telefonePattern(pessoa.getTelefone())) {
            entradaTelefone.setError("Telefone inválido");
            validacao = false;
        }

        return validacao;
    }

    private void processoCadastro() {
        String nome = entradaNome.getText().toString();
        String email = entradaEmail.getText().toString();
        String senha = entradaSenha.getText().toString();
        String telefone = entradaTelefone.getText().toString();

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setStatus(Status.ATIVADO);

        Pessoa pessoa = new Pessoa();
        pessoa.setNome(nome);
        pessoa.setUsuario(usuario);
        pessoa.setTelefone(telefone);


        try {
            if (validaCampos(pessoa)) {
                executarCadastro(pessoa);
            }

        } catch (UsuarioException e) {
            entradaEmail.setError(e.getMessage());
        }
    }


    private void executarCadastro(Pessoa pessoa) throws UsuarioException {
        PessoaServices negocioPessoa = PessoaServices.getInstancia(getBaseContext());
        UsuarioServices negocioUsuario = UsuarioServices.getInstancia(getBaseContext());

        if (negocioPessoa.verificaTelefoneExistente(pessoa.getTelefone())) {
            negocioUsuario.inserirUsuario(pessoa.getUsuario());
            int usuarioid = negocioUsuario.retornaUsuarioID(pessoa.getUsuario().getEmail());
            pessoa.getUsuario().setId(usuarioid);
            negocioPessoa.inserirPessoa(pessoa);

            int pessoaid = negocioPessoa.retornaPessoa(usuarioid).getId();
            pessoa.setId(pessoaid);

            Intent entidade = new Intent(CriarContaActivity.this, TermosActivity.class);
            entidade.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            sessao.setPessoa(pessoa);
            sessao.salvarSessao();
            sessao.iniciaSessao();

            startActivity(entidade);
            finish();
        } else {
            entradaTelefone.setError("Telefone já cadastrado");
        }

    }


}
