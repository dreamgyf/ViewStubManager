package com.dreamgyf.viewstubmanager;

import android.view.View;
import android.view.ViewStub;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

/**
 * 每次使用ViewStub，inflate、切换、判断是否加载等，
 * 都需要很多重复代码，为了简化这些工作，定义了这个管理类，
 * 用来方便的管理多个ViewStub
 *
 * @Author: dreamgyf
 * @Date: 2021/11/3
 */
public class ViewStubManager {

	public static class ViewStubHolder {

		public View rootView;

		public ViewStubHolder(View rootView) {
			this.rootView = rootView;
		}
	}

	/**
	 * 当ViewStub的内部布局首次被创建时触发
	 */
	public interface OnViewStubInflateListener<T extends ViewStubHolder> {
		void onViewStubInflate(T holder);
	}

	private static class ViewStubStorage<T extends ViewStubHolder> {
		public ViewStub viewStub;
		public Class<T> holderClz;
		public OnViewStubInflateListener<T> onViewStubInflateListener;
		public T holder;
	}

	private final Map<String, ViewStubStorage<? extends ViewStubHolder>> mViewStubMap = new HashMap<>();

	public void put(String id, ViewStub viewStub) {
		put(id, viewStub, ViewStubHolder.class, null);
	}

	public void put(String id, ViewStub viewStub, @Nullable OnViewStubInflateListener<ViewStubHolder> listener) {
		put(id, viewStub, ViewStubHolder.class, listener);
	}

	public <T extends ViewStubHolder> void put(String id, ViewStub viewStub, Class<T> holderClz) {
		put(id, viewStub, holderClz, null);
	}

	public <T extends ViewStubHolder> void put(String id, ViewStub viewStub, Class<T> holderClz, @Nullable OnViewStubInflateListener<T> listener) {
		ViewStubStorage<T> storage = new ViewStubStorage<>();
		storage.viewStub = viewStub;
		storage.holderClz = holderClz;
		storage.onViewStubInflateListener = listener;
		mViewStubMap.put(id, storage);
	}

	public <T extends ViewStubHolder> T inflate(String id) {
		ViewStubStorage storage = mViewStubMap.get(id);

		if (storage == null) {
			return null;
		}

		ViewStub viewStub = storage.viewStub;
		if (!isInflate(viewStub)) {
			View view = viewStub.inflate();

			try {
				storage.holder = createViewStubHolder(storage.holderClz, view);
			} catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}

			mViewStubMap.put(id, storage);

			if (storage.holder != null && storage.onViewStubInflateListener != null) {
				storage.onViewStubInflateListener.onViewStubInflate(storage.holder);
			}
		}

		return (T) storage.holder;
	}

	public <T extends ViewStubHolder> T getViewStubHolderById(String id) {
		return inflate(id);
	}

	public boolean isInflate(String id) {
		ViewStubStorage<? extends ViewStubHolder> storage = mViewStubMap.get(id);

		if (storage == null) {
			return false;
		}

		return isInflate(storage.viewStub);
	}

	public void show(String id, boolean show) {
		if (show) {
			ViewStubHolder holder = getViewStubHolderById(id);

			if (holder != null) {
				holder.rootView.setVisibility(View.VISIBLE);
			}
		} else {
			if (isInflate(id)) {
				ViewStubHolder holder = getViewStubHolderById(id);

				if (holder != null) {
					holder.rootView.setVisibility(View.GONE);
				}
			}
		}
	}

	private boolean isInflate(ViewStub viewStub) {
		if (viewStub == null) {
			return false;
		}

		return viewStub.getParent() == null;
	}

	private ViewStubHolder createViewStubHolder(Class<? extends ViewStubHolder> holderClz, View view) throws Throwable {
		Constructor<?> constructor = holderClz.getConstructor(View.class);
		return (ViewStubHolder) constructor.newInstance(view);
	}

}
