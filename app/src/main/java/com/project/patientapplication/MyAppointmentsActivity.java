package com.project.patientapplication;

import static com.project.patientapplication.utilities.Constants.KEY_COLLECTION_APPOINTMENTS;
import static com.project.patientapplication.utilities.Constants.KEY_COLLECTION_DOCTORS;
import static com.project.patientapplication.utilities.Constants.KEY_DATE;
import static com.project.patientapplication.utilities.Constants.KEY_EMAIL;
import static com.project.patientapplication.utilities.Constants.KEY_FCM_TOKEN;
import static com.project.patientapplication.utilities.Constants.KEY_IMAGE;
import static com.project.patientapplication.utilities.Constants.KEY_NAME;
import static com.project.patientapplication.utilities.Constants.KEY_STATUS;
import static com.project.patientapplication.utilities.Constants.KEY_TIME;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.patientapplication.adapters.AppointmentAdapter;
import com.project.patientapplication.adapters.UsersAdapter;
import com.project.patientapplication.databinding.ActivityMyAppointmentsBinding;
import com.project.patientapplication.model.Appointment;
import com.project.patientapplication.model.User;

import java.util.ArrayList;
import java.util.List;

public class MyAppointmentsActivity extends AppCompatActivity {

    ActivityMyAppointmentsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyAppointmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        getAppointments();
    }

    private void setListeners() {
        binding.bookAppointment.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),DoctorsAppointmentActivity.class)));
    }

    private void getAppointments() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(KEY_COLLECTION_APPOINTMENTS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if(task.isSuccessful() && task.getResult()!=null){
                        List<Appointment> appointments = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                            Appointment appointment = new Appointment();
                            appointment.time = queryDocumentSnapshot.getString(KEY_TIME);
                            appointment.date = queryDocumentSnapshot.getString(KEY_DATE);
                            appointment.status = queryDocumentSnapshot.getString(KEY_STATUS);
                            appointment.patient = queryDocumentSnapshot.getString("patient");
                            appointment.patientName = queryDocumentSnapshot.getString("patientName");
                            appointment.doctor = queryDocumentSnapshot.getString("doctor");
                            appointment.doctorName = queryDocumentSnapshot.getString("doctorName");
                            appointments.add(appointment);
                        }
                        if(appointments.size()>0){
                            AppointmentAdapter appointmentAdapter = new AppointmentAdapter(appointments);
                            binding.usersRecyclerView.setAdapter(appointmentAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}