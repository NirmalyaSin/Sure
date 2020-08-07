package com.surefiz.screens.barcodescanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.surefiz.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class BarCodeScanner extends AppCompatActivity implements ZBarScannerView.ResultHandler {

    private static final String TAG ="Scanner" ;

    @BindView(R.id.iv_back)
    ImageView iv_back;
    ZBarScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code_scanner);
        ButterKnife.bind(this);



        mScannerView = findViewById(R.id.zBarScannerView);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent();
                intent.putExtra("barCode","");
                setResult(RESULT_CANCELED,intent);

                finish();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {

        Log.v(TAG, rawResult.getContents()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().getName());
        mScannerView.resumeCameraPreview(this);

        Intent intent=new Intent();
        intent.putExtra("barCode",rawResult.getContents());
        setResult(RESULT_OK,intent);

        onBackPressed();

    }
}
