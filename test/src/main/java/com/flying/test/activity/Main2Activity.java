package com.flying.test.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;

import com.flying.test.R;
import com.flying.test.adapter.ActAdapter;
import com.flying.test.db.DbTestActivity;
import com.flying.test.fragment.TestFragmentActivity;

import java.util.Arrays;


public class Main2Activity extends Activity {

    Class[] actList = {MainActivity.class,
                       DbTestActivity.class,
                       TestMeasureActivity.class,
                       TestListActivity.class,
                        ScrollingActivity.class,
                        TestFragmentActivity.class,
                        TestRunningActivity.class,
                        DownloadActivity.class,
                        };
    RecyclerView recyclerView;
    ActAdapter actAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initAdapter();
        initViews();
    }

    private void initViews(){
        recyclerView = (RecyclerView)findViewById(R.id.act_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(actAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //actionState : action状态类型，有三类 ACTION_STATE_DRAG （拖曳），ACTION_STATE_SWIPE（滑动），ACTION_STATE_IDLE（静止）
                int dragFlags = makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.UP | ItemTouchHelper.DOWN
                        | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);//支持上下左右的拖曳
                int swipeFlags = makeMovementFlags(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);//表示支持左右的滑动
                return makeMovementFlags(dragFlags, swipeFlags);//直接返回0表示不支持拖曳和滑动
            }

            /**
             * @param recyclerView attach的RecyclerView
             * @param viewHolder 拖动的Item
             * @param target 放置Item的目标位置
             * @return
             */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();//要拖曳的位置
                int toPosition = target.getAdapterPosition();//要放置的目标位置
                actAdapter.moveItem(fromPosition, toPosition);
                return true;
            }

            /**
             * @param viewHolder 滑动移除的Item
             * @param direction
             */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();//获取要滑动删除的Item位置
                actAdapter.removeItem(position);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return super.isLongPressDragEnabled();//不支持长按拖曳效果直接返回false
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return super.isItemViewSwipeEnabled();//不支持滑动效果直接返回false
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private void initAdapter(){
        actAdapter = new ActAdapter(this, Arrays.<Class<?>>asList(actList));
    }
}
