package com.example.chu_de_1_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.chu_de_1_2.adapters.StudentAdapter;
import com.example.chu_de_1_2.databinding.ActivityMainBinding;
import com.example.chu_de_1_2.entities.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements StudentAdapter.UpdateCallback {
    private ArrayList<Student> listStudents;
    private ActivityMainBinding binding;
    private StudentAdapter studentAdapter;

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

    private void initListener() {
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Student student = getStudent(null);
                if (student != null) {
                    listStudents.add(student);
                    studentAdapter.notifyItemInserted(listStudents.size());

                    binding.edtName.setText("");
                    binding.edtYear.setText("");
                    binding.edtPhone.setText("");
                    binding.edtSpecialized.setText("");
                }
            }
        });

        binding.btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortStudent(studentAdapter.getListStudent());
            }
        });

        binding.btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = binding.spinnerFilter.getSelectedItem().toString();
                ArrayList<Student> listStudentFilter = new ArrayList<>();

                for (Student student : listStudents) {
                    if (student.getType().equals(type)) {
                        listStudentFilter.add(student);
                    }
                }
                studentAdapter.setListStudent(listStudentFilter);
                studentAdapter.notifyDataSetChanged();
            }
        });

        binding.btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studentAdapter.setListStudent(listStudents);
                studentAdapter.notifyDataSetChanged();
            }
        });

        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word = binding.edtSearch.getText().toString().toLowerCase().trim();
                ArrayList<Student> listStudentSearch = new ArrayList<>();
                for (Student student : listStudents) {
                    if (student.getName().toLowerCase().contains(word) || student.getPhone().toLowerCase().contains(word)
                            || student.getYear().toLowerCase().contains(word) || student.getSpecialized().toLowerCase().contains(word)
                            || student.getType().toLowerCase().contains(word)) {
                        listStudentSearch.add(student);
                    }
                }
                studentAdapter.setListStudent(listStudentSearch);
                studentAdapter.notifyDataSetChanged();
            }
        });
    }

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
        Student s = listStudents.get(position);
        binding.btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Student student = getStudent(s);
                if (student != null) {
                    listStudents.set(position, student);
                    studentAdapter.notifyItemChanged(position);
                }
            }
        });
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
}