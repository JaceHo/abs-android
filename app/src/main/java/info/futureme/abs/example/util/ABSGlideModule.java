/*
 * 
 * Copyright (c) 2015-2016 All Rights Reserved.
 * Project Name: lmrp-android app
 * Create Time: 16-2-16 下午6:46
 */

package info.futureme.abs.example.util;


import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpGlideModule;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;

import java.io.File;
import java.io.InputStream;

import info.futureme.abs.example.ABSApplication;
import info.futureme.abs.util.FileHelper;


/**
 * <i>todo:</i>
 * <ul>
 * 	<li>1. progurad.pro   </li>
 * 	<li>2. module register <a href="$inet://https://github.
 * com/bumptech/glide/wiki/Configuration"><font color="#0000ff"><u>https://github.
 * com/bumptech/glide/wiki/Configuration</u></font></a>   </li>
 * 	<li>3. <manifest ...> </li>
 * 	<li>    <!-- ... permissions --> </li>
 * 	<li>    <application ...> </li>
 * 	<li>        <meta-data </li>
 * 	<li>android:name="com.mypackage.MyGlideModule" </li>
 * 	<li>            android:value="GlideModule" /> </li>
 * 	<li>        <!-- ... activities and other components --> </li>
 * 	<li>    </application> </li>
 * 	<li></manifest> </li>
 * 	<li>4. replace imageloader with glide</li>
 * </ul>
 * @author Jeffrey
 * @version 1.0
 * @updated 17-一月-2016 14:18:36
 */
public class ABSGlideModule extends OkHttpGlideModule {
	@Override
	public void registerComponents(Context context, Glide glide) {
		glide.register(GlideUrl.class, InputStream.class, new ABSOkHttpUrlLoader.Factory());
	}

	@Override
	public void applyOptions(Context context, GlideBuilder builder) {
        // Apply options to the builder here.
        builder.setDiskCache(new DiskLruCacheFactory(new DiskLruCacheFactory.CacheDirectoryGetter() {
            @Override
            public File getCacheDirectory() {
				FileHelper.ensureDir(ABSApplication.getCachedDir().getAbsolutePath());
				return ABSApplication.getCachedDir();
			}
		}, 1024*1024*50));
		builder.setMemoryCache(new LruResourceCache(1024 * 1024 * 3));
		builder.setBitmapPool(new LruBitmapPool(1024 * 1024 * 3));
    }
}
