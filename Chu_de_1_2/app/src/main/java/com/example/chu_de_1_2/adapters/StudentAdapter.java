package com.example.chu_de_1_2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chu_de_1_2.databinding.LayoutItemStudentBinding;
import com.example.chu_de_1_2.entities.Student;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    ArrayList<Student> listStudent;
    UpdateCallback updateCallback;

    public StudentAdapter(ArrayList<Student> listStudent) {
        this.listStudent = listStudent;
    }

    public ArrayList<Student> getListStudent() {
        return listStudent;
    }

    public void setListStudent(ArrayList<Student> listStudent) {
        this.listStudent = listStudent;
    }

    public void setUpdateCallback(UpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StudentViewHolder(LayoutItemStudentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        holder.bind(holder.getAdapterPosition());
    }

    @Override
    public int getItemCount() {
        return listStudent.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        private final LayoutItemStudentBinding binding;

        public StudentViewHolder(LayoutItemStudentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(int position) {
            binding.txtName.setText(listStudent.get(position).getName());
            binding.txtYear.setText(listStudent.get(position).getYear());
            binding.txtPhone.setText(listStudent.get(position).getPhone());
            binding.txtSpecialized.setText(listStudent.get(position).getSpecialized());
            binding.txtType.setText(listStudent.get(position).getType());
            binding.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCallback.selectedItem(position);
                }
            });

            binding.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listStudent.remove(listStudent.get(getAdapterPosition()));
                    notifyItemRemoved(getAdapterPosition());
                }
            });
        }
    }


    public interface UpdateCallback {
        void selectedItem(int position);
    }
}


