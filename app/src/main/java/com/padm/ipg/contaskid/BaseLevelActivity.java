package com.padm.ipg.contaskid;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

/** Shared presentation and state management for the six arithmetic levels. */
public abstract class BaseLevelActivity extends AppCompatActivity {
    private static final int[] DIGITS = {
        R.drawable.zero, R.drawable.um, R.drawable.dois, R.drawable.tres,
        R.drawable.quatro, R.drawable.cinco, R.drawable.seis,
        R.drawable.sete, R.drawable.oito, R.drawable.nove
    };
    private static final int[] LEVEL_LAYOUTS = {
        R.layout.activity_nivel1, R.layout.activity_nivel2, R.layout.activity_nivel3,
        R.layout.activity_nivel4, R.layout.activity_nivel5, R.layout.activity_nivel6
    };
    private static final int[] LEVEL_TITLES = {
        R.string.Toast_NiveUM, R.string.Toast_NivelDois, R.string.Toast_NiveTres,
        R.string.Toast_NivelQuatro, R.string.Toast_NiveCinco, R.string.Toast_NivelSeis
    };

    private final Random random = new Random();
    private TextView scoreView;
    private ImageView firstView, secondView, operatorView, livesView;
    private EditText answerInput;
    private MediaPlayer backgroundAudio, correctAudio, incorrectAudio;
    private String playerName;
    private int score;
    private int lives;
    private GameLogic.Question question;

    protected abstract int level();

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        int levelIndex = level() - 1;
        setContentView(LEVEL_LAYOUTS[levelIndex]);

        scoreView = findViewById(R.id.textView_score);
        TextView playerView = findViewById(R.id.textView_nome);
        firstView = findViewById(R.id.imageView_NumUn);
        secondView = findViewById(R.id.imageView_NumDois);
        operatorView = findViewById(R.id.imageView_signo);
        livesView = findViewById(R.id.imageView_vidas);
        answerInput = findViewById(R.id.editText_resultado);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        }

        if (savedState != null) {
            playerName = savedState.getString("playerName");
            score = savedState.getInt("score");
            lives = savedState.getInt("lives");
            question = new GameLogic.Question(
                    savedState.getInt("first"), savedState.getInt("second"),
                    savedState.getChar("operator", '+'));
        } else {
            playerName = getIntent().getStringExtra("jogador");
            score = getIntent().getIntExtra("score", 0);
            lives = getIntent().getIntExtra("vidas", 3);
            question = GameLogic.newQuestion(level(), random);
            Toast.makeText(this, LEVEL_TITLES[levelIndex], Toast.LENGTH_SHORT).show();
        }

        if (playerName == null || playerName.trim().isEmpty() || lives <= 0) {
            goHome();
            return;
        }
        playerView.setText(getString(R.string.player_format, playerName));
        render();
    }

    private void render() {
        scoreView.setText(getString(R.string.score_format, score));
        int livesDrawable = lives >= 3 ? R.drawable.tresvidas
                : lives == 2 ? R.drawable.duasvidas : R.drawable.umavida;
        livesView.setImageResource(livesDrawable);
        firstView.setImageResource(DIGITS[question.first]);
        secondView.setImageResource(DIGITS[question.second]);
        int symbol = question.operator == '+' ? R.drawable.adiciona
                : question.operator == '-' ? R.drawable.subtrai : R.drawable.multiplicacao;
        operatorView.setImageResource(symbol);
    }

    /** Bound to the Check button on each level layout. */
    public void Comparar(View view) {
        String raw = answerInput.getText().toString().trim();
        if (raw.isEmpty()) {
            Toast.makeText(this, R.string.Toast_IndicaResposta, Toast.LENGTH_SHORT).show();
            return;
        }
        int answer;
        try {
            answer = Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            Toast.makeText(this, R.string.answer_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        answerInput.setText("");
        if (answer == question.answer) {
            playEffect(correctAudio);
            score++;
            try (AdminSQLiteOpenHelper store = new AdminSQLiteOpenHelper(this)) {
                store.saveIfRecord(playerName, score);
            }
            if (score >= GameLogic.targetScore(level())) {
                if (level() == 6) {
                    Toast.makeText(this, R.string.game_complete, Toast.LENGTH_LONG).show();
                    goHome();
                } else {
                    nextLevel();
                }
                return;
            }
        } else {
            playEffect(incorrectAudio);
            lives--;
            if (lives == 0) {
                Toast.makeText(this, R.string.Toast_SemVidas, Toast.LENGTH_LONG).show();
                goHome();
                return;
            }
            Toast.makeText(this, lives == 2 ? R.string.Toast_DuasVidas
                    : R.string.Toast_UmaVida, Toast.LENGTH_SHORT).show();
        }

        question = GameLogic.newQuestion(level(), random);
        render();
    }

    private void playEffect(MediaPlayer player) {
        if (player != null) {
            player.seekTo(0);
            player.start();
        }
    }

    private void nextLevel() {
        Class<?> target;
        switch (level()) {
            case 1: target = Nivel2Activity.class; break;
            case 2: target = Nivel3Activity.class; break;
            case 3: target = Nivel4Activity.class; break;
            case 4: target = Nivel5Activity.class; break;
            default: target = Nivel6Activity.class; break;
        }
        Intent intent = new Intent(this, target);
        intent.putExtra("jogador", playerName);
        intent.putExtra("score", score);
        intent.putExtra("vidas", lives);
        startActivity(intent);
        finish();
    }

    private void goHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (question == null) return;
        state.putString("playerName", playerName);
        state.putInt("score", score);
        state.putInt("lives", lives);
        state.putInt("first", question.first);
        state.putInt("second", question.second);
        state.putChar("operator", question.operator);
    }

    @Override
    protected void onStart() {
        super.onStart();
        backgroundAudio = MediaPlayer.create(this, R.raw.goats);
        correctAudio = MediaPlayer.create(this, R.raw.wonderful);
        incorrectAudio = MediaPlayer.create(this, R.raw.bad);
        if (backgroundAudio != null) {
            backgroundAudio.setLooping(true);
            backgroundAudio.start();
        }
    }

    @Override
    protected void onStop() {
        if (backgroundAudio != null) {
            backgroundAudio.release();
            backgroundAudio = null;
        }
        if (correctAudio != null) {
            correctAudio.release();
            correctAudio = null;
        }
        if (incorrectAudio != null) {
            incorrectAudio.release();
            incorrectAudio = null;
        }
        super.onStop();
    }
}
