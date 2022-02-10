package com.dreamgyf.viewstubmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	private final String VIEW_STUB_ID_1 = "view_stub_view_1";
	private final String VIEW_STUB_ID_2 = "view_stub_view_2";

	private ViewStubManager mViewStubManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViewStub();
		initListener();
	}

	private void initViewStub() {
		mViewStubManager = new ViewStubManager();
		mViewStubManager.put(VIEW_STUB_ID_1, findViewById(R.id.view_stub_1), ViewStub1Holder.class, new ViewStubManager.OnViewStubInflateListener<ViewStub1Holder>() {
			@Override
			public void onViewStubInflate(ViewStub1Holder holder) {
				holder.textView.setText("View 1 have been Inflate");
			}
		});
		mViewStubManager.put(VIEW_STUB_ID_2, findViewById(R.id.view_stub_2), ViewStub2Holder.class, new ViewStubManager.OnViewStubInflateListener<ViewStub2Holder>() {
			@Override
			public void onViewStubInflate(ViewStub2Holder holder) {
				holder.textView.setText("View 2 have been Inflate");
			}
		});
	}

	private void initListener() {
		findViewById(R.id.btn1).setOnClickListener(this);
		findViewById(R.id.btn2).setOnClickListener(this);
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn1:
				mViewStubManager.show(VIEW_STUB_ID_1, true);
				mViewStubManager.show(VIEW_STUB_ID_2, false);
				break;
			case R.id.btn2:
				mViewStubManager.show(VIEW_STUB_ID_1, false);
				mViewStubManager.show(VIEW_STUB_ID_2, true);
				break;
		}
	}

	private static class ViewStub1Holder extends ViewStubManager.ViewStubHolder {

		public TextView textView;

		public ViewStub1Holder(View rootView) {
			super(rootView);
			textView = rootView.findViewById(R.id.tv_view_1);
		}
	}

	private static class ViewStub2Holder extends ViewStubManager.ViewStubHolder {

		public TextView textView;

		public ViewStub2Holder(View rootView) {
			super(rootView);
			textView = rootView.findViewById(R.id.tv_view_2);
		}
	}
}