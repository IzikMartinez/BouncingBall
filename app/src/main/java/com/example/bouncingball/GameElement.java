package com.example.bouncingball;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class GameElement {
   protected BallView view;
   protected Paint paint = new Paint();
   protected Rect shape;
   private float velocityY;
   private int soundId;
   public GameElement(BallView view, int color, int soundId, int x,
                      int y, int width, int length, float velocityY) {
      this.view = view;
      paint.setColor(color);
      shape = new Rect(x, y, x + width, y + length);
      this.soundId = soundId;
      this.velocityY = velocityY;
   }
   public void update(double interval){}
   public void draw(Canvas canvas) {}
}


