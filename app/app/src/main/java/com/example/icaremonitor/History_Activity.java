package com.example.icaremonitor;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import static com.example.icaremonitor.Database.TABLE_NAME2;

public class History_Activity extends AppCompatActivity {
    ImageView image ;
    final  File sdCard = Environment.getExternalStorageDirectory();
    Database mDatabase;
    ArrayList<User> userList;

    private ListView mListview;

    Button btnDelete;
    Button btnExport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        btnDelete = (Button)findViewById(R.id.btndel);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.deleteAll();
                mListview.setAdapter(null);
                Toast.makeText(History_Activity.this, "Đã xóa tất cả ", Toast.LENGTH_LONG).show();
            }
        });

        image = (ImageView)findViewById(R.id.btnback);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(History_Activity.this,Main2Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.animotion,R.anim.animotion2);
            }
        });
        mDatabase = new Database(this);

        userList = new ArrayList <>();
        Cursor data = mDatabase.getData();

        mListview = (ListView) findViewById(R.id.listView);

        int numRows=data.getCount();
        if (numRows == 0) {
            Toast.makeText(History_Activity.this, "Nothing to show ", Toast.LENGTH_LONG).show();
        } else {
            while (data.moveToNext()) {
                String value;
                String date;
                String ex;
                Integer id;
                id = data.getInt(0);
                value = data.getString(1);
                date = data.getString(2);
                ex = data.getString(3);
                User user = new User(id, value, date, ex);
                userList.add(user);

                ThreeColumn_ListAdapter adapter = new ThreeColumn_ListAdapter(this, R.layout.list_row, userList);

                mListview.setAdapter(adapter);
            }
        }
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> parent, View view, int position, long id) {

              User user=  userList.get(position);
              Intent intent= new Intent(History_Activity.this, Item_ListView.class);
              intent.putExtra("Value",user);
              startActivity(intent);
            }
        });
        Database sql= new Database(this);
        final SQLiteDatabase db = sql.getReadableDatabase();



        btnExport = (Button)findViewById(R.id.btnexport);
        btnExport.setOnClickListener(new View.OnClickListener() {
         Cursor c = null ;

            @Override
            public void onClick(View v) {
               try {
                   c = db.rawQuery("SELECT * FROM " + TABLE_NAME2,null);
                   int rowCount = 0;
                   int colCount = 0;
                    File exportDir = new File(sdCard, "/MyIcareMonitor/");
                    if (!exportDir.exists()) { exportDir.mkdirs(); }
                   String fileName = "Myheart.csv";
                   File saveFile= new File(exportDir,fileName);
                   FileWriter fW= new FileWriter(saveFile);
                   BufferedWriter bW = new BufferedWriter(fW);



                   rowCount = c.getCount();
                   colCount = c.getColumnCount();
                   if(rowCount>0){
                       c.moveToFirst();
                       for (int i=1; i<colCount; i++){
                           if( i != colCount-1){
                               bW.write('\uFEFF');
                               bW.write(c.getColumnName(i)+" - ");
                           }else{
                               bW.write('\uFEFF');
                               bW.write(c.getColumnName(i));
                           }
                       }
                       bW.newLine();
                       for (int i=0; i<rowCount; i++){
                           c.moveToPosition(i);
                           for(int j=1; j<colCount; j++ ){
                               if(j!=colCount-1){
                                   bW.write(c.getString(j)+ " - ");
                               }else {
                                   bW.write(c.getString(j));
                               }
                           }
                           bW.newLine();
                       }
                       ///là gì v?
                       bW.flush();
                       bW.close();

                       Toast.makeText(History_Activity.this, "Exported Successfully", Toast.LENGTH_LONG).show();

                   }


               }catch (Exception ex){
                   if(db.isOpen()){
                       db.close();
                       Toast.makeText(History_Activity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();

                   }

                }finally {
               }
                shareFile();



            }
        });
    }

    private void shareFile(){
        File exportFile = new File(Environment.getExternalStorageDirectory(), "/MyIcareMonitor/");
        String fileName = "Myheart.csv";
        File shareFlie = new File(exportFile,fileName);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        Uri uri = FileProvider.getUriForFile(History_Activity.this, BuildConfig.APPLICATION_ID + ".provider", shareFlie);
        shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
        startActivity(Intent.createChooser(shareIntent,"Share CSV"));
    }
}
