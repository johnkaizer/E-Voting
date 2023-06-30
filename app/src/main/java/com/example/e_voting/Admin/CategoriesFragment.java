package com.example.e_voting.Admin;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.e_voting.Adapters.CategoryAdapter;
import com.example.e_voting.DB.EVotingDB;
import com.example.e_voting.Models.CategoryModel;
import com.example.e_voting.R;
import com.example.e_voting.databinding.FragmentCategoriesBinding;

import java.util.ArrayList;

public class CategoriesFragment extends Fragment {
    private FragmentCategoriesBinding binding;
    RecyclerView categoryRec;
    ArrayList<CategoryModel> categoryModels;
    CategoryAdapter categoryAdapter;
    SQLiteDatabase sqLiteDatabase;
    EVotingDB eVotingDB;
    EditText searchTxt;
    String searchText = "";
    LottieAnimationView noScheduleAnimationView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        eVotingDB = new EVotingDB(getContext());
        categoryRec = root.findViewById(R.id.categoryRv);
        noScheduleAnimationView = root.findViewById(R.id.lottieAnimationView);
        categoryRec.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        categoryRec.setHasFixedSize(true);
        categoryRec.setNestedScrollingEnabled(false);
        searchTxt = root.findViewById(R.id.editText);
        searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText = s.toString();
                getCategories();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        getCategories();

        binding.scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.add_category, null);
                EditText categoryTitle = dialogView.findViewById(R.id.idEdtNoteName);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = categoryTitle.getText().toString();

                        // Check if any of the fields are empty
                        if (TextUtils.isEmpty(title) ) {
                            Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ContentValues cv = new ContentValues();
                        cv.put("title", title);

                        sqLiteDatabase = eVotingDB.getWritableDatabase();
                        Long recinsert = sqLiteDatabase.insert(EVotingDB.TABLE_NAME_CATEGORY, null, cv);
                        if (recinsert != null) {
                            Toast.makeText(getContext(), "Successfully created a category", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            getCategories();
                        }
                    }
                });
                dialogView.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });

        return root;
    }

    private void getCategories() {
        sqLiteDatabase = eVotingDB.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + EVotingDB.TABLE_NAME_CATEGORY + " WHERE title LIKE ?",
                new String[]{"%" + searchText + "%"});

        ArrayList<CategoryModel> categoryModels = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            categoryModels.add(new CategoryModel(id, title));
        }
        cursor.close();

        if (categoryModels.isEmpty()) {
            // Clear the existing adapter to hide the RecyclerView
            categoryRec.setAdapter(null);

            noScheduleAnimationView.setVisibility(View.VISIBLE);
            noScheduleAnimationView.playAnimation();
        } else {
            // Hide the animation and set the adapter for the RecyclerView
            noScheduleAnimationView.setVisibility(View.GONE);
            categoryAdapter = new CategoryAdapter(getContext(), categoryModels, sqLiteDatabase, R.layout.category_item);
            categoryRec.setAdapter(categoryAdapter);
        }
    }
}