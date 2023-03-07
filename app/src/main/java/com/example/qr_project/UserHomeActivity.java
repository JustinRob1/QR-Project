package com.example.qr_project;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.app.AlertDialog;

// TODO: 1) Change layout from temporary once it's done
//       2) Implement add QRCode
//       3) Implement remove QRCode
//       4) Figure out how Player account interacts with UserHomeActivity
//            4.1) Stored as a variable of UserHomeActivity?
public class UserHomeActivity extends AppCompatActivity {
    private Button addQRCodeButton;
    private View.OnClickListener addQRCodeButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addQRCodeButtonClicked();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_temporary);

        // View initialization
        addQRCodeButton = findViewById(R.id.add_qrcode_button);

        // Button listener initialization
        addQRCodeButton.setOnClickListener(addQRCodeButtonOnClickListener);
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    /**
     * Initiates scanning
     * */
    private void addQRCodeButtonClicked() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setBeepEnabled(true);
        integrator.setPrompt("Scan QR Code");
        integrator.initiateScan();
    }

    /**
     * Adds a QRCode to the Player's account after scanning
     * */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        // The event handled is related to IntentResult
        if (scanResult != null) {
            // User scanned a QRCode
            if (scanResult.getContents() != null){
                // TODO 2: Implement adding QRCode contents
            }
            // User canceled scanning
            else{

            }
            System.out.printf(scanResult.getContents());
        }
        // Else the event handled is not related to IntentResult.
    }
}
