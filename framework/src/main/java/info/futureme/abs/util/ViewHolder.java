package info.futureme.abs.util;

import android.util.SparseArray;
import android.view.View;

/**
 * <font color="#008000">universally used ViewHolder with sparse array and generic
 * return type considered for ease use</font>
 * <font color="#008000">
 * </font><font color="#008000"> </font><font color="#0000ff"><b>Example to use
 * ViewHolder</b></font>
 * <font color="#008000">	 </font>
 * <font color="#008000">	</font><font color="#008040">public View getView(int
 * position, View convertView, ViewGroup parent) {</font>
 * <font color="#008040">		 </font>
 * <font color="#008040">	    if (convertView == null) {</font>
 * <font color="#008040">	        convertView = LayoutInflater.
 * from(context)</font>
 * <font color="#008040">	 .inflate(R.layout.banana_phone, parent, false);</font>
 * <font color="#008040">	    }</font>
 * <font color="#008040">	 </font>
 * <font color="#008040">	    ImageView bananaView = ViewHolder.get(convertView, R.
 * id.banana);</font>
 * <font color="#008040">	    TextView phoneView = ViewHolder.get(convertView, R.
 * id.phone);</font>
 * <font color="#008040">	 </font>
 * <font color="#008040">	    BananaPhone bananaPhone = getItem(position);</font>
 * <font color="#008040">	 phoneView.setText(bananaPhone.getPhone());</font>
 * <font color="#008040">	 bananaView.setImageResource(bananaPhone.getBanana());
 * </font>
 * <font color="#008040">	 </font>
 * <font color="#008040">	    return convertView;</font>
 * <font color="#008040">	}</font>
 * @author Jeffrey
 * @version 1.0
 * @updated 26-一月-2016 13:33:28
 */
public class ViewHolder {
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}
	

}
