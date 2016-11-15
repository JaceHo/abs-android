package info.futureme.abs.example.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import info.futureme.abs.FApplication;
import info.futureme.abs.base.ActionBarFragment;
import info.futureme.abs.base.BaseActionBarMockingActivity;
import info.futureme.abs.base.FBaseActivity;
import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.entity.Result;
import info.futureme.abs.example.R;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.example.entity.Ticket;
import info.futureme.abs.example.entity.TicketRequest;
import info.futureme.abs.example.rest.ServiceGenerator;
import info.futureme.abs.example.rest.TicketAPI;
import info.futureme.abs.example.service.GetuiPushReceiver;
import info.futureme.abs.example.ui.MainActivity;
import info.futureme.abs.example.ui.WebActivity;
import info.futureme.abs.example.ui.adapter.TicketAdapter;
import info.futureme.abs.rest.NetworkObserver;
import info.futureme.abs.service.LocationService;
import info.futureme.abs.util.rx.RxBus;
import info.futureme.abs.view.ContentLoaderView;
import info.futureme.abs.view.FXRecyclerView;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class TicketListFragment extends ActionBarFragment implements ContentLoaderView.OnRefreshListener, ContentLoaderView.OnMoreListener, TicketAdapter.OnTicketSelectedListener, BaseActionBarMockingActivity.OnSearchListener, LocationService.LocationSuccessListener, ContentLoaderView.OnUpdateStateResourceCallback{

    public static final String KEY_ORDER_LIST = "key_order_list";

    //ticket list producing events
    public static final String REFRESH_LSIT_ACTIVE = "key_refresh_list";
    public static final String REFRESH_LSIT_RECEIVE = "key_refresh_receive_list";
    public static final String RECEIVE_SUCCESS = "receive_success";
    public static final String SIGNIN_SUCCESS = "signin_success";
    public static final String REMOVE_POSITION = "remove_position";
    private static Pair<Long, LatLng> latLng;
    Scheduler.Worker worker = Schedulers.newThread().createWorker();
    @Bind(R.id.content_loader)
    ContentLoaderView loaderView;
    @Bind(R.id.recycler)
    FXRecyclerView recyclerView;
    Intent intent;
    //变量命名
    TicketAPI apiService;
    boolean hasLoaded = false;
    TicketAdapter adapter;
    int current_page = 1;

    public TicketRequest getRequest() {
        return request;
    }

    private TicketRequest request = new TicketRequest();
    private Subscription _subscription;
    private ServiceConnection conn;
    private String type;
    private RxBus rxBus;

    public static Fragment newInstance(String type, Bundle args) {
        TicketListFragment fragment = new TicketListFragment();
        args.putString(MVSConstants.FragmentType.FRAGMENT_ORDERTYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment newInstance(String type) {
        return newInstance(type, new Bundle());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            if (adapter != null && adapter.getTicketList() == null)
                adapter.setListData(new ArrayList<Ticket>());
            if (adapter != null)
                outState.putSerializable(KEY_ORDER_LIST + type, adapter.getTicketList());
        }
    }

    private void countDownList() {
        if ((MVSConstants.FragmentType.FRAGMENT_GRABORDERS+ "," + MVSConstants.FragmentType.FRAGMENT_RECEIVEORDERS).equals(type)) {
            worker.unsubscribe();
            worker = Schedulers.newThread().createWorker();
            worker.schedulePeriodically(new Action0() {
                @Override
                public void call() {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if(adapter != null) {
                                if(recyclerView != null)
                                    adapter.refreshTimers(recyclerView);
                            }
                        }
                    });
                }
            }, 1, 1, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = new Intent(this.getContext(), LocationService.class);
        conn = LocationService.connection(this, intent);
        request.setPage(MVSConstants.ListParamConstants.page_default);
        request.setLimit(MVSConstants.ListParamConstants.pageSize_default);
        rxBus = FApplication.getRxBus();
        rxBus.toObserverable()
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if(o.equals(RECEIVE_SUCCESS)) {
                            if ((MVSConstants.FragmentType.FRAGMENT_GRABORDERS + "," + MVSConstants.FragmentType.FRAGMENT_RECEIVEORDERS).equals(type)
                                    || (MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS + "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS).equals(type)) {
                                if (adapter != null && (MVSConstants.FragmentType.FRAGMENT_GRABORDERS + "," + MVSConstants.FragmentType.FRAGMENT_RECEIVEORDERS).equals(type)){
                                    if(adapter.getTicketList().size() <= 2) {
                                        FApplication.getRxBus().send(MainActivity.CLOSE_RECEIVE_FRAGMENT);
                                    }else{
                                        final List<Ticket> tickets = adapter.getTicketList();
                                        int postion = getArguments().getInt(REMOVE_POSITION);
                                        for(int i = postion; i>=0; i--){
                                            Ticket tmp = tickets.get(i);
                                            if(tmp.isHeader){
                                                if(--tmp.total == 0){
                                                    tickets.remove(tmp);
                                                    postion--;
                                                }
                                                adapter.notifyDataSetChanged();
                                                break;
                                            }
                                        }
                                        if(getView() != null){
                                            final int finalPostion = postion;
                                            getView().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tickets.remove(finalPostion);
                                                    //header counted in
                                                    adapter.notifyItemRemoved(1 + finalPostion);
                                                }
                                            }, 100);
                                        }
                                        //do nothing
//                                        onRefresh(false);
                                    }
                                } else {
                                    onRefresh(false);
                                }
                            }
                        } else if (o instanceof Ticket) {
                            onRefresh(false);
                        } else if((o.equals(GetuiPushReceiver.NEW_GRAB)
                                || o.equals(GetuiPushReceiver.NEW_RECEIVE))
                                &&
                                (MVSConstants.FragmentType.FRAGMENT_GRABORDERS+ "," + MVSConstants.FragmentType.FRAGMENT_RECEIVEORDERS).equals(type)){
                            onRefresh(false);
                        } else if(
                                o.equals(GetuiPushReceiver.NEW_NOTIFICATION_UNREAD) ){
                            //speaker button show or not
                            if((MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS + "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS).equals(type)){
                                if(adapter != null){
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        } else if (
                                REFRESH_LSIT_RECEIVE.equals(o) && (MVSConstants.FragmentType.FRAGMENT_GRABORDERS+ "," + MVSConstants.FragmentType.FRAGMENT_RECEIVEORDERS).equals(type)
                                || (MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS + "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS).equals(type) && TicketListFragment.REFRESH_LSIT_ACTIVE.equals(o)
                                || (MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS + "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS).equals(type) && TicketListFragment.SIGNIN_SUCCESS.equals(o)
                                ) {
                            onRefresh(false);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = super.onCreateView(inflater, container, savedInstanceState);
        type = getArguments().getString(MVSConstants.FragmentType.FRAGMENT_ORDERTYPE);

        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SpaceItemDecoration(getItemSpace()));
        recyclerView.setBackgroundColor(getResources().getColor(R.color.select_drawable_bg));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        request.getFilter().setStatus(type);
        adapter = new TicketAdapter(type, worker, this);
        if (MVSConstants.FragmentType.FRAGMENT_DONE_HISTORYORDERS.equals(type)
                        || MVSConstants.FragmentType.FRAGMENT_CLOSE_HISTORYORDERS.equals(type)){
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }else if((MVSConstants.FragmentType.FRAGMENT_GRABORDERS + "," + MVSConstants.FragmentType.FRAGMENT_RECEIVEORDERS).equals(type)){
            int floatY = getArguments().getInt(MainActivity.FLOAT_RAW_Y, 0);
//            recyclerView.setItemAnimator(new ReceiveAnimator(floatY));
//            recyclerView.setLayoutManager(new LayoutManager(getContext()));
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            View footer = LayoutInflater.from(getActivity()).inflate(R.layout.reyclerview_footer, null, false);
            recyclerView.addFootView(footer);
        }else{
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            View footer = LayoutInflater.from(getActivity()).inflate(R.layout.reyclerview_footer, null, false);
            recyclerView.addFootView(footer);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                    adapter.closeAllItems();
                }
            });
        }

        adapter.setOnOrderSelectedListener(this);
        loaderView.setAdapter(adapter);

        loaderView.setUpdateStateResourceCallback(this);
        loaderView.setOnRefreshListener(this);
        loaderView.setMoreListener(this);

        return view;
    }


    @Override
    public int provideContentRes() {
        return R.layout.fragment_list;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(loaderView != null)
            loaderView.setAdapter(null);
        if(worker != null) {
            worker.unsubscribe();
            worker = null;
        }
    }

    @Override
    protected void onFragmentInVisible(Bundle savedInstanceState) {
    }

    @Override
    protected void onFragmentVisible(Bundle savedInstanceState) {
        if (adapter.getItemCount() == 0) {
            if (savedInstanceState != null) {
                ArrayList<Ticket> anchorList = (ArrayList<Ticket>) savedInstanceState.getSerializable(KEY_ORDER_LIST + type);
                adapter.setListData(anchorList);
                adapter.notifyDataSetChanged();
            } else {
                loadData(request);
            }
        } else {
            countDownList();
        }
        if((MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS+ "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS).equals(type) && (latLng == null || System.currentTimeMillis() - latLng.first > 5*60*1000)){
            ((FBaseActivity) getActivity()).requestPermission(new Runnable() {
                @Override
                public void run() {
                    if(getContext() != null) {
                        try {
                            getActivity().bindService(intent, conn, Context.BIND_AUTO_CREATE);
                        } catch (Exception e) {
                        }
                    }
                }
            }, null, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
    }

    public void loadData(final TicketRequest params) {
        apiService = ServiceGenerator.createService(TicketAPI.class);
        if (!hasLoaded) {
            //TODO load from cache
        }
        if (_subscription == null || _subscription.isUnsubscribed()) {
                _subscription = apiService.tickets(params)
                    .compose(this.<Result<ArrayList<Ticket>>>bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribe(new NetworkObserver<Result<ArrayList<Ticket>>>() {
                        @Override
                        public void onSuccess(Result<ArrayList<Ticket>> ticketListResult) {
                            if(adapter == null || loaderView == null) return;
                            int page = request.getPage();
                            if (ticketListResult.getEcode() == 0) {//success
                                ArrayList<Ticket> orderList = ticketListResult.getResult();
//                                if(orderList == null || orderList.size() == 0) {
//                                    onFailure("");
//                                    return;
//                                }

                                if (page == MVSConstants.ListParamConstants.FIRST_PAGE) {
                                    adapter.setListData(orderList);
                                } else {
                                    adapter.addListData(orderList);
                                }

                                if (adapter.getTicketList().size() > 0) {
                                    countDownList();
                                }

                                if (adapter.getTicketList().size() < MVSConstants.ListParamConstants.pageSize_default) {
                                    current_page = page;
                                    loaderView.setPage(page, current_page);//最后一页数据少于约定数据量，停止加载
                                } else {
                                    current_page = page + 1;
                                    loaderView.setPage(page, current_page);//设置当前页和最后一页
                                }
                                hasLoaded = true;
                                adapter.notifyDataSetChanged();

                            } else {//failed
                                onFailure(ticketListResult.getReason());
                            }
                        }

                        @Override
                        public void onFailure(String tip) {

//                            Result<ArrayList<Ticket>> list = new Result<ArrayList<Ticket>>();
//                            list.setEcode(0);
//                            ArrayList<Ticket> tickets = new ArrayList<Ticket>();
//                            for (int i = 0; i < 10; i++) {
//                                Ticket t = new Ticket();
//                                t.setCtime(new Date(((i % 3) + 1) * 30 * 24 * 3600 * 1000 + System.currentTimeMillis()));
//                                t.getStatus().setCurrstate("预约");
//                                t.getStatus().setPrevstate("接单");
//                                t.setItsmcode(i * 32 + "");
//                                t.setIssue("描述：pos机. 设备型号>屏碎，但能正常使用");
//                                t.getStatus().setState(4);
//                                t.getCustomer().setLatitude(i + 50.000);
//                                t.setNotes("notice" + i);
//                                t.getCustomer().setLongitude(i + 60.000);
//                                t.setProject("project" + i);
//                                t.setTicketid("ID:2016040616301234");
//                                t.setResplevel("7*12*8");
//                                t.setTimers(new Date[]{new Date(System.currentTimeMillis() + 10000), new Date(System.currentTimeMillis() + 20000)});
//                                t.getShortcut().setName("预约");
//                                t.getShortcut().setLink("baidu.com");
//                                t.getShortcut().setIcon("http://static.uberx.net.cn/yangtze/2.0.0/static.uberx.net.cn/ob/images/value_prop_1-4051e03fb8.png");
//                                t.getShortcut().setTodo(i%2);
//                                SLA sla = new SLA();
//                                sla.setAlarmtime(new Date(System.currentTimeMillis() - 20000 + i * 10000));
//                                sla.setOvertime(new Date(System.currentTimeMillis() + 100000));
//                                sla.setExpecttime(new Date(System.currentTimeMillis() + 100000));
//                                t.setSla(sla);
//                                t.setAmount(10.00 + i);
//                                Customer c = new Customer();
//                                c.setAddress("client address" + i);
//                                c.setName("WINCOR中关村店");
//                                t.setCustomer(c);
//                                tickets.add(t);
//                            }
//                            list.setResult(tickets);
//                            onSuccess(list);

                            if (loaderView != null) {
                                loaderView.notifyLoadFailed(new Exception(tip));
                            }
                        }
                    });
        }
    }

    @Override
    public void onRefresh(boolean fromSwipe) {
        request.setPage(MVSConstants.ListParamConstants.page_default);
        current_page = request.getPage();
        loadData(request);
    }

    @Override
    public void onMore(int page) {
        request.setPage(page);
        loadData(request);
    }

    @Override
    public void onTicketSelected(Ticket ticket, Context context) {
        Intent intent = new Intent(ContextManager.context(), WebActivity.class);
        intent.putExtra(WebActivity.CLIENT_NAME, ticket.getCustomer().getName());
        intent.putExtra(WebActivity.TICKET_ID, ticket.getTicketid());
        intent.putExtra(WebActivity.TITLE_EXTRA, getString(R.string.ticket_detail));
    }

    @Override
    public int getActionBarRightResourceId() {
        return 0;
    }

    @Override
    public int getActionBarRight2ResourceId() {
        return 0;
    }

    @Override
    public void onActionBarRight2Click() {
    }

    @Override
    public void onActionBarRightClick() {
    }

    @Override
    public void onActionBarTitleRightClick() {

    }

    @Override
    public void onActionBarTitleLeftClick() {

    }

    @Override
    public void onTextChanged(String text) {
        request.getFilter().setKeyword(text);
        if(adapter != null) {
            adapter.filterList(text);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSearchEnter() {
        if(adapter != null) {
            loadData(request);
        }
    }

    @Override
    public void onEnterSearchMode() {
    }

    @Override
    public void onExitSearchMode() {
        request.getFilter().setKeyword(null);
        if (getActivity() != null) {
            if (((BaseActionBarMockingActivity) getActivity()).isSearching()
                    && adapter != null) {
                adapter.filterList(null);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        try {
            getActivity().unbindService(conn);
        }catch (Exception e){}
        if (conn != null && location != null) {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            latLng = new Pair<>(System.currentTimeMillis(), ll);
            if(adapter != null){
                adapter.setLatLng(ll);
                if(loaderView != null) {
                    if (loaderView.getDisplayState() != ContentLoaderView.STATE_LOADING) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    public int getItemSpace() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int space = (int) (dm.density * 36 / 10.75);
        return space;
    }

    @Override
    public void onUpdateResource(int state, ImageView image, TextView textView) {
        image.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        if(state == ContentLoaderView.STATE_EMPTY){
            if(type.equals(MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS+ "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS)) {
                textView.setText(R.string.active_nothing);
                image.setImageResource(R.drawable.icon_order_done);
                if(loaderView != null)
                    loaderView.setVisibility(View.GONE);
                FApplication.getRxBus().send(MainActivity.TICKET_DONE);
            }else if(type.equals(MVSConstants.FragmentType.FRAGMENT_GRABORDERS + "," + MVSConstants.FragmentType.FRAGMENT_RECEIVEORDERS)){
                textView.setText(R.string.nothing);
                image.setImageResource(R.drawable.order_empty);
            }else{
                textView.setText(R.string.nothing);
                image.setImageResource(R.drawable.order_empty);
            }
        }else if(state == ContentLoaderView.STATE_ERROR){
            textView.setText(R.string.network_error);
            image.setImageResource(R.drawable.error_net);
        }else if(state == ContentLoaderView.STATE_CONTENT){
            if(adapter != null && adapter.getTicketList().size() != 0) {
                if(loaderView != null){
                    if(loaderView.getVisibility() == View.GONE){
                        loaderView.setVisibility(View.VISIBLE);
                        FApplication.getRxBus().send(MainActivity.TICKET_DONE_REVERT);
                    }
                }
            }
        }
    }

    public ContentLoaderView getLoaderView() {
        return loaderView;
    }

    /**
     * 设置RecycleView之间的间距
     */

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            if (parent.getChildPosition(view) != 0)
                outRect.top = space;
        }
    }
}
