package com.flying.compound;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.flying.compound.view.CheckSwitchButton;
import com.flying.compound.view.SlideSwitchView;


/**
 * @author RA
 * @blog http://blog.csdn.net/vipzjyno1
 */
public class MainActivity extends Activity {
	private ToggleButton mTogBtn;
	private CheckSwitchButton mCheckSwithcButton;
	private CheckSwitchButton mEnableCheckSwithcButton;
	private SlideSwitchView mSlideSwitchView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initView();
	}

	private void initView() {
		mTogBtn = (ToggleButton) findViewById(R.id.mTogBtn); // 获取到控件
		mTogBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					//选中
				}else{
					//未选中
				}
			}
		});// 添加监听事件
		mCheckSwithcButton = (CheckSwitchButton)findViewById(R.id.mCheckSwithcButton);
		mEnableCheckSwithcButton = (CheckSwitchButton)findViewById(R.id.mEnableCheckSwithcButton);
		mCheckSwithcButton.setChecked(false);
		mCheckSwithcButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					mEnableCheckSwithcButton.setEnabled(false);
					mSlideSwitchView.setEnabled(false);
				}else{
					mEnableCheckSwithcButton.setEnabled(true);
					mSlideSwitchView.setEnabled(true);
				}
			}
		});
		mSlideSwitchView = (SlideSwitchView) findViewById(R.id.mSlideSwitchView);
		mSlideSwitchView.setOnChangeListener(new SlideSwitchView.OnSwitchChangedListener() {

			@Override
			public void onSwitchChange(SlideSwitchView switchView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){

				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

