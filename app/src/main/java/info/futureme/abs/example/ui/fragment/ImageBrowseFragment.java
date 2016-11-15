package info.futureme.abs.example.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;

import butterknife.Bind;
import info.futureme.abs.base.ActionBarFragment;
import info.futureme.abs.example.R;
import info.futureme.abs.example.util.FileCacheUtil;
import info.futureme.abs.example.util.Utils;
import info.futureme.abs.example.widget.photoview.PhotoView;
import info.futureme.abs.util.BitmapHelper;
import info.futureme.abs.util.DLog;
import info.futureme.abs.view.overscroll.OverScrollDecoratorHelper;

/**
 * Created by hippo on 12/20/15.
 */
public class ImageBrowseFragment extends ActionBarFragment {
    public static final String IS_REMOTE = "is_remote";
    public static final String PATH = "display_path";
    public static final String DELTED = "deleted";

    @Bind(R.id.photoview_openImageWebJS)
    PhotoView photoView;
    private File file;
    private boolean remote;
    @Bind(R.id.loading_bar)
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Bundle args = getArguments();
        remote = args.getBoolean(IS_REMOTE);
        String path = args.getString(PATH);
        file = new File(path);
        progressBar.setVisibility(View.GONE);
        loadData(path, remote);
        if (Utils.hasIceCreamSandwich()) {
            OverScrollDecoratorHelper.setUpStaticOverScroll(view, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        }
        return view;
    }

    private void loadData(String path, boolean remote){
        if(remote) {
            progressBar.setVisibility(View.VISIBLE);
            FileCacheUtil.getFileFromWebOrLocal(path, new FileCacheUtil.FileLoadListener() {
                @Override
                public void onGotFile(File file) {
                    int degree = BitmapHelper.readPictureDegree(file.getAbsolutePath());
                    Bitmap bitmap = BitmapHelper.safeDecodeScaledBitmapFromFileSystem(file.getAbsolutePath(), photoView.getMeasuredWidth(), photoView.getMeasuredHeight());
                    if(photoView != null && progressBar != null) {
                        // Decode image size
                        if(degree > 0) {
                            DLog.i("browser rotate:", degree + "");
                            bitmap = BitmapHelper.rotateBy(bitmap, degree);
                            DLog.i("rotate:", "success!");
                        }
                        photoView.setImageBitmap(bitmap);
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFail(String error) {
                    if(progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onProgress(int progress) {

                }
            }, getActivity());
        }else{
            Bitmap bitmap = BitmapHelper.safeDecodeScaledBitmapFromFileSystem(new File(path).getAbsolutePath(), 600, 800);
            // Decode image size
            int degree = BitmapHelper.readPictureDegree(path);
            if(degree > 0) {
                DLog.i("browser rotate:", degree + "");
                bitmap = BitmapHelper.rotateBy(bitmap, degree);
                DLog.i("rotate:", "success!");
            }
            photoView.setImageBitmap(bitmap);
        }
    }


    @Override
    public int getActionBarRightResourceId() {
        return R.drawable.trash;
    }

    @Override
    public void onActionBarRightClick() {
        new AlertDialog.Builder(getContext()).setTitle("确认删除？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (file.exists())
                            file.delete();
                        getArguments().putBoolean(DELTED, true);
                        getActivity().getIntent().putExtras(getArguments());
                        getActivity().setResult(Activity.RESULT_OK, getActivity().getIntent());
                        getActivity().finish();
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //点击不删除，不进行操作
            }
        }).show();
    }

    @Override
    public void onActionBarTitleRightClick() {

    }

    @Override
    public void onActionBarTitleLeftClick() {

    }

    @Override
    public int provideContentRes() {
        return R.layout.image_display_fragment;
    }

    @Override
    protected void onFragmentInVisible(Bundle savedInstanceState) {

    }

    @Override
    protected void onFragmentVisible(Bundle savedInstanceState) {

    }
}
