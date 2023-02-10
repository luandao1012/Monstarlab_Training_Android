package com.example.chu_de_1_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.chu_de_1_2.adapters.StudentAdapter;
import com.example.chu_de_1_2.databinding.ActivityMainBinding;
import com.example.chu_de_1_2.entities.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements StudentAdapter.UpdateCallback, View.OnClickListener {
    private ArrayList<Student> listStudents;
    private ActivityMainBinding binding;
    private StudentAdapter studentAdapter;
    private int localPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        initListener();
    }

    private void init() {
        listStudents = new ArrayList<>();
        studentAdapter = new StudentAdapter(listStudents);
        studentAdapter.setUpdateCallback(this);
        binding.recyclerViewStudent.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewStudent.setAdapter(studentAdapter);
    }

    //implements onClickListener
    private void initListener() {
        binding.btnAdd.setOnClickListener(this);
        binding.btnSort.setOnClickListener(this);
        binding.btnFilter.setOnClickListener(this);
        binding.btnReturn.setOnClickListener(this);
        binding.btnSearch.setOnClickListener(this);
        binding.btnChange.setOnClickListener(this);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortStudent(ArrayList<Student> arrayList) {
        String attribute = binding.spinnerSort.getSelectedItem().toString();
        Collections.sort(arrayList, new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                return getAttribute(attribute, s1).compareTo(getAttribute(attribute, s2));
            }
        });
        studentAdapter.notifyDataSetChanged();
    }

    private String getAttribute(String s, Student student) {
        switch (s) {
            case "Name":
                return student.getName();
            case "Phone":
                return student.getPhone();
            case "Year":
                return student.getYear();
            case "Specialized":
                return student.getSpecialized();
        }
        return s;
    }

    private Student getStudent(Student s) {
        String name = binding.edtName.getText().toString().trim();
        String phone = binding.edtPhone.getText().toString().trim();
        String year = binding.edtYear.getText().toString().trim();
        String specialized = binding.edtSpecialized.getText().toString().trim();
        String type = binding.spinnerType.getSelectedItem().toString();
        if (!check(name, phone, year, specialized, s)) {
            return null;
        }
        return new Student(name, year, phone, specialized, type);
    }

    private boolean check(String name, String phone, String year, String specialized, Student s) {
        if (name.isEmpty()) {
            binding.edtName.setError("Not empty");
            binding.edtName.requestFocus();
            return false;
        }
        if (phone.isEmpty()) {
            binding.edtPhone.setError("Not empty");
            binding.edtPhone.requestFocus();
            return false;
        }
        String reg = "^(0)\\d{9}";
        if (!phone.matches(reg)) {
            binding.edtPhone.setError("Malformed");
            binding.edtPhone.requestFocus();
            return false;
        }
        for (Student student : listStudents) {
            if (student.getPhone().equals(phone) && student != s) {
                binding.edtPhone.setError("Phone already exists");
                binding.edtPhone.requestFocus();
                return false;
            }
        }
        if (year.isEmpty()) {
            binding.edtYear.setError("Not empty");
            binding.edtYear.requestFocus();
            return false;
        }
        if (specialized.isEmpty()) {
            binding.edtSpecialized.setError("Not empty");
            binding.edtSpecialized.requestFocus();
            return false;
        }
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setListAdapter(ArrayList<Student> list) {
        studentAdapter.setListStudent(list);
        studentAdapter.notifyDataSetChanged();
    }

    @Override
    public void selectedItem(int position) {
        binding.edtName.setText(listStudents.get(position).getName());
        binding.edtYear.setText(listStudents.get(position).getYear());
        binding.edtPhone.setText(listStudents.get(position).getPhone());
        binding.edtSpecialized.setText(listStudents.get(position).getSpecialized());
        if (listStudents.get(position).getType().equals("College")) {
            binding.spinnerType.setSelection(0);
        } else {
            binding.spinnerType.setSelection(1);
        }
        localPosition = position;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                Student student = getStudent(null);
                if (student != null) {
                    listStudents.add(student);
                    studentAdapter.notifyItemInserted(listStudents.size());

                    binding.edtName.setText("");
                    binding.edtYear.setText("");
                    binding.edtPhone.setText("");
                    binding.edtSpecialized.setText("");
                }
                break;
            case R.id.btnSort:
                sortStudent(studentAdapter.getListStudent());
                break;
            case R.id.btnFilter:
                String type = binding.spinnerFilter.getSelectedItem().toString();
                ArrayList<Student> listStudentFilter = new ArrayList<>();

                for (Student s : listStudents) {
                    if (s.getType().equals(type)) {
                        listStudentFilter.add(s);
                    }
                }
                setListAdapter(listStudentFilter);
                break;
            case R.id.btnReturn:
                setListAdapter(listStudents);
                break;
            case R.id.btnSearch:
                String word = binding.edtSearch.getText().toString().toLowerCase().trim();
                ArrayList<Student> listStudentSearch = new ArrayList<>();
                for (Student s : listStudents) {
                    if (s.searchStudent(word)) {
                        listStudentSearch.add(s);
                    }
                }
                setListAdapter(listStudentSearch);
                break;
            case R.id.btnChange:
                if (localPosition >= 0) {
                    Student s = getStudent(listStudents.get(localPosition));
                    if (s != null) {
                        listStudents.set(localPosition, s);
                        studentAdapter.notifyItemChanged(localPosition);
                    }
                }
                break;
        }
    }
}