package com.project.patientapplication;

import static com.project.patientapplication.utilities.Constants.*;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.firebase.firestore.FirebaseFirestore;
import com.project.patientapplication.databinding.ActivityAppointmentBinding;
import com.project.patientapplication.model.User;
import com.project.patientapplication.utilities.PreferenceManager;
import com.project.patientapplication.utilities.ToastUtility;

import java.util.HashMap;

public class AppointmentActivity extends AppCompatActivity {
    ActivityAppointmentBinding binding;
    ArrayAdapter<CharSequence> adapterItems;
    private DatePickerDialog datePickerDialog;
    ToastUtility toastUtility;
    User recievedUser;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toastUtility = ToastUtility.getInstance(getApplicationContext());
        adapterItems = ArrayAdapter.createFromResource(this,R.array.blood_group,R.layout.list_item);
        adapterItems.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.dropdown.setAdapter(adapterItems);
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        loadRecievedDetails();
    }

    private void loadRecievedDetails(){
        recievedUser = (User) getIntent().getSerializableExtra(KEY_USER);
        binding.userName.setText(recievedUser.name);
    }

    private void setListeners() {
        binding.datePicker.setOnClickListener(v -> {
            setDatePickerDialog();
        });
        binding.backBtnImage.setOnClickListener(v -> onBackPressed());
        binding.submitButton.setOnClickListener(v -> {
            if(isValidDetails()){
                addAppointment();
            }
        });
    }
    private void setDatePickerDialog(){
        datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String day = dayOfMonth<10?"0"+dayOfMonth:dayOfMonth+"";
            String month1 = month<10?"0"+month:month+"";
            String date = day+"-"+month1+"-"+year;
            binding.dateText.setText(date);
        },2000,1,1);
        datePickerDialog.show();
    }

    private boolean isValidDetails(){
        if(binding.dateText.getText().toString().trim().equals("Date")){
            toastUtility.shortToast("Select a date");
            return false;
        }else if(binding.description.getText().toString().trim().isEmpty()){
            toastUtility.shortToast("Enter description");
            return false;
        }
        return true;
    }

    private void addAppointment(){
        try {
            loading(true);
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            HashMap<String,Object> appointment = new HashMap<>();
            appointment.put(KEY_DATE,binding.dateText.getText().toString());
            appointment.put(KEY_TIME,binding.dropdown.getSelectedItem().toString());
            appointment.put(KEY_DESCRIPTION,binding.description.getText().toString());
            appointment.put(KEY_STATUS,APPOINTMENT_BOOKED);
            appointment.put("doctor",recievedUser.id);
            appointment.put("doctorName",recievedUser.name);
            appointment.put("patient",preferenceManager.getString(KEY_USER_ID));
            appointment.put("patientName",preferenceManager.getString(KEY_NAME));
            database.collection(KEY_COLLECTION_APPOINTMENTS)
                    .add(appointment)
                    .addOnSuccessListener(documentReference -> {
                        loading(false);
                        Intent intent = new Intent(getApplicationContext(), MyAppointmentsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        toastUtility.shortToast("Appointment added successfully");
                        startActivity(intent);
                        finish();
                    }).addOnFailureListener(exception -> {
                        loading(false);
                        toastUtility.exceptionToast(exception.getLocalizedMessage(),"SignUpActivity::signUp");
                    });
        }catch (Exception e){
            toastUtility.longToast(e.getLocalizedMessage());
            toastUtility.longToast("......"+recievedUser.id);
        }
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.submitButton.setVisibility(View.INVISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.submitButton.setVisibility(View.VISIBLE);
        }
    }
}