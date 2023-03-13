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
                new QR_Code()
        }
        Player player = mockPlayer();

    }

    @Test
    void TestDeleteQrCode(){

    }


}
