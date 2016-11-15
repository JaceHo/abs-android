package info.futureme.abs.example.util.update;

import android.app.Activity;
import android.content.Context;

import info.futureme.abs.base.FBaseActivity;
import info.futureme.abs.entity.UpdateResponse;
import info.futureme.abs.example.R;
import info.futureme.abs.example.ui.fragment.UpdateDialogFragment;

public class UpdateDialog {

    public static void show(final Context context, final UpdateResponse updateResponse) {
        if (isContextValid(context)) {
            UpdateDialogFragment updateDialogFragment = UpdateDialogFragment.newInstance(context.getString(R.string.android_auto_update_dialog_title)+updateResponse.getAppVersionName(),
                    updateResponse.getUpdateLog());
            updateDialogFragment.setListener(new UpdateDialogFragment.DialogClickListener() {
                @Override
                public void doPositiveClick(String tag) {
                    CheckUpdateTask.goToDownload(context, updateResponse);
                }

                @Override
                public void doNegativeClick(String tag) {

                }
            });

            //点击对话框外面,对话框不消失
            updateDialogFragment.setCancelable(false);
            updateDialogFragment.show(FBaseActivity.getActivites().get(0).getSupportFragmentManager(), "update");
        }
    }

    private static boolean isContextValid(Context context) {
        return context instanceof Activity && !((Activity) context).isFinishing();
    }
}
