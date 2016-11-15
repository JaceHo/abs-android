package info.futureme.abs.example.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.trello.rxlifecycle.ActivityEvent;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import info.futureme.abs.FApplication;
import info.futureme.abs.base.ActionBarFragmentActivity;
import info.futureme.abs.base.BaseActionBarMockingActivity;
import info.futureme.abs.base.BaseDialogFragment;
import info.futureme.abs.base.InjectableFragment;
import info.futureme.abs.entity.FGson;
import info.futureme.abs.entity.Result;
import info.futureme.abs.entity.UpdateResponse;
import info.futureme.abs.example.ABSApplication;
import info.futureme.abs.example.R;
import info.futureme.abs.example.biz.AccountManagerImpl;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.example.conf.MessageType;
import info.futureme.abs.example.entity.BillNumber;
import info.futureme.abs.example.entity.g.Notification;
import info.futureme.abs.example.entity.g.NotificationDao;
import info.futureme.abs.example.rest.ServiceGenerator;
import info.futureme.abs.example.rest.TicketAPI;
import info.futureme.abs.example.service.UploadService;
import info.futureme.abs.example.ui.adapter.NavigationViewAdapter;
import info.futureme.abs.example.ui.fragment.AppSettingFragment;
import info.futureme.abs.example.ui.fragment.POIMarkerFragment;
import info.futureme.abs.example.ui.fragment.TicketListFragment;
import info.futureme.abs.example.util.update.CheckUpdateTask;
import info.futureme.abs.rest.NetworkObserver;
import info.futureme.abs.util.CompatHelper;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.FPreferenceManager;
import info.futureme.abs.util.update.UpdateListener;
import info.futureme.abs.view.overscroll.OverScrollDecoratorHelper;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActionBarMockingActivity
        implements AdapterView.OnItemClickListener,
        BaseDialogFragment.DialogFragmentDismissListener, UpdateListener{
    public static final String OPEN_NOTIFICATION = "open_notification";
    public static final String OPEN_RECEIVING = "open_receving";
    public static final String OPEN_ACTIVE = "open_filter";
    public static final String OPEN_GRAB = "open_grab";

    //main activity producing events
    public static final String REFRESH_TICKETS_NUMBER_ONLY = "refresh_tickets_number_only";
    public static final String TICKET_DONE = "ticket_done";
    public static final String CLOSE_RECEIVE_FRAGMENT = "close_receive_fragment";
    public static final String TICKET_DONE_REVERT = "ticket_done_revert";
    public static final String FLOAT_RAW_Y = "float_raw_y";
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.actionbar_left)
    ImageView menuIcon;
    @Bind(R.id.actionbar_left_layout)
    FrameLayout left_layout;
    @Bind(R.id.read_tip)
    View readTip;
    private NavigationViewAdapter navigationViewAdapter;
    private TicketListFragment fragment;
    private NotificationDao dao;
    private TicketAPI ticketAPI;
    private int total = 0;
    private View headerview;
    @Bind(R.id.tickets_done_textview)
    TextView doneTip;
    private AsyncTask<Void, Void, UpdateResponse> updateTask;

    @Override
    protected void onNewIntent(Intent intent) {
        DLog.i("newintent", getIntent() == null ? "null" : getIntent().getExtras() == null ? "null" : getIntent().getExtras().toString());
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!AccountManagerImpl.checkIn(this)) {
            finish();
            return;
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        dao = ABSApplication.getDaoSession().getNotificationDao();
        ticketAPI = ServiceGenerator.createService(TicketAPI.class);
        initRx();
        initPager();
        //lazy load
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    initNavigation();
                    updateTask = new CheckUpdateTask(MainActivity.this, MVSConstants.APIConstants.TYPE_DIALOG, false, MainActivity.this).execute();
                    if (AccountManagerImpl.instance.isLogin()) {
                        AccountManagerImpl.async();
                    }
                }catch (Exception e){
                    //should not happen!!!
                    e.printStackTrace();
                    finish();
                }
            }
        }, 1000);
    }

    private void initRx() {
        FApplication.getRxBus().toObserverable()
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object event) {
                        if (event instanceof UploadService.AttachmentChangeInfo) {
                            UploadService.AttachmentChangeInfo obj = (UploadService.AttachmentChangeInfo) event;
                            if (
                                    obj.status == UploadService.AttachmentChangeInfo.ATTACHMENT_STATUS.NEW ||
                                            obj.status == UploadService.AttachmentChangeInfo.ATTACHMENT_STATUS.DEL
                                            || obj.status == UploadService.AttachmentChangeInfo.ATTACHMENT_STATUS.SUCCESS)
                                if (navigationViewAdapter != null) {
                                    navigationViewAdapter.notifyDataSetChanged();
                                }
                        } else if(event instanceof Integer){
                            int count = (int) event;
                            if(count > 0)
                                readTip.setVisibility(View.VISIBLE);
                            else
                                readTip.setVisibility(View.GONE);
                        }else if(event.equals(REFRESH_TICKETS_NUMBER_ONLY)
                                || event.equals(TicketListFragment.RECEIVE_SUCCESS)){
                            updateTicketNumberDataAsync();
                        }else if(event.equals(TICKET_DONE_REVERT)){
                            doneTip.setVisibility(View.GONE);
                        }else if(event.equals(TICKET_DONE)){
                            if(fragment.getLoaderView().getVisibility() == View.GONE) {
                                String account = FPreferenceManager.getString(MVSConstants.ACCOUNT_SIGNED, "");
                                boolean firstTime = FPreferenceManager.isFirstTime(LoginActivity.class.getName() + account);
                                if(total == 0 && !firstTime){
                                    doneTip.setText(R.string.tickets_done);
                                }else{
                                    doneTip.setText(R.string.click_to_receive_tickets);
                                }
                                doneTip.setVisibility(View.VISIBLE);
                                doneTip.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        doneTip.setVisibility(View.GONE);
                                        fragment.getLoaderView().setVisibility(View.VISIBLE);
                                        fragment.getLoaderView().triggerRetry();
                                    }
                                });
                            }
                        }
                    }
                });
    }

    @Override
    protected void onResume(){
        super.onResume();
        //Clear all notification
        updateTicketNumberDataAsync();
    }


    private void initNavigation() {
        LayoutInflater inflater = LayoutInflater.from(this);
        headerview = inflater.inflate(R.layout.nav_header_main, null);

        ListView navListView = (ListView) headerview.findViewById(R.id.nav_listView);
        navigationViewAdapter = new NavigationViewAdapter(null);
        navListView.setAdapter(navigationViewAdapter);
        navListView.setOnItemClickListener(this);
        if (CompatHelper.hasIceCreamSandwich()) {
            OverScrollDecoratorHelper.setUpOverScroll(navListView);
        }
        navigationView.addView(headerview);
        left_layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final float menuWidth = left_layout.getLayoutParams().width;
                final float drawerWidth = drawer.getLayoutParams().width;
                drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        super.onDrawerSlide(drawerView, slideOffset);
                        float radio = slideOffset * drawerWidth / menuWidth;
                        menuIcon.setRotation((radio > 1 ? 1 : radio) * 90 * 18);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        //TODO
                        //rxBus.send(REFRESH_TICKETS_NUMBER);
                        drawer.setTag(false);
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                    }
                });
                //remove this!!
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    left_layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    left_layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    private void initPager() {
        fragment = (TicketListFragment) TicketListFragment.newInstance(MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS + "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.tickets_mapview)
    public void onClickMapView(){
        Intent intent = new Intent(this, ActionBarFragmentActivity.class);
        intent.putExtra(ActionBarFragmentActivity.FRAGMENT_LEFT_TITLE, R.string.my_tickets);
        intent.putExtra(ActionBarFragmentActivity.FRAGMENT_RIGHT_TITLE, R.string.receive_order_pool);
        intent.putExtra(InjectableFragment.HARDWARE_ACCELERATED, true);
        intent.putExtra(ActionBarFragmentActivity.FRAGMENT_CLASS_NAME, POIMarkerFragment.class.getName());
        intent.putExtra(MVSConstants.FragmentType.FRAGMENT_ORDERTYPE, MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS + "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS);
        startActivity(intent);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getActionBarTitleStringId() {
        return R.string.app_name;
    }

    @Override
    protected int getActionBarRightResourceId() {
        return R.drawable.add_new;
    }

    @Override
    protected void onActionBarRightClick() {
        //补单的HTML页面
        getIntent().putExtra(MVSConstants.X5WEBVIEW_INITIAL_URL,
                String.format(MVSConstants.APIConstants.TICKET_OPEN_PAGE, 2));
        getIntent().putExtra(WebActivity.TITLE_EXTRA, getString(R.string.open_ticket));
        openActivity(WebActivity.class, getIntent().getExtras());
    }

    protected void onActionBarLeftClick() {
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        if (isSearching()) {
            triggerSearchUi(false);
            return;
        }
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            Bundle bundle = new Bundle();
            bundle.putString(ActionBarFragmentActivity.FRAGMENT_CLASS_NAME, AppSettingFragment.class.getName());
            bundle.putInt(ActionBarFragmentActivity.FRAGMENT_TITLE, R.string.about);
            openActivity(ActionBarFragmentActivity.class, bundle);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    @Override
    protected void onDestroy() {
        if(updateTask != null)
            updateTask.cancel(true);
        super.onDestroy();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onRetrieveDialogFragmentData(Bundle b, int Tag) {
//        if(Tag == TicketReceiveDialogFragment.class.getName().hashCode()){
//            if(b != null) {
//                if(b.getBoolean(TicketListFragment.REFRESH_LSIT_ACTIVE, false)) {
//                    fragment.onRefresh(true);
//                    updateTicketNumberDataAsync();
//                }
//            }
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == (MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS + "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS).hashCode()) {
                fragment.onRefresh(false);
            }else if (requestCode == MVSConstants.FragmentType.FRAGMENT_RECEIVEORDERS.hashCode()
                    || requestCode == MVSConstants.FragmentType.FRAGMENT_GRABORDERS.hashCode()) {
                FApplication.getRxBus().send(TicketListFragment.REFRESH_LSIT_RECEIVE);
                FApplication.getRxBus().send(TicketListFragment.REFRESH_LSIT_ACTIVE);
                FApplication.getRxBus().send(MainActivity.REFRESH_TICKETS_NUMBER_ONLY);
            }
        }
    }

    private void updateTicketNumberDataAsync() {
        ticketAPI.ticketsLeft()
                .compose(this.<Result<BillNumber>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new NetworkObserver<Result<BillNumber>>() {
                    @Override
                    public void onSuccess(Result<BillNumber> modeNumberResult) {
                        try {
                            //TODO
                            if (modeNumberResult.getResult() != null && modeNumberResult.getEcode() == 0) {
                                String account = FPreferenceManager.getString(MVSConstants.ACCOUNT_SIGNED, "");
                                dao.queryBuilder().where(NotificationDao.Properties.Type.eq(MessageType.BILL_NUMBER.getValue()), NotificationDao.Properties.Account.eq(account)).buildDelete().executeDeleteWithoutDetachingEntities();
                                Notification notification = new Notification();
                                notification.setDate(new Date());
                                notification.setAccount(account);
                                notification.setType(MessageType.BILL_NUMBER.getValue());
                                notification.setData(FGson.gson().toJson(modeNumberResult.getResult()));
                                dao.insert(notification);
                                refreshOrder(modeNumberResult.getResult().getGrabqty(), modeNumberResult.getResult().getAcceptqty());
                            } else {
                                if (!refreshDao()) {
                                    refreshOrder(0, 0);
                                }
                            }
                        } catch (Exception e) {
                            if (!refreshDao()) {
                                refreshOrder(0, 0);
                            }
                        }
                    }

                    @Override
                    public void onFailure(String tip) {
                        if (!refreshDao()) {
                            refreshOrder(0, 0);
                        }
                    }
                });
    }

    public void refreshOrder(int grab, int receive) {
        DLog.d("tip:", "grab:" + grab + "  receive:" + receive);
        if(doneTip != null && doneTip.getVisibility() == View.VISIBLE){
            if(total == 0){
                doneTip.setText(R.string.tickets_done);
            }else{
                doneTip.setText(R.string.click_to_receive_tickets);
            }
        }
    }

    private boolean refreshDao() {
        String account = FPreferenceManager.getString(MVSConstants.ACCOUNT_SIGNED, "");
        List<Notification> list = dao.queryBuilder().where(NotificationDao.Properties.Type.eq(MessageType.BILL_NUMBER.getValue()), NotificationDao.Properties.Account.eq(account)).list();
        if (list != null && list.size() > 0) {
            BillNumber billNumber = FGson.gson().fromJson(list.get(0).getData(), BillNumber.class);
            refreshOrder(billNumber.getGrabqty(), billNumber.getAcceptqty());
            return true;
        }
        return false;
    }

    @Override
    public void onUpdateReturned(UpdateResponse result) {

    }
}


