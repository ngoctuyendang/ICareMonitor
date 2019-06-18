package com.example.icaremonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

public class Item_ListView extends AppCompatActivity {
    public TextView tvValue, tvTime ;
    public EditText  edNote;
    public Button btnDelete,btnUpdate;
    public ImageButton imgBack;
    Database database;

    public Integer IDholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item__list_view);
        tvValue= (TextView) findViewById (R.id.tvSetValue);
        tvTime= (TextView) findViewById (R.id.tvSetTime);
        edNote= (EditText) findViewById (R.id.tvSetNote);
        btnDelete= (Button) findViewById (R.id.btnDelete);
        btnUpdate=(Button)findViewById(R.id.btnUpdate);
        imgBack = (ImageButton) findViewById(R.id.imgBack);
        final User user = (User) getIntent().getExtras().getSerializable("Value");
        tvValue.setText(user.getValue());
        tvTime.setText(user.getTime());
        edNote.setText(user.getNote());
        database= new Database(this);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IDholder = user.getId() ;
                database.DELETE_Data(IDholder);
                Toast.makeText(Item_ListView.this, "Đã xóa", Toast.LENGTH_LONG).show();

                Intent intent= new Intent(Item_ListView.this, History_Activity.class);
                startActivity(intent);
                finish();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IDholder = user.getId() ;
                String newNote = edNote.getText().toString();
                if(!newNote.equals(""))
                {
                    database.updateName(newNote,IDholder,user.getNote());
                    Intent intent= new Intent(Item_ListView.this, History_Activity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(Item_ListView.this, "You must enter a note", Toast.LENGTH_LONG).show();
                }

            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Item_ListView.this, History_Activity.class);
                startActivity(intent);
            }
        });
    }
}
