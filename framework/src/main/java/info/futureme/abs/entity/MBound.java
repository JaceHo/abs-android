package info.futureme.abs.entity;

import com.baidu.mapapi.model.LatLng;

/**
 * mound entity to hold an map area
 */
public class MBound {

    private double rightTopLat;
    private double rightTopLng;
    private double leftBottomLat;
    private double leftBottomLng;


    private LatLng rightTop;
    private LatLng leftBottom;


    public MBound(LatLng rightTop, LatLng leftBottom) {
        super();
        this.rightTop = rightTop;
        this.leftBottom = leftBottom;
        rightTopLat = rightTop.latitude;
        rightTopLng = rightTop.longitude;
        leftBottomLat = leftBottom.latitude;
        leftBottomLng = leftBottom.longitude;

    }

    public MBound(double rightTopLat, double rightTopLng, double leftBottomLat, double leftBottomLng) {
        this.rightTopLat = rightTopLat;
        this.rightTopLng = rightTopLng;
        this.leftBottomLat = leftBottomLat;
        this.leftBottomLng = leftBottomLng;
        rightTop = new LatLng(rightTopLat, rightTopLng);
        leftBottom = new LatLng(leftBottomLat, leftBottomLng);


    }

    public double getRightTopLat() {
        return rightTopLat;
    }

    public void setRightTopLat(int rightTopLat) {
        this.rightTopLat = rightTopLat;
    }

    public double getRightTopLng() {
        return rightTopLng;
    }

    public void setRightTopLng(int rightTopLng) {
        this.rightTopLng = rightTopLng;
    }

    public double getLeftBottomLat() {
        return leftBottomLat;
    }

    public void setLeftBottomLat(int leftBottomLat) {
        this.leftBottomLat = leftBottomLat;
    }

    public double getLeftBottomLng() {
        return leftBottomLng;
    }

    public void setLeftBottomLng(int leftBottomLng) {
        this.leftBottomLng = leftBottomLng;
    }

    public LatLng getRightTop() {
        return rightTop;
    }

    public void setRightTop(LatLng rightTop) {
        this.rightTop = rightTop;
    }

    public LatLng getLeftBottom() {
        return leftBottom;
    }

    public void setLeftBottom(LatLng leftBottom) {
        this.leftBottom = leftBottom;
    }

}

