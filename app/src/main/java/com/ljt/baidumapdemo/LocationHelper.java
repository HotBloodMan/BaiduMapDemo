package com.ljt.baidumapdemo;

import android.content.Context;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 定位辅助类。
 * 对百度定位的封装。
 *
 */

public class LocationHelper {

    final BDLocationService locationService;
    final LocationClientOption option;

//    public LocationHelper(Context context){
//        this(new Builder(context));
//    }

    private LocationHelper(Builder builder){
        this.locationService = builder.mLocationService;
        this.option = builder.mOption;
    }

    public boolean registerLocationListener(LocationListener listener){
        return locationService.registerLocationListener(listener);
    }
    public void unRegisterLocationListener(LocationListener listener){
        locationService.unRegisterLocationListener(listener);
    }


    public void start(){
        locationService.start();
    }
    public void stop(){
        locationService.stop();
    }

    public static final class Builder{
        public static final int LOCATION_MODE_HIGH_ACCURACY = 1;
        public static final int LOCATION_MODE_BATTERY_SAVING = 2;
        public static final int LOCATION_MODE_DEVICE_SENSORS = 3;

        LocationClientOption mOption;
        BDLocationService mLocationService;

        public Builder(Context context) {
            this.mLocationService=BDLocationService.getSingleton(context);
            this.mOption=mLocationService.getDefaultLocationClientOption();
        }

        public LocationHelper build(){
            return new LocationHelper(this);
        }

        /**
         * 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
         * @param milliseconds
         */
        public Builder setScanSpan(int milliseconds){
            mOption.setScanSpan(milliseconds);
            return this;
        }
        /**
         * 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
         * @param isNeed
         */
        public Builder setIsNeedLocationDescribe(boolean isNeed){
            mOption.setIsNeedLocationDescribe(isNeed);
            return this;
        }
    }
     public static abstract class  LocationListener implements BDLocationListener{

         @Override
         public void onReceiveLocation(BDLocation bdLocation) {
             if(null !=bdLocation && bdLocation.getLocType() !=BDLocation.TypeServerError){
                 LocationEntity location = new LocationEntity(bdLocation);
                 onReceiveLocation(location);
                 String errorMsg=null;
                 if(bdLocation.getLocType()==BDLocation.TypeServerError){
                     errorMsg="";
                 }
             }
         }
         public abstract  void onReceiveLocation(LocationEntity location);
         public void onError(Throwable e){
         }

     }




    public static class LocationEntity{
        BDLocation bdLocation;

        public LocationEntity(BDLocation bdLocation){
            this.bdLocation=bdLocation;
        }
        public String getTime(){
            return this.bdLocation.getTime();
        }

        @Override
        public String toString() {
            if(null !=bdLocation && bdLocation.getLocType() !=BDLocation.TypeServerError){
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");

                sb.append(bdLocation.getTime());
                sb.append("\nlocType : ");
                sb.append(bdLocation.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(bdLocation.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(bdLocation.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(bdLocation.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(bdLocation.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(bdLocation.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(bdLocation.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(bdLocation.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(bdLocation.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(bdLocation.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(bdLocation.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(bdLocation.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(bdLocation.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(bdLocation.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(bdLocation.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (bdLocation.getPoiList() != null && !bdLocation.getPoiList().isEmpty()) {
                    for (int i = 0; i < bdLocation.getPoiList().size(); i++) {
                        Poi poi = (Poi) bdLocation.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }

                if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(bdLocation.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(bdLocation.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(bdLocation.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(bdLocation.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (bdLocation.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(bdLocation.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(bdLocation.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (bdLocation.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                return sb.toString();
            }
            return "";
        }
    }


}
