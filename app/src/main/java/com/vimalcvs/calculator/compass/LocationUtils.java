package com.vimalcvs.calculator.compass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocationUtils {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    @SuppressLint("StaticFieldLeak")
    private static volatile LocationUtils uniqueInstance;
    private final WeakReference<Context> mContext;
    private Location location;
    private LocationManager locationManager;

    private LocationUtils(Context context) {
        mContext = new WeakReference<>(context.getApplicationContext());
    }

    public static LocationUtils getInstance(Context context, LocationListener locationListener) {
        if (null == uniqueInstance) {
            synchronized (LocationUtils.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new LocationUtils(context);
                }
            }
        }
        uniqueInstance.getLocation(locationListener);
        return uniqueInstance;
    }

    public static void getAddress(Context context, Location location
            , OnAddressFetchedListener listener) {
        if (location == null || listener == null) {
            return;
        }
        EXECUTOR.execute(() -> {
            String address = "";
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (!addresses.isEmpty()) {
                    address = addresses.get(0).getAddressLine(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            listener.onAddressFetched(address);
        });
    }


    private void getLocation(LocationListener locationListener) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }
        // 需要检查权限,否则编译报错,想抽取成方法都不行,还是会报错。只能这样重复 code 了。
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //1.获取位置管理器
        if (null == locationManager) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        //2.获取位置提供器，GPS或是NetWork
        List<String> providers = locationManager.getProviders(true);
        if (providers.isEmpty()) {
            return;
        }
        String locationProvider;
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            // 如果 GPS 提供器可用，优先选择使用 GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else {
            // 如果 GPS 提供器不可用，但还有其他位置提供器可用，则选择第一个位置提供器
            locationProvider = providers.get(0);
        }

        if (locationProvider == null) {

            return;
        }

        location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            setLocation(location);
        }


        locationManager.requestLocationUpdates(locationProvider, 1000, 1, locationListener);
    }

    public Location getLocation() {
        return location;
    }

    private void setLocation(Location location) {
        this.location = location;
    }

    public interface OnAddressFetchedListener {
        void onAddressFetched(String address);
    }
}
