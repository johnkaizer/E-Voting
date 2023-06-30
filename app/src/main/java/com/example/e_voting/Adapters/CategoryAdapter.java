package com.example.e_voting.Adapters;

import static com.example.e_voting.DB.EVotingDB.TABLE_NAME_CATEGORY;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_voting.DB.EVotingDB;
import com.example.e_voting.Models.CategoryModel;
import com.example.e_voting.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    Context context;
    ArrayList<CategoryModel>list;
    SQLiteDatabase sqLiteDatabase;
    int category_item;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> list, SQLiteDatabase sqLiteDatabase, int category_item) {
        this.context = context;
        this.list = list;
        this.sqLiteDatabase = sqLiteDatabase;
        this.category_item = category_item;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false));

    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CategoryModel categoryModel = list.get(position);
        holder.title.setText(categoryModel.getTitle());
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation!!");
                builder.setMessage("Are you sure you want to delete category?");
                builder.setIcon(android.R.drawable.ic_menu_delete);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EVotingDB eVotingDB = new EVotingDB(context);
                        sqLiteDatabase =eVotingDB.getReadableDatabase();
                        long recdelete= sqLiteDatabase.delete(TABLE_NAME_CATEGORY,"id="+categoryModel.getId(),null);
                        if (recdelete!=-1){
                            Toast.makeText(context,"Deleted successfully",Toast.LENGTH_SHORT).show();
                            list.remove(position);
                            notifyDataSetChanged();
                        }

                    }
                });
                builder.setNegativeButton("No",null);
                builder.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageButton imageButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleText);
            imageButton = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
