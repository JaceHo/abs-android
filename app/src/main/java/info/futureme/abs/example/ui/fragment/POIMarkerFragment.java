/*
 * 
 * Copyright (c) 2015-2016 All Rights Reserved.
 * Project Name: lmrp-android app
 * Create Time: 16-2-16 下午6:44
 */

package info.futureme.abs.example.ui.fragment;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.platform.comjni.map.basemap.BaseMapCallback;
import com.baidu.platform.comjni.map.basemap.b;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.OnClick;
import info.futureme.abs.FApplication;
import info.futureme.abs.base.ActionBarFragment;
import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.entity.Cluster;
import info.futureme.abs.entity.Result;
import info.futureme.abs.example.R;
import info.futureme.abs.example.conf.BizErrorCode;
import info.futureme.abs.example.conf.ErrorCode;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.example.entity.PositionLatLng;
import info.futureme.abs.example.entity.Ticket;
import info.futureme.abs.example.entity.TicketRequest;
import info.futureme.abs.example.rest.ServiceGenerator;
import info.futureme.abs.example.rest.TicketAPI;
import info.futureme.abs.example.ui.MainActivity;
import info.futureme.abs.example.ui.WebActivity;
import info.futureme.abs.example.ui.adapter.TicketAdapter;
import info.futureme.abs.example.util.Utils;
import info.futureme.abs.example.widget.swipe.util.Attributes;
import info.futureme.abs.rest.NetworkObserver;
import info.futureme.abs.service.LocationService;
import info.futureme.abs.ui.BridgedX5WebViewFragment;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.ReflectHelper;
import info.futureme.abs.util.WindowUtils;
import info.futureme.abs.util.maputil.OverlayManager;
import info.futureme.abs.view.overscroll.OverScrollDecoratorHelper;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class POIMarkerFragment extends ActionBarFragment implements BaiduMap.OnMapStatusChangeListener, BaiduMap.OnMarkerClickListener, TicketAdapter.OnTicketSelectedListener, LocationService.LocationSuccessListener {
    //常量声明
    public static final int mMaxZoom = 12;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static int zoomLevel[] = {2000000, 1000000, 500000, 200000, 100000,
            50000, 25000, 20000, 10000, 5000, 2000, 1000, 500, 100, 50, 20, 0};
    public static double mDistance = 600000;
    //use average center by default
    public static boolean isAverageCenter = false;
    public static Integer mGridSize = 60;
    //控件绑定
    @Bind(R.id.layout_orderInfo)
    RecyclerView recyclerView;
    @Bind(R.id.layout_orderInfo_container)
    LinearLayout orderInfoViewContainer;
    @Bind(R.id.mPoiMarkerMapView)
    MapView mMapView;
    @Bind(R.id.image_guideWay)
    ImageView image_mapMode;
    //全局变量
    private BaiduMap mBaidumap = null;//地图实例
    private MyLocationConfiguration.LocationMode mCurrentMode;//地图模式
    private BitmapDescriptor mCurrentMarker;//当前使用定位标注
    private BitmapDescriptor landMark;
    private BitmapDescriptor landMarkGray;
    private BitmapDescriptor mark_selected;
    private BitmapDescriptor mark_normal;
    private TicketAPI ticketAPI;//数据接口
    private TicketRequest ticketListRequest;
    private List<Ticket> mOrders = new ArrayList<>();
    //获取到的订单列表
    private Subscription _subscription;
    private Scheduler.Worker worker;
    private Set<MarkerOptions> mMarkeroptions = new HashSet<>();//标注数据集
    private Cluster mCluster;
    private Animation orderInfoAnim_in;
    private Animation orderInfoAnim_out;
    private int left;//屏幕左边界
    private int top;//屏幕上边界
    private int right;//屏幕右边界
    private int bottom;//屏幕下边界
    private Point p1, p2, p3, p4;//屏幕四个脚标点
    private LatLng llg1, llg2, llg3, llg4;//与屏幕相对应的四个脚标地理坐标
    private boolean isMearured = false;
    private Marker selected;
    private PositionLatLng rectangle1;
    private PositionLatLng rectangle2;
    private PositionLatLng rectangle3;
    private PositionLatLng rectangle4;
    private boolean inited = false;
    private BDLocation bdLocation;
    private String status;
    private int justonce = 1;
    private List<Overlay> overlays;
    private ServiceConnection connection;
    private TicketAdapter adapter;

    public POIMarkerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getContext().getApplicationContext());
        ticketListRequest = new TicketRequest();
        ticketListRequest.setLimit(10000);
        ticketListRequest.setPage(MVSConstants.ListParamConstants.page_default);

        rectangle1 = new PositionLatLng();
        rectangle2 = new PositionLatLng();
        rectangle3 = new PositionLatLng();
        rectangle4 = new PositionLatLng();
        FApplication.getRxBus().toObserverable()
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (TicketListFragment.REFRESH_LSIT_RECEIVE.equals(o) && (MVSConstants.FragmentType.FRAGMENT_GRABORDERS+ "," + MVSConstants.FragmentType.FRAGMENT_RECEIVEORDERS).equals(status)
                            || (MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS + "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS).equals(status) && TicketListFragment.REFRESH_LSIT_ACTIVE.equals(o)
                                || o.equals(TicketListFragment.RECEIVE_SUCCESS)
                                ||
                                o.equals(TicketListFragment.SIGNIN_SUCCESS)
                                ){
                            loadData(bdLocation);
                        }
                    }
                });
    }

    public void onResume() {
        super.onResume();
        mMapView.onResume();
//        if (selected != null) {
//            onMarkerClick(selected);
//        }
    }

    /**
     * 获取屏幕宽高
     */
    public void getWidthAndHeight() {
        final DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = getActivity().getWindowManager();
        windowManager.getDefaultDisplay().getMetrics(dm);
        mMapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!isMearured && mMapView != null) {
                    left = mMapView.getLeft();
                    top = dm.heightPixels - mMapView.getBottom();
                    right = mMapView.getRight();
                    bottom = mMapView.getBottom();
                    DLog.w("point", "left = " + left + " top = " + top + " right = " + right + " bottom = " + bottom);
                    p1 = new Point(left, top);
                    p2 = new Point(right, top);
                    p3 = new Point(right, bottom);
                    p4 = new Point(left, bottom);
                }
                //remove this!!
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mMapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mMapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        status = getArguments().getString(MVSConstants.FragmentType.FRAGMENT_ORDERTYPE);
        ticketListRequest.getFilter().setStatus(status);
        adapter =  new TicketAdapter(status, Schedulers.newThread().createWorker(), this);
        adapter.setMode(Attributes.Mode.Single);
        adapter.setOnOrderSelectedListener(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setBackgroundColor(getResources().getColor(R.color.select_drawable_bg));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        if (Utils.hasIceCreamSandwich()) {
            OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                adapter.closeAllItems();
            }
        });
        //mMapView.showScaleControl(false);
        mMapView.showZoomControls(false);
        initData();
        getWidthAndHeight();
        return view;
    }

    /**
     * 初始化数据
     */
    public void initData() {
        mBaidumap = mMapView.getMap();
        mBaidumap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaidumap.setMyLocationEnabled(true);
        //地图模式
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mCurrentMarker = null;
        //mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.main_icon_follow);
        //mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_mylocation);
        //landMark = BitmapDescriptorFactory.fromResource(R.drawable.icon_landmark);
        //landMarkGray = BitmapDescriptorFactory.fromResource(R.drawable.icon_landmarkgray);
        mark_normal = BitmapDescriptorFactory.fromResource(R.drawable.ticket_map_blue_sec);

        //mark_normal = BitmapDescriptorFactory.fromResource(R.drawable.back_bg);
        mark_selected = BitmapDescriptorFactory.fromResource(R.drawable.ticket_map_red_sec);
        mBaidumap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
        mBaidumap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                /*if(selected != null)
                    selected.setIcon(mark_normal);*/
                if (orderInfoViewContainer.getVisibility() == View.VISIBLE) {
                    orderInfoViewContainer.setVisibility(View.GONE);
                    orderInfoViewContainer.startAnimation(orderInfoAnim_out);
                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        //缩放地图状态监听
        mBaidumap.setOnMapStatusChangeListener(this);
        //标注点击监听
        mBaidumap.setOnMarkerClickListener(this);
        worker = Schedulers.newThread().createWorker();
        final Intent intent = new Intent(ContextManager.context(), LocationService.class);
        mBaidumap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //30s update location
                worker.schedulePeriodically(new Action0() {
                    @Override
                    public void call() {
                        connection = LocationService.connection(POIMarkerFragment.this, intent);
                        getActivity().bindService(intent, connection, Service.BIND_AUTO_CREATE);
                    }
                }, 0, 30, TimeUnit.SECONDS);
                mBaidumap.setOnMapLoadedCallback(null);
            }
        });

        mCluster = new Cluster(getContext().getApplicationContext(), mMapView, isAverageCenter, mGridSize, mDistance);
        //数据请求相关初始化
        ticketAPI = ServiceGenerator.createService(TicketAPI.class);
        //信息面板动画
        orderInfoAnim_in = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.push_bottom_in);
        orderInfoAnim_out = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.push_bottom_out);
    }


    @Override
    public int provideContentRes() {
        return R.layout.fragment_quick_order;
    }

    @Override
    protected void onFragmentInVisible(Bundle savedInstanceState) {

    }

    @Override
    protected void onFragmentVisible(Bundle savedInstanceState) {

    }

    public void onReceiveLocation(BDLocation bdLocation) {
        if(connection != null){
            try {
                getActivity().unbindService(connection);
                connection = null;
            }catch (Exception e){}
        }
        if(bdLocation != null) {
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            // 设置定位数据
            mBaidumap.setMyLocationData(locData);
        }
        if(justonce == 1) {//仅更新一次数据
            //根据当前位置初始化请求参数
            MapStatus.Builder builder = new MapStatus.Builder();
            if (bdLocation != null) {
                builder.target(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
                LatLngBounds.Builder builder1 = new LatLngBounds.Builder();
                builder1.include(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(builder1.build());
                mBaidumap.setMapStatus(u);
            }

            LatLng pp = builder.build().target;
            BDLocation ll = new BDLocation();
            ll.setLatitude(pp == null ? 0 : pp.latitude);
            ll.setLongitude(pp == null ? 0 : pp.longitude);
            justonce = -1;
            loadData(new BDLocation(ll));
        }
        //onMapStatusChangeFinish(builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (_subscription != null && !_subscription.isUnsubscribed()) {
            _subscription.unsubscribe();
        }
        if(worker != null)
            worker.unsubscribe();
        if(connection != null){
            try {
                getActivity().unbindService(connection);
                connection = null;
            }catch (Exception e){}
        }
        //标注点击监听
        mBaidumap.setOnMapLoadedCallback(null);
        //缩放地图状态监听
        mBaidumap.setOnMapStatusChangeListener(null);
        //标注点击监听
        mBaidumap.setOnMarkerClickListener(null);
        mBaidumap.clear();
        if (mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }
        LongSparseArray<b> sparseArray =
                ReflectHelper.getFieldValue(new BaseMapCallback(), "a");
        DLog.i("sparsearray", "" + sparseArray);
        if(sparseArray != null){
            Object[] objects = ReflectHelper.getFieldValue(sparseArray, "mValues");
            DLog.i("gc", "" + Arrays.deepToString(objects));
            DLog.i("fix callback", "clearing");
            if (objects != null) {
                for(int i = 0; i< objects.length; i++){
                    objects[i] = null;
                }
            }
        }
        com.baidu.platform.comjni.engine.a.a();
        mCluster = null;
    }

    /**
     * 获取订单信息
     */
    public void loadData(final BDLocation bdLocation) {
        this.bdLocation = bdLocation;
        if(_subscription != null)
            _subscription.unsubscribe();
        _subscription = ticketAPI.tickets(ticketListRequest)
            .subscribeOn(Schedulers.io())
            .compose(this.<Result<ArrayList<Ticket>>>bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
                .subscribe(new NetworkObserver<Result<ArrayList<Ticket>>>() {
                @Override
                public void onSuccess(Result<ArrayList<Ticket>> arrayListResult) {
                    if (arrayListResult.getEcode() != 0) {
                        ErrorCode bizErrorCode = BizErrorCode.errorMap.get(arrayListResult.getEcode());
                        if (bizErrorCode != null) {
                            onFailure(bizErrorCode.getReason());
                        } else {
                            if (arrayListResult.getReason() != null && !TextUtils.isEmpty(arrayListResult.getReason()))
                                onFailure(arrayListResult.getReason());
                            else
                                onFailure(getString(R.string.unknown_error));
                        }
                        return;
                    }

                    if (arrayListResult.getResult() != null) {//数据成功获取
                        mOrders.clear();
                        mOrders.addAll(arrayListResult.getResult());
                        mMarkeroptions.clear();
                        if (overlays != null){
                            for(Overlay overlay : overlays){
                                overlay.remove();
                            }
                            overlays = null;
                        }
                        for (Ticket order : mOrders) {
                            LatLng latlng = new LatLng(order.getCustomer().getLatitude(), order.getCustomer().getLongitude());
                            MarkerOptions option = new MarkerOptions()
                                    .position(latlng)
                                    .icon(mark_normal);

                            Bundle b = new Bundle();
                            b.putSerializable(Cluster.ITEM, order);
                            option.extraInfo(b);
                            mMarkeroptions.add(option);
                        }
                        refreshMarks();
                    } else {
                        mOrders.clear();
                        mMarkeroptions.clear();
                        refreshMarks();
                        //generateMarkers();
                        //Toast.makeText(getContext(), R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(String tip) {
                    if(getActivity() == null || getContext() == null) return;
                    Toast.makeText(POIMarkerFragment.this.getContext(), tip, Toast.LENGTH_SHORT).show();
                    if(adapter != null)
                        adapter.notifyDatasetChanged();
                    /*
                    Result<ArrayList<Ticket>> res = new Result<ArrayList<Ticket>>();
                    res.setEcode(0);
                    ArrayList<Ticket> list = new ArrayList<Ticket>();
                    for(int i = 0; i<4;i++){
                        Ticket t = new Ticket();
                        Customer c = new Customer();
                        c.setLatitude(ticketListRequest.getFilter().getRectangle().get(i).getLatitude() - 0.2);
                        c.setLongitude(ticketListRequest.getFilter().getRectangle().get(i).getLongitude() - 0.2);
                        t.setCustomer(c);
                        t.setCtime(new Date());
                        TicketStatus s = new TicketStatus();
                        s.setCurrstate("curr");
                        s.setPrevstate("prev");
                        if(MVSConstants.FragmentType.FRAGMENT_GRABORDERS.equals(status)) {
                            s.setState(i % 2);
                        }else {
                            s.setState(2);
                        }
                        t.setStatus(s);
                        list.add(t);
                    }
                    res.setResult(list);
                    onSuccess(res);
                    DLog.toast("fake success!" + tip);
                    */
                }
            });
    }

    private void notifyDataSetchanged(){
        if(adapter != null) {
            List<Ticket> tickets = adapter.getTicketList();
            if (tickets.size() == 0) {
                if (orderInfoViewContainer.getVisibility() == View.VISIBLE) {
                    orderInfoViewContainer.setVisibility(View.GONE);
                    orderInfoViewContainer.startAnimation(orderInfoAnim_out);
                }
            } else {
                int item = 0, showCount = 0, header = 0, body = 0;
                for(int i=0; i< tickets.size(); i++){
                    if(!tickets.get(i).isHeader){
                        body++;
                        if(++item == 2){
                            showCount = i+1;
                            break;
                        }
                    }else{
                        header++;
                    }
                }
                if(item != 2) {
                    showCount = 2;
                }

                int height = 0;
                //receive 130,7dp
                //active  135,7dp
                if((MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS + "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS).equalsIgnoreCase(status)){
                    height = WindowUtils.dp2px(155*body + 25*header);
                }else{
                    height = WindowUtils.dp2px(165*body + 25*header);
                }
                recyclerView.getLayoutParams().height = height;
                adapter.notifyDatasetChanged();
            }
        }
    }

    /**
     * 绘制订单标注方法
     */
    public List<Overlay> refreshOrderMarkers(List<MarkerOptions> optionses) {
        List<Overlay> overlays = new ArrayList<>();
        if (mBaidumap != null) {
            for (MarkerOptions option : optionses) {
                Overlay overlay = mBaidumap.addOverlay(option);
                overlay.setExtraInfo(option.getExtraInfo());
                overlays.add(overlay);
            }
        }
        if (mMapView != null)
            mMapView.refreshDrawableState();
        return overlays;
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {
    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        Projection projection = mBaidumap.getProjection();
        llg1 = projection.fromScreenLocation(p1);
        llg2 = projection.fromScreenLocation(p2);
        llg3 = projection.fromScreenLocation(p3);
        llg4 = projection.fromScreenLocation(p4);

//        if(!inited) {
//            inited = true;
//            //查表法得到zoomlevel
//            int i;
//            for(i=0;i<17;i++){
//                if(zoomLevel[i]<= MVSConstants.MIN_RADIS_AREA){
//                    break;
//                }
//            }
//            float zoom = i+8;
//            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(mapStatus.target, zoom);
//            mBaidumap.setMapStatus(u);
//        }

        DLog.i(getClass().getName(), "llg1=lat:" + llg1.latitude + "long=" + llg1.longitude +
                "---llg2=lat:" + llg2.latitude + "long=" + llg2.longitude +
                "---llg3=lat:" + llg3.latitude + "long=" + llg3.longitude +
                "---llg4=lat:" + llg4.latitude + "long=" + llg4.longitude);
        rectangle1.setLatitude(llg1.latitude);
        rectangle1.setLongitude(llg1.longitude);
        rectangle2.setLatitude(llg2.latitude);
        rectangle2.setLongitude(llg2.longitude);
        rectangle3.setLatitude(llg3.latitude);
        rectangle3.setLongitude(llg3.longitude);
        rectangle4.setLatitude(llg4.latitude);
        rectangle4.setLongitude(llg4.longitude);
        ticketListRequest.getFilter().getRectangle().clear();
    }

    //patch ticket list
    private void refreshOverlayTickets(){
        if(adapter != null) {
            List<Ticket> tickets = adapter.getTicketList();
            for(Ticket ticket : tickets){
                if(ticket.isHeader){
                    tickets.remove(ticket);
                }
            }
            for (int i = 0; i < tickets.size(); i++) {
                boolean found = false;
                for (Ticket ticket : mOrders) {
                    if (ticket.getTicketid().equals(tickets.get(i).getTicketid())) {
                        found = true;
                        tickets.set(i, ticket);
                    }
                }
                if(!found){
                    tickets.remove(tickets.get(i));
                }
            }
            List<Ticket> list = new ArrayList<>();
            list.addAll(adapter.getTicketList());
            adapter.setListData(list);
            notifyDataSetchanged();
        }
    }

    //标注点击事件
    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker != selected && selected != null)
            selected.setIcon(mark_normal);
        BitmapDescriptor descriptor = marker.getIcon();
        List<Ticket> list1 = new ArrayList<>();
        if (//descriptor.hashCode() == landMark.hashCode() || descriptor.hashCode() == landMarkGray.hashCode() ||
                descriptor.hashCode() == mark_selected.hashCode() || descriptor.hashCode() == mark_normal.hashCode()) {

            if (orderInfoViewContainer.getVisibility() == View.VISIBLE) {
                orderInfoViewContainer.setVisibility(View.GONE);
                orderInfoViewContainer.startAnimation(orderInfoAnim_out);
                if(marker.getIcon().hashCode() == mark_selected.hashCode()){
                    marker.setIcon(mark_normal);
                }
            }
            Ticket order = (Ticket) marker.getExtraInfo().getSerializable(Cluster.ITEM);
            list1.add(order);
            if(marker.getIcon().hashCode() == mark_normal.hashCode()){
                marker.setIcon(mark_selected);
            }
            selected = marker;
        } else {
            list1.addAll((List<Ticket>) marker.getExtraInfo().getSerializable(Cluster.ITEMS));
            if (mBaidumap.getMapStatus().zoom < mBaidumap.getMaxZoomLevel()) {
                float newZoom = mBaidumap.getMapStatus().zoom + 2.0f;
                mBaidumap.setMapStatus(MapStatusUpdateFactory.zoomTo(newZoom));
            }
        }

        recyclerView.setAdapter(adapter);
        if(list1 != null) {
            DLog.i("tickets before", Arrays.deepToString(list1.toArray()));
            ((TicketAdapter) recyclerView.getAdapter()).setListData(list1);
        }
        DLog.i("tickets after", Arrays.deepToString(((TicketAdapter) recyclerView.getAdapter()).getTicketList().toArray()));

        notifyDataSetchanged();

        orderInfoViewContainer.setVisibility(View.VISIBLE);
        orderInfoViewContainer.startAnimation(orderInfoAnim_in);

        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(marker.getPosition());
        MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(builder.build());
        mBaidumap.setMapStatus(u);
        return false;
    }

    @OnClick(R.id.flow_down)
    public void onClickFlowDown(){
        if (orderInfoViewContainer.getVisibility() == View.VISIBLE) {
            orderInfoViewContainer.setVisibility(View.GONE);
            orderInfoViewContainer.startAnimation(orderInfoAnim_out);
        }
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
        if (orderInfoViewContainer.getVisibility() == View.VISIBLE) {
            orderInfoViewContainer.setVisibility(View.GONE);
            orderInfoViewContainer.startAnimation(orderInfoAnim_out);
        }
        status = MVSConstants.FragmentType.FRAGMENT_GRABORDERS + "," + MVSConstants.FragmentType.FRAGMENT_RECEIVEORDERS;
        adapter.setStatus(status);
        ticketListRequest.getFilter().setStatus(status);
        getArguments().putString(MVSConstants.FragmentType.FRAGMENT_ORDERTYPE, status);
        justonce = -1;
        loadData(bdLocation);
    }

    @Override
    public void onActionBarTitleLeftClick() {
        if (orderInfoViewContainer.getVisibility() == View.VISIBLE) {
            orderInfoViewContainer.setVisibility(View.GONE);
            orderInfoViewContainer.startAnimation(orderInfoAnim_out);
        }
        status =  MVSConstants.FragmentType.FRAGMENT_ACTIVORDERS + "," + MVSConstants.FragmentType.FRAGMENT_FINISH_ORDERS;
        adapter.setStatus(status);
        getArguments().putString(MVSConstants.FragmentType.FRAGMENT_ORDERTYPE, status);
        ticketListRequest.getFilter().setStatus(status);
        justonce = -1;
        loadData(bdLocation);
    }

    /**
     * 地图模式切换
     */
    @OnClick(R.id.image_guideWay)
    public void changeMapMode(View view) {
        if (mCurrentMode.equals(MyLocationConfiguration.LocationMode.NORMAL)) {
            image_mapMode.setImageResource(R.drawable.icon_guidenormal);
            mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
            mBaidumap
                    .setMyLocationConfigeration(new MyLocationConfiguration(
                            mCurrentMode, true, mCurrentMarker));

        } else if (mCurrentMode.equals(MyLocationConfiguration.LocationMode.FOLLOWING)) {
            image_mapMode.setImageResource(R.drawable.icon_guidefollow);
            mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
            mBaidumap
                    .setMyLocationConfigeration(new MyLocationConfiguration(
                            mCurrentMode, true, mCurrentMarker));

        }
    }

    /**
     * @param contentRelative
     * @param v
     */

    private void setImgViewParams(View contentRelative, double v) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        contentRelative.measure(w, h);
        int width = contentRelative.getMeasuredWidth();
        int height = contentRelative.getMeasuredHeight();
        ViewGroup.LayoutParams params = contentRelative.getLayoutParams();
        params.width = (int) (width * v);
        params.height = (int) (height * v);
        contentRelative.setLayoutParams(params);

    }

    /**
     * 如果当前地图缩放程度超过设置的最大值，则不改变点坐标，否则重新进行聚合运算
     */
    private void refreshMarks() {
        if(adapter != null)
            adapter.notifyDatasetChanged();
        if (overlays != null){
            for(Overlay overlay : overlays){
                overlay.remove();
            }
            overlays = null;
        }
        if (mBaidumap.getMapStatus().zoom >= mMaxZoom) {
            //refreshOrderMarkers(refreshVersionClusterMarker(mMarkeroptions));
            ArrayList<MarkerOptions> clusters = mCluster.createCluster(refreshVersionClusterMarker(mMarkeroptions));
            overlays = refreshOrderMarkers(clusters);
        } else {
            ArrayList<MarkerOptions> clusters = mCluster.createCluster(refreshVersionClusterMarker(mMarkeroptions));
            overlays = refreshOrderMarkers(clusters);
        }
        if(justonce == -1) {
            justonce = -2;
            OverlayManager.zoomToSpan(mBaidumap, overlays, mBaidumap == null ? null : mBaidumap.getLocationData());
        }else{
            refreshOverlayTickets();
        }
    }

    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    private ArrayList<MarkerOptions> refreshVersionClusterMarker(Set<MarkerOptions> list) {
        MapStatus mapStatus = mBaidumap.getMapStatus();
        ArrayList<MarkerOptions> result = new ArrayList<MarkerOptions>();
        result.addAll(list);
        return result;
    }

    @Override
    public boolean onBackPressed(){
        if (orderInfoViewContainer != null && orderInfoViewContainer.getVisibility() == View.VISIBLE) {
            orderInfoViewContainer.setVisibility(View.GONE);
            orderInfoViewContainer.startAnimation(orderInfoAnim_out);
            return true;
        }
        return false;
    }

    @Override
    public void onTicketSelected(Ticket ticket, Context context) {
        Intent intent = new Intent(ContextManager.context(), WebActivity.class);
        intent.putExtra(WebActivity.TICKET_ID, ticket.getTicketid());
        intent.putExtra(WebActivity.CLIENT_NAME, ticket.getCustomer().getName());
        if(ticket.getStatus().getState() == 0){
            startActivityForResult(intent, status.hashCode());
        }else if(ticket.getStatus().getState() == 1){
            startActivityForResult(intent, status.hashCode());
        }else {
            intent.putExtra(BridgedX5WebViewFragment.IS_DETAIL, true);
            startActivityForResult(intent, status.hashCode());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == status.hashCode()) {
                FApplication.getRxBus().send(TicketListFragment.REFRESH_LSIT_RECEIVE);
                FApplication.getRxBus().send(TicketListFragment.REFRESH_LSIT_ACTIVE);
                FApplication.getRxBus().send(MainActivity.REFRESH_TICKETS_NUMBER_ONLY);
            }
        }
    }
}
