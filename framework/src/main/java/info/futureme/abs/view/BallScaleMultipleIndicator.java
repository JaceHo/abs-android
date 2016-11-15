package info.futureme.abs.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.jakewharton.rxbinding.view.RxView;
import com.jcodecraeer.xrecyclerview.progressindicator.indicator.BaseIndicatorController;

import java.util.ArrayList;
import java.util.List;

import info.futureme.abs.R;
import rx.functions.Action1;

/**
 * Created by Jack on 2015/10/19.
 */

/**
 * Created by Jack on 2015/10/19.
 */
public class BallScaleMultipleIndicator extends BaseIndicatorController {

    float[] scaleFloats=new float[]{1.0f,1.0f,1.0f, 1.0f};
    float[] alphaInts=new float[]{155, 155, 155, 155};
    PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofFloat(View.ALPHA.getName(), 155.0f, 0f);
    PropertyValuesHolder scaleHolder = PropertyValuesHolder.ofFloat(View.ALPHA.getName(), 1.0f, 2.4f);

    @Override
    public void draw(Canvas canvas, Paint paint) {
        //draw filling circle
        int c = paint.getColor();
        paint.setColor(getTarget().getContext().getResources().getColor(R.color.indicator_primary));
        for (int i = 0; i < 4; i++) {
            paint.setAlpha((int) alphaInts[i]);
            canvas.drawCircle(getWidth()/2, getHeight()/2,
                    scaleFloats[i]*getWidth()/2,paint);
        }
        paint.setAlpha(255);
        paint.setColor(c);
        canvas.scale(1.0f, 1.0f, getWidth()/2, getHeight()/2);
        canvas.drawCircle(getWidth()/2,getHeight()/2,getWidth()/2,paint);
    }

    @Override
    public List<Animator> createAnimation() {
        final List<Animator> animators = new ArrayList<>();
        long[] delays=new long[]{0, 400, 800, 1200};
        for (int i = 0; i < 4; i++) {
            final int index = i;
            final ValueAnimator valueAnimator =  ObjectAnimator.ofPropertyValuesHolder(alphaHolder, scaleHolder);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.setDuration(1200);
            valueAnimator.setStartDelay(delays[i]);
            valueAnimator.setRepeatCount(-1);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    scaleFloats[index] = 1.0f + 1.4f*animation.getAnimatedFraction();
                    alphaInts[index] = 155f*(1.0f-animation.getAnimatedFraction());
                    postInvalidate();
                }
            });
            valueAnimator.start();

            animators.add(valueAnimator);
        }
        RxView.detaches(getTarget())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        for(Animator animator : animators)
                            animator.cancel();
                    }
                });
        return animators;
    }
}
