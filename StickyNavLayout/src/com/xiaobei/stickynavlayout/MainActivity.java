package com.xiaobei.stickynavlayout;

import java.util.ArrayList;

import android.app.Activity;
import android.media.MediaCodecList;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
public class MainActivity extends Activity implements OnClickListener{
	private ImageView searchBtn;
	private ImageView backBtn;
	private ImageView headIv;
	private TextView desTv;
	private TextView titleTv, tv_subscribe_title;
	private TextView subsribeTv;
	private ColumnHorizontalScrollView mColumnHorizontalScrollView;
	LinearLayout mRadioGroup_content;
	LinearLayout ll_more_columns;
	RelativeLayout rl_column;
	private int columnSelectIndex = 0;
	public ImageView shade_left;
	public ImageView shade_right;
	private int mScreenWidth = 0;
	private int mItemWidth = 0;
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	public final static int CHANNELREQUEST = 1;
	public final static int CHANNELRESULT = 10;
	private android.support.v4.view.ViewPager mViewPager;
	private boolean flag = false;
	private NewsFragmentPagerAdapter mAdapetr;
	private ArrayList<Category> mList = new ArrayList<Category>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mScreenWidth = getWindowsWidth(this);
		mItemWidth = mScreenWidth / 5;
		initView();
		initData();
		initTabColumn();
	}

	@Override
	public void onClick(View v) {
		
	}
	private void initView() {
		mColumnHorizontalScrollView = (ColumnHorizontalScrollView) findViewById(R.id.mColumnHorizontalScrollView);
		mRadioGroup_content = (LinearLayout) findViewById(R.id.mRadioGroup_content);
		rl_column = (RelativeLayout) findViewById(R.id.rl_column);
		mViewPager = (ViewPager) findViewById(R.id.id_stickynavlayout_viewpager);
		shade_left = (ImageView) findViewById(R.id.shade_left);
		shade_right = (ImageView) findViewById(R.id.shade_right);
		backBtn = (ImageView) findViewById(R.id.subscribe_list_iv_back);
		searchBtn = (ImageView) findViewById(R.id.subscribe_list_iv_search);
		desTv = (TextView) findViewById(R.id.subscribe_list_tv_des);
		titleTv = (TextView) findViewById(R.id.subscribe_list_tv_title);
		tv_subscribe_title = (TextView) findViewById(R.id.tv_subscribe_title);
		headIv = (ImageView) findViewById(R.id.subscribe_list_iv_head_image);
		searchBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		
	}
	private void initData(){
		for(int i=0;i<=10;i++){
			Category category = new Category();
			category.setName("ÄÚÈÝ"+i);
			category.setId(i+"");
			mList.add(category);
		}
	}
	private void initTabColumn() {
		mRadioGroup_content.removeAllViews();
		int count = mList.size();
		if (count > 0) {
			mColumnHorizontalScrollView.setParam(this, mScreenWidth, mRadioGroup_content, shade_left, shade_right,
					ll_more_columns, rl_column);
			for (int i = 0; i < count; i++) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				params.leftMargin = 5;
				params.rightMargin = 5;
				TextView columnTextView = new TextView(this);
				columnTextView.setTextAppearance(this, R.style.top_category_scroll_view_item_text);
				columnTextView.setGravity(Gravity.CENTER);
				columnTextView.setPadding(10, 5, 10, 5);
				columnTextView.setId(i);
				columnTextView.setText(mList.get(i).getName());
				columnTextView.setTextColor(getResources().getColorStateList(R.color.color_video_nomrmal));
				if (columnSelectIndex == i) {
					columnTextView.setSelected(true);
					columnTextView.setTextColor(
							getResources().getColorStateList(R.color.color_red));
					columnTextView.setTextAppearance(this,R.style.top_category_scroll_view_item_text_select);
				}
				columnTextView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
							TextView localView = (TextView) mRadioGroup_content.getChildAt(i);
							if (localView != v) {
								localView.setSelected(false);
								localView.setTextColor(getResources().getColorStateList(R.color.color_video_nomrmal));
								localView.setTextAppearance(MainActivity.this,R.style.top_category_scroll_view_item_text);
							} else {
								localView.setSelected(true);
								localView.setTextColor(getResources().getColorStateList(R.color.color_red));
								localView.setTextAppearance(MainActivity.this,R.style.top_category_scroll_view_item_text_select);
								mViewPager.setCurrentItem(i,false);
							}
						}
					}
				});
				mRadioGroup_content.addView(columnTextView, i, params);
			}
		} else {
			mColumnHorizontalScrollView.setVisibility(View.GONE);
		}

	}
	private void selectTab(final int tab_postion) {
		columnSelectIndex = tab_postion;
		for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					TextView checkView = (TextView) mRadioGroup_content.getChildAt(tab_postion);
					int k = checkView.getMeasuredWidth();
					int l = checkView.getLeft();
					int i2 = l + k / 2 - mScreenWidth / 2;
					mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
					checkView.setTextColor(getResources().getColorStateList(R.color.color_red));
					checkView.setTextAppearance(MainActivity.this,R.style.top_category_scroll_view_item_text_select);
				}
			}, 200);
		}
		for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
			View checkView = mRadioGroup_content.getChildAt(j);
			boolean ischeck;
			if (j == tab_postion) {
				ischeck = true;
			} else {
				ischeck = false;
				((TextView) checkView).setTextColor(getResources().getColorStateList(R.color.color_video_nomrmal));
				((TextView) checkView).setTextAppearance(MainActivity.this,R.style.top_category_scroll_view_item_text);
			}
			checkView.setSelected(ischeck);
		}
	}
	private void initFragment() {
		fragments.clear();
		int count = mList.size();
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				Bundle data = new Bundle();
				// ·ÖÀàid
				data.putString("id", mList.get(i).getId());
				VideoFragment videoFragment = new ListFragment();
				videoFragment.setArguments(data);
				fragments.add(videoFragment);
			}
			NewsFragmentPagerAdapter mAdapetr = new NewsFragmentPagerAdapter(getSupportFragmentManager(), fragments);
			mViewPager.setAdapter(mAdapetr);
			mViewPager.setOnPageChangeListener(pageListener);
		}

	}
	public OnPageChangeListener pageListener = new OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			selectTab(position);
		}
	};
	public  static int getWindowsWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	public class NewsFragmentPagerAdapter extends FragmentPagerAdapter {
		private ArrayList<Fragment> fragments;
		private FragmentManager fm;

		public NewsFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
			this.fm = fm;
		}

		public NewsFragmentPagerAdapter(FragmentManager fm,
				ArrayList<Fragment> fragments) {
			super(fm);
			this.fm = fm;
			this.fragments = fragments;
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void setFragments(ArrayList<Fragment> fragments) {
			if (this.fragments != null) {
				FragmentTransaction ft = fm.beginTransaction();
				for (Fragment f : this.fragments) {
					ft.remove(f);
				}
				ft.commit();
				ft = null;
				fm.executePendingTransactions();
			}
			this.fragments = fragments;
			notifyDataSetChanged();
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			Object obj = super.instantiateItem(container, position);
			return obj;
		}
	}
}
