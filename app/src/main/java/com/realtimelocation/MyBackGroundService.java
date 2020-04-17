package com.realtimelocation;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class MyBackGroundService extends Service {

    private static MyBackGroundService sMyService;
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        sMyService = this;
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<ApplicationInfo> appList = printCurrentTask(getInstalledApplication());
                StringBuilder stringBuilder = new StringBuilder();
                for (ApplicationInfo obj : appList) {
                    stringBuilder.append(obj.name + "\n");
                }
                Intent intent = new Intent(MyActivity.CURRENT_RUNNING_APP_NAME);
                intent.putExtra("AppNames", stringBuilder.toString());
                sendBroadcast(intent);
                handler.postDelayed(this, 1000);
            }
        }, 1000);

    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    private List<ApplicationInfo> printCurrentTask(List<ApplicationInfo> applicationLists) {
        ArrayList<ApplicationInfo> currentRunningApp = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService("usagestats");
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, time - (1000 * 120
            ), time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (ApplicationInfo appInfo : applicationLists) {
                    for (UsageStats usageStats : appList) {
                        if (appInfo.packageName.equals(usageStats.getPackageName())) {
                            mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                            currentRunningApp.add(appInfo);
                        }
                    }
                }
            }
        }
        return currentRunningApp;
    }

    private List<ApplicationInfo> getInstalledApplication() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationInfo> sortedAppsInfo = new ArrayList<>();

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);

                String[] requestedPermissions = packageInfo.requestedPermissions;
                int[] requestedPermissionsFlags = packageInfo.requestedPermissionsFlags;
                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i].equals("android.permission.ACCESS_FINE_LOCATION") && requestedPermissionsFlags[i] > 1) {
                            sortedAppsInfo.add(applicationInfo);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sortedAppsInfo;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(null);
    }
}