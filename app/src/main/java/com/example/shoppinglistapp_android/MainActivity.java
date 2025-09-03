package com.example.shoppinglistapp_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends Activity {


    private EditText meuTexto;
    private ListView minhaLista;
    private Button meuBotao;
    private SQLiteDatabase bancoDeDados;

    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<Integer> ids;
    private ArrayList<String> itens;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        meuTexto = findViewById(R.id.editText);
        minhaLista = findViewById(R.id.minhaLista);
        meuBotao = findViewById(R.id.button);


        try{
            bancoDeDados = openOrCreateDatabase("ShoppingList", MODE_PRIVATE, null);
            bancoDeDados.execSQL("Create Table IF NOT EXISTS meusItens(id INTEGER PRIMARY KEY AUTOINCREMENT, item VARCHAR)");
        } catch (RuntimeException e) {
            Toast.makeText(this, "Erro ao inicializar o banco de dados.", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }

        carregarItems();
        minhaLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Segure o item para deletar.", Toast.LENGTH_SHORT).show();
            }
        });

        minhaLista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                alertaApagarTarefa(position);
                return false;
            }
        });
        meuBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarItem(meuTexto.getText().toString());
            }
        });


    }

    private void carregarItems(){
        try{
            Cursor cursor = bancoDeDados.rawQuery("SELECT * FROM meusItens ORDER BY id DESC", null);

            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaItem = cursor.getColumnIndex("item"); // Corrigido para 'item'

            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String itemId = cursor.getString(indiceColunaId);
                    String itemNome = cursor.getString(indiceColunaItem);
                    Log.i("Logx", "ID: " + itemId + " | Item: " + itemNome);
                    itens.add(itemNome);
                    ids.add(Integer.parseInt(itemId));
                } while (cursor.moveToNext());
            }

            if (cursor != null) {
                cursor.close();
            }

            itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1, itens);

            minhaLista.setAdapter(itensAdaptador);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void adicionarItem(String novoItem){
        try{
          if(novoItem.trim().isEmpty()){
              Toast.makeText(this, "Digite o nome do item!", Toast.LENGTH_SHORT).show();
          }else {
              Toast.makeText(this, "Item: " + novoItem +  " inserido!", Toast.LENGTH_SHORT).show();

              String newItem = meuTexto.getText().toString();
              bancoDeDados.execSQL("INSERT INTO meusItens(item) VALUES('" + newItem + "')");
              carregarItems();
              meuTexto.setText("");
          }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao adicionar item.", Toast.LENGTH_SHORT).show();
        }
    }

    private void apagarItem(Integer id){
        try{
            bancoDeDados.execSQL("DELETE FROM meusItens WHERE id=" + id);
            carregarItems();
            Toast.makeText(this, "Item removido.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Item não encontrado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void alertaApagarTarefa(Integer idSelecionado){
        String tarefaSelecionada = itens.get(idSelecionado);
        final Integer numeroId = idSelecionado;

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Aviso!")
                .setMessage("Deseja apagar a tarefa: " + tarefaSelecionada + " ?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        apagarItem(ids.get(numeroId));
                    }
                }).setNegativeButton("Não", null).show();
    }
}