//package com.realtimelocation;
//
//import android.app.Activity;
//import android.app.AppOpsManager;
//import android.app.usage.UsageStatsManager;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.UserHandle;
//import android.os.UserManager;
//import android.provider.Settings;
//
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//public class TestActivity extends AppCompatActivity {
//
//
//    public List<Request> getAppList(Context mContext) {
//        AppOpsManager aoManager =
//                (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
//        List<AppOpsManager.PackageOps> appOps = aoManager.getPackagesForOps(LOCATION_OPS);
//
//        final int appOpsCount = appOps != null ? appOps.size() : 0;
//
//        ArrayList<Request> requests = new ArrayList(popCount);
//        final long now = System.currentTimeMillis();
//        final UserManager um = (UserManager) mContext.getSystemService(Context.USER_SERVICE);
//        final List<UserHandle> profiles = um.getUserProfiles();
//
//        for (int i = 0; i < appOpsCount; ++i) {
//            AppOpsManager.PackageOps ops = appOps.get(i);
//            String packageName = ops.getPackageName();
//            int uid = ops.getUid();
//            int userId = UserHandle.getUserId(uid);
//            boolean Android os =
//
//                    (uid == Process.SYSTEM_UID) && ANDROID_SYSTEM_PACKAGE_NAME.equals(packageName);
//            if (isAndroidos || !profiles.contains(new UserHandle(userId))) {
//
//                continue;
//            }
//            Request request = getRequestFromOps(now, ops);
//            if (request != null) {
//                requests.add(request);
//            }
//        }
//        return requests;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    void checkIfAppUsageAccess() {
//        boolean granted = false;
//        AppOpsManager appOps = (AppOpsManager) this
//                .getSystemService(Context.APP_OPS_SERVICE);
//        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
//                android.os.Process.myUid(), getPackageName());
//
//        if (mode == AppOpsManager.MODE_DEFAULT) {
//            granted = (checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
//        } else {
//            granted = (mode == AppOpsManager.MODE_ALLOWED);
//        }
//        if (Build.VERSION.SDK_INT >= 21 && !granted) {
//            UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
//            long time = System.currentTimeMillis();
//            List stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
//
//            if (stats == null || stats.isEmpty()) {
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//                startActivity(intent);
//            }
//        } else {
//            startService(backGroundService);
//        }
//    }
//
//}

//
//    @RequiresApi(Build.VERSION_CODES.KITKAT)
//    fun getAppList(): List<Request> {
//        val aoManager: AppOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
//        val appOps: List<AppOpsManager.PackageOps> =
//            aoManager.getPackagesForOps(Request.LOCATION_OPS)
//
//        val appOpsCount = appOps?.size ?: 0
//
//        var requests: ArrayList<Request> = ArrayList<>(appOpsCount);
//        val now = System.currentTimeMillis();
//        val um: UserManager = getSystemService(Context.USER_SERVICE) as UserManager
//        val profiles = um.getUserProfiles()
//
//        for (obj in appOps) {
//            val ops: AppOpsManager.PackageOps
//        }
//        for (int i = 0; i < appOpsCount; ++i) {
//            aoManager.PackageOps ops = appOps . get (i);
//            String packageName = ops . getPackageName ();
//            int uid = ops . getUid ();
//            int userId = UserHandle . getUserId (uid);
//            boolean AndroidOs =(uid == Process.SYSTEM_UID) && ANDROID_SYSTEM_PACKAGE_NAME.equals(
//                packageName
//            ); if (isAndroidos || !profiles.contains(new UserHandle (userId))) {
//            continue; }
//            Request request = getRequestFromOps (now, ops);
//            if (request != null) {
//                requests.add(request);
//            }
//        }
//        return requests;
//    }

