package info.futureme.abs.example.ui.fragment;

/**
 * Created by Jeffrey on 6/15/16.
 */
/*
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import info.futureme.abs.base.InjectableFragment;
import info.futureme.abs.example.R;


/**
 * This fragment displays a featured, specified image.
 */
public class ImageFragment extends InjectableFragment{
    private static final String ARG_PATTERN = "pattern";


    @Bind(R.id.image_view)
    ImageView imageView;
    private int resId;

    /**
     * Create a {@link ImageFragment} displaying the given image.
     *
     * @param resId to display as the featured image
     * @return a new instance of {@link ImageFragment}
     */
    public static ImageFragment newInstance(int resId) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PATTERN, resId);
        fragment.setArguments(args);
        return fragment;
    }

    public ImageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            resId = getArguments().getInt(ARG_PATTERN);
        }
    }

    @Override
    public int provideContentRes() {
        return R.layout.fragment_image;
    }

    @Override
    protected void onFragmentInVisible(Bundle savedInstanceState) {

    }

    @Override
    protected void onFragmentVisible(Bundle savedInstanceState) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            Glide.with(this).load(resId).skipMemoryCache(true).fitCenter().crossFade().into(imageView);
        }

        return view;
    }

}
