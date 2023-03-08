package com.example.qr_project;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

// TODO: 1) Change layout from temporary once it's done
//       2) Implement add QRCode
//       3) Implement remove QRCode
//       4) Figure out how Player account interacts with UserHomeActivity
//            4.1) Stored as a variable of UserHomeActivity?
public class UserHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
    }

    /**
     * Handles Camera Icon being clicked
     * @param view
     */
    public void onCameraClick(View view) {
        addQRCodeButtonClicked();
    }


    /**
     * Dummy method for map button
     * @param view
     */
    public void onMapClick(View view) {
        Toast.makeText(this, "Map Button Click", Toast.LENGTH_SHORT).show();
    }

    /**
     * Dummy method for leaderboard button
     */
    public void onLeaderboardClick(View view) {
        Toast.makeText(this, "Leaderboard Button Click", Toast.LENGTH_SHORT).show();
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
