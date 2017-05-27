package com.android.event;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends Activity {

    ListView mListView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        mListView = (ListView)findViewById(R.id.list_view);

    }

    class ListViewAdapter extends ArrayAdapter<Class<?>>{
        List<Class<?>> actList;

        public ListViewAdapter(Context mc, List<Class<?>> actList){
            super(mc,android.R.layout.simple_list_item_1,actList);
            this.actList = actList;
        }

        @Override
        public int getCount() {
            return actList==null?0:actList.size();
        }

        @Override
        public Class<?> getItem(int position) {
            return actList==null?null:actList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}
