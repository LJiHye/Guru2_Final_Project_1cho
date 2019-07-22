package com.example.cho1.guru2_final_project_1cho.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.cho1.guru2_final_project_1cho.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapActivity extends AppCompatActivity {

    private SupportMapFragment mMapFragment;
    private LocationManager mLocationManager;
    private LatLng mCurPosLatLng;    //현재위치 저장 위도,경도 변수
    private int mClickIndex = 0; //어떤 버튼의 index 가 클릭됐는지를 저장 = 0; //어떤 버튼의 index 가 클릭됐는지를 저장

    private Spinner spinFree;

    @Override    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        /*Spinner dropdown = (Spinner) findViewById(R.id.spinFree);
        String[] items = new String[]{"50주년기념관", "인문사회관", "제1과학관", "제2과학관", "도서관", "학생누리관", "조형예술관", "샬롬하우스", "바롬인성교육관", "체육관", "정문", "후문", "X"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(GoogleMapActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);*/


        mMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        //구글맵이 로딩이 완료되면 아래의 이벤트가 발생한다.
        mMapFragment.getMapAsync(mapReadyCallback);

        //GSP 가 켜져 있는지 확인한다.
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //GSP 설정하는 Setting 화면으로 이동한다.
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(i);
        }

        if(
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        )
        {
            return;
        }

        //GPS 위치를 0.1초마다 10m 간격범위안에서 이동하면 위치를 listener 로 보내주도록 등록한다.
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, locationListener);
        //WIFI 위치를 0.1초마다 10m 간격범위안에서 이동하면 위치를 listener 로 보내주도록 등록한다.
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 10, locationListener);

       /* spinFree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                switch (spinFree.getSelectedItem().toString()){
                    case "50주년기념관":
                        mClickIndex = 1;
                        mCurPosLatLng = new LatLng(37.626251, 127.093109);
                        break;

                    case "인문사회관":
                        mClickIndex = 2;
                        mCurPosLatLng = new LatLng(37.628044, 127.092574);
                        break;

                    case "제1과학관":
                        mClickIndex = 3;
                        mCurPosLatLng = new LatLng(37.628987, 127.089674);
                        break;

                    case "제2과학관":
                        mClickIndex = 4;
                        mCurPosLatLng = new LatLng(37.629285, 127.090489);
                        break;

                    case "도서관":
                        mClickIndex = 5;
                        mCurPosLatLng = new LatLng(37.628325, 127.091229);
                        break;

                    case "학생누리관":
                        mClickIndex = 6;
                        mCurPosLatLng = new LatLng(37.629039, 127.091734);
                        break;

                    case "조형예술관":
                        mClickIndex = 7;
                        mCurPosLatLng = new LatLng(37.628750, 127.090425);
                        break;

                    case "샬롬하우스":
                        mClickIndex = 8;
                        mCurPosLatLng = new LatLng(37.628911, 127.088933);
                        break;

                    case "바롬인성교육관":
                        mClickIndex = 9;
                        mCurPosLatLng = new LatLng(37.627569, 127.088440);
                        break;

                    case "체육관":
                        mClickIndex = 10;
                        mCurPosLatLng = new LatLng(37.625539, 127.088914);
                        break;

                    case "정문":
                        mClickIndex = 11;
                        mCurPosLatLng = new LatLng(37.625775, 127.093657);
                        break;

                    case "후문":
                        mClickIndex = 12;
                        mCurPosLatLng = new LatLng(37.625416, 127.088289);
                        break;
                    default:
                        mClickIndex = 0;
                        mCurPosLatLng = null;

                }//end switch
                mMapFragment.getMapAsync(mapReadyCallback); //map refresh
            }
        });*/

       findViewById(R.id.btn50).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               mCurPosLatLng = new LatLng(37.626251, 127.093109);
               mMapFragment.getMapAsync(mapReadyCallback); //map refresh
           }
       });
    }//end onCreate()

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //위치 변경시 위도, 경도 정보 update 수신
            mCurPosLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            Toast.makeText(getBaseContext(), "현재 위치가 갱신 되었습니다. " + mCurPosLatLng.latitude + ", " + mCurPosLatLng.longitude, Toast.LENGTH_SHORT).show();
            //구글맵을 현재 위치로 이동시킨다.
            mMapFragment.getMapAsync(mapReadyCallback);
            //현재 위치를 한번만 호출하기 위해 리스너 해지
            mLocationManager.removeUpdates(locationListener);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }

        @Override
        public void onProviderEnabled(String s) { }

        @Override
        public void onProviderDisabled(String s) { }
    };


    //구글맵 로딩완료후 이벤트
    private OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(final GoogleMap googleMap) {

            if(
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            )
            {
                return;
            }
            //현재버튼 추가
            googleMap.setMyLocationEnabled(true);
            //줌인 줌아웃 버튼 추가
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            //나침반 추가
            googleMap.getUiSettings().setCompassEnabled(true);



            //맵을 클릭했을 때 이벤트를 등록한다.
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("클릭한 장소 ");
                    markerOptions.snippet("위도:" + latLng.latitude + ", 경도: " + latLng.longitude);
                    googleMap.addMarker(markerOptions).showInfoWindow();
                }
            });

            //snippet 클릭시 마커삭제
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    marker.remove();
                }
            });

            if(mCurPosLatLng != null) {
                //깃발 표시
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(mCurPosLatLng);

                if(mClickIndex == 1) {
                    //세종대왕
                    markerOptions.title("서울여대");
                    markerOptions.snippet("50주년기념관");
                }
                else if(mClickIndex == 2) {
                    markerOptions.title("해운대");
                    markerOptions.snippet("인문사회관");
                }
                else if(mClickIndex == 3) {
                    markerOptions.title("서울여대");
                    markerOptions.snippet("제1과학관");
                }
                else if(mClickIndex == 4) {
                    markerOptions.title("서울여대");
                    markerOptions.snippet("제2과학관");
                }
                else if(mClickIndex == 5) {
                    markerOptions.title("서울여대");
                    markerOptions.snippet("도서관");
                }
                else if(mClickIndex == 6) {
                    markerOptions.title("서울여대");
                    markerOptions.snippet("학생누리관");
                }
                else if(mClickIndex == 7) {
                    markerOptions.title("서울여대");
                    markerOptions.snippet("조형예술관");
                }
                else if(mClickIndex == 8) {
                    markerOptions.title("서울여대");
                    markerOptions.snippet("샬롬하우스");
                }
                else if(mClickIndex == 9) {
                    markerOptions.title("서울여대");
                    markerOptions.snippet("바롬인성교육관");
                }
                else if(mClickIndex == 10) {
                    markerOptions.title("서울여대");
                    markerOptions.snippet("체육관");
                }
                else if(mClickIndex == 11) {
                    markerOptions.title("서울여대");
                    markerOptions.snippet("정문");
                }
                else if(mClickIndex == 12) {
                    markerOptions.title("서울여대");
                    markerOptions.snippet("후문");
                }

                googleMap.addMarker(markerOptions).showInfoWindow();

                //구글맵을 위도,경도 위치로 이동시킨다.
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurPosLatLng));
            }
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        }
    };


}
