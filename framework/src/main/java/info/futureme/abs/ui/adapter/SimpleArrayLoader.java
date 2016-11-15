package info.futureme.abs.ui.adapter;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

public abstract class SimpleArrayLoader<T> extends
		AsyncTaskLoader<List<T>> {

	private List<T> mList;

	public SimpleArrayLoader(Context context) {
		super(context);
	}

	@Override
	public abstract List<T> loadInBackground();

	@Override
	public void onCanceled(List<T> list) {
		// TODO Auto-generated method stub
		super.onCanceled(list);

		list = null;
	}

	@Override
	public void deliverResult(List<T> list) {
		if (isReset()) {
			// An async query came in while the loader is stopped. We
			// don't need the result.
			if (list != null) {
				list = null;
			}
		}
		List<T> oldList = mList;
		mList = list;

		if (isStarted()) {
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(list);
		}

		// At this point we can release the resources associated with
		// 'oldApps' if needed; now that the new result is delivered we
		// know that it is no longer in use.
		if (oldList != null) {
			oldList = null;
		}
	}

	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		// At this point we can release the resources associated with 'apps'
		// if needed.
		if (mList != null) {
			mList = null;
		}
	}

	@Override
	protected void onStartLoading() {
		if (mList != null) {
			// If we currently have a result available, deliver it
			// immediately.
			
			deliverResult(mList);
		}

		if (takeContentChanged() || mList == null) {
			// If the data has changed since the last time it was loaded
			// or is not currently available, start a load.
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

}
