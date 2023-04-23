package com.project.patientapplication.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.patientapplication.databinding.ItemAppointmentContainerBinding;
import com.project.patientapplication.model.Appointment;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>{
    private List<Appointment> appointments;

    public AppointmentAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAppointmentContainerBinding itemAppointmentContainerBinding = ItemAppointmentContainerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AppointmentViewHolder(itemAppointmentContainerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        holder.setUserData(appointments.get(position));
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    class AppointmentViewHolder extends RecyclerView.ViewHolder{
        ItemAppointmentContainerBinding binding;
        AppointmentViewHolder(ItemAppointmentContainerBinding itemAppointmentContainerBinding){
            super(itemAppointmentContainerBinding.getRoot());
            binding = itemAppointmentContainerBinding;
        }
        void setUserData(Appointment appointments){
            String status[] = binding.appointmentStatusValue.getText().toString().split(":");
            String Date[] = binding.appointmentDateValue.getText().toString().split(":");
            String Doctor[] = binding.appointmentDoctorValue.getText().toString().split(":");

            status[1] = appointments.status;
            Date[1] = appointments.date+" "+appointments.time;
            Doctor[1] = appointments.doctorName;

            binding.appointmentDoctorValue.setText(Doctor[0]+" : "+Doctor[1]);
            binding.appointmentDateValue.setText(Date[0]+" : "+Date[1]);
            binding.appointmentStatusValue.setText(status[0]+" : "+status[1]);
        }
    }
}
