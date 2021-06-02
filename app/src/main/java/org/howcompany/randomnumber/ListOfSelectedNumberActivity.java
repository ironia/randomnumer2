package org.howcompany.randomnumber;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;


public class ListOfSelectedNumberActivity extends AppCompatActivity {

    ArrayList<Integer> selectedListForInteger = new ArrayList<>();
    Toolbar toolbar;
    RecyclerView recyclerView;
    RadioButton selectedLotto;
    RadioButton selectedNumber;
    RadioButton checkedButtonFront;
    RadioButton checkedButtonEnd;
    RadioGroup selectGroupFront;
    RadioGroup selectGroupEnd;
    MyDecoration deco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_selected_number); // 엑티비티 설정   xml 파일 설정

        selectGroupFront = findViewById(R.id.select_group1);
        selectGroupEnd = findViewById(R.id.select_group2);
        selectedLotto = findViewById(R.id.select_button_1_1);
        selectedNumber = findViewById(R.id.select_button_1_2);
        checkedButtonFront = findViewById(R.id.select_button_2_1);
        checkedButtonEnd = findViewById(R.id.select_button_2_2);


        AdView mAdView = findViewById(R.id.adView_list);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                /*SJK.EdgeView(getApplicationContext(), webView, new EdgeViewCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail() {

                    }
                });*/

            }
        });
        mAdView.loadAd(adRequest);
        //엑티비티 시작 및 정보 획득
        toolbar = findViewById(R.id.list_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            Log.d("list", "intent 처리");
            selectedListForInteger = intent.getIntegerArrayListExtra("selectedListForInteger");
            if (selectedListForInteger != null) {
                for (int i = 0; i < selectedListForInteger.size(); i++) {
                    Log.d("list", selectedListForInteger.get(i) + "");
                }
            }
        }


        recyclerView = findViewById(R.id.recycler_list);
        selectGroupFront.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.select_button_1_1:
                        checkedButtonFront.setText("뽑힌순");
                        checkedButtonEnd.setText("최신순");
                        break;
                    case R.id.select_button_1_2:
                        checkedButtonFront.setText("오름차순");
                        checkedButtonEnd.setText("내림차순");
                        break;
                }
                        ViewSortList(SortListString(selectedListForInteger,CheckSortCase()));
            }
        });
        selectGroupEnd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                ViewSortList(SortListString(selectedListForInteger,CheckSortCase()));

            }
        });




        if (selectedListForInteger != null) {
            ViewSortList(SortListString(selectedListForInteger,CheckSortCase()));

        } else {
            ViewSortList(null);
        }



    }


    // 정렬 케이스를 체크하는 단계
    private int CheckSortCase() {
        int type;
        if (selectedLotto.isChecked()) {
            if (checkedButtonFront.isChecked()) {
                //추첨순 1번째 선택칸 오래된 것부터
                type =0;
            } else {
                //추첨순 2번째 선택칸 최근것 부터
                type =1;
            }
        } else {
            if (checkedButtonFront.isChecked()) {
                //번호순으로 정렬 작은 수 부터
                type =2;
            } else {
                //번호순으로 정렬 큰 수 부
                type =3;
            }

        }
        Log.d("List","Case : "+ type);
        return type;

    }
    //정렬 케이스를 확인하고 그에 따라 정렬하는 단
    private ArrayList<String> SortListString(ArrayList<Integer> list, int type){
        Log.d("SortListString", "start");
            ArrayList<Integer> listInt = new ArrayList<>(list);
            switch (type){
                case 0:// 추첨 오래된 순
                    break;
                case 1://추첨 최근 순 0의 역순으로 변경;
                    Collections.reverse(listInt);
                    break;
                case 2://번호순 작은 수 부터
                    Collections.sort(listInt);
                    break;
                case 3://번호순 큰 수 부터
                    Collections.sort(listInt);
                    Collections.reverse(listInt);
                    break;

            }
            return  MakeListForRecycler(listInt);

    }

    private void ViewSortList(ArrayList<String> list){
        Log.d("ViewSortList", "start");
        MyAdapter myAdapter = new MyAdapter(list);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(deco!=null)
            recyclerView.removeItemDecoration(deco);
        deco = new MyDecoration();
        recyclerView.addItemDecoration(deco);

    }
    private ArrayList<String> MakeListForRecycler(ArrayList<Integer> list) {
        Log.d("MakeListForRecycler", "start");

        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            result.add(String.valueOf(list.get(i)));
        }
        Log.d("size", result.size() + "개");

        return result;
    }

    /*private void MakeRecyclerView(ArrayList<String> list) {
        Log.d("MakeRecyclerView", "start");
        MyAdapter myAdapter = new MyAdapter(list);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(deco!=null)
            recyclerView.removeItemDecoration(deco);
        deco = new MyDecoration();
        recyclerView.addItemDecoration(deco);
        *//*if (recyclerView != null) {
            Log.d("state", "recyclerView null");
        } else {
            recyclerView.addItemDecoration(new MyDecoration());
        }*//*
    }
*/
    /*private ArrayList<Integer> SortList(ArrayList<Integer> list, int type) {
        ArrayList<Integer> result = new ArrayList<>(list);

        switch (type) {
            case 0://오름차순
                Collections.sort(result, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return (o1 - o2);
                    }
                });
                break;
            case 1://내림차순
                Collections.sort(result, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return (o2 - o1);
                    }
                });
                break;
        }
        return result;
    }
*/

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView listView;
        private TextView selectedView;

        private MyViewHolder(View itemView) {
            super(itemView);
            listView = itemView.findViewById(R.id.listNumber);
            selectedView = itemView.findViewById(R.id.selectedNumber);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<String> list;

        private MyAdapter(ArrayList<String> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            view = LayoutInflater.from(ListOfSelectedNumberActivity.this).inflate(R.layout.list_view_layout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            String index = String.valueOf(position + 1);
            MyViewHolder viewHolder = (MyViewHolder) holder;
            viewHolder.listView.setText(index);
            viewHolder.selectedView.setText(list.get(position));

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class MyDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            ViewCompat.setElevation(view, 5.0f);
            outRect.set(20, 10, 20, 10);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case android.R.id.home: {
                finish();
                return true;
            }
            case android.R.id.redo: {

            }

        }

        return super.onOptionsItemSelected(item);

    }

}




