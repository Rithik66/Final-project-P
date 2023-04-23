package com.project.patientapplication;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.project.patientapplication.utilities.Constants.*;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.patientapplication.databinding.ActivityMainBinding;
import com.project.patientapplication.utilities.PreferenceManager;
import com.project.patientapplication.utilities.ToastUtility;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    PreferenceManager preferenceManager;
    Dialog dialog;
    ToastUtility toastUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.logout_dialog);
        dialog.getWindow().setLayout(MATCH_PARENT,WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        getPatientDetails();
        setListeners();
    }

    private void getPatientDetails(){
        binding.textName.setText(preferenceManager.getString(KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        Glide.with(MainActivity.this).load(bitmap).into(binding.imageProfile);
    }

    private void setListeners() {
        binding.appointments.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),MyAppointmentsActivity.class));
        });
        binding.imageSignOut.setOnClickListener(v -> {
            MaterialCardView cancel = dialog.findViewById(R.id.cancelButton);
            MaterialCardView logout = dialog.findViewById(R.id.acceptLogoutButton);
            ImageView imageView = dialog.findViewById(R.id.imageLogout);
            Glide.with(MainActivity.this).load(ContextCompat.getDrawable(MainActivity.this, R.drawable.logout)).into(imageView);
            logout.setOnClickListener(i -> {
                try {
                    toastUtility.shortToast("Signing out...");
                    signOut();
                }catch (Exception e){
                    toastUtility.exceptionToast(e.getLocalizedMessage(),"MainActivity::binding:imageSignOut");
                }
            });
            cancel.setOnClickListener(i -> dialog.dismiss());
            dialog.show();
        });
    }

    public void signOut(){
        try {
            try {
                Thread.sleep(1000);
            }catch(Exception e){
                toastUtility.exceptionToast(e.getLocalizedMessage(),"MainActivity::signOut");
            }
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            DocumentReference documentReference = database.collection(KEY_COLLECTION_DOCTORS).document(preferenceManager.getString(KEY_USER_ID));
            HashMap<String,Object> updates = new HashMap<>();
            updates.put(KEY_FCM_TOKEN, FieldValue.delete());
            documentReference.update(updates)
                    .addOnSuccessListener(unused -> {
                        preferenceManager.clear();
                        startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> toastUtility.longToast("Unable to sign in"));
            dialog.dismiss();
        }catch (Exception e){
            toastUtility.exceptionToast(e.getLocalizedMessage(),"MainActivity::signOut");
        }
    }
}