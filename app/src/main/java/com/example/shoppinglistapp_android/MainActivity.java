package com.example.shoppinglistapp_android;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class MainActivity extends Activity {


    private EditText meuTexto;
    private ListView minhaLista;
    private Button meuBotao;

    private SQLiteDatabase bancoDeDados;

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

        meuBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                adicionarItem(meuTexto.getText().toString());


            }
        });
    }

    private void carregarItems(){
        try{
            Cursor cursor = bancoDeDados.rawQuery("SELECT * FROM meusItens", null);

            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaItem = cursor.getColumnIndex("item"); // Corrigido para 'item'

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String itemId = cursor.getString(indiceColunaId);
                    String itemNome = cursor.getString(indiceColunaItem);
                    Log.i("Logx", "ID: " + itemId + " | Item: " + itemNome);
                } while (cursor.moveToNext());
            }

            if (cursor != null) {
                cursor.close();
            }

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
              meuTexto.setText("");
              String newItem = meuTexto.getText().toString();
              bancoDeDados.execSQL("INSERT INTO meusItens(item) VALUES('" + newItem + "')");
              carregarItems();

          }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao adicionar item.", Toast.LENGTH_SHORT).show();
        }
    }

    private void apagarItem(){

    }
}