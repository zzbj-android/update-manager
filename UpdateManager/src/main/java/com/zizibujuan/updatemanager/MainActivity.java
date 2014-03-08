package com.zizibujuan.updatemanager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.zizibujuan.android.util.Request;
import com.zizibujuan.android.util.ResponseObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static final String URL_GET_VERSION = "/";
    private static final String URL_GET_APK = "/";
    public void onLoadLatestVersionInfo(VersionInfo versionInfo){
        Context context = this;
        try {
            int currentVersionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            if(versionInfo.getVersionCode() > currentVersionCode){
                // 先提供静默更新
                downloadLatestApk(appId);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void downloadLatestApk(int appId){
        String strUrl = URL_GET_APK + appId;

        Context context = this;
        DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(strUrl));
        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        long downloadId = downloadManager.enqueue(request);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){
                    DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    Cursor cursor = downloadManager.query(query);
                    if(cursor.moveToFirst()){
                        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        int status = cursor.getInt(columnIndex);
                        if(DownloadManager.STATUS_SUCCESSFUL == status){
                            String urlString = context.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            installApk(urlString);
                        }
                    }
                }
            }
        };

        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void installApk(String urlString){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(urlString),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    // TODO: 要先判断网络是否有连接
    // appId
    private int appId = 1;
    public void requestServerVersion(){
        VersionInfo versionInfo1 = null;

        Map<String, Object> params = new HashMap<String, Object>();
        Request.get(URL_GET_VERSION + appId, null, new ResponseObject() {
            @Override
            public void callback(Map<String, Object> data) {
                VersionInfo versionInfo = new VersionInfo();
                versionInfo.setVersionCode(Integer.valueOf(data.get("versionCode").toString()));
                versionInfo.setVersionName(data.get("versionName").toString());
                versionInfo.setAppName(data.get("appName").toString());
                versionInfo.setApkName(data.get("apkName").toString());

                onLoadLatestVersionInfo(versionInfo);
            }
        }, new ResponseObject() {
            @Override
            public void callback(Map<String, Object> error) {

            }
        });;
    }
}
