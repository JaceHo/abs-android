package edu.hebtu.movingcampus.activity.setting;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	Activity activity;
	public Bitmap[] image;

	public ImageAdapter(Activity paramActivity) {
		this.activity = paramActivity;
	}

	public static Bitmap drawableToBitmap(Drawable paramDrawable) {
		int i = paramDrawable.getIntrinsicWidth();
		int j = paramDrawable.getIntrinsicHeight();
		if (paramDrawable.getOpacity() != -1)
			;
		for (Bitmap.Config localConfig = Bitmap.Config.ARGB_8888;; localConfig = Bitmap.Config.RGB_565) {
			Bitmap localBitmap = Bitmap.createBitmap(i, j, localConfig);
			Canvas localCanvas = new Canvas(localBitmap);
			paramDrawable.setBounds(0, 0, paramDrawable.getIntrinsicWidth(),
					paramDrawable.getIntrinsicHeight());
			paramDrawable.draw(localCanvas);
			return localBitmap;
		}
	}

	@Override
	public int getCount() {
		return this.image.length;
	}

	@Override
	public Object getItem(int paramInt) {
		return this.image[paramInt];
	}

	@Override
	public long getItemId(int paramInt) {
		return paramInt;
	}

	@Override
	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
		ImageView localImageView;
		if (paramView == null) {
			localImageView = new ImageView(this.activity);
			localImageView
					.setLayoutParams(new AbsListView.LayoutParams(40, 40));
			localImageView.setAdjustViewBounds(false);
			localImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		}
		while (true) {
			/*
			 * localImageView.setImageBitmap(this.image[paramInt]); return
			 * localImageView; localImageView = (ImageView)paramView;
			 */
		}
	}
}

/*
 * Location: /tmp/apksrc_tmp_9gTLc/classes-dex2jar.jar Qualified Name:
 * com.caii101.bean.ImageAdapter JD-Core Version: 0.6.2
 */