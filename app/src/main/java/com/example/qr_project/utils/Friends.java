package com.example.qr_project.utils;

import android.provider.BaseColumns;

public class Friends {
    public static final class FriendEntry implements BaseColumns {
        public static final String TABLE_NAME = "friend";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_EMAIL = "email";
    }

    public void addFriend(Player friend) {
        Friends.add(friend);
    }

    private static void add(Player friend) {
    }

    public void deleteFriend(QR_Code qrCode) {
        Friends.remove(qrCode);
    }

    private static void remove(QR_Code qrCode) {
    }
}
