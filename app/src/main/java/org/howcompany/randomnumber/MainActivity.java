package org.howcompany.randomnumber;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.sreg.japnq.kwrgon.op.EdgeViewCallback;
import com.sreg.japnq.kwrgon.op.SJK;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;// 전면 광고객체 형
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ConstraintLayout MainLayout;
    EditText startEditText;//처음 숫자 입력 버튼
    EditText endEditText;// 마지막 숫자 입력 버튼
    TextView container; // 뽑힌 숫자가 표시되는 영역
    TextView countTextView;
    TextView selectedView;
    //TextView message_menu;
    ImageView ad_rent;
    InputMethodManager im;
    boolean checkBoxState = false;
    boolean tutorial2 = false;
    int startNum;
    int endNum;
    int cnt = 0;
    int[] rNum;
    Toolbar toolbar;
    String list="";
    DrawerLayout drawerLayout;
    View drawerView;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    ScrollView scrollView;
    ArrayList<String> selectedList=new ArrayList<>();
    ArrayList<Integer> selectedListForInteger = new ArrayList<>();
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 중복 메뉴 체크박스
        switch (item.getItemId()) {


            case R.id.reset:
                try {
                    Resetting();

                } catch (Exception e) {
                    Log.d("Button2", "err");
                }
                break;

            case R.id.list:
                Intent intent = new Intent(MainActivity.this, ListOfSelectedNumberActivity.class);
                intent.putIntegerArrayListExtra("selectedListForInteger", selectedListForInteger);
                startActivity(intent);
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    public int getAppVersionCode(){
        PackageInfo packageInfo;
        try{
            packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            return -1;
        }

        return packageInfo.versionCode;
    }
    public static double getUpdateVersionCheck(String key){
        try {
            FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
            return remoteConfig.getDouble(key);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
      return -1;
    }
    public void initRemoteConfig(){
        try {
            firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            final FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(3600)
                    .build();

            firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
            firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
            firebaseRemoteConfig.fetch(0).addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) firebaseRemoteConfig.activate();

                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    private void sendScroll(){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }).start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Random randomAd = new Random();
        int i = randomAd.nextInt(1000);
        Log.d("pick",i+"");
        if(i>300&&i<501) {
            setContentView(R.layout.activity_main_sjk);
        }else{
            setContentView(R.layout.activity_main);
        }
        //드로우 메뉴 만들
        drawerLayout = findViewById(R.id.drawer_main);//activity_main;
        drawerView = findViewById(R.id.drawer_layout);
        //
       // drawerLayout.



        //뽑힌 리스트 보이기
        selectedView = findViewById(R.id.selectedList);
        scrollView = findViewById(R.id.selectedMain);


        // 업데이트 확인 방법
        initRemoteConfig();
        checkUpdate();
        //액션 바 꾸미기

        toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setHomeButtonEnabled(true);
            Log.d("MainMenu","actionBar is not null");

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.common_open_on_phone,R.string.close_btn);
        drawerLayout.addDrawerListener(drawerToggle);


        //기본 셋팅을 위한 저장 요소.
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(sharedPreferences!=null) {
            tutorial2 = sharedPreferences.getBoolean("tutorial2", false);
            if (tutorial2 && sharedPreferences.getBoolean("save",true)) {
                startNum = sharedPreferences.getInt("startNum", 1);
                endNum = sharedPreferences.getInt("endNum", 10);
            }
        }
        if(sharedPreferences!=null) {
            if (!tutorial2) {
                try {
                    editor.putBoolean("tutorial2", true);
                    editor.apply();
                    Intent intent = new Intent(MainActivity.this, Tutorial.class);
                    startActivity(intent);
                } catch (NullPointerException e){
                    e.printStackTrace();
                }

            }
            checkBoxState = sharedPreferences.getBoolean("check_Box", false);

        }

        //드로우 메뉴 수정
        CheckBox duplication = findViewById(R.id.duplication);
        CheckBox save = findViewById(R.id.save);
        duplication.setChecked(sharedPreferences.getBoolean("check_box",false));
        save.setChecked(sharedPreferences.getBoolean("save", true));
        duplication.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {

                    buttonView.setChecked(false);
                    editor.putBoolean("check_box", false);

                } else {

                    buttonView.setChecked(true);
                    editor.putBoolean("check_box", true);

                }
                editor.apply();
                rNum = null;
                cnt = 0;
                checkBoxState = isChecked;
            }
        });

        //배너 광고 관련 메모리 업

        SJK.Sjki(getApplicationContext());
        final WebView webView = findViewById(R.id.webView);
        if(i<301||i>500) {
            AdView mAdView = findViewById(R.id.adView_main);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }else{
            SJK.EdgeView(getApplicationContext(), webView, new EdgeViewCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFail() {

                }
            });
        }



        //뷰 구성요소 메모리 업
        MobileAds.initialize(this,"ca-app-pub-4844195353069534~3901374380" );
        MobileAds.setAppVolume(0.5f);
        MainLayout = findViewById(R.id.MainLayout);
        startEditText = findViewById(R.id.startEditText);
        if(startNum!=0){
            startEditText.setText(String.valueOf(startNum));
        }
        endEditText = findViewById(R.id.endEditText);
        if(endNum!=0){
            endEditText.setText(String.valueOf(endNum));
        }
        container = findViewById(R.id.container);
        countTextView = findViewById(R.id.count_textView);
        String strColor = "#000000";
        container.setTextColor(Color.parseColor(strColor));

        im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        // 키보드 관련 변환 부분.. 다음으로 바로 바꾸는 부분.. 끝내고 바로 숫자 입력하는 시스템으로 완성.
        startEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        endEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        endEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    // 완료 버튼을 누르면 키보드 내려가는 부분.
                    im.hideSoftInputFromWindow(endEditText.getWindowToken(), 0);


                }
                return true;
            }
        });
        LinearLayout send = findViewById(R.id.send_menu);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                String[] address = {"ironia211@gmail.com"};
                email.putExtra(Intent.EXTRA_EMAIL, address);
                email.putExtra(Intent.EXTRA_SUBJECT, "문의 내용");
                startActivity(email);
            }
        });
        LinearLayout tutorial = findViewById(R.id.tutorial);
        tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tutorialIntent = new Intent(MainActivity.this, Tutorial.class);
                startActivity(tutorialIntent);
            }
        });
        ad_rent=findViewById(R.id.ad_rent);
        ad_rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=org.howcompany.rentcalculator"));
                startActivity(intent);
            }
        });
        MakeRequestNewInterstitial(R.string.banner_ad_unit_id2);


        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    im.hideSoftInputFromWindow(endEditText.getWindowToken(), 0);
                    startNum = Integer.parseInt(startEditText.getText().toString());
                    endNum = Integer.parseInt(endEditText.getText().toString());
                    StartMakeRandomNumber(startNum, endNum);

                } catch (Exception e) {
                    Log.d("container", "err");
                }
            }
        });




    }

    public void checkUpdate(){
        if(getAppVersionCode()<getUpdateVersionCheck("update_version_call")){
            AlertDialog.Builder programEndBuilder = new AlertDialog.Builder(this);
            programEndBuilder.setTitle("업데이트 출시");
            programEndBuilder.setIcon(R.drawable.icon_logo);
            programEndBuilder.setMessage("새로운 업데이트 버전이 출시 되었습니다. 업데이트 하시겠습니까?");
            programEndBuilder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent  = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=org.howcompany.randomnumber"));
                    startActivity(intent);                }
            });
            programEndBuilder.setPositiveButton("아니요", null);
            programEndBuilder.show();
        }
    }
        // 종료 버튼
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(drawerView)){
            drawerLayout.closeDrawer(drawerView);
        }else {
            AlertDialog.Builder programEndBuilder = new AlertDialog.Builder(this);
            programEndBuilder.setTitle(R.string.exit);
            programEndBuilder.setIcon(android.R.drawable.ic_dialog_alert);
            programEndBuilder.setMessage(R.string.exitMessage);
            programEndBuilder.setNegativeButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            programEndBuilder.setPositiveButton(R.string.cancel, null);
            programEndBuilder.show();
        }

    }


    // 시작 버튼을 클릭하면 첫번째 뽑기 숫자가 나오고 뽑기 버튼으로 이름을 바꾼다.


    public void StartMakeRandomNumber(int startNum, int endNum) {
        try {

            editor.putInt("startNum",startNum);
            editor.putInt("endNum",endNum);
            editor.apply();
            Random random = new Random();
            int length = endNum - startNum + 1;

            if (rNum == null) {// 리스트가 없을 때

                if (endNum < startNum) {// 숫자가 크면 못만들고
                    //Toast.makeText(getApplicationContext(), R.string.errorStrangeNumber, Toast.LENGTH_LONG).show();
                    Snackbar.make(MainLayout, R.string.errorStrangeNumber, Snackbar.LENGTH_LONG)
                            .setAction(R.string.edit_view, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startEditText.requestFocus();
                                }
                            }).show();

                } else if (endNum > startNum) {// 제대로 만든 경우 쓰레드를 활용하여 작성
                    try {
                        MyTaskThread task = new MyTaskThread(length);
                        task.start();
                        if(length<1000) {
                            task.join();
                        }
                    }catch (Exception e){
                        Log.d("Error", "StartMakeRandomNumber : if // rNum==null // : task didn't work");
                    }

                } else {//그래도 아니면 같은 수의 상황
                    Snackbar.make(MainLayout, R.string.errorSameNumber, Snackbar.LENGTH_LONG)
                            .setAction(R.string.edit_view, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startEditText.requestFocus();
                                }
                            }).show();

                }
            }
            // 추첨 리스트가 만들어 졌을 때
            // 중복 포함 일 경우 / 계속 새로 뽑아서 보여준다. 앞의 숫자랑은 다른 숫자로... 설정
            if (sharedPreferences.getBoolean("check_box", false)) {
                int num = rNum[random.nextInt(length)];

                cnt++;
                settingContainerView(num);
                settingCountView(cnt, 0);
            } else {// 중복이 아닐 경우 만든 내용을 보여준다.
                if (cnt == length) {

                    Snackbar.make(MainLayout, R.string.errorReset, Snackbar.LENGTH_LONG)
                            .setAction(R.string.reset, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        if (rNum != null) {
                                            Resetting();
                                        } else {
                                            Toast.makeText(getApplicationContext(), R.string.emptyNumber, Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (Exception e) {
                                        Log.d("err", "StartMakeButton => Reset snackBar didn't work!");
                                    }
                                }
                            }).show();
                } else {
                    settingContainerView(rNum[cnt]);
                    cnt++;
                    settingCountView(cnt, length);

                }
            }


            } catch (NullPointerException e) {
                    Log.d("Error", "StartMakeNum");
        }
    }
    //입력 값에 따른 화면 글씨 크기 변환


    public String ChangeTextForView(int rNum) {
        if (0 < rNum && rNum < 1000) {
            container.setTextSize(150);
        } else if (999 < rNum && rNum < 10000) {
            container.setTextSize(120);
        } else if (9999 < rNum && rNum < 100000) {
            container.setTextSize(90);
        } else if (99999 < rNum && rNum < 1000000) {
            container.setTextSize(80);
        } else if (999999 < rNum && rNum < 10000000) {
            container.setTextSize(70);
        } else{
            container.setTextSize(50);
        }

        return String.valueOf(rNum);
    }
        // 추첨내역 저장 및 컨텐츠 화면에 입력하기
    public void settingContainerView(int num) {
        selectedListForInteger.add(num);
        container.setText(ChangeTextForView(num));
        selectedList.add(ChangeTextForView(num));
        if(cnt==0){
            list= String.valueOf(num);
        }else{
            list = list+" / " +num;
        }
        selectedView.setText(list);
        sendScroll();

    }
    public void settingCountView(int cnt, int length){
        String text;
        if(sharedPreferences.getBoolean("check_box", false)){
            // 중복 포함 뽑기일 경우
            // 뽑기한 카운트 숫자를 넣기.
            // 카운트 숫자 필요. 그것으로 셋팅
            text = ""+cnt;
        }else{
            // 중복 포함 뽑기가 아닐 경우
            // 추첨 뽑기를 한 길이와 남은 뽑기가 들어가야함.
            text  = cnt +" / "+length;
        }
        countTextView.setText(text);

    }


    // 광고 보여주는 메소드

    public void MakeRequestNewInterstitial(int Id) {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(Id));
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                AdRequest adRequest = new AdRequest.Builder().build();
                mInterstitialAd.loadAd(adRequest);
                super.onAdClosed();
            }
        });

    }
    public void SaveNumber(){
        editor.putInt("startNum",startNum);
        editor.putInt("endNum",endNum);
        editor.apply();

    }
    public void Resetting() {
        SaveNumber();
        container.setTextSize(100);
        container.setText(R.string.draw);
        countTextView.setText("");
        cnt = 0;
        rNum = null;
        list = null;
        selectedView.setText("");
        selectedListForInteger.clear();
        selectedList.clear();
        if(sharedPreferences.getBoolean("save",true)){
            startEditText.setText(sharedPreferences.getInt("startNum",1));
            endEditText.setText(sharedPreferences.getInt("endNum",10));
        }else{
            startEditText.setText("");
            endEditText.setText("");
        }
        Log.d("setting","글자 세팅");

    }

    class MyTaskThread extends Thread {

        private int length;
        //생성하면서 길이 값 획득
        private MyTaskThread(int length){
            this.length=length;
        }
       //여기서
        public void run(){
            try {
                Random random = new Random();
                rNum = new int[length];
                for (int i = 0; i < length; i++) {
                    rNum[i] = random.nextInt(length) + startNum;
                    if (i != 0) {
                        for (int j = 0; j < i; j++)
                            if (rNum[i] == rNum[j]) {
                                i--;
                                break;
                            }

                    }

                }
            } catch (Exception e) {
                Log.d("Error", "Thread");
            }
        }
    }
}
