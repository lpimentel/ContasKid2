package com.padm.ipg.contaskid;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Nivel4Activity extends AppCompatActivity {

    //** Declaração dos objetos **//

    private TextView tv_nome, tv_score;
    private ImageView iv_Aum, iv_Adois, iv_vidas, iv_signo;
    private EditText et_resposta;
    private MediaPlayer mp, mp_great, mp_bad;



    //** Declaração de variáveis e vetor de correspondência às operações **//

    int score, numAleatorio_um, numAleatorio_dois, resultado, vidas = 3;
    String nome_jogador, string_score, string_vidas;

    String numero [] = {"zero", "um", "dois", "tres", "quatro", "cinco", "seis", "sete", "oito","nove"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nivel4);


        Toast.makeText(this, getString(R.string.Toast_NivelQuatro), Toast.LENGTH_SHORT).show();

        //** Cricão das relações entre a parte lógica e gráfica/ altera o signo das contas **//

        tv_nome = (TextView)findViewById(R.id.textView_nome);
        tv_score = (TextView)findViewById(R.id.textView_score);
        iv_vidas = (ImageView)findViewById(R.id.imageView_vidas);
        iv_Aum = (ImageView)findViewById(R.id.imageView_NumUn);
        iv_Adois = (ImageView)findViewById(R.id.imageView_NumDois);
        iv_signo = (ImageView)findViewById(R.id.imageView_signo);
        et_resposta = (EditText)findViewById(R.id.editText_resultado);

        //** Obtenção do nome do jogar proveniente do terceiro nível **//

        nome_jogador = getIntent().getStringExtra("jogador");
        tv_nome.setText("Jogador: " + nome_jogador);


        //** Recupera o score **//

        string_score = getIntent().getStringExtra("score");
        score = Integer.parseInt(string_score);
        tv_score.setText("Score: " + score);

        //** Recupera as vidas **//

        string_vidas = getIntent().getStringExtra("vidas");
        vidas = Integer.parseInt(string_vidas);
        if (vidas ==3){
            iv_vidas.setImageResource(R.drawable.tresvidas);
        }if (vidas ==2){
            iv_vidas.setImageResource(R.drawable.duasvidas);
        }if (vidas ==1){
            iv_vidas.setImageResource(R.drawable.umavida);
        }


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        //** Colocação dos sons **//

        mp = MediaPlayer.create(this, R.raw.goats);
        mp.start();
        mp.setLooping(true);

        //** Carregamento de dados relativos a sons de certo ou errado **//

        mp_great = MediaPlayer.create(this, R.raw.wonderful);
        mp_bad = MediaPlayer.create(this, R.raw.bad);

        //** Indica utilização do método aleatóruio do número **//

        NumAleatorio();

    }

    // ** Configuração de validação de resposta **//

    public void Comparar(View view){
        String resposta = et_resposta.getText().toString();

        // ** Configura falta de resposta por parte do utilizador; som para acerto ou falha; número de vidas **//

        if (!resposta.equals("")){

            int resposta_jogador = Integer.parseInt(resposta);
            if (resultado == resposta_jogador){

                mp_great.start();
                score ++;
                tv_score.setText("Score:" + score);
                et_resposta.setText("");
                BaseDeDados();


            } else {

                mp_bad.start();
                vidas--;
                BaseDeDados();

                switch (vidas){
                    case 3:
                        iv_vidas.setImageResource(R.drawable.tresvidas);
                        break;
                    case 2:
                        Toast.makeText(this,getString(R.string.Toast_DuasVidas), Toast.LENGTH_LONG).show();
                        iv_vidas.setImageResource(R.drawable.duasvidas);
                        break;
                    case 1:
                        Toast.makeText(this, getString(R.string.Toast_UmaVida), Toast.LENGTH_LONG).show();
                        iv_vidas.setImageResource(R.drawable.umavida);
                        break;
                    case 0:
                        Toast.makeText(this, getString(R.string.Toast_SemVidas), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        mp.stop();
                        mp.release();
                        break;
                }

                et_resposta.setText("");

            }

            NumAleatorio();


        }   else{
            Toast.makeText(this, getString(R.string.else_IndicaResposta), Toast.LENGTH_SHORT).show();

        }
    }

    // ** Método para criação de somas e subtrações cuja soma não seja menor que 40 **//

    public void NumAleatorio (){

        if (score<=39){

            numAleatorio_um = (int) (Math.random() * 10);
            numAleatorio_dois = (int) (Math.random() * 10);

            if (numAleatorio_um>= 0 && numAleatorio_dois <4){
                resultado = numAleatorio_um + numAleatorio_dois;
                iv_signo.setImageResource(R.drawable.adiciona);
                } else {
                    resultado = numAleatorio_um - numAleatorio_dois;
                    iv_signo.setImageResource(R.drawable.subtrai);

            }


            // **Condição para impedir números negativos**//

            if (resultado>=0){
                for (int i = 0; i  < numero.length; i++) {
                    int id = getResources().getIdentifier(numero[i], "drawable", getPackageName());
                    if (numAleatorio_um == i) {
                        iv_Aum.setImageResource(id);

                    }if (numAleatorio_dois == i){
                        iv_Adois.setImageResource(id);
                    }
                }

            } else {
                NumAleatorio();
            }



            //** Passa para a próxima atividade **//

        } else {
            Intent intent = new Intent(this, Nivel5Activity.class);

            //** Envia o nome e o score para a próxima atividade**//

            string_score = String.valueOf(score);
            string_vidas = String.valueOf(vidas);
            intent.putExtra("jogador", nome_jogador);
            intent.putExtra("score", string_score);
            intent.putExtra("vidas", string_vidas);

            //** Inicia a próxima atividade **//

            startActivity(intent);
            finish();
            mp.stop();
            mp.release();

        }

    }
    //** Implementa o score do jogador com maior pontuação **//

    public void BaseDeDados (){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"BD", null, 1);
        SQLiteDatabase BD = admin.getWritableDatabase();

        Cursor consulta = BD.rawQuery("select * from pontos where score = (select max (score) from pontos)", null);
        if(consulta.moveToFirst()){
            String temp_nome = consulta.getString(0 );
            String temp_score = consulta.getString(1);

            int bestScore = Integer.parseInt((temp_score));

            if (score > bestScore){
                ContentValues modificacao = new ContentValues();
                modificacao.put ("nome", nome_jogador);
                modificacao.put ("score", score);


                BD.update("pontos", modificacao, "score=" + bestScore, null);
            }

            BD.close();

        } else {
            ContentValues insertar = new ContentValues();

            insertar.put("nome", nome_jogador);
            insertar.put("score", score);

            BD.insert("pontos", null, insertar);
            BD.close();

        }


    }

    @Override
    public void onBackPressed(){



    }


}