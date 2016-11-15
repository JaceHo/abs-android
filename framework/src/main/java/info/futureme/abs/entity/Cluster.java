package info.futureme.abs.entity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import info.futureme.abs.R;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.maputil.MapUtils;

/**
 * cluster object used in baidu map
 */
public class Cluster {

    private static final String TAG = "Cluster";
    private static final String TAG_ADD_Cluster = "AddCluster_method";
    public static final String ITEM = "item";
    public static final String ITEMS = "items";
    private Context mContext;
    private MapView mMapView;
    private Boolean isAverageCenter;
    private int mGridSize;
    private double mDistance;

    private List<ClusterMarker> mClusterMarkers;


    public Cluster(Context context, MapView mapView
            , Boolean isAverageCenter
            , int mGridSize, double mDistance) {
        this.mContext = context;
        this.mMapView = mapView;
        this.isAverageCenter = isAverageCenter;
        this.mGridSize = mGridSize;
        this.mDistance = mDistance;
        mClusterMarkers = new ArrayList<>();
    }

    public ArrayList<MarkerOptions> createCluster(List<MarkerOptions> markerList) {
        this.mClusterMarkers.clear();
        ArrayList<MarkerOptions> itemList = new ArrayList<MarkerOptions>();
        for (int i = 0; i < markerList.size(); i++) {
            addCluster(markerList.get(i));
        }
        for (int i = 0; i < mClusterMarkers.size(); i++) {
            ClusterMarker cm = mClusterMarkers.get(i);
            DLog.e(TAG, "cluster itemList.size:" + cm.getmMarkers().size());
            cm.getMarkerOptions().position(cm.getmCenter());
            setClusterDrawable(cm);
            itemList.add(cm.getMarkerOptions());
        }

        DLog.e(TAG, "itemList.size:" + itemList.size());
        return itemList;
    }

    /**
     * 添加标注点，如果第一次添加，直接新建，否则与地图上原有的点进行判断，如果距离小于mDistance,则进行聚合
     *
     * @param marker
     */

    private void addCluster(MarkerOptions marker) {
        LatLng markGeo = marker.getPosition();
        // 没有ClusterMarkers
        if (mClusterMarkers.size() == 0) {
            ClusterMarker clusterMarker = new ClusterMarker(marker.getPosition());
            clusterMarker.getMarkerOptions().icon(marker.getIcon());
            clusterMarker.getMarkerOptions().extraInfo(marker.getExtraInfo());
            clusterMarker.addMarker(marker, isAverageCenter);
            MBound bound = new MBound(markGeo.latitude, markGeo.longitude, markGeo.latitude, markGeo.longitude);
            bound = MapUtils.getExtendedBounds(mMapView, bound, mGridSize);
            clusterMarker.setmGridBounds(bound);
            mClusterMarkers.add(clusterMarker);
        } else {
            ClusterMarker clusterContain = null;
            double distance = mDistance;

            for (int i = 0; i < mClusterMarkers.size(); i++) {
                ClusterMarker clusterMarker = mClusterMarkers.get(i);
                DLog.e(TAG_ADD_Cluster, "in mClusterMarker.size  size = = " + mClusterMarkers.size());
                LatLng center = clusterMarker.getmCenter();
                double d = DistanceUtil.getDistance(center, marker.getPosition());

                //[]--------选择clusterMarker 中最近的，clusterMarker-------双重循环-----------[]
                if (d < distance) {
                    distance = d;
                    clusterContain = clusterMarker;
                } else {
					DLog.d(TAG_ADD_Cluster, "d>distence,不满足聚合距离");
                }

            }

            // 现存的clusterMarker 没有符合条件的
            if (clusterContain == null || !isMarkersInCluster(markGeo, clusterContain.getmGridBounds())) {
				DLog.e(TAG_ADD_Cluster, "======clusterContain=======================--------------");
                ClusterMarker clusterMarker = new ClusterMarker(marker.getPosition());

                clusterMarker.getMarkerOptions().icon(marker.getIcon());
                clusterMarker.addMarker(marker, isAverageCenter);
                clusterMarker.getMarkerOptions().extraInfo(marker.getExtraInfo());
                MBound bound = new MBound(markGeo.latitude, markGeo.longitude, markGeo.latitude, markGeo.longitude);
                bound = MapUtils.getExtendedBounds(mMapView, bound, mGridSize);
                clusterMarker.setmGridBounds(bound);

                mClusterMarkers.add(clusterMarker);

            } else {
                List<MarkerOptions> mMarkers = clusterContain.getmMarkers();
                List list = new ArrayList();
                for(MarkerOptions m : mMarkers){
                    list.add(m.getExtraInfo().getSerializable(ITEM));
                }
                clusterContain.addMarker(marker, isAverageCenter);
                Bundle b = new Bundle();
                b.putSerializable(ITEMS, (Serializable) list);
                clusterContain.getMarkerOptions().extraInfo(b);
                DLog.e(TAG_ADD_Cluster, "添加到选中 clusterMarker:--->clusterContain.size:---->" + clusterContain.getmMarkers().size());
            }
        }
    }

