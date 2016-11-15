package info.futureme.abs.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * <b><u>BaseListAdapter </u></b>is usually used in a <b>listview </b>as a
 * customized T entity item object in the adapter, this class also provide
 * selection index trace and listview <i>reachbottom </i>listener to trigger
 * listview status changing.
 * @author Jeffrey
 * @version 1.0
 * @updated 25-一月-2016 15:17:49
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {

    protected List<T> mList = null;
	/**
	 * selection index in the adapter list
	 */
    protected int index = 0;

    /**
     * list == null, not save adater data state, list!=null, save it's state
     * util next shown
     *
     * @param list
     */
    public BaseListAdapter(List<T> list) {
        super();
        if (list == null)
            mList = new LinkedList<T>();
        else
            this.mList = list;
    }

    public List<T> getList() {
        return mList;
    }

    /*
     * 往后加
     */
    public void appendToList(List<T> list) {
        if (list == null) {
            return;
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    /*
     * 往前加
     */
    public void appendToTopList(List<T> list) {
        if (list == null) {
            return;
        }
        mList.addAll(0, list);
        notifyDataSetChanged();
    }

	/**
	 * clear dataset and notify dataset changed
	 */
    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public T getItem(int position) {
        if (position > mList.size() - 1) {
            return null;
        }
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == getCount() - 1) {
            onReachBottom();
        }
        return getNextView(position, convertView, parent);
    }


	/**
	 * set listview current selection index.
	 * 
	 * @param index
	 */
    public void setIndex(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

	/**
	 * get nextview is called to retrieve item view
	 * 
	 * @param position
	 * @param convertView
	 * @param parent
	 */
    protected abstract View getNextView(int position, View convertView,
                                        ViewGroup parent);

	/**
	 * on reachbottom is called when listview last item is visible.
	 */
    protected abstract void onReachBottom();

    public interface ListAdapterWrapper {
        /**
         * get nextview for displaying
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        View getNextView(int position, View convertView, ViewGroup parent);

        /**
         * callback function when listview reached bottom
         */
        void onReachBottom();
    }

}
