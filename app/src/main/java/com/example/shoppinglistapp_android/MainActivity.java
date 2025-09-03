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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends Activity {


    private TextView meuTexto;
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

        meuBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    bancoDeDados = openOrCreateDatabase("ShoppingList", MODE_PRIVATE, null);
                    bancoDeDados.execSQL("Create Table IF NOT EXISTS meusItens(id INTEGER PRIMARY KEY AUTOINCREMENT, item VARCHAR)");

                    String newItem = meuTexto.getText().toString();
                    bancoDeDados.execSQL("INSERT INTO meusItens(tarefa) VALUES('" + newItem + "')");


                    Cursor cursor = bancoDeDados.rawQuery("SELECT * FROM meusItens", null);

                    int indiceColunaId = cursor.getColumnIndex("id");
                    int indiceColunaItem = cursor.getColumnIndex("id");

                    cursor.moveToFirst();
                    while(cursor != null){
                        Log.i("Logx", "ID: " + cursor.getString(indiceColunaId) + "Item: " + cursor.getString(indiceColunaItem));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}