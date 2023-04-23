package com.project.patientapplication;

import static com.project.patientapplication.utilities.Constants.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.patientapplication.databinding.ActivitySignInBinding;
import com.project.patientapplication.utilities.PreferenceManager;
import com.project.patientapplication.utilities.ToastUtility;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;
    ToastUtility toastUtility;
    PreferenceManager preferenceManager;
    String className = this.getClass().getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            preferenceManager = new PreferenceManager(getApplicationContext());
            if(preferenceManager.getBoolean(KEY_IS_SIGNED_IN)){
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
            toastUtility = ToastUtility.getInstance(this);
            binding = ActivitySignInBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            setListeners();
        }catch (Exception e){
            toastUtility.exceptionToast(e.getLocalizedMessage(),className);
        }
    }

    private void setListeners(){
        binding.textCreateNewAccount.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),SignUpActivity.class)));
        binding.buttonSignIn.setOnClickListener(v -> {
            if(isValidSignIn()){
                signIn();
            }
        });
    }

    private void signIn(){
        toastUtility.shortToast("IN");
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        try{
            database.collection(KEY_COLLECTION_PATIENTS_LOGIN)
                    .whereEqualTo(KEY_EMAIL,binding.inputEmail.getText().toString())
                    .whereEqualTo(KEY_PASSWORD,binding.inputPassword.getText().toString())
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful() && task.getResult()!=null && task.getResult().getDocuments().size()>0){
                            loading(false);
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            preferenceManager.putBoolean(KEY_IS_SIGNED_IN,true);
                            preferenceManager.putString(KEY_USER_ID,documentSnapshot.getId());
                            preferenceManager.putString(KEY_NAME,documentSnapshot.getString(KEY_NAME));
                            preferenceManager.putString(KEY_IMAGE,documentSnapshot.getString(KEY_IMAGE));
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else{
                            loading(false);
                            toastUtility.longToast("Unable to sign in");
                        }
                    });

        } catch (Exception e){
            toastUtility.exceptionToast(e.getLocalizedMessage(),"SiginActivity::signIn");
        }
    }

    private boolean isValidSignIn(){
        if(binding.inputEmail.getText().toString().trim().isEmpty()){
            toastUtility.shortToast("Enter email");
            return false;
        }else if(binding.inputPassword.getText().toString().trim().isEmpty()){
            toastUtility.shortToast("Enter password");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            toastUtility.shortToast("Enter a valid email");
            return false;
        }
        return true;
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }

}