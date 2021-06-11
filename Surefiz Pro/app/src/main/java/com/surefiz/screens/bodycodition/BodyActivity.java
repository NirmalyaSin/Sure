package com.surefiz.screens.bodycodition;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.surefiz.R;
import com.surefiz.screens.bodycodition.adapter.BodyAdapter;
import com.surefiz.screens.bodycodition.model.BodyItem;
import com.surefiz.utils.OnitemClickListner;

import java.util.ArrayList;

public class BodyActivity extends AppCompatActivity implements View.OnClickListener {

    protected RelativeLayout back_arrow;
    protected Button btn_next;
    protected TextView tv_top_text,tv_header;
    protected RecyclerView rv_genres;

    BodyAdapter genresAdapter;
    protected ArrayList<BodyItem> list;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);

        list= (ArrayList<BodyItem>) getIntent().getSerializableExtra("selectedBody");

        viewBinds();

        setAdapter();

    }

    protected void viewBinds(){
        back_arrow=findViewById(R.id.rl_back);
        btn_next=findViewById(R.id.btn_next);
        rv_genres=findViewById(R.id.rv_genres);


        setOnClickListner();
    }

    protected void setOnClickListner(){
        back_arrow.setOnClickListener(this);
        btn_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_next:
                if(list.size()>0) {
                    Intent intent = new Intent();
                    intent.putExtra("selectedBody", list);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;

            case R.id.rl_back:
                finish();
                break;
        }

    }



    protected void setAdapter(){

        genresAdapter=new BodyAdapter(BodyActivity.this,list,new OnitemClickListner(){

            @Override
            public void onClick(View view, int position) {

                //Log.d("position",":::"+position+"="+list.get(position).isSelection());


                    if (list.get(position).isSelection()) {
                        list.get(position).setSelection(false);
                    }else {
                        if(position==list.size()-1){
                            for (int i = 0; i <list.size() ; i++) {
                                if(i==list.size()-1){
                                    list.get(i).setSelection(true);

                                }else{
                                    list.get(i).setSelection(false);

                                }
                            }
                        }else {
                            list.get(position).setSelection(true);
                            list.get(list.size() - 1).setSelection(false);
                        }
                    }

                genresAdapter.notifyDataSetChanged();
            }
        });
        rv_genres.setLayoutManager(new LinearLayoutManager(this));
        rv_genres.setAdapter(genresAdapter);

    }


}
