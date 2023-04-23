package com.project.patientapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.project.patientapplication.databinding.ActivityAppointmentBinding;

public class AppointmentActivity extends AppCompatActivity {
    ActivityAppointmentBinding binding;
    ArrayAdapter<CharSequence> adapterItems;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        adapterItems = ArrayAdapter.createFromResource(this,R.array.blood_group,R.layout.list_item);
        adapterItems.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.dropdown.setAdapter(adapterItems);
        setListeners();
    }

    private void setListeners() {
        binding.datePicker.setOnClickListener(v -> {
            setDatePickerDialog();
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
}