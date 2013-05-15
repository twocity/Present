package com.twocities.pomodoro;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.twocities.pomodoro.adapters.SwipeAdapter;
import com.twocities.pomodoro.widget.swipelistview.SwipeListView;
import com.twocities.pomodoro.widget.swipelistview.SwipeListViewListener;

public abstract class SwipeListFragment extends Fragment implements
		SwipeListViewListener {
	final private Handler mHandler = new Handler();

	final private Runnable mRequestFocus = new Runnable() {
		public void run() {
			mList.focusableViewAvailable(mList);
		}
	};

	SwipeAdapter mAdapter;
	SwipeListView mList;
	View mEmptyView;
	TextView mStandardEmptyView;
	View mProgressContainer;
	View mListContainer;
	CharSequence mEmptyText;
	boolean mListShown;

	public SwipeListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.swipe_list_content, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ensureList();
	}

	/**
	 * Detach from list view.
	 */
	@Override
	public void onDestroyView() {
		mHandler.removeCallbacks(mRequestFocus);
		mList = null;
		mListShown = false;
		mEmptyView = mProgressContainer = mListContainer = null;
		mStandardEmptyView = null;
		super.onDestroyView();
	}

	/**
	 * Provide the cursor for the list view.
	 */
	public void setListAdapter(SwipeAdapter adapter) {
		boolean hadAdapter = mAdapter != null;
		mAdapter = adapter;
		if (mList != null) {
			mList.setAdapter(adapter);
			if (!mListShown && !hadAdapter) {
				// The list was hidden, and previously didn't have an
				// adapter. It is now time to show it.
				setListShown(true, getView().getWindowToken() != null);
			}
		}
	}

	/**
	 * Set the currently selected list item to the specified position with the
	 * adapter's data
	 * 
	 * @param position
	 */
	public void setSelection(int position) {
		ensureList();
		mList.setSelection(position);
	}

	/**
	 * Get the position of the currently selected list item.
	 */
	public int getSelectedItemPosition() {
		ensureList();
		return mList.getSelectedItemPosition();
	}

	/**
	 * Get the cursor row ID of the currently selected list item.
	 */
	public long getSelectedItemId() {
		ensureList();
		return mList.getSelectedItemId();
	}

	/**
	 * Get the activity's list view widget.
	 */
	public ListView getListView() {
		ensureList();
		return mList;
	}

	/**
	 * The default content for a ListFragment has a TextView that can be shown
	 * when the list is empty. If you would like to have it shown, call this
	 * method to supply the text it should use.
	 */
	public void setEmptyText(CharSequence text) {
		ensureList();
		if (mStandardEmptyView == null) {
			throw new IllegalStateException(
					"Can't be used with a custom content view");
		}
		mStandardEmptyView.setText(text);
		if (mEmptyText == null) {
			mList.setEmptyView(mStandardEmptyView);
		}
		mEmptyText = text;
	}

	/**
	 * Control whether the list is being displayed. You can make it not
	 * displayed if you are waiting for the initial data to show in it. During
	 * this time an indeterminant progress indicator will be shown instead.
	 * 
	 * <p>
	 * Applications do not normally need to use this themselves. The default
	 * behavior of ListFragment is to start with the list not being shown, only
	 * showing it once an adapter is given with
	 * {@link #setListAdapter(ListAdapter)}. If the list at that point had not
	 * been shown, when it does get shown it will be do without the user ever
	 * seeing the hidden state.
	 * 
	 * @param shown
	 *            If true, the list view is shown; if false, the progress
	 *            indicator. The initial value is true.
	 */
	public void setListShown(boolean shown) {
		setListShown(shown, true);
	}

	/**
	 * Like {@link #setListShown(boolean)}, but no animation is used when
	 * transitioning from the previous state.
	 */
	public void setListShownNoAnimation(boolean shown) {
		setListShown(shown, false);
	}

	/**
	 * Control whether the list is being displayed. You can make it not
	 * displayed if you are waiting for the initial data to show in it. During
	 * this time an indeterminant progress indicator will be shown instead.
	 * 
	 * @param shown
	 *            If true, the list view is shown; if false, the progress
	 *            indicator. The initial value is true.
	 * @param animate
	 *            If true, an animation will be used to transition to the new
	 *            state.
	 */
	private void setListShown(boolean shown, boolean animate) {
		ensureList();
		if (mProgressContainer == null) {
			throw new IllegalStateException(
					"Can't be used with a custom content view");
		}
		if (mListShown == shown) {
			return;
		}
		mListShown = shown;
		if (shown) {
			if (animate) {
				mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
						getActivity(), android.R.anim.fade_out));
				mListContainer.startAnimation(AnimationUtils.loadAnimation(
						getActivity(), android.R.anim.fade_in));
			} else {
				mProgressContainer.clearAnimation();
				mListContainer.clearAnimation();
			}
			mProgressContainer.setVisibility(View.GONE);
			mListContainer.setVisibility(View.VISIBLE);
		} else {
			if (animate) {
				mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
						getActivity(), android.R.anim.fade_in));
				mListContainer.startAnimation(AnimationUtils.loadAnimation(
						getActivity(), android.R.anim.fade_out));
			} else {
				mProgressContainer.clearAnimation();
				mListContainer.clearAnimation();
			}
			mProgressContainer.setVisibility(View.VISIBLE);
			mListContainer.setVisibility(View.GONE);
		}
	}

	/**
	 * Get the ListAdapter associated with this activity's ListView.
	 */
	public SwipeAdapter getListAdapter() {
		return mAdapter;
	}

	private void ensureList() {
		if (mList != null) {
			return;
		}
		View root = getView();
		if (root == null) {
			throw new IllegalStateException("Content view not yet created");
		}
		mStandardEmptyView = (TextView) root.findViewById(R.id.swipe_empty);
		if (mStandardEmptyView == null) {
			mEmptyView = root.findViewById(android.R.id.empty);
		} else {
			mStandardEmptyView.setVisibility(View.GONE);
		}
		mProgressContainer = root.findViewById(R.id.progressContainer);
		mListContainer = root.findViewById(R.id.listContainer);
		View rawListView = root.findViewById(R.id.swipe_list);
		if (!(rawListView instanceof SwipeListView)) {
			throw new RuntimeException(
					"Content has view with id attribute 'R.id.swipe_list' "
							+ "that is not a ListView class");
		}
		mList = (SwipeListView) rawListView;
		if (mList == null) {
			throw new RuntimeException(
					"Your content must have a ListView whose id attribute is "
							+ "'R.id.swipe_list'");
		}
		if (mEmptyView != null) {
			mList.setEmptyView(mEmptyView);
		} else if (mEmptyText != null) {
			mStandardEmptyView.setText(mEmptyText);
			mList.setEmptyView(mStandardEmptyView);
		}
		mListShown = true;
//		mList.setOnItemClickListener(this);
		mList.setSwipeListViewListener(this);
		mList.setSwipeOpenOnLongPress(false);
		if (mAdapter != null) {
			SwipeAdapter adapter = mAdapter;
			mAdapter = null;
			setListAdapter(adapter);
		} else {
			// We are starting without an adapter, so assume we won't
			// have our data right away and start with the progress indicator.
			if (mProgressContainer != null) {
				setListShown(false, false);
			}
		}
		mHandler.post(mRequestFocus);
	}

//	@Override
//	public void onItemClick(AdapterView<?> parent, View view, int position,
//			long id) {
//	}

	@Override
	public void onOpened(int position, boolean toRight) {

	}

	@Override
	public void onClosed(int position, boolean fromRight) {
	}

	@Override
	public void onListChanged() {

	}

	@Override
	public void onMove(int position, float x) {
	}

	@Override
	public void onStartOpen(int position, int action, boolean right) {

	}

	@Override
	public void onStartClose(int position, boolean right) {

	}

	@Override
	public void onClickFrontView(int position) {

	}

	@Override
	public void onClickBackView(int position) {

	}

	@Override
	public void onDismiss(int[] reverseSortedPositions) {
		Toast.makeText(getActivity(), "onDismiss", Toast.LENGTH_SHORT).show();
	}

	@Override
	public int onChangeSwipeMode(int position) {
		return SwipeListView.SWIPE_MODE_DEFAULT;
	}

}