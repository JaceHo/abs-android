package info.futureme.abs.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.leakcanary.RefWatcher;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import info.futureme.abs.FApplication;
import info.futureme.abs.R;
import info.futureme.abs.conf.Theme;
import info.futureme.abs.util.BugFix;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.FPreferenceManager;
import info.futureme.abs.util.ToastHelper;
import info.futureme.abs.util.ViewHelper;
import info.futureme.abs.util.ViewServer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Since RxJava is on top of all async processing in activity lifecycle,
 * FBaseActivity is considered to be an instance of RxAppCompatActivity, <b>
 * then observable could use composed to binding activity lifecycle to itself.
 * framework </b>base activity have activity lifecycle debug mode called in debug
 * build; common function like <i>hidekeyboard</i>,  <i>showShortToast</i>,
 * <i>openActivity </i> activity theme, <b>permission request functionality</b>
 * needed in (android6.0) are also customized
 *
 * @author Jeffrey
 * @version 1.0
 * @updated 16-2月-2016 17:50:42
 */
public abstract class FBaseActivity extends RxAppCompatActivity {

    /**
     * permisson request code used after android6.0 in activity
     */
    public static final int REQUEST_PERMISSION = 0x3e;
    public static final String PRE_RESULT = "pre_activity_result";
    protected static String TAG = "BaseActivity";

    /**
     * processbar used for display loading screen with alpa dim on top of activity screen
     */
    protected BaseDialogFragment processBar;
    /**
     * this array list is used for for retrieve data from parent activity, or finish
     * activities in line
     */
    protected static final List<FBaseActivity> activities = new CopyOnWriteArrayList<>();

    /**
     * on precreate is used for theme setting to styling activities here
     */
    protected void onPreCreate() {
        final Theme currentTheme = FPreferenceManager.getCurrentTheme();
        switch (currentTheme) {
            case Blue:
                this.setTheme(R.style.BlueTheme);
                break;
            case Green:
                this.setTheme(R.style.GreenTheme);
                break;
            case Red:
                this.setTheme(R.style.RedTheme);
                break;
            case Indigo:
                this.setTheme(R.style.IndigoTheme);
                break;
            case BlueGrey:
                this.setTheme(R.style.BlueGreyTheme);
                break;
            case Black:
                this.setTheme(R.style.BlackTheme);
                break;
            case Orange:
                this.setTheme(R.style.OrangeTheme);
                break;
            case Purple:
                this.setTheme(R.style.PurpleTheme);
                break;
            case Pink:
                this.setTheme(R.style.PinkTheme);
                break;
            default:
                this.setTheme(R.style.BlackTheme);
                break;
        }
    }

    /**
     * ***********************【Activity LifeCycle For Debug】
     * **************************
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //keep activity stack trace for later use
        if (!activities.contains(this)) {
            activities.add(this);
        } else {
            activities.remove(this);
            activities.add(this);
        }

        // setting activity theme
        onPreCreate();

        super.onCreate(savedInstanceState);

        for (final FBaseActivity a : activities) {
            DLog.i("activities", a.getClass().getName());
        }
        ;

        TAG = this.getClass().getName();
        DLog.d(TAG, this.getClass().getSimpleName()
                + " onCreate() invoked!!");
        // Set content view, etc.
        if (FApplication.DEBUG)
            ViewServer.get(this).addWindow(this);

    }

    /**
     * hide keyboard whenever needed
     *
     * @param event
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null
                    && getCurrentFocus().getWindowToken() != null) {
                hideKeyboard(this.getWindow().getDecorView().findViewById(android.R.id.content));
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * show shorttoast is a ease use of Toast.makeText(..,..,..).show();
     *
     * @param pResId
     */
    public void showShortToast(int pResId) {
        showShortToast(getString(pResId));
    }

