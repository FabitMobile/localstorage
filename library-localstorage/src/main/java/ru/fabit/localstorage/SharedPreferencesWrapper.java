package ru.fabit.localstorage;

import android.content.Context;
import android.content.SharedPreferences;

import com.ironz.binaryprefs.BinaryPreferencesBuilder;
import com.ironz.binaryprefs.encryption.AesValueEncryption;
import com.ironz.binaryprefs.encryption.XorKeyEncryption;


public class SharedPreferencesWrapper {

    private static String sharedPreferencesName = "ParkingSharedPreferences";
    private static SharedPreferences sharedPreferences;

    public static void init(Context context) {
        String key = context.getApplicationInfo().packageName;
        String shortKey = context.getApplicationInfo().packageName.substring(0, 16);
        sharedPreferences = new BinaryPreferencesBuilder(context)
                .keyEncryption(new XorKeyEncryption(key.getBytes()))
                .valueEncryption(new AesValueEncryption(shortKey.getBytes(), shortKey.getBytes()))
                .migrateFrom(context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE))
                .build();
    }

    public static SharedPreferences getSharedPreferences() {
        if (sharedPreferences != null) {
            return sharedPreferences;
        } else {
            throw new IllegalStateException("SharedPreferences not initialized");
        }
    }
}