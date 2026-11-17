package com.padm.ipg.contaskid;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private EditText playerInput;
    private MediaPlayer introAudio;

    private static final int[] CHARACTERS = {
        R.drawable.ini1, R.drawable.ini2, R.drawable.ini3,
        R.drawable.ini4, R.drawable.ini5, R.drawable.ini6
    };

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);

        playerInput = findViewById(R.id.txt_nome);
        ImageView character = findViewById(R.id.imageView_Personagem);
        TextView bestScore = findViewById(R.id.textView_BestScore);
        character.setImageResource(CHARACTERS[new Random().nextInt(CHARACTERS.length)]);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        }

        try (AdminSQLiteOpenHelper store = new AdminSQLiteOpenHelper(this)) {
            AdminSQLiteOpenHelper.Record record = store.getBestScore();
            if (record != null) {
                bestScore.setText(getString(R.string.best_score_format, record.score, record.name));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        introAudio = MediaPlayer.create(this, R.raw.alphabet_song);
        if (introAudio != null) {
            introAudio.setLooping(true);
            introAudio.start();
        }
    }

    @Override
    protected void onStop() {
        if (introAudio != null) {
            introAudio.release();
            introAudio = null;
        }
        super.onStop();
    }

    /** Bound to the Play button in activity_main.xml. */
    public void Jogar(View view) {
        String name = playerInput.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, R.string.toast_introduzNome, Toast.LENGTH_SHORT).show();
            playerInput.requestFocus();
            InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (input != null) {
                input.showSoftInput(playerInput, InputMethodManager.SHOW_IMPLICIT);
            }
            return;
        }
        Intent intent = new Intent(this, Nivel1Activity.class);
        intent.putExtra("jogador", name);
        intent.putExtra("score", 0);
        intent.putExtra("vidas", 3);
        startActivity(intent);
        finish();
    }
}
