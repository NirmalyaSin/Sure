package com.surefiz.screens.acountabiltySearch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.acountabiltySearch.adapter.SearchCircleUserAdapter;
import com.surefiz.screens.accountability.models.CircleUserResponse;
import com.surefiz.screens.accountability.models.User;
import com.surefiz.screens.acountabiltySearch.models.AddToCircleResponse;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.SpacesItemDecoration;
import com.surefiz.utils.progressloader.LoadingData;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchAcountabilityActivity extends BaseActivity implements SearchCircleUserAdapter.OnSearchCircleUserClickListener {
    public View view;
    private RecyclerView recyclerView;
    private EditText searchBar;
    private ImageView imgCancel;
    private LoadingData loadingData;
    private ArrayList<User> arrayListUsers = new ArrayList<User>();
    private SearchCircleUserAdapter mSearchCircleUserAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_search_accountability, null);
        addContentView(view);
        initializeView();
    }

    private void initializeView() {
        setHeaderView();
        loadingData = new LoadingData(this);
        recyclerView = view.findViewById(R.id.rv_items);
        searchBar = view.findViewById(R.id.searchBar);
        imgCancel = view.findViewById(R.id.imgCancel);
        setRecyclerViewItem();

        //Add watcher to monitor search string.
        searchBar.addTextChangedListener(new TextWatcher() {
            long lastChange = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
              //  Log.d("@@beforeTextChanged: ", "Searching... start: "+start+" , after: "+after+" , count: "+count+" , Char: "+s);
            }

            @Override
            public void onTextChanged(CharSequence key, int start, int before, int count) {

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (System.currentTimeMillis() - lastChange >= 1000) {
                            //send request
                            Log.d("@@onTextChanged: ", "Searching... count: "+count
                                    +" , Char: "+key);
                            String keyword = searchBar.getText().toString().trim();
                            if(!keyword.equals("")) {
                                //Call Api to list users based on the keyword
                                callSearchCircleUserApi(key.toString());
                            }else {
                                arrayListUsers.clear();
                                mSearchCircleUserAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }, 1000);
                lastChange = System.currentTimeMillis();
            }

            @Override
            public void afterTextChanged(Editable s) {
            //    Log.d("@@afterTextChanged: ", "Searching... Editable: "+s);
            }
        });
    }

    private void callSearchCircleUserApi(final String keyword) {
        loadingData.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<CircleUserResponse> searchCircleUserListApi = apiInterface.call_SearchCircleUserListApi(
                LoginShared.getRegistrationDataModel(this).getData().getToken(), keyword);

        searchCircleUserListApi.enqueue(new Callback<CircleUserResponse>() {
            @Override
            public void onResponse(Call<CircleUserResponse> call, Response<CircleUserResponse> response) {
                if (loadingData != null && loadingData.isShowing()) {
                    loadingData.dismiss();

                }

                arrayListUsers.clear();

                try {
                    if (response.body().getStatus() == 1) {
                        arrayListUsers.addAll(response.body().getData().getUserList());
                        Log.d("@@UserItem : " , arrayListUsers.get(0).toString());
                    }

                } catch (Exception e) {
                  //  MethodUtils.errorMsg(SearchAcountabilityActivity.this, getString(R.string.error_occurred));
                }

                mSearchCircleUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CircleUserResponse> call, Throwable t) {
                if (loadingData != null && loadingData.isShowing())
                    loadingData.dismiss();
             //   MethodUtils.errorMsg(SearchAcountabilityActivity.this, getString(R.string.error_occurred));
            }
        });

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setText("");
            }
        });


    }

    private void setRecyclerViewItem() {
        mSearchCircleUserAdapter = new SearchCircleUserAdapter(this,
                arrayListUsers, this);
        recyclerView.setAdapter(mSearchCircleUserAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SpacesItemDecoration decoration = new SpacesItemDecoration((int) 10);
        recyclerView.addItemDecoration(decoration);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setHeaderView() {
        tv_universal_header.setText("Search Circle Users");
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        iv_AddPlus.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        img_topbar_menu.setVisibility(View.GONE);
        rl_back.setVisibility(View.VISIBLE);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onViewClick(int position) {
        //Not yet defined
    }

    @Override
    public void onCancelRequest(int position) {
        //API Call to cancel.
        callAddToCircleUserApi(position, RequestState.REQUEST_STATUS_CANCEL);
    }

    @Override
    public void onAddToCircle(int position) {
        //API Call to add.
        callAddToCircleUserApi(position, RequestState.REQUEST_STATUS_SEND);
    }

    //Service call to send/cancel request to add in circle
    private void callAddToCircleUserApi(final int listPosition, final String connectionType) {
        loadingData.show_with_label("Requesting...");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<AddToCircleResponse> call_AddToCircleUserApi = apiInterface.call_AddToCircleUserApi(
                        LoginShared.getRegistrationDataModel(this).getData().getToken(),
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId(),
                arrayListUsers.get(listPosition).getUser_id(),
                connectionType);

        call_AddToCircleUserApi.enqueue(new Callback<AddToCircleResponse>() {
            @Override
            public void onResponse(Call<AddToCircleResponse> call, Response<AddToCircleResponse> response) {
                if (loadingData != null && loadingData.isShowing()) {
                    loadingData.dismiss();
                }
                if(response.body()!=null) {
                    Log.d("@@RequestData : ", response.body().toString());
                }

                if(response.body().getStatus()==1){
                    //Show success dialog
                    showResponseDialog(response.body().getStatus(),
                            response.body().getData().getMessage());
                }else {
                    //Show dialog to show response from server
                    MethodUtils.errorMsg(SearchAcountabilityActivity.this,
                            response.body().getData().getMessage());
                }

            }

            @Override
            public void onFailure(Call<AddToCircleResponse> call, Throwable t) {
                if (loadingData != null && loadingData.isShowing()) {
                    loadingData.dismiss();
                }
                //Show error dialog
               MethodUtils.errorMsg(SearchAcountabilityActivity.this,
                       getString(R.string.error_occurred));
            }
        });

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setText("");
            }
        });
    }

    public void showResponseDialog(int status, String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setMessage(message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Cancel the dialog.
                dialog.dismiss();
                //Call Privacy list Api Again
                callSearchCircleUserApi(searchBar.getText().toString().trim());
            }
        });

        dialog.create();
        dialog.show();
    }
}
