package info.futureme.abs.example.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import info.futureme.abs.example.R;

public class SettingDialogFragment extends DialogFragment {

    public void setListener(DialogClickListener mListener) {
        this.mListener = mListener;
    }

    public static interface DialogClickListener{
        void doPositiveClick(String tag);
        void doNegativeClick(String tag);
    }

    private  DialogClickListener mListener;
    public static SettingDialogFragment newInstance(String title,String message){
        SettingDialogFragment sdf = new SettingDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("alert-title", title);
        bundle.putString("alert-message", message);
        sdf.setArguments(bundle);

        return sdf;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(),R.style.DialogStyle);

        //自定义样式
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.common_dialog, null, false);

        String title = getArguments().getString("alert-title");
        String message = getArguments().getString("alert-message");
        if (title != null && title.length() > 0) {
            TextView t = (TextView) view.findViewById(R.id.alertDialog_title);
            t.setText(title);
        }

        if (message != null && message.length() > 0) {
            TextView m = (TextView) view
                    .findViewById(R.id.alertDialog_message);
            m.setText(message);
        }

        View ok = view.findViewById(R.id.btn_ok);
        View cancel = view.findViewById(R.id.btn_cancel);

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mListener != null) {
                    mListener.doPositiveClick(getTag());
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mListener != null) {
                    mListener.doNegativeClick(getTag());
                }
            }

        });

        dialog.setContentView(view);

        return dialog;
    }
}
