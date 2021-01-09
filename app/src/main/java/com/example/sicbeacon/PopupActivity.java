package com.example.sicbeacon;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.appcompat.app.AlertDialog;


public class PopupActivity extends Activity implements BeaconConsumer{

    private ListView listView;

    private BeaconManager beaconManager;

    private List<Beacon> beaconList = new ArrayList<>();

    private static final String TAG = "sicbeacon";

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    // TODO : 비콘 수신시 기능 구현을 하기위한 핸들러 생성
    private CustomHandler handler =new CustomHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_activity);

//        // TODO: 비콘 매니저 생성
        beaconManager = BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        beaconManager.bind(this);

    }

    public void btn_Refresh(View view){
        // 아래에 있는 handleMessage를 부르는 함수. 맨 처음에는 0초간격이지만 한번 호출되고 나면
        // 1초마다 불러온다.
        handler.sendEmptyMessage(0);
    }

    private class CustomHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            // 객체 초기화
            listView = findViewById(R.id.StorelistView);


            // 비콘의 아이디와 거리를 측정하여 textView에 넣는다.
            for(final Beacon beacon : beaconList){
                int major = beacon.getId2().toInt(); //beacon major
                    if (major == 113){
                        String StoreName = "가산디지털점";

                    List<String> data = new ArrayList<>();
                    data.add(StoreName);

                    ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, data);
                    listView = findViewById(R.id.StorelistView);
                    listView.setAdapter(adapter);

                    }

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("major", beacon.getId2().toInt());
                        startActivity(intent);//다음 액티비티로 이동

//                            String major = beacon.getId2().toString();
//                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.naver.com"));
//                            startActivity(intent);

                        } // end onItemClick
                    }); // end setOnItemClickListener

            } // end For
        } // end handleMessage
    } // end CustomHandler

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            // 비콘이 감지되면 해당 함수가 호출된다. Collection<Beacon> beacons에는 감지된 비콘의 리스트가,
            // region에는 비콘들에 대응하는 Region 객체가 들어온다.
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    beaconList.clear();
                    for (Beacon beacon : beacons) {
                        beaconList.add(beacon);
                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }

    }


    //확인 버튼 클릭
    public void mOnClose(View v){
        //액티비티(팝업) 닫기
        finish();
    }

    // TODO : 바깥 레이어 클릭시 리스트뷰가 안닫히게 설정
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    // TODO : 안드로이드 백버튼 막기
    @Override
    public void onBackPressed() {
        return;
    }

}
