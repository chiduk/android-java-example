package kr.co.toyappfactory.gosung.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by chiduk on 2016. 11. 18..
 */

public class CustomImageView extends ImageView {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Canvas canvas;

    public CustomImageView(Context context) {
        super(context);

    }

    public CustomImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onDraw(Canvas canvas){
        this.canvas = canvas;

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        canvas.drawCircle(event.getX(), event.getY(), 50, paint);

        return false;
    }
}
