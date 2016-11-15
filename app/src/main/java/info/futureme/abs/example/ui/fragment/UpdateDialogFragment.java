package info.futureme.abs.example.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.futureme.abs.base.BaseDialogFragment;
import info.futureme.abs.base.InjectableDialogFragment;
import info.futureme.abs.example.R;
import info.futureme.abs.util.WindowUtils;

/**
 * Created by lit on 2015/12/8.
 */
public class UpdateDialogFragment extends InjectableDialogFragment{

    public void setListener(DialogClickListener mListener) {
        this.mListener = mListener;
    }

    public interface DialogClickListener{
        void doPositiveClick(String tag);
        void doNegativeClick(String tag);
    }

    private  DialogClickListener mListener;
    public static UpdateDialogFragment newInstance(String title, String message){
        UpdateDialogFragment sdf = new UpdateDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("alert-title", title);
        bundle.putString("alert-message", message);
        bundle.putInt(BaseDialogFragment.LAYOUT_HEIGHT, WindowUtils.dp2px(288));
        bundle.putInt(BaseDialogFragment.LAYOUT_WIDTH, WindowUtils.dp2px(255));
        sdf.setArguments(bundle);

        return sdf;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = super.onCreateView(inflater, container, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //自定义样式

        String title = getArguments().getString("alert-title");
        String message = getArguments().getString("alert-message");
        if (title != null && title.length() > 0) {
            TextView t = (TextView) view.findViewById(R.id.update_title);
            t.setText(title);
        }

        if (message != null && message.length() > 0) {
            TextView m = (TextView) view
                    .findViewById(R.id.update_log);
            m.setMovementMethod(new ScrollingMovementMethod());
            m.setText(message);
        }

        View ok = view.findViewById(R.id.update_ok);
        View cancel = view.findViewById(R.id.update_close);

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.doPositiveClick(getTag());
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.doNegativeClick(getTag());
                }
            }

        });

        return view;
    }

    @Override
    public int provideContentRes() {
        return R.layout.update_dialog;
    }

    @Override
    protected void onFragmentInVisible(Bundle savedInstanceState) {

    }

    @Override
    protected void onFragmentVisible(Bundle savedInstanceState) {

    }
}
