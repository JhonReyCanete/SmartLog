package com.example.smartlog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.PopupMenu;

public class BaseActivity extends AppCompatActivity {

    Button loginBtn;
    Button settingsBtn;
    PopupMenu popupSettingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        
        loginBtn = findViewById(R.id.loginBtn);
        settingsBtn = findViewById(R.id.Settings_button);

        popupSettingMenu = new PopupMenu(BaseActivity.this, settingsBtn);
        popupSettingMenu.getMenu().add("Server Details");

        loginBtn.setOnClickListener(v-> {
            startActivity(new Intent(BaseActivity.this, MainActivity.class));
        });

        settingsBtn.setOnClickListener(v-> {
            popMenu();
        });

        popupSettingMenu.setOnMenuItemClickListener(item -> {
            int selected = item.getItemId();
            if(selected == 0){
                startActivity(new Intent(BaseActivity.this, Fresh_Start.class));
            }
            return true;
        });
    }

    public void popMenu(){
        popupSettingMenu.show();
    }
}