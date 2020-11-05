package com.example.bouncingball;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


public class BallView extends SurfaceView implements SurfaceHolder.Callback {

    public static double ballRadius = 0.0480;
    public  double ballSpeed = 0.15;
    public  double paddleWidth = 0.35;
    public static double paddleLength = 0.035;
    public static double paddleSize = 0.25;
    public static double paddleSpeed = 1.0;
    public static double paddlePosition = 1.0;
    private BallThread ballThread;
    private Ball ball;
    private Paddle paddle;
    private int screenWidth;
    private int screenHeight;
    private double timeIncrements;
    public static final int WALL_HIT = 0;
    public static final int PADDLE_HIT = 1;
    public static final int GAMEOVER_HIT = 2;
    private SoundPool soundPool;
    private SparseIntArray soundMap;
    private Paint backgroundPaint;
    // constructor
    public BallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setUsage(AudioAttributes.USAGE_GAME);
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(1);
        builder.setAudioAttributes(attrBuilder.build());
        soundPool = builder.build();
        soundMap = new SparseIntArray(3); // create new SparseIntArray
        soundMap.put(WALL_HIT,
                soundPool.load(context, R.raw.wall_hit, 1));
        soundMap.put(PADDLE_HIT,
                soundPool.load(context, R.raw.paddle_hit, 1));
        soundMap.put(GAMEOVER_HIT,
                soundPool.load(context, R.raw.gameover_hit, 1));
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
    }
    public void playSound(int soundId) {
        soundPool.play(soundMap.get(soundId), 1, 1, 1, 0, 1f);
    }

    public BallView(Context context) {
        super(context);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
    }
    public int getScreenWidth() {
        return screenWidth;
    }
    public int getScreenHeight() {
        return screenHeight;
    }
    public void newGame() {
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            paddle = new Paddle(this, Color.BLACK,
                    (int) (paddleSize * screenWidth),
                    (int) (paddlePosition*screenHeight),
                    (int) (paddleWidth * screenWidth),
                    (int) (paddleLength * screenHeight),
                    (float) (paddleSpeed * screenHeight));

            ball = new Ball(this, Color.BLACK,
                    (int) (paddleSize * screenWidth),
                    (int) (.8 * screenHeight),
                    (int) (0.01 * screenWidth),
                    (int) (this.getScreenHeight() * ballRadius),
                    (float) (ballSpeed *
                            this.getScreenWidth()),
                    (float) (ballSpeed *
                            this.getScreenHeight()));

        } else {
            paddle = new Paddle(this, Color.BLACK,
                    (int) (paddleSize * screenWidth),
                    (int) (paddlePosition*screenHeight),
                    (int) (paddleWidth * screenHeight),
                    (int) (paddleLength * screenWidth),
                    (float) (paddleSpeed * screenHeight));

            ball = new Ball(this, Color.BLACK,
                    (int) (.25 * screenWidth),
                    (int) (.8 * screenHeight),
                    (int) (0.01 * screenWidth),
                    (int) (this.getScreenWidth() * ballRadius),
                    (float) (ballSpeed *
                            this.getScreenWidth()),
                    (float) (ballSpeed *
                            this.getScreenHeight()));

        }
        timeIncrements = 0;
        hideSystemBars();
    }
    private void hideSystemBars() {
        setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
    private void challenge(){
        ballSpeed += 0.10;
        ball.velocityX = (float) (ballSpeed*getScreenWidth());
        ball.velocityY = (float) (ballSpeed*getScreenHeight());
        paddleWidth -= 0.03;
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            paddle.width = (int) (paddleWidth*screenHeight);
        } else {
            paddle.width = (int) (paddleWidth*screenWidth);
        }
    }

    private void updatePositions(double elapsedTimeMS) {
        double interval = elapsedTimeMS / 1000.0;
        ball.update(interval);
        timeIncrements += interval;
        if(ball.sound){
            playSound(WALL_HIT);
            ball.sound=false;
        }
    }
    public void moveBar(MotionEvent event) {
        Point touchPoint = new Point((int) event.getX(),
                (int) event.getY());
        paddle.move((int) (touchPoint.x-(paddleSize*screenWidth)/2));
    }
    public void drawGameElements(Canvas canvas) {
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
        ball.draw(canvas);
        paddle.draw(canvas);
    }
    public void testForCollisions() {
        if (ball.collidesWith(paddle)) {
           playSound(PADDLE_HIT);
            ball.reverseVelocityY();
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height) { }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        boolean dialogIsDisplayed = false;
        if (!dialogIsDisplayed) {
            newGame();
            ballThread = new BallThread(holder);
            ballThread.setRunning(true);
            ballThread.start();
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        ballThread.setRunning(false);
        while (retry) {
            try {
                ballThread.join();
                retry = false;
            }
            catch (InterruptedException ignored) {
            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getAction();
        if (action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_MOVE) {
            moveBar(e);
        }
        return true;
    }
    public void stopGame() {

            playSound(GAMEOVER_HIT);
            ballThread.setRunning(false);
    }
    public void releaseResources() {
        soundPool.release();
        soundPool = null;
    }
    private class BallThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private boolean threadIsRunning = true;
        public BallThread(SurfaceHolder holder) {
            surfaceHolder = holder;
            setName("BallThread");
        }
        public void setRunning(boolean running) {
            threadIsRunning = running;
        }
        @Override
        public void run() {
            Canvas canvas = null; // used for drawing
            long previousFrameTime = System.currentTimeMillis();
            while (threadIsRunning) {
                try {
                    canvas = surfaceHolder.lockCanvas(null);
                    synchronized(surfaceHolder) {
                        long currentTime = System.currentTimeMillis();
                        double elapsedTimeMS = currentTime - previousFrameTime;
                        timeIncrements += elapsedTimeMS / 1000.0;
                        updatePositions(elapsedTimeMS);
                        testForCollisions();
                        drawGameElements(canvas);
                        previousFrameTime = currentTime;
                        if (ball.gameOver){
                            stopGame();
                        }
                        if(timeIncrements>=25){
                            challenge();
                            drawGameElements(canvas);
                            timeIncrements=0;
                        }

                    }
                }
                finally {
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
