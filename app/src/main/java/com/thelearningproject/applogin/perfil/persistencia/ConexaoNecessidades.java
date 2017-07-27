package com.thelearningproject.applogin.perfil.persistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Pichau on 26/07/2017.
 */

public class ConexaoNecessidades {

    private static ConexaoNecessidades sInstance;
    private SQLiteOpenHelper banco;
    private static final String TABELA = "usuario_materia";
    private static final String IDPERFIL = "id_perfil";
    private static final String IDMATERIA = "id_materia";

    public static synchronized ConexaoNecessidades getInstance(Context context){
        if(sInstance == null){
            sInstance = new ConexaoNecessidades(context.getApplicationContext());
        }
        return sInstance;
    }

    public ConexaoNecessidades(Context context){
        this.banco = BancoNecessidade.getInstance(context);
    }


    public void insereConexao(int id_perfil, int id_materia){
        ContentValues values = new ContentValues();
        values.put(IDPERFIL, id_perfil);
        values.put(IDMATERIA, id_materia);
        banco.getWritableDatabase().insert(TABELA, null, values);
    }

    public boolean verificatupla(int id_perfil, int id_materia){
        Cursor cursor = banco.getReadableDatabase().query(TABELA,new String[]{IDPERFIL},IDPERFIL + " = ? AND " +IDMATERIA+ " = ?",new String[]{Integer.toString(id_perfil),Integer.toString(id_materia)},null,null,null);
        return cursor.moveToFirst();
    }

    //Retorna todos os usuarios que buscaram a matéria de id = id_materia
    public ArrayList<Integer> retornaUsuarios(int id_materia){
        ArrayList<Integer> usuarios = new ArrayList<Integer>();
        Cursor cursor = banco.getReadableDatabase().query(TABELA,new String[]{IDPERFIL},IDMATERIA+" = ?",new String[]{Integer.toString(id_materia)},null,null,null );

        while(cursor.moveToNext()){
            usuarios.add(cursor.getColumnIndex(IDPERFIL));
        }
        return usuarios;
    }

    //Retorna todas as materias buscadas pelo usuario de id = id_perfil
    public ArrayList<Integer> retornaMaterias(int id_perfil){
        ArrayList<Integer> materias = new ArrayList<Integer>();
        Cursor cursor = banco.getReadableDatabase().query(TABELA,new String[]{IDMATERIA},IDPERFIL+" = ?",new String[]{Integer.toString(id_perfil)},null,null,null );

        while(cursor.moveToNext()){
            materias.add(cursor.getColumnIndex(IDPERFIL));
        }
        return materias;
    }

    public ArrayList<Integer> retornaFrequencia(int id_materia){

        String subtabela = "SELECT " + IDPERFIL+ " FROM "+ TABELA + " WHERE " + IDMATERIA + " = " + id_materia;
        ArrayList<Integer> usuarios = new ArrayList<Integer>();

        Cursor cursor = banco.getReadableDatabase().rawQuery(
                "SELECT " +IDMATERIA+ ", count(" +IDMATERIA+ ")" +
                " FROM " +TABELA+
                " WHERE " +IDPERFIL+ " IN ("+subtabela+")" +
                " GROUP BY " +IDMATERIA+" ORDER BY count("+IDMATERIA+") DESC", null
        );

        while(cursor.moveToNext()){
            usuarios.add(cursor.getColumnIndex(IDPERFIL));
        }

        return usuarios;
    }


}