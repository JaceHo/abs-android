package info.futureme.abs.example.ui.fragment;

/**
 * Created by hippo on 12/3/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;

import butterknife.Bind;
import info.futureme.abs.base.ActionBarFragment;
import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.entity.UpdateResponse;
import info.futureme.abs.example.ABSApplication;
import info.futureme.abs.example.BuildConfig;
import info.futureme.abs.example.R;
import info.futureme.abs.example.biz.AccountManagerImpl;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.example.service.UploadService;
import info.futureme.abs.example.ui.WebActivity;
import info.futureme.abs.example.util.PreferenceManager;
import info.futureme.abs.example.util.Utils;
import info.futureme.abs.example.util.update.CheckUpdateTask;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.FileHelper;
import info.futureme.abs.util.FileSizeHelper;
import info.futureme.abs.util.ViewHelper;
import info.futureme.abs.util.update.UpdateListener;
import info.futureme.abs.view.overscroll.OverScrollDecoratorHelper;
import rx.functions.Action1;

/**
 * 设置
 */
public class AppSettingFragment extends ActionBarFragment implements CheckBox.OnCheckedChangeListener, View.OnClickListener, SettingDialogFragment.DialogClickListener, UpdateListener{
    private static final String UPDATE_TAG = "upgrade";
    @Bind(R.id.relative_upgrade)
    RelativeLayout update;
    @Bind(R.id.relative_clearCache)
    RelativeLayout clearcache;
    @Bind(R.id.clear_cache_size)
    TextView clearcacheTip;
    @Bind(R.id.relative_feedBack)
    RelativeLayout feedback;
    @Bind(R.id.tv_update_mindIcon)
    TextView tv_updateIcon;
    @Bind(R.id.tv_currentVersion)
    TextView tv_currentVersion;
    @Bind(R.id.version_name)
    TextView versionName;
    private UpdateResponse updateResponse;
    public static Uri tempUri;

    private boolean clicked = false;

    public static AppSettingFragment newInstance() {
        AppSettingFragment fragment = new AppSettingFragment();
        return fragment;
    }

    public AppSettingFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ArrayList<View> list = ViewHelper.getAllChildren(view);
        int i = 0;
        for (View v : list) {
            if (v instanceof CheckBox) {
                ((CheckBox) v).setOnCheckedChangeListener(this);
                //related with ui xml
                if(AccountManagerImpl.KEY_UPLOAD_MODE_WIFI_ONLY.equals(v.getTag())) {
                    ((CheckBox) v).setChecked(PreferenceManager.getBoolean("" + v.getTag(), false));
                }else{
                    ((CheckBox) v).setChecked(PreferenceManager.getBoolean("" + v.getTag(), true));
                }
            }
        }
        clearcache.setOnClickListener(this);
        String size;
        String sysCache = getActivity().getCacheDir().getAbsolutePath();
        double sysSize = FileSizeHelper.getFileOrFilesSize(sysCache, FileSizeHelper.SIZETYPE_KB);
        String appCache = ABSApplication.getAppDataDir() + "/cache";
        double appSize = FileSizeHelper.getFileOrFilesSize(appCache, FileSizeHelper.SIZETYPE_KB);
        double total = sysSize;
        if (!sysCache.equals(appCache)) {
            total += appSize;
        }
        if (total > 1024) {
            total = total / 1024;
            size = String.format("%.2f", total) + "MB";
        } else {
            size = String.format("%.2f", total) + "KB";
        }
        clearcacheTip.setText(size);
        update.setOnClickListener(this);
        feedback.setOnClickListener(this);
        tv_updateIcon.setVisibility(View.GONE);
        tv_currentVersion.setText(tv_currentVersion.getText().toString() + "V " + BuildConfig.VERSION_NAME);
        versionName.setText("V " + BuildConfig.VERSION_NAME);
        RxView.longClicks(versionName).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if(versionName.getText().toString().equals("V " + BuildConfig.VERSION_NAME )) {
                    versionName.setText("V " + BuildConfig.VERSION_NAME + "." + (BuildConfig.VERSION_CODE % 100));
                }else {
                    versionName.setText("V " + BuildConfig.VERSION_NAME);
                }
            }
        });
        new CheckUpdateTask(getContext(), MVSConstants.APIConstants.TYPE_NOTIFICATION, false, this).execute();
        if (Utils.hasIceCreamSandwich()) {
            OverScrollDecoratorHelper.setUpStaticOverScroll(view, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        }
        return view;
    }

    @Override
    public int provideContentRes() {
        return R.layout.app_settings;
    }

    @Override
    protected void onFragmentInVisible(Bundle savedInstanceState) {

    }

    @Override
    protected void onFragmentVisible(Bundle savedInstanceState) {
    }

    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        PreferenceManager.putBoolean(AccountManagerImpl.KEY_UPLOAD_MODE_WIFI_ONLY, isChecked);
        DLog.w("upload", "tag:" + AccountManagerImpl.KEY_UPLOAD_MODE_WIFI_ONLY + buttonView.getTag() + "" + isChecked);
        if (AccountManagerImpl.KEY_MSG_NOTIFY.equals(buttonView.getTag())) {
            EMChatOptions options = EMChatManager.getInstance().getChatOptions();
            options.setNotificationEnable(isChecked);
        } else if (AccountManagerImpl.KEY_UPLOAD_MODE_WIFI_ONLY.equals(buttonView.getTag())) {
            UploadService.actionStart(this.getActivity());
        }
    }


    @Override
    public void onClick(View v) {
        clicked = false;
        switch (v.getId()) {
            case R.id.relative_upgrade:
                clicked = true;
                new CheckUpdateTask(getContext(), MVSConstants.APIConstants.TYPE_NOTIFICATION, false, this).execute();
                break;
            case R.id.relative_clearCache:
                SettingDialogFragment cleardialogFragment = SettingDialogFragment.newInstance(getActivity().getResources().getString(R.string.clear_cache), getActivity().getResources().getString(R.string.text_confirmto_clearcache));
                cleardialogFragment.setListener(this);
                cleardialogFragment.show(getFragmentManager(), "clearCache");
                break;
            case R.id.relative_feedBack:
                Intent intent = new Intent(ContextManager.context(), WebActivity.class);
                intent.putExtra(MVSConstants.X5WEBVIEW_INITIAL_URL, MVSConstants.APIConstants.FEED_BACK_URL);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void doPositiveClick(String tag) {
        if ("clearCache".equals(tag)) {
            String size;
            String sysCache = getActivity().getCacheDir().getAbsolutePath();
            double sysSize = FileSizeHelper.getFileOrFilesSize(sysCache, FileSizeHelper.SIZETYPE_KB);
            String appCache = ABSApplication.getAppDataDir() + "/cache";
            double appSize = FileSizeHelper.getFileOrFilesSize(appCache, FileSizeHelper.SIZETYPE_KB);
            double total = sysSize;
            FileHelper.delete(sysCache);
            if (!sysCache.equals(appCache)) {
                total += appSize;
                FileHelper.delete(appCache);
            }
            if (total > 1024) {
                total = total / 1024;
                size = String.format("%.2f", total) + "MB";
            } else {
                size = String.format("%.2f", total) + "KB";
            }
            clearcacheTip.setText(R.string.no_cache);
            Toast.makeText(getContext(), R.string.clear_success, Toast.LENGTH_LONG).show();
        } else if (UPDATE_TAG.equals(tag)) {//更新
            CheckUpdateTask.goToDownload(getContext(), updateResponse);
            Toast.makeText(getActivity(), "开始更新", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void doNegativeClick(String tag) {
    }

    @Override
    public int getActionBarRightResourceId() {
        return 0;
    }

    @Override
    public int getActionBarRight2ResourceId() {
        return 0;
    }

    @Override
    public void onActionBarRight2Click() {

    }

    @Override
    public void onActionBarRightClick() {

    }

    @Override
    public void onActionBarTitleRightClick() {

    }

    @Override
    public void onActionBarTitleLeftClick() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = null;
        if(resultCode == Activity.RESULT_OK && data != null)
            switch (requestCode) {

                case WebActivity.GET_PICTURE_FROMTHUMB_REQUESTCODE:
                    uri = data.getData();
                    break;
            }
    }

    @Override
    public void onUpdateReturned(final UpdateResponse updateResponse) {
        if (update == null || updateResponse == null) return;
        Log.e("LEE", "updateStatus = " + updateResponse + " | hasUpdate = " + updateResponse.getUpdate());
        if (!updateResponse.getUpdate()) {
            if (clicked) {
                Toast.makeText(getContext(), getString(R.string.current_is_newest), Toast.LENGTH_LONG).show();
            }

        } else if (updateResponse.getUpdate()) {
            if (updateResponse.getUpdate()) {
                this.updateResponse = updateResponse;
            }
            tv_updateIcon.setVisibility(View.VISIBLE);
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String versionCode = getActivity().getResources().getString(R.string.detected_new_version) + updateResponse.getAppVersionName();
                    String packageSize = getActivity().getResources().getString(R.string.update_packget_size) + String.format("%.2f", Double.valueOf(updateResponse.getAppSize()) / 1048576) + "MB";
                    SettingDialogFragment updatedialogFragment = SettingDialogFragment.newInstance(versionCode, packageSize);
                    updatedialogFragment.setListener(AppSettingFragment.this);
                    updatedialogFragment.show(getFragmentManager(), UPDATE_TAG);
                }
            });

        }
    }
}

