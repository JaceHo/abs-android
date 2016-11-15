package info.futureme.abs.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import info.futureme.abs.base.FBaseActivity;
import info.futureme.abs.example.R;
import info.futureme.abs.example.biz.AccountManagerImpl;


/**
 * Created by Jeffrey on 6/20/16.
 * https://www.bignerdranch.com/blog/splash-screens-the-right-way/
 */
public class SplashActivity extends FBaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);

        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        if (imageView != null) {
            Glide.with(this).load(R.drawable.splash).skipMemoryCache(true).fitCenter().crossFade().into(imageView);
        }
        if(AccountManagerImpl.instance.isLogin()){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            new Handler(Looper.getMainLooper())
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 2000);
        }
    }
}

