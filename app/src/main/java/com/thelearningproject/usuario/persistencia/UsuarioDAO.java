package com.thelearningproject.usuario.persistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.thelearningproject.infraestrutura.persistencia.Banco;
import com.thelearningproject.infraestrutura.utils.Status;
import com.thelearningproject.usuario.dominio.Usuario;

/**
 * Criado por Nícolas em 22/07/2017.
 */

public final class UsuarioDAO {
    private static UsuarioDAO instancia;
    private SQLiteOpenHelper banco;
    private Status[] valores = Status.values();

    private static final String TABELA_USUARIOS = "usuarios";
    private static final String ID_USUARIO = "id";
    private static final String EMAIL_USUARIO = "email";
    private static final String SENHA_USUARIO = "senha";
    private static final String STATUS_USUARIO = "status";

    public static synchronized UsuarioDAO getInstancia(Context context) {
        if (instancia == null) {
            instancia = new UsuarioDAO(context.getApplicationContext());
        }
        return instancia;
    }

    private UsuarioDAO(Context context) {
        this.banco = Banco.getInstancia(context);
    }

    public void inserir(Usuario usuario) {
        SQLiteDatabase db = banco.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EMAIL_USUARIO, usuario.getEmail());
        values.put(SENHA_USUARIO, usuario.getSenha());
        values.put(STATUS_USUARIO, usuario.getStatus().getValor());
        db.insert(TABELA_USUARIOS, null, values);
        db.close();

    }

    public Boolean consultaUsuarioEmail(String email) {
        Cursor cursor = banco.getReadableDatabase().query(TABELA_USUARIOS, null, "email = ?", new String[]{email}, null, null, null);
        boolean resultado = cursor.moveToFirst();
        cursor.close();
        return resultado;
    }

    public Boolean consultaUsuarioEmailStatus(String email, String status) {
        Cursor cursor = banco.getReadableDatabase().query(TABELA_USUARIOS, null, "email = ? AND status = ?", new String[]{email, status}, null, null, null);
        boolean resultado = cursor.moveToFirst();
        cursor.close();
        return resultado;
    }

    public Usuario retornaUsuarioPorEmail(String email) {
        String[] colunas = {ID_USUARIO, EMAIL_USUARIO, STATUS_USUARIO};
        Cursor cursor = banco.getReadableDatabase().query(TABELA_USUARIOS, colunas, "email = ?", new String[]{email}, null, null, null);
        Usuario usuario = null;

        if (cursor.moveToFirst()) {
            usuario = new Usuario();
            usuario.setId(cursor.getInt(cursor.getColumnIndex(ID_USUARIO)));
            usuario.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL_USUARIO)));
            usuario.setStatus(valores[(cursor.getInt(cursor.getColumnIndex(STATUS_USUARIO)))]);
        }

        cursor.close();
        return usuario;
    }

    public int retornaUsuarioID(String email) {
        String[] colunas = {ID_USUARIO, EMAIL_USUARIO, SENHA_USUARIO, STATUS_USUARIO};
        Cursor cursor = banco.getReadableDatabase().query(TABELA_USUARIOS, colunas, "email = ? ", new String[]{email}, null, null, null);

        int id = -1;

        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(ID_USUARIO));
        }

        cursor.close();
        return id;
    }

    public Usuario retornaUsuario(String email, String senha) {
        String[] colunas = {ID_USUARIO, EMAIL_USUARIO, SENHA_USUARIO, STATUS_USUARIO};
        Cursor cursor = banco.getReadableDatabase().query(TABELA_USUARIOS, colunas, "email = ? AND senha = ?", new String[]{email, senha}, null, null, null);
        Usuario usuario = null;

        if (cursor.moveToFirst()) {
            usuario = new Usuario();
            usuario.setId(cursor.getInt(cursor.getColumnIndex(ID_USUARIO)));
            usuario.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL_USUARIO)));
            usuario.setSenha(cursor.getString(cursor.getColumnIndex(SENHA_USUARIO)));
            usuario.setStatus(valores[(cursor.getInt(cursor.getColumnIndex(STATUS_USUARIO)))]);
        }

        cursor.close();
        return usuario;
    }

    public void deletaUsuario(Usuario usuario) {
        usuario.setStatus(Status.DESATIVADO);
        alterarUsuario(usuario);

    }

    //diferente da função retorna usuario essa aqui é so uma busca por id e não uma validação email e senha
    public Usuario pesquisarUsuario(int codigo) {
        String[] colunas = {ID_USUARIO, EMAIL_USUARIO, SENHA_USUARIO, STATUS_USUARIO};
        Cursor cursor = banco.getReadableDatabase().query(TABELA_USUARIOS, colunas, ID_USUARIO + " = ?",
                new String[]{String.valueOf(codigo)}, null, null, null, null);
        Usuario usuario = null;

        if (cursor.moveToFirst()) {
            usuario = new Usuario();
            usuario.setId(cursor.getInt(cursor.getColumnIndex(ID_USUARIO)));
            usuario.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL_USUARIO)));
            usuario.setSenha(cursor.getString(cursor.getColumnIndex(SENHA_USUARIO)));
            usuario.setStatus(valores[(cursor.getInt(cursor.getColumnIndex(STATUS_USUARIO)))]);
        }
        cursor.close();
        return usuario;
    }

    public void alterarUsuario(Usuario usuario) {
        SQLiteDatabase db = banco.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EMAIL_USUARIO, usuario.getEmail());
        values.put(SENHA_USUARIO, usuario.getSenha());
        values.put(STATUS_USUARIO, usuario.getStatus().getValor());

        db.update(TABELA_USUARIOS, values, ID_USUARIO + " = ?",
                new String[]{String.valueOf(usuario.getId())});
        db.close();
    }
}
