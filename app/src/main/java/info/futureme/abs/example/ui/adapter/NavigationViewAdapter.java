package info.futureme.abs.example.ui.adapter;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import info.futureme.abs.FApplication;
import info.futureme.abs.base.BaseListAdapter;
import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.example.ABSApplication;
import info.futureme.abs.example.R;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.example.conf.MessageType;
import info.futureme.abs.example.entity.g.NotificationDao;
import info.futureme.abs.example.service.UploadService;
import info.futureme.abs.util.FPreferenceManager;
import info.futureme.abs.util.ViewHolder;

/**
 * Created by lit on 2015/12/3.
 */

public class NavigationViewAdapter extends BaseListAdapter<Pair<Integer, Integer>> {


    private int sum;//会话数量
    public static final int[] icons = new int[]{
            R.drawable.historyorder_normal,
//            R.drawable.nav_history,
            R.drawable.nav_data_sync,
            R.drawable.nav_im,
//            R.drawable.nav_qrcode,
//            R.drawable.nav_nav,
            R.drawable.nav_about,
    };

//    public static final int[] titles = new int[]{
//            R.string.historyorder,
//            R.string.my_works,
//            R.string.data_sync,
//            R.string.messages,
//            R.string.client_qrcodes,
//            R.string.repair_nav,
//            R.string.about,
//    };
    public static final int[] titles = new int[]{
            R.string.historyorder,
            R.string.data_sync,
            R.string.messages,
            R.string.about,
    };

    /**
     * list == null, not save adater data state, list!=null, save it's state
     * util next shown
     *
     * @param list
     */
    public NavigationViewAdapter(List<Pair<Integer, Integer>> list) {
        super(list);
        for (int i = 0; i < icons.length; i++)
            getList().add(new Pair<Integer, Integer>(icons[i], titles[i]));
    }

    @Override
    protected View getNextView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_main_drawer_item, null);
        }
        ImageView image = ViewHolder.get(convertView, R.id.nav_view_drawer_image);
        TextView alart = ViewHolder.get(convertView, R.id.nav_view_drawer_alart);
        TextView title = ViewHolder.get(convertView, R.id.nav_view_drawer_text);
        image.setImageResource(icons[position]);
        title.setText(ContextManager.context().getString(titles[position]));
        //default style
        alart.setVisibility(View.INVISIBLE);

        if (position == 1) {
            alart.setBackgroundResource(R.drawable.nav_view_alert_blue_shape);
            alart.setVisibility(View.VISIBLE);
            String left = UploadService.getUploadingSize();
            if(left == null){
                alart.setText(" 0.00KB ");
            }else{
                alart.setText(" " + UploadService.getUploadingSize() + " ");
            }
        }else if(position == 2){
            alart.setBackgroundResource(R.drawable.nav_view_alert_red_shape);
            alart.setVisibility(View.VISIBLE);
            NotificationDao notificationDao = ABSApplication.getDaoSession().getNotificationDao();
            String account = FPreferenceManager.getString(MVSConstants.ACCOUNT_SIGNED, "");
            long count = notificationDao.queryBuilder().where(
                    NotificationDao.Properties.Readed.eq(false)
                    ,NotificationDao.Properties.Type.notEq(MessageType.BILL_NUMBER.getValue()),
                    NotificationDao.Properties.Type.notEq(MessageType.RECEIVE_NOTIFY.getValue()),
                    NotificationDao.Properties.Type.notEq(MessageType.GRAB_NOTIFY.getValue()),
                    NotificationDao.Properties.Account.eq(account)
            ).count();
            FApplication.getRxBus().send((int) count);
            if(count == 0){
                alart.setVisibility(View.INVISIBLE);
            }else {
                alart.setText("" + count);
            }
        }
        return convertView;
    }

    @Override
    protected void onReachBottom() {

    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
