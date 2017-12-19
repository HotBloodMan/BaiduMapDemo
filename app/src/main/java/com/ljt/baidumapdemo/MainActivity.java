package com.ljt.baidumapdemo;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    public static String TAG=MainActivity .class.getSimpleName();
    LocationHelper locateHelper;
    TextView textView;
    private LocationHelper.LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView = (TextView) findViewById(R.id.text);
        locateHelper=new LocationHelper.Builder(getApplicationContext())
                .setScanSpan(0)
                .setIsNeedLocationDescribe(true).build();

        mLocationListener=new LocationHelper.LocationListener() {
            @Override
            public void onReceiveLocation(LocationHelper.LocationEntity location) {
                System.out.println("location= "+location.toString());
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        };
        locateHelper.registerLocationListener(mLocationListener);

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        Log.d(TAG,TAG+" ----->>>call ");
                        if (aBoolean) {
                            locateHelper.start();
                        } else {
                            Log.e("MainActivity", "watchLocation request locate permission denied ");
                        }
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,TAG+" ----->>>onStop ");
        locateHelper.unRegisterLocationListener(mLocationListener);
        locateHelper.stop();
    }
}
