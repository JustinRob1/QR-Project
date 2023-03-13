package com.example.qr_project;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.qr_project.utils.Player;
import com.example.qr_project.utils.QR_Code;

import java.util.HashMap;
import java.util.Map;

public class PlayerTest {

    private Player mockPlayer(){
        Player player = new Player("username", "email",
                "phoneNumber", "userID");
        return player;
    }

    @Test
    void TestAddQrCode(){
        QR_Code[] qrCodes = {
                new QR_Code("1"),
                new QR_Code("2"),
                new QR_Code("3")
        };
        Player player = mockPlayer();

        int oldTotalScore = player.getTotalScore();
        int newTotalScore = oldTotalScore;

        // Zero score when player was just created
        assertEquals(newTotalScore, oldTotalScore);

        // Score increases with each added QR Code
        for (QR_Code qrCode : qrCodes) {
            player.addQRCode(qrCode);
            oldTotalScore = newTotalScore;
            newTotalScore = player.getTotalScore();
            assertTrue(newTotalScore > oldTotalScore);
        }

    }

    @Test
    void TestDeleteQrCode(){
        QR_Code[] qrCodes = {
                new QR_Code("4"),
                new QR_Code("5"),
                new QR_Code("6")
        };

        Player player = mockPlayer();

        for (QR_Code qrCode : qrCodes) {
            player.addQRCode(qrCode);
        }

        int oldTotalScore = player.getTotalScore();
        int newTotalScore = oldTotalScore;

        // Score decreases with each deleted QR Code
        for (QR_Code qrCode : qrCodes) {
            player.deleteQrCode(qrCode);
            oldTotalScore = newTotalScore;
            newTotalScore = player.getTotalScore();
            assertTrue(newTotalScore < oldTotalScore);
        }
    }


}
