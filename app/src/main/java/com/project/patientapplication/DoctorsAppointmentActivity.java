package com.project.patientapplication;

import static com.project.patientapplication.utilities.Constants.KEY_COLLECTION_DOCTORS;
import static com.project.patientapplication.utilities.Constants.KEY_EMAIL;
import static com.project.patientapplication.utilities.Constants.KEY_FCM_TOKEN;
import static com.project.patientapplication.utilities.Constants.KEY_IMAGE;
import static com.project.patientapplication.utilities.Constants.KEY_NAME;
import static com.project.patientapplication.utilities.Constants.KEY_USER;
import static com.project.patientapplication.utilities.Constants.KEY_USER_ID;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.patientapplication.adapters.UsersAdapter;
import com.project.patientapplication.databinding.ActivityDoctorsAppointmentBinding;
import com.project.patientapplication.listeners.UserListener;
import com.project.patientapplication.model.User;
import com.project.patientapplication.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class DoctorsAppointmentActivity extends AppCompatActivity implements UserListener {

    ActivityDoctorsAppointmentBinding binding;
    PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorsAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners() {
        binding.backBtnImage.setOnClickListener(v -> onBackPressed());
    }

    private void getUsers() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(KEY_COLLECTION_DOCTORS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if(task.isSuccessful() && task.getResult()!=null){
                        List<User> users = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if(users.size()>0){
                            UsersAdapter usersAdapter = new UsersAdapter(users,this);
                            binding.usersRecyclerView.setAdapter(usersAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        }else{
                            showErrorMessage();
                        }
                    }
                    else{
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText("No user(s) available");
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onUserClicker(User user) {
        Intent intent = new Intent(getApplicationContext(),AppointmentActivity.class);
        intent.putExtra(KEY_USER,user);
        startActivity(intent);
    }
}