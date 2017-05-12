package com.iambenzo.dailypackt;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.NoSuchPaddingException;

import devliving.online.securedpreferencestore.SecuredPreferenceStore;

public class LoginActivity extends AppCompatActivity {

    private EditText userText;
    private EditText passText;
    private Button goButton;
    private String user;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userText = (EditText) findViewById(R.id.textUser);
        passText = (EditText) findViewById(R.id.textPassword);

        passText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                goButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        goButton = (Button) findViewById(R.id.buttonGo);

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = userText.getText().toString();
                pass  = passText.getText().toString();
                Log.e("USER", user);
                go();
            }
        });

        Button signUpButton = (Button) findViewById(R.id.buttonSignUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.packtpub.com/register")));
            }
        });

    }

    private void go(){

        try {
            SecuredPreferenceStore prefStore = SecuredPreferenceStore.getSharedInstance(getApplicationContext());
            SecuredPreferenceStore.Editor editor = prefStore.edit();

            editor.putString("email", user);
            editor.putString("password", pass);
            editor.commit();

            Intent i = new Intent(getApplicationContext(), LoaderActivity.class);
            startActivity(i);

        } catch (IOException | CertificateException | NoSuchAlgorithmException | InvalidKeyException | UnrecoverableEntryException | InvalidAlgorithmParameterException | NoSuchPaddingException | NoSuchProviderException | KeyStoreException e) {
            e.printStackTrace();
        }
    }

}
