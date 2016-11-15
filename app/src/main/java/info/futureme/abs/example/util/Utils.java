package info.futureme.abs.example.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.util.DLog;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class Utils {

	public static Subscriber<? super String> subscriber;
	private static long prev = 0;
	private static Subscription _subscription;
	private static Toast toast ;
	private Utils() {
	};

	public static void showAlert(Activity activity, String title, String message, String ok, String no, DialogInterface.OnClickListener okCallback, DialogInterface.OnClickListener cancelCallback){
		try {
			new AlertDialog.Builder(activity).setTitle(title)
					.setMessage(message)
					.setPositiveButton(ok, okCallback)
					.setNegativeButton(no, cancelCallback).show();
		}catch (Exception e){
			//ignore alert when activity finishes
		}
	}

	public static String getSimpleDay(long millis) {
		Calendar calendar = Calendar.getInstance();
		String res = new SimpleDateFormat(MVSConstants.DATE_FORMAT_DAY).format(calendar.getTime());
		calendar.setTimeInMillis(millis);
		Calendar now = Calendar.getInstance();
		int dayNow = now.get(Calendar.DAY_OF_YEAR);
		int dayOther = calendar.get(Calendar.DAY_OF_YEAR);
		int diff = dayNow - dayOther;
		if (diff == 0) {
			res = "今天";
		} else if (diff == 1){
			res = "昨天";
		//}else if(diff == 2){
		//	res = "前天";
		}else if(diff == -1){
			res = "明天";
		//}else if(diff == -2){
		//	res = "后天";
		}
		return res;
	}

	public static void debounceToast(final String text){
		if(_subscription == null)
			_subscription = Observable.create(new Observable.OnSubscribe<String>() {

				@Override
				public void call(Subscriber<? super String> subscriber) {
					Utils.subscriber = subscriber;
				}
			})
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Action1<String>() {
						@Override
						public void call(String s) {
							if(toast == null) {
								toast = Toast.makeText(ContextManager.context(), "", Toast.LENGTH_SHORT);
							}
							if(System.currentTimeMillis() - prev > 2000) {
								toast.setText(s);
								toast.show();
							}
							DLog.i("toast time delta:", "" + (System.currentTimeMillis() - prev));
							prev = System.currentTimeMillis();
						}
					});
		subscriber.onNext(text);
	}

	public static void unsubscribe(){
		if(_subscription != null)
			_subscription.unsubscribe();
	}

	public static boolean hasFroyo() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.FROYO;

	}

	public static boolean hasIceCreamSandwich() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
	}

	public static boolean hasKitKat() {
		return Build.VERSION.SDK_INT >= 19;
	}

	public static List<Size> getResolutionList(Camera camera)
	{
		Parameters parameters = camera.getParameters();
		List<Size> previewSizes = parameters.getSupportedPreviewSizes();
		return previewSizes;
	}

	public static class ResolutionComparator implements Comparator<Size> {

		@Override
		public int compare(Size lhs, Size rhs) {
			if(lhs.height!=rhs.height)
			return lhs.height-rhs.height;
			else
			return lhs.width-rhs.width;
		}
		 
	}
	
	
	
	
}