    /**
     * 设置聚合点的颜色与中间数字
     *
     * @param clusterMarker
     */
    private void setClusterDrawable(ClusterMarker clusterMarker) {

        View drawableView = LayoutInflater.from(mContext).inflate(
                R.layout.drawable_mark, null);
        drawableView.setPadding(0, 5, 0, 25);
        TextView text = (TextView) drawableView.findViewById(R.id.drawble_mark);
        text.setPadding(3, 3, 3, 18);

        int markNum = clusterMarker.getmMarkers().size();
        if (markNum >= 2) {
            text.setText(markNum + "");
            /*if (markNum < 11) {
                text.setBackgroundResource(R.drawable.icon_few_order_blue);
            } else if (markNum > 10 && markNum < 21) {
                text.setBackgroundResource(R.drawable.icon_few_order_blue);
            } else if (markNum > 20 && markNum < 31) {
                text.setBackgroundResource(R.drawable.icon_few_order_blue);
            } else if (markNum > 30 && markNum < 41) {
                text.setBackgroundResource(R.drawable.icon_few_order_blue);
            } else {
                text.setBackgroundResource(R.drawable.icon_few_order_blue);
            }*/
            List<MarkerOptions> mMarkers = clusterMarker.getmMarkers();
            List<Object> list = new ArrayList<>();
            for(MarkerOptions m : mMarkers) {
                list.add(m.getExtraInfo().getSerializable(ITEM));
            }
            Bundle b = new Bundle();
            b.putSerializable(ITEMS, (Serializable) list);
            clusterMarker.getMarkerOptions().extraInfo(b);
            clusterMarker.getMarkerOptions().icon(BitmapDescriptorFactory.fromView(drawableView));
        } else {
            clusterMarker.getMarkerOptions().icon(clusterMarker.getmMarkers().get(0).getIcon());
        }
    }

    /**
     * 判断坐标点是否在MBound 覆盖区域内
     *
     * @param markerGeo
     * @param bound
     * @return
     */
    private Boolean isMarkersInCluster(LatLng markerGeo, MBound bound) {

        DLog.e(TAG, "rightTopLat:" + bound.getRightTopLat());
        DLog.e(TAG, "rightTopLng:" + bound.getRightTopLng());
        DLog.e(TAG, "leftBottomLat:" + bound.getLeftBottomLat());
        DLog.e(TAG, "leftBottomlng:" + bound.getLeftBottomLng());

        if (markerGeo.latitude > bound.getLeftBottomLat()
                && markerGeo.latitude < bound.getRightTopLat()
                && markerGeo.longitude > bound.getLeftBottomLng()
                && markerGeo.longitude < bound.getRightTopLng()) {
            return true;
        }
        return false;

    }

}
