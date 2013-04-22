package com.twocities.pomodoro;

import net.simonvt.menudrawer.MenuDrawer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.twocities.pomodoro.settings.SettingActivity;

public class HomeActivity extends Activity implements MenuDrawer.OnDrawerStateChangeListener {
	private MenuDrawer mMenuDrawer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_home);
		setupActionBar();
		initViews();
		switchContent(new TodayTodoList());
	}
	
	private void initViews() {
        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_WINDOW);
        mMenuDrawer.setContentView(R.layout.activity_home);
        mMenuDrawer.setMenuView(R.layout.layout_menudrawer);
        mMenuDrawer.setDropShadowEnabled(false);
//        mMenuDrawer.setDropShadowColor(Color.parseColor("#88000000"));
//        mMenuDrawer.setDropShadowSize(20);
	}
	
	private void setupActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	private void switchContent(Fragment fragment) {
		mMenuDrawer.closeMenu();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.replace(R.id.content, fragment);
		ft.commit();
	}
	
	public void onLeftMenuSelected(View v) {
		switch (v.getId()) {
		case R.id.text_today:
			switchContent(new TodayTodoList());
			mMenuDrawer.setActiveView(v);
			break;
		case R.id.text_future:
			mMenuDrawer.setActiveView(v);
			switchContent(new FutureTodoList());
			break;
		case R.id.text_analysis:
			mMenuDrawer.setActiveView(v);
			switchContent(new AnalysisFragment());
			break;
		case R.id.text_settings:
			Intent i = new Intent(this, SettingActivity.class);
			this.startActivity(i);
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()) {
			case android.R.id.home:
				mMenuDrawer.toggleMenu();
				return true;
			default:
				return false;
		}
	}
	
    @Override
    public void onBackPressed() {
        final int drawerState = mMenuDrawer.getDrawerState();
        if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
            mMenuDrawer.closeMenu();
            return;
        }

        super.onBackPressed();
    }

	@Override
	public void onDrawerStateChange(int oldState, int newState) {
		
	}
}