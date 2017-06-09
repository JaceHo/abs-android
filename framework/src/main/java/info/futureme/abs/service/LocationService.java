/*
 * Lenovo Group
 * Copyright (c) 2015-2016 All Rights Reserved.
 * Project Name: lmrp-android framework
 * Create Time: 16-2-16 下午6:51
 */

package info.futureme.abs.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import info.futureme.abs.util.DLog;

/**
 * more detail, visit <a href="$inet://http://lbsyun.baidu.com/"><font
 * color="#0000ff"><u>http://lbsyun.baidu.com/</u></font></a>
 * @author JeffreyHe
 * @version 1.0
 * @updated 26-一月-2016 15:56:07
 */
public class LocationService extends Service implements BDLocationListener {
    private LocationClient mLocationClient;
    private LocationBinder locationBinder;
    private static LocationService instance;
    private static final long FAILURE_DELAYTIME = 5000;
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static LocationSuccessListener obj = new LocationSuccessListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //do nothing since history location already saved
            DLog.w("loc receive obj:", getClass().getName());
        }
    };
    private static ConcurrentHashMap<Intent, LocationReference<LocationSuccessListener>> listenerLinkedHashMap = new ConcurrentHashMap<>();
    //地点和时间信息存起来备用, 1 分钟之内LocationService返回相同位置, 容量10
    private static final int HISTORY_LOCATIONS = 10;
    private static ConcurrentHashMap<Long, BDLocation> locations = new ConcurrentHashMap<>();
    private LocationClientOption mOption;

    public LocationService() {
        for (Intent intent : listenerLinkedHashMap.keySet()) {
            if (listenerLinkedHashMap.get(intent).get() == null) {
                listenerLinkedHashMap.remove(intent);
            }else {
                DLog.i("loc init with listeners", "init" + listenerLinkedHashMap.size() + listenerLinkedHashMap.get(intent).get().getClass().getName());
            }
        }
        //listenerLinkedHashMap.clear();
    }


    public static BDLocation getRealTimeLatLngTimeless(){
        if(locations.size() > 0) {
            Long max = Collections.max(locations.keySet());
            BDLocation bdLocation = locations.get(max);
            DLog.i("loc real no time", bdLocation.getCity());
            return bdLocation;
        }
        return new BDLocation();
    }

    public static BDLocation getRealTimeLatLng(){
        if(locations.size() > 0) {
            Long max = Collections.max(locations.keySet());
            if (System.currentTimeMillis() - max < 2*FAILURE_DELAYTIME) {
                BDLocation bdLocation = locations.get(max);
                DLog.i("loc real time", bdLocation.getCity());
                return bdLocation;
            }
        }
        return null;
    }

    public boolean onUnbind(Intent intent){
        for (Intent i: listenerLinkedHashMap.keySet()) {
            if (listenerLinkedHashMap.get(i).get() == null)
                listenerLinkedHashMap.remove(i);
            else {
                DLog.i("loc destroy listeners", "init" + listenerLinkedHashMap.size() + listenerLinkedHashMap.get(i).get().getClass().getName());
            }
        }
        DLog.i("loc unbind remove", (listenerLinkedHashMap.get(intent) == null || listenerLinkedHashMap.get(intent).get() == null)? "null":listenerLinkedHashMap.get(intent).getClass().getName());
        listenerLinkedHashMap.remove(intent);
        try {
            return super.onUnbind(intent);
        }catch (Exception e){
            return false;
        }
    }

    public static void updateLocation(BDLocation location) {
        if(locations.size() > HISTORY_LOCATIONS){
            Long min = Collections.min(locations.keySet());
            locations.remove(min);
        }
        if(location.getLatitude() != 0 && location.getLongitude() != 0)
            locations.put(System.currentTimeMillis(), location);
    }

    public static class LocationConnection implements ServiceConnection, Runnable {
        private final Intent intent;
        private LocationReference<LocationSuccessListener> listener;

        public LocationConnection(LocationSuccessListener listener, Intent intent){
            this.intent = intent;
            if(listener == null) {
                this.listener = new LocationReference<>(obj);
            }else{
                this.listener = new LocationReference<>(listener);
            }
            synchronized (LocationService.class){
                listenerLinkedHashMap.put(intent, this.listener);
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DLog.w("loc connected:", name + "location service bind success");
            //用来访问service中的数据
            if(listener == null)
                listener = new LocationReference<>(obj);
            handler.postDelayed(this, FAILURE_DELAYTIME);// 设置超过5秒还没有定位到就停止定位
            synchronized (LocationService.class){
                    listenerLinkedHashMap.put(intent, listener);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            synchronized (LocationService.class){
                DLog.i("loc remove", (listenerLinkedHashMap.get(intent) == null || listenerLinkedHashMap.get(intent).get() == null) ? "null" : listenerLinkedHashMap.get(intent).get().getClass().getName());
                listenerLinkedHashMap.remove(intent);
                DLog.i("loc listeners", listenerLinkedHashMap.size() + "");
            }
            DLog.w("loc connection:", "location bind fail");
        }

        @Override
        public void run() {
            DLog.w("loc timeout:", "getRealTimeLatLng");
            if(getRealTimeLatLng() == null){
                if(instance != null) {
                    instance.onDestroy(null);
                    instance.onCreate();
                }
            }else{
                handleLocationReceive(getRealTimeLatLng());
            }
        }
    }

    private void onDestroy(Object tmp) {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.stop();
            mLocationClient = null;
        }
    }

    public static ServiceConnection connection(final LocationSuccessListener listener, final Intent intent){
        return new LocationConnection(listener, intent);
    }


    private static void handleLocationReceive(BDLocation bdLocation){
        Set<Intent> intents = new HashSet<>();
        for (Intent in : listenerLinkedHashMap.keySet()) {
            LocationReference<LocationSuccessListener> listener = listenerLinkedHashMap.get(in);
            if (listener.get() != null && listener.get() != obj) {
                DLog.w("loc call:", listener.get().getClass().getName());
                listener.get().onReceiveLocation(bdLocation);
                intents.add(in);
            }
        }
        for(Intent i : intents){
            DLog.i("loc bind remove call", (listenerLinkedHashMap.get(i) == null || listenerLinkedHashMap.get(i).get() == null)? "null":listenerLinkedHashMap.get(i).getClass().getName());
            listenerLinkedHashMap.remove(i);
        }
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection connection, int flags){
        DLog.i("loc bind", intent + "");
        if(locations.size() > 0) {
            Long max = Collections.max(locations.keySet());
            if (System.currentTimeMillis() - max < 2000) {
                BDLocation bdLocation = locations.get(max);
                DLog.i("loc onreceive local", bdLocation.getCity());
                handleLocationReceive(bdLocation);
                return false;
            }
        }

        return super.bindService(intent, connection, flags);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return locationBinder;
    }

    public class LocationBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        locationBinder = new LocationBinder();
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(this);
        initLocation();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = this;
        return super.onStartCommand(intent, flags, startId);
    }

    private void initLocation() {

        if(mOption == null){
            mOption = new LocationClientOption();
            mOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            mOption.setScanSpan(2000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            mOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
            mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            mOption.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
            mOption.setTimeOut(2000);
            mOption.setOpenGps(true);//可选，默认false,设置是否使用gps
            mOption.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        }

        mLocationClient.setLocOption(mOption);
        mLocationClient.start();
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        DLog.w("loc receive", bdLocation.toString());
        for(LocationReference<LocationSuccessListener> listener : listenerLinkedHashMap.values()){
            DLog.i("loc listeners", listenerLinkedHashMap.size() + ((listener == null || listener.get() == null )? "null" : listener.get().getClass().getName()));
        }
        if(listenerLinkedHashMap.size() == 0){
            DLog.i("loc nothing", "shutdown");
            onDestroy();
        }
        if(locations.size() > HISTORY_LOCATIONS){
            Long min = Collections.min(locations.keySet());
            locations.remove(min);
        }
        if ((bdLocation.getLocType() == BDLocation.TypeGpsLocation || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation || bdLocation.getLocType() == BDLocation.TypeOffLineLocation)) { // 离线定位结果
            if (bdLocation.getLongitude() != 0 && bdLocation.getLatitude() != 0)
                locations.put(System.currentTimeMillis(), bdLocation);
            DLog.i("loc onreceive remote", bdLocation.getCity());
            handleLocationReceive(bdLocation);
        }else {
            handleLocationReceive(bdLocation);
            if (bdLocation.getLocType() == BDLocation.TypeServerError) {
                DLog.i("loc :", "服务端网络定位失败");
            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
                DLog.i("loc :", "网络不同导致定位失败，请检查网络是否通畅");
            } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
                DLog.i("loc :", "无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            } else {
                DLog.i("loc :", "无法获取有效定位依据导致定位失败，未知错误");
            }
        }
    }

	/**
	 * location service using baidu location
	 * @author JeffreyHe
	 * @version 1.0
	 * @updated 26-一月-2016 15:56:07
	 */
    public interface LocationSuccessListener {
        void onReceiveLocation(BDLocation location);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.stop();
            mLocationClient = null;
        }
        DLog.i("loc destroy", "listeners");
        handleLocationReceive(null);
        instance = null;
    }


    public static class LocationReference<T> extends WeakReference<T>{
        private T strongRef;
        public LocationReference(T r, ReferenceQueue<? super T> q) {
            super(r, q);
            tryToRef(r);
        }

        public LocationReference(T r) {
            super(r);
            tryToRef(r);
        }

        private void tryToRef(T r){
            Class superClass = r.getClass().getSuperclass();
            if(superClass == null || superClass.equals(Object.class)){
                Class[] interfaces = r.getClass().getInterfaces();
                if(interfaces != null && interfaces.length == 1){
                    if(interfaces[0].equals(LocationSuccessListener.class)){
                        Log.e("strong ref", r.getClass().getName());
                        strongRef = r;
                    }
                }
            }
        }

        public T get() {
            return strongRef == null ? super.get() : strongRef;
        }
    }
}