    /**
     * show longtoast is a ease use of Toast.makeText(..,..,..).show();
     *
     * @param pMsg
     */
    protected void showLongToast(String pMsg) {
        Toast.makeText(this, pMsg, Toast.LENGTH_LONG).show();
    }

    /**
     * show short toast message
     *
     * @param pMsg
     */
    public void showShortToast(String pMsg) {
        Toast.makeText(this, pMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * open activity with constructed intent
     *
     * @param pClass
     */
    protected void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    /**
     * open activity with bundle arguments
     *
     * @param pClass
     * @param pBundle
     */
    public void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    /**
     * open activity with action filter
     *
     * @param pAction
     */
    protected void openActivity(String pAction) {
        openActivity(pAction, null);
    }

    /**
     * open activity with action filter and bundle arguments
     *
     * @param pAction
     * @param pBundle
     */
    protected void openActivity(String pAction, Bundle pBundle) {
        Intent intent = new Intent(pAction);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    /**
     * common method to hide keyboard
     *
     * @param view
     */
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * show process loading Dialog
     *
     * @param tip      remind string
     * @param listener dismisslistener
     */
    //tintColor: true 表示 false 表示
    public void processPreview(final String tip, final PopupWindow.OnDismissListener listener) {
        processPreview(tip, listener, false);
    }

    /**
     * show a customized default blocking task preview dialog whenever needed on frontend
     *
     * @param tip
     * @param listener
     * @param isLoginActivity
     */
    public void processPreview(final String tip, final PopupWindow.OnDismissListener listener, final boolean isLoginActivity) {
        processDismiss();
        Bundle bundle = new Bundle();
        //default animation style
        bundle.putInt(BaseDialogFragment.GRAVITY, Gravity.CENTER);
        bundle.putInt(BaseDialogFragment.LAYOUT, R.layout.process_loading);
        //bundle.putInt(BaseDialogFragment.LAYOUT_HEIGHT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //bundle.putInt(BaseDialogFragment.LAYOUT_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        bundle.putInt(BaseDialogFragment.LAYOUT_HEIGHT, ViewGroup.LayoutParams.MATCH_PARENT);
        bundle.putInt(BaseDialogFragment.LAYOUT_WIDTH, ViewGroup.LayoutParams.MATCH_PARENT);
        //bundle.putInt(BaseDialogFragment.WINDOW_ANIM, R.style.AnimFade);
        bundle.putInt(BaseDialogFragment.WINDOW_ANIM, -1);
        bundle.putBoolean(BaseDialogFragment.IS_NORMAL, false);
        if (isLoginActivity) {
            bundle.putBoolean(BaseDialogFragment.NEED_TRANSPARENT, true);
        } else {
            bundle.putBoolean(BaseDialogFragment.NEED_TRANSPARENT, false);
        }
        bundle.putBoolean(BaseDialogFragment.DISMISSABLE, false);
        //customized processbar
        processBar = BaseDialogFragment.newInstance(bundle, new Runnable() {
            @Override
            public void run() {
                try {
                    if (getWindow() != null && getWindow().isActive() && !FBaseActivity.this.isFinishing()) {
                        if (tip == null) {
                            /*(processBar.getDialog()
                                    .findViewById(R.id.process_text)).setVisibility(View.GONE);*/
                            ((TextView) (processBar.getDialog()
                                    .findViewById(R.id.process_text))).setText("");
                        } else {
                            ((TextView) (processBar.getDialog()
                                    .findViewById(R.id.process_text))).setText(tip);
                        }
                        //processBar.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        //processBar.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                        RelativeLayout layout = (RelativeLayout) processBar.getDialog().findViewById(R.id.loading_layout_need_bg);
                        View centerView = processBar.getDialog().findViewById(R.id.loading_view);
                        ViewHelper.ensureTop(centerView);
                        //RelativeLayout layout_whole = (RelativeLayout) processBar.getDialog().findViewById(R.id.layout_outside_whole_loading);
                        if (isLoginActivity) {//登陆界面, 需要background
                            layout.setBackgroundColor(Color.parseColor("#00000000"));
                        } else {//非登陆界面，无需background
                            layout.setBackgroundResource(R.drawable.loading_bg_shape);
                        }
                        if (listener != null) {
                            processBar.getDialog().setCancelable(true);
                            processBar.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    listener.onDismiss();
                                    Toast.makeText(getApplicationContext(), "任务已经取消", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            processBar.getDialog().setCanceledOnTouchOutside(false);
                        }
                    }
                } catch (Exception e) {
                }
            }
        });

        //actually show the dialog
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (processBar != null) {
                    try {
                        processBar.show(getSupportFragmentManager(), "process");
                    } catch (Exception e) {
                        DLog.p(e);
                    }
                }
            }
        });
    }

    /**
     * async dismiss loading dialog
     */
    public synchronized void processDismiss() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0) != null)
                    ((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //clear animation
                                if (processBar != null) {
                                    processBar.dismiss();
                                    processBar = null;
                                }
                            } catch (Exception e) {
                            }
                        }
                    });
            }
        });
    }

    /**
     * async dismiss loading dialog
     */
    public synchronized void processDismiss(final boolean success, final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0) != null)
                    ((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //clear animation
                                if (processBar != null && processBar.getDialog().isShowing()) {
                                    processBar.dismiss();
                                    processBar = null;
                                }
                            } catch (Exception e) {
                            }
                            String tips = tip;
                            if (tip == null || "".equals(tip.trim())) {
                                if (success) {
                                    tips = getString(R.string.successes);
                                } else {
                                    tips = getString(R.string.failed);
                                }
                            }
                            if (success) {
                                ToastHelper.makeText(getApplicationContext(), tips, 600).show();
                            } else {
                                ToastHelper.makeText(getApplicationContext(), tips, 2000).show();
                            }
                        }
                    });
            }
        });
    }


    @Override
    public void startActivity(Intent intent) {
        //default behavior
        hideKeyboard(findViewById(android.R.id.content));

        super.startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        //default behavior
        hideKeyboard(findViewById(android.R.id.content));

        super.startActivity(intent, options);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        //default behavior
        hideKeyboard(findViewById(android.R.id.content));

        super.startActivityForResult(intent, requestCode, options);
    }

    /**
     * ***************************** 【Activity LifeCycle For Debug】 ******************************************
     */

    @Override
    protected void onStart() {
        DLog.d(TAG, this.getClass().getSimpleName() + " onStart() invoked!!");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        DLog.d(TAG, this.getClass().getSimpleName()
                + " onRestart() invoked!!");
        super.onRestart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DLog.i("onresult intent", "res:" + resultCode + " req:" + requestCode + " " + (data == null ? "null" : data.getExtras() == null ? "null" : data.getExtras().toString()));
    }

    @Override
    protected void onResume() {
        DLog.d(TAG, this.getClass().getSimpleName()
                + " onResume() invoked!!");
        super.onResume();
        DLog.i("onresume intent", getIntent() == null ? "null" : getIntent().getExtras() == null ? "null" : getIntent().getExtras().toString());
        if (getIntent() != null && 0x3f != getIntent().getIntExtra(PRE_RESULT, 0x3f)) {
            int res = getIntent().getIntExtra(PRE_RESULT, Activity.RESULT_CANCELED);
            setResult(res);
        }
        if (!FApplication.DEBUG)
            MobclickAgent.onResume(this);
        if (FApplication.DEBUG)
            ViewServer.get(this).setFocusedWindow(this);
    }

    @Override
    protected void onPause() {
        /**
         * You should usually use the onPause() callback to: Stop animations or other ongoing actions that could consume CPU.

         Source : http://developer.android.com/training/basics/activity-lifecycle/pausing.html#Pause
         */
        //when calling onPause , this activity must be removed from activities list at this time
        if (processBar != null) {
            processDismiss();
            processBar = null;
        }
        DLog.d(TAG, this.getClass().getSimpleName() + " onPause() invoked!!");
        super.onPause();
        if (!FApplication.DEBUG)
            MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        DLog.d(TAG, this.getClass().getSimpleName() + " onStop() invoked!!");
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        activities.remove(this);

        DLog.d(TAG, this.getClass().getSimpleName() + " onDestroy() invoked!!");

        super.onDestroy();
        for (Activity a : activities) {
            DLog.i("activities", a.getClass().getName());
        }

        BugFix.destoryCallback(
                ((ViewGroup) getWindow().getDecorView().findViewById(
                        android.R.id.content)).getChildAt(0)
        );

        RefWatcher refWatcher = FApplication.getRefWatcher();
        refWatcher.watch(this);

        if (FApplication.DEBUG)
            ViewServer.get(this).removeWindow(this);

    }

    /**
     * recreate all activities
     */
    public void recreateAll() {
        for (final Activity a : activities) {
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    a.recreate();
                }
            });
        }
    }


    /**
     * finish with transaction
     */
    @Override
    public void finish() {
        super.finish();
    }


    /**
     * finishall activities in line
     */
    public static void finishAll() {
        synchronized (activities) {
            for (final Activity a : activities) {
                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activities.remove(a);
                        try {
                            DLog.i("activity", a.getClass().getName());
                            a.finish();
                        } catch (Exception e) {
                        }
                    }
                });
            }
        }
    }

    @Override
    public void recreate() {
        super.recreate();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * finish without transacion
     */
    public void defaultFinish() {
        processDismiss();
        super.finish();
    }


    /**
     * As mentioned above, because your app may be restarted during the permission request, the request must be done during an initialization phase. This may be Activity.onCreate/onResume, or View.onFinishInflate or others
     * request permission after android 6.0 in runtime
     * <p/>
     * https://github.com/tbruyelle/RxPermissions
     *
     * @param permission
     * @param ok
     * @param deny
     */
    public void requestPermission(final Runnable ok, final Runnable deny, final String... permission) {
        RxPermissions.getInstance(this)
                .request(permission)
                .compose(this.<Boolean>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (selfPermissionGranted(permission)) {
                            if (aBoolean && ok != null) {
                                ok.run();
                            }
                        } else if (deny != null) {
                            deny.run();
                        }
                    }
                });
    }

    /**
     * http://my.oschina.net/u/990728/blog/549914
     * pessimistic strategy
     *
     * @param permission
     * @return
     */
    public boolean selfPermissionGranted(String... permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result;

//        try {
//            final PackageInfo info = getPackageManager().getPackageInfo(
//                    getPackageName(), 0);
//            int targetSdkVersion = info.applicationInfo.targetSdkVersion;
        for (String p : permission) {
            // targetSdkVersion < Android M, we have to use PermissionChecker
            result = PermissionChecker.checkSelfPermission(this, p)
                    == PermissionChecker.PERMISSION_GRANTED;
//                    if (targetSdkVersion >= Build.VERSION_CODES.M) {
//                        // targetSdkVersion >= Android M, we can
//                        // use Context#checkSelfPermission

            if (result)
                result = ContextCompat.checkSelfPermission(this, p) == PackageManager.PERMISSION_GRANTED;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && result)
                result = checkSelfPermission(p)
                        == PackageManager.PERMISSION_GRANTED;

//                    } else {
//                        // targetSdkVersion < Android M, we have to use PermissionChecker
//                        result = PermissionChecker.checkSelfPermission(this, p)
//                                == PermissionChecker.PERMISSION_GRANTED;
//                    }
            if (!result) return false;
        }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
        return true;
    }

    public static synchronized List<FBaseActivity> getActivites() {
        return activities;
    }

    static {
        System.loadLibrary("apk_patch");
    }
}
