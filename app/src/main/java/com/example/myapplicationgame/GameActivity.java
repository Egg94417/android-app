package com.example.myapplicationgame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    TextView scoreText, timeText;
    ImageView fish;
    RelativeLayout gameRoot;

    int score = 0;
    Random random = new Random();
    Handler handler = new Handler();

    Runnable trashRunnable;
    Runnable fishSpawnRunnable;

    boolean isFishActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        scoreText = findViewById(R.id.scoreText);
        timeText = findViewById(R.id.timeText);
        fish = findViewById(R.id.fish);
        gameRoot = findViewById(R.id.gameRoot);

        fish.setVisibility(ImageView.INVISIBLE);

        fish.setOnClickListener(v -> {
            if (!isFishActive) return;
            score--;
            scoreText.setText("Score: " + score);
            fish.clearAnimation();
            fish.setVisibility(ImageView.INVISIBLE);
            isFishActive = false;
            scheduleFishSpawn();
        });

        trashRunnable = new Runnable() {
            @Override
            public void run() {
                spawnTrashFromEdge();
                handler.postDelayed(this, 1200);
            }
        };
        handler.postDelayed(trashRunnable, 500);

        fishSpawnRunnable = this::spawnFishFromEdge;
        handler.postDelayed(fishSpawnRunnable, 2000);

        new CountDownTimer(30000, 1000) {
            public void onTick(long ms) {
                timeText.setText("Time: " + ms / 1000);
            }

            public void onFinish() {
                handler.removeCallbacks(trashRunnable);
                handler.removeCallbacks(fishSpawnRunnable);
                fish.clearAnimation();
                gameRoot.removeAllViews();

                Intent intent = new Intent(GameActivity.this, ResultActivity.class);
                intent.putExtra("score", score);
                startActivity(intent);
                finish();
            }
        }.start();
    }

    private void scheduleFishSpawn() {
        handler.removeCallbacks(fishSpawnRunnable);
        handler.postDelayed(fishSpawnRunnable, 1000 + random.nextInt(1500));
    }

    private void spawnFishFromEdge() {
        if (isFishActive) return;
        isFishActive = true;

        int startEdge = random.nextInt(4);
        int exitEdge = (startEdge + 2 + random.nextInt(3)) % 4;

        int margin = -dpToPx(80);
        float startX = 0, startY = 0, exitX = 0, exitY = 0;

        switch (startEdge) {
            case 0: startX = random.nextInt(getResources().getDisplayMetrics().widthPixels); startY = margin; break;
            case 1: startX = getResources().getDisplayMetrics().widthPixels + margin; startY = random.nextInt(getResources().getDisplayMetrics().heightPixels); break;
            case 2: startX = random.nextInt(getResources().getDisplayMetrics().widthPixels); startY = getResources().getDisplayMetrics().heightPixels + margin; break;
            case 3: startX = margin; startY = random.nextInt(getResources().getDisplayMetrics().heightPixels); break;
        }

        switch (exitEdge) {
            case 0: exitX = random.nextInt(getResources().getDisplayMetrics().widthPixels); exitY = margin; break;
            case 1: exitX = getResources().getDisplayMetrics().widthPixels + margin; exitY = random.nextInt(getResources().getDisplayMetrics().heightPixels); break;
            case 2: exitX = random.nextInt(getResources().getDisplayMetrics().widthPixels); exitY = getResources().getDisplayMetrics().heightPixels + margin; break;
            case 3: exitX = margin; exitY = random.nextInt(getResources().getDisplayMetrics().heightPixels); break;
        }

        fish.setX(startX);
        fish.setY(startY);
        fish.setVisibility(ImageView.VISIBLE);
        fish.setAlpha(1f);

        float dx = exitX - startX;
        float dy = exitY - startY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float duration = distance * 4;

        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        
        if (dx > 0) {
            fish.setScaleX(-1);
            fish.setRotation(angle);
        } else {
            fish.setScaleX(1);
            fish.setRotation(angle + 180f);
        }

        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("x", startX, exitX);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("y", startY, exitY);
        ObjectAnimator moveAnim = ObjectAnimator.ofPropertyValuesHolder(fish, pvhX, pvhY);
        moveAnim.setDuration((long) duration);
        moveAnim.setInterpolator(new LinearInterpolator());

        moveAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isFishActive) {
                    fish.setVisibility(ImageView.INVISIBLE);
                    isFishActive = false;
                    scheduleFishSpawn();
                }
            }
        });

        moveAnim.start();
    }

    private void spawnTrashFromEdge() {
        int edge = random.nextInt(4);
        int margin = -dpToPx(50);
        float startX = 0, startY = 0;

        switch (edge) {
            case 0: startX = random.nextInt(getResources().getDisplayMetrics().widthPixels); startY = margin; break;
            case 1: startX = getResources().getDisplayMetrics().widthPixels + margin; startY = random.nextInt(getResources().getDisplayMetrics().heightPixels); break;
            case 2: startX = random.nextInt(getResources().getDisplayMetrics().widthPixels); startY = getResources().getDisplayMetrics().heightPixels + margin; break;
            case 3: startX = margin; startY = random.nextInt(getResources().getDisplayMetrics().heightPixels); break;
        }

        float centerX = getResources().getDisplayMetrics().widthPixels / 2f - dpToPx(50);
        float centerY = getResources().getDisplayMetrics().heightPixels / 2f - dpToPx(50);

        ImageView newTrash = new ImageView(this);
        newTrash.setImageResource(R.drawable.rubbish);
        newTrash.setLayoutParams(new RelativeLayout.LayoutParams(dpToPx(100), dpToPx(100)));
        newTrash.setX(startX);
        newTrash.setY(startY);
        gameRoot.addView(newTrash);

        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("x", startX, centerX);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("y", startY, centerY);
        ObjectAnimator trashAnim = ObjectAnimator.ofPropertyValuesHolder(newTrash, pvhX, pvhY);
        trashAnim.setDuration(4000);
        trashAnim.setInterpolator(new LinearInterpolator());
        trashAnim.start();

        newTrash.setOnClickListener(v -> {
            score++;
            scoreText.setText("Score: " + score);
            newTrash.clearAnimation();
            gameRoot.removeView(newTrash);
        });
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
