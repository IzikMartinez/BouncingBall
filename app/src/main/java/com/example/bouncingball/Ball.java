package com.example.bouncingball;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Ball extends GameElement {
    public float velocityY;
    public float velocityX;
    private float radius;
    public boolean sound = false;
    public boolean gameOver = false;
    public Ball(BallView view, int color, int soundId, int x,
                      int y, int radius, float velocityX, float velocityY) {
        super(view, color, soundId, x, y,
                2 * radius, 2 * radius, velocityY);
        this.velocityY = velocityY;
        this.velocityX = velocityX;
       this.radius = (int) (view.getScreenHeight() *BallView.ballRadius);
    }
    private int getRadius() {
        return (shape.right - shape.left) / 2;
    }
    public boolean collidesWith(GameElement element) {
        return (Rect.intersects(shape, element.shape));
    }
    public void reverseVelocityY() {
        velocityX*=-1;
    }
    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(shape.left + getRadius(),
                shape.top + getRadius(), getRadius(), paint);
    }
   @Override
    public void update(double interval) {
        shape.offset((int) (velocityY * interval), (int) (velocityX*interval));
        if ((shape.right < 0 && velocityY < 0 ||
                shape.left > view.getScreenWidth() && velocityY > 0)){
            velocityY *= -1;
            sound =true;
        }
        if ((shape.top < 0 && velocityX < 0)){
            velocityX*=-1;
            sound =true;
        }
        if(shape.bottom >= view.getScreenHeight()){
            gameOver = true;
        }
    }
}