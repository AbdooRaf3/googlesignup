package com.example.googlesignup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.googlesignup.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth firebaseAuth;
    private Button googleSignIn;
    private int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing Auth
        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignIn = (Button) findViewById(R.id.googleSignIn);
        googleSignIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                signIn();
            }

            private void signIn()
            {
                Intent signIn = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signIn,RC_SIGN_IN);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //Google Sign Up
        if(requestCode ==RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }

    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this,"Signed In Successfully",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(account);
        }
        catch (ApiException e){
            Toast.makeText(MainActivity.this,"Sign In Failed",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);

            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    Intent mainIntent = new Intent(MainActivity.this, Home.class);
                    startActivity(mainIntent);
                }
            }, 500);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account)
    {
        //check if account is null
        if (account != null)
        {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {

                        startActivity(new Intent(MainActivity.this, Home.class));
                        finish();

                    } else {
                        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        updateUI(user);
                    }
                }
            });
        }
        else{
            Toast.makeText(MainActivity.this, "acc failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(FirebaseUser fUser)
    {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null)
        {
            String personName = account.getDisplayName();
            String personEmail = account.getEmail();

            Toast.makeText(MainActivity.this, personName + personEmail, Toast.LENGTH_SHORT).show();
        }

    }
}