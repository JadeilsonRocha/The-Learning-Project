package com.thelearningproject.applogin.pessoa.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.thelearningproject.applogin.R;
import com.thelearningproject.applogin.infraestrutura.utils.Auxiliar;
import com.thelearningproject.applogin.infraestrutura.utils.ControladorSessao;
import com.thelearningproject.applogin.pessoa.dominio.Pessoa;
import com.thelearningproject.applogin.pessoa.negocio.PessoaServices;
import com.thelearningproject.applogin.usuario.dominio.Usuario;
import com.thelearningproject.applogin.usuario.negocio.UsuarioServices;


public class RecuperarContaActivity extends AppCompatActivity {
    private EditText entradaTelefone;
    private ControladorSessao sessao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_conta);

        sessao = ControladorSessao.getInstancia(this.getApplicationContext());
        entradaTelefone = (EditText) findViewById(R.id.telefoneID);
        Button botaoContinuar = (Button) findViewById(R.id.botaoConfirmaID);

        botaoContinuar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                processoRecuperacao();
            }
        });

    }

    private void processoRecuperacao(){
        String telefone = entradaTelefone.getText().toString();

        PessoaServices negocioPessoa = PessoaServices.getInstancia(getBaseContext());
        Pessoa pessoa = negocioPessoa.retornaPessoa(telefone);

        Auxiliar.criarToast(this, String.valueOf(validaRecuperacao(pessoa)));
        if (validaRecuperacao(pessoa)) {
            executarRecuperacao(pessoa);
        }

    }
    private boolean validaRecuperacao(Pessoa pessoa) {
        boolean validacao = true;
        if(pessoa== null){
            entradaTelefone.setError("Telefone inexistente");
            validacao=false;
        }else if (pessoa.getTelefone() == null || pessoa.getTelefone().trim().length() == 0) {
            entradaTelefone.setError("Telefone inválido");
            validacao = false;
        }

        return validacao;
    }

    private void executarRecuperacao(Pessoa pessoa){
        UsuarioServices negocioUsuario = UsuarioServices.getInstancia(getBaseContext());
        Usuario usuario = negocioUsuario.consulta(pessoa.getUsuario().getId());
        pessoa.setUsuario(usuario);

        String codigo = Auxiliar.geraCodigo();
        Auxiliar.enviaSms(codigo);
        sessao.setCodigo(codigo);
        sessao.setPessoa(pessoa);
        Intent entidade = new Intent(RecuperarContaActivity.this, ConfimaRecuperarActivity.class);
        entidade.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(entidade);
    }

}