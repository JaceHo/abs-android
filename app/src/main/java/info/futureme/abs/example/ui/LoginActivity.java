package info.futureme.abs.example.ui;

import android.Manifest;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.igexin.sdk.PushManager;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import info.futureme.abs.base.ActionBarFragmentActivity;
import info.futureme.abs.base.FBaseActivity;
import info.futureme.abs.base.InjectableActivity;
import info.futureme.abs.entity.AccessToken;
import info.futureme.abs.entity.Result;
import info.futureme.abs.example.R;
import info.futureme.abs.example.biz.AccountManagerImpl;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.example.entity.TokenRequest;
import info.futureme.abs.example.rest.AuthAPI;
import info.futureme.abs.example.rest.ServiceGenerator;
import info.futureme.abs.example.util.CommonUtil;
import info.futureme.abs.example.util.Utils;
import info.futureme.abs.rest.NetworkObserver;
import info.futureme.abs.util.AppHelper;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.FPreferenceManager;
import info.futureme.abs.util.NetworkUtil;
import info.futureme.abs.util.TextHelper;
import info.futureme.abs.util.WindowUtils;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends InjectableActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    boolean isOpened = false;

    InputMethodManager imm;
    public static final String UPDATE_TOKEN_TIME_LONG_AGO = "long_ago";
    private static final long UPDATE_DELAY_MILLIS = 2 * 1000;
    public static final String IS_FROM_LOGIN = "is_from_login";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // UI references.
    @Bind(R.id.account)
    EditText mAccountView;
    @Bind(R.id.password)
    EditText mPasswordView;
    @Bind(R.id.email_login_form)
    LinearLayout form;
    @Bind(R.id.root_layout)
    RelativeLayout mMainLayout;
    @Bind(R.id.login_logoimg)
    ImageView mLogo_img;
    @Bind(R.id.account_sign_in_button)
    Button mLogin_btn;
    @Bind(R.id.forget_password)
    TextView scrollToView;
    String account, password;
    private AuthAPI accountAPI;
    private Animation an;
    View focusView = null;
    /*
     * network request subscription
     */
    private Subscription _subscription;
    private Scheduler.Worker worker;
    private boolean request = false;

    public LoginActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //in case of new task launching activities
        FBaseActivity.finishAll();
        super.onCreate(savedInstanceState);
        worker = AndroidSchedulers.mainThread().createWorker();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        accountAPI = ServiceGenerator.createService(AuthAPI.class);

        // Set up the login form.
        boolean expire = getIntent().getBooleanExtra(LoginActivity.UPDATE_TOKEN_TIME_LONG_AGO, false);
        if (expire) {
            Utils.debounceToast(getString(R.string.expire_longago));
        }
        mAccountView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        mPasswordView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        populateAutoComplete();
        mMainLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @OnTextChanged(R.id.account)
    void onAccountChange(CharSequence text, int v1, int v2, int v3) {
        /*
        if (!TextHelper.isEmptyOrSpaces(password) && !TextHelper.isEmptyOrSpaces(text.toString())) {
            mLogin_btn.setEnabled(true);
        }else {
            mLogin_btn.setEnabled(false);
        }
        */
        account = text.toString();
    }

    @OnTextChanged(R.id.password)
    void onPasswordChange(CharSequence text, int v1, int v2, int v3) {
        /*
        if (!TextHelper.isEmptyOrSpaces(account) && !TextHelper.isEmptyOrSpaces(text.toString())) {
            mLogin_btn.setEnabled(true);
        }else {
            mLogin_btn.setEnabled(false);
        }
        */
        if (text != null)
            password = text.toString();
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_login;
    }

    @OnClick(R.id.account_sign_in_button)
    public void onSignin(View view) {
        attemptLogin();
    }

    @OnClick(R.id.account_sign_up_button)
    public void onSignUp(View view) {
        hideKeyboard(view);
        Bundle bundle = new Bundle();
//        bundle.putString(ActionBarFragmentActivity.FRAGMENT_CLASS_NAME, PhoneVerifyFragment.class.getName());
        bundle.putInt(ActionBarFragmentActivity.FRAGMENT_TITLE, R.string.getpassword);
        openActivity(ActionBarFragmentActivity.class, bundle);
    }

    @OnFocusChange(R.id.account)
    void onAccountFocues(View view, boolean focus) {
        DLog.i("focus", "account" + focus);
        if (focus)
            focusView = mAccountView;
    }

    @OnFocusChange(R.id.password)
    void onPasswordFocues(View view, boolean focues) {
        DLog.i("focus", "password" + focues);
        if (focues)
            focusView = mPasswordView;
    }

    @OnEditorAction(R.id.password)
    public boolean onEditorActionPassword(TextView textView, int id, KeyEvent keyEvent) {
        focusView = mPasswordView;
        if (id == EditorInfo.IME_NULL
                && keyEvent.getAction() == KeyEvent.ACTION_DOWN
                || id == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard(mMainLayout);
            //match this behavior to your 'Send' (or Confirm) button
            mLogin_btn.performClick();
        }
        return true;
    }

    @OnEditorAction(R.id.account)
    public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        focusView = mAccountView;
        if (id == R.id.login || id == EditorInfo.IME_NULL) {
            attemptLogin();
            return true;
        }
        return false;
    }

    @OnClick(R.id.forget_password)
    public void onForgetPassword(View view) {
        hideKeyboard(view);
        Bundle bundle = new Bundle();
//        bundle.putString(ActionBarFragmentActivity.FRAGMENT_CLASS_NAME, ThreeStepsFragment.class.getName());
        bundle.putInt(ActionBarFragmentActivity.FRAGMENT_TITLE, R.string.getpassword);
        ArrayList<String> titles = new ArrayList<>();
        titles.add(getString(R.string.verify));
        titles.add(getString(R.string.change_password_title));
        titles.add(getString(R.string.done));
//        bundle.putStringArrayList(ThreeStepsFragment.STEP_FRAGMENT_TITLES, titles);
        titles = new ArrayList<>();
//        titles.add(PhoneVerifyFragment.class.getName());
//        titles.add(ChangePassFragment.class.getName());
//        titles.add(ChangeSuccessFragment.class.getName());
//        bundle.putStringArrayList(ThreeStepsFragment.STEP_FRAGMENT_NAMES, titles);
        openActivity(ActionBarFragmentActivity.class, bundle);
    }

    private void populateAutoComplete() {
        String account = FPreferenceManager.getString(MVSConstants.ACCOUNT_SIGNED, "");
        if (!TextHelper.isEmptyOrSpaces(account)) {
            mAccountView.setText(account);
            if (!TextHelper.isEmptyOrSpaces(password))
                mLogin_btn.setEnabled(true);
        } else {
            //mLogin_btn.setEnabled(false);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mAccountView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String account = mAccountView.getText().toString();
        final String password = mPasswordView.getText().toString();
        boolean cancel = false;

        // Check for a valid password, if the user entered one.

        // Check for a valid account.
        if (TextUtils.isEmpty(account)) {
            Utils.debounceToast(getString(R.string.error_field_required));
            focusView = mAccountView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            Utils.debounceToast(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            Utils.debounceToast(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else {
            if (!TextHelper.isEmail(account) && !TextHelper.isPhoneNum(account) && !TextHelper.isAccount(account)) {
                Utils.debounceToast(getString(R.string.error_invalid_account));
                focusView = mAccountView;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            final String[] uid = new String[1];

            requestPermission(new Runnable() {
                @Override
                public void run() {
                    uid[0] = AppHelper.uid();
                    login(account, password, uid[0]);
                }
            }, new Runnable() {
                @Override
                public void run() {
                    showShortToast(R.string.login_need_identity_your_phone_state);
                }
            }, Manifest.permission.READ_PHONE_STATE);
        }
    }


    private void login(final String account, final String password, final String uid) {
        String cid = FPreferenceManager.getString(MVSConstants.KEY_GETUI_CID, "");
        if (!NetworkUtil.isNetworkAvailable(this)) {
            showLongToast(getString(R.string.network_isnot_available));
            return;
        }
        if ("".equals(cid)) {
            PushManager.getInstance().initialize(getApplicationContext());
            showShortToast(getString(R.string.initialzing));
            return;
        }
        String appversion = AppHelper.versionName();
        String osversion = Build.VERSION.RELEASE;
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        processPreview(null, null, true);
        final TokenRequest req = new TokenRequest();
        req.setLoginname(account);
        req.setPassword(CommonUtil.getDoubleBase64Md5(password));
        req.setAppversion(appversion);
        req.setDeviceid(uid);
        req.setOsversion(osversion);
        req.setCid(cid);
        FPreferenceManager.putString(MVSConstants.ACCOUNT_SIGNED, account);
        if (_subscription == null || _subscription.isUnsubscribed()) {
            request = true;
            hideKeyboard(mMainLayout);
            _subscription = accountAPI.getAccessToken(req)
                    // Started in onStart(), running until in onStop()
                    .compose(this.<Result<AccessToken>>bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .doOnTerminate(new Action0() {
                        @Override
                        public void call() {
                            _subscription = null;
                        }
                    })
                    //丢掉点击后2s内的请求
                    .throttleFirst(1, TimeUnit.SECONDS)
                    //3s内只接受一次请求操作
                    .debounce(UPDATE_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribe(new NetworkObserver<Result<AccessToken>>() {
                        @Override
                        public void onFailure(String tip) {
                            request = false;
                            processDismiss();
                            Utils.debounceToast(tip);
                            if (focusView != null)
                                focusView.requestFocus();
                        }

                        @Override
                        public void onSuccess(final Result<AccessToken> accessToken) {
                            request = false;
                            if (accessToken.getEcode() == 0) {
                                //no slide back here
                                AccountManagerImpl.instance.setToken(accessToken.getResult());
                                FPreferenceManager.putString(MVSConstants.ACCOUNT_PASSWORD, req.getPassword());
                                FPreferenceManager.putString(MVSConstants.ACCOUNT_USERNAME, account);
                                FPreferenceManager.putString(MVSConstants.ENGINEER_ID, "" + accessToken.getResult().getEngineerid());
                                //如果app是第一次登录, 初始化appconfig
                                if (!FPreferenceManager.getBoolean(MVSConstants.LOGIN_NOT_FIRST_TIME, false)) {
                                    FPreferenceManager.putBoolean(MVSConstants.LOGIN_NOT_FIRST_TIME, true);
                                }
                                getIntent().putExtra(IS_FROM_LOGIN, true);
                                openActivity(MainActivity.class, getIntent().getExtras());
                                finish();
                            } else {
                                if (accessToken.getReason() != null && !TextUtils.isEmpty(accessToken.getReason()))
                                    onFailure(accessToken.getReason());
                                else
                                    onFailure(getString(R.string.unknown_error));
                            }
                        }
                    });
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        if (worker != null) {
            worker.unsubscribe();
            worker = null;
        }
        if(an != null){
            an.cancel();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mMainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }else{
            mMainLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        request = false;
        mMainLayout.requestLayout();
        requestPermission(null, null, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (an != null) {
                an.cancel();
            }
            isOpened = imm.isActive();//isOpen若返回true，则表示输入法打开
            if (isOpened) {
                hideKeyboard(findViewById(android.R.id.content));
            } else {
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onGlobalLayout() {
        if(worker == null) return;
        Rect rect = new Rect();
        // 获取root在窗体的可视区域
        mMainLayout.getWindowVisibleDisplayFrame(rect);
        // 获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
        int rootInvisibleHeight = mMainLayout.getRootView()
                .getHeight() - rect.bottom;
        //isOpen若返回true，则表示输入法打开
        if (rootInvisibleHeight > 150) {
            // 若不可视区域高度大于150，则键盘显示
            int[] location = new int[2];
            scrollToView.getLocationInWindow(location);

            if (!isOpened) {
                if (worker.isUnsubscribed())
                    worker = AndroidSchedulers.mainThread().createWorker();
                int navi = WindowUtils.getNavigationBarHeight(LoginActivity.this);
                final int[] scrollY = {(location[1] + scrollToView.getHeight()) - rect.bottom - navi};
                final int orig = scrollY[0];
                final int delta = scrollY[0] / 20;
                worker.schedulePeriodically(new Action0() {
                    @Override
                    public void call() {
                        scrollY[0] -= delta;
                        if (scrollY[0] >= 0) {
                            mMainLayout.scrollTo(0, orig - scrollY[0]);
                        } else {
                            mMainLayout.scrollTo(0, orig);
                            worker.unsubscribe();
                        }
                    }
                }, 0, 5, TimeUnit.MILLISECONDS);
                an = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_fadeout);
                an.setAnimationListener(new Animation.AnimationListener() {

                    public void onAnimationStart(Animation animation) {
                        // TODO Auto-generated method stub

                    }

                    public void onAnimationEnd(Animation animation) {
                        mLogo_img.setVisibility(View.INVISIBLE);
                    }

                    public void onAnimationRepeat(Animation animation) {
                        // TODO Auto-generated method stub
                    }
                });
                mLogo_img.setAnimation(an);
                mLogo_img.startAnimation(an);
            }
            isOpened = true;
        } else if (isOpened) {
            isOpened = false;
            // 键盘隐藏
            an = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_fadein);
            if (request) {
                mMainLayout.scrollTo(0, 0);
                mLogo_img.setVisibility(View.VISIBLE);
            } else {
                if (worker.isUnsubscribed())
                    worker = AndroidSchedulers.mainThread().createWorker();
                final int[] scrollY = {mMainLayout.getScrollY()};
                final int delta = scrollY[0] / 20;
                worker.schedulePeriodically(new Action0() {
                    @Override
                    public void call() {
                        scrollY[0] -= delta;
                        if (scrollY[0] >= 0) {
                            mMainLayout.scrollTo(0, scrollY[0]);
                        } else {
                            mMainLayout.scrollTo(0, 0);
                            worker.unsubscribe();
                        }
                    }
                }, 0, 5, TimeUnit.MILLISECONDS);
                an.setAnimationListener(new Animation.AnimationListener() {

                    public void onAnimationStart(Animation animation) {
                        // TODO Auto-generated method stub
                    }

                    public void onAnimationEnd(Animation animation) {
                        // TODO Auto-generated method stub
                        if (mLogo_img != null)
                            mLogo_img.setVisibility(View.VISIBLE);
                        if (focusView != null)
                            focusView.requestFocus();
                    }

                    public void onAnimationRepeat(Animation animation) {
                        // TODO Auto-generated method stub
                    }
                });
                mLogo_img.setAnimation(an);
                mLogo_img.startAnimation(an);
            }
            //没有键盘
        } else {
            mMainLayout.scrollTo(0, 0);
            mLogo_img.setVisibility(View.VISIBLE);
        }
    }
}

