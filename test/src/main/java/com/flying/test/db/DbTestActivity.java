package com.flying.test.db;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.flying.test.Note;
import com.flying.test.R;
import com.flying.test.TestApplication;

import java.util.Date;

public class DbTestActivity extends Activity implements View.OnClickListener{

    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_test);
        initViews();
    }

    private void initViews(){
        Button btnCreate = (Button) findViewById(R.id.create);
        Button btnUpdate = (Button) findViewById(R.id.update);
        Button btnDelete = (Button) findViewById(R.id.delete);
        Button btnQuery =  (Button) findViewById(R.id.query);
        btnCreate.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.create:
                i++;
                invokeCreate();
                break;
            case R.id.update:
                invokeUpdate();
                break;
            case R.id.delete:
                invokeDelete();
                break;
            case R.id.query:
                invokeQuery();
                break;
        }
    }


    private void invokeCreate(){
        Note note = new Note();
        note.setId(Long.valueOf(i));
        note.setComment("comment: "+i);
        note.setText("text: "+i);
        note.setDate(new Date());
        TestApplication.getTestApplication().getDaoSession().insert(note);
    }

    private void invokeUpdate(){
        Note note = new Note();
        note.setId(Long.valueOf(i));
        note.setComment("comment_update: "+i);
        note.setText("text_update: "+i);
        note.setDate(new Date());
        TestApplication.getTestApplication().getDaoSession().update(note);
    }

    private void invokeDelete(){
        Note note = new Note();
        note.setId(Long.valueOf(i));
        TestApplication.getTestApplication().getDaoSession().delete(note);
    }

    private void invokeQuery(){
        Note note = new Note();
        note.setId(Long.valueOf(i));

    }
}
