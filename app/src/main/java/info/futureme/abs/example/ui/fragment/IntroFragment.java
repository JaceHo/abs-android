package info.futureme.abs.example.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.OnClick;
import info.futureme.abs.base.InjectableFragment;
import info.futureme.abs.example.R;
import info.futureme.abs.example.ui.LoginActivity;

/**
 * Created by Jeffrey on 6/15/16.
 */
public class IntroFragment extends InjectableFragment{
    @Bind(R.id.image_view)
    ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Glide.with(this).load(R.drawable.intro2).skipMemoryCache(true).fitCenter().crossFade().into(imageView);
        return view;
    }

    @OnClick(R.id.done_btn)
    void onClickDone(){
        getActivity().startActivity(new Intent(getContext(), LoginActivity.class));
        getActivity().finish();
    }


    @Override
    public int provideContentRes() {
        return R.layout.intro_done;
    }

    @Override
    protected void onFragmentInVisible(Bundle savedInstanceState) {

    }

    @Override
    protected void onFragmentVisible(Bundle savedInstanceState) {
    }
}
