package com.example.bouncingball;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

public class Paddle extends GameElement {
    private Point point = new Point();
    private double  position;
    private int x=0,y=0,length=0;
    public int width=0;
    public Paddle(BallView view, int color, int x,
                   int y, int width, int length, float velocityY) {
        super(view, color, BallView.PADDLE_HIT, x, y, width, length,
                0);
        shape = new Rect(x, y, x + width, y + length);
        this.x=x;
        this.y=y;
        this.width=width;
        this.length=length;
    }
    public void move(int position) {
        this.position = position;
        point.x = (int) position;

    }
    @Override
    public void draw(Canvas canvas) {
        shape = new Rect(point.x, y, (point.x + width), y + length);
        canvas.drawRect(shape, paint);
    }

}
