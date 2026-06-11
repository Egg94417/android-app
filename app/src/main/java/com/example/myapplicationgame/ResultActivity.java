package com.example.myapplicationgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    TextView resultText;
    Button restartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultText = findViewById(R.id.resultText);
        restartBtn = findViewById(R.id.restartBtn);

        int score = getIntent().getIntExtra("score", 0);
        resultText.setText("你的分數：" + score);

        // 🔄 重新遊玩
        restartBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
