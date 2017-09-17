package com.wt.apkinfo.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hivedi.console.Console;
import com.wt.apkinfo.BuildConfig;
import com.wt.apkinfo.R;
import com.wt.apkinfo.R2;
import com.wt.apkinfo.activity.ApplicationDetailsActivity;
import com.wt.apkinfo.entity.ApplicationEntity;
import com.wt.apkinfo.viewmodel.ApplicationListViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ApplicationsFragment extends Fragment {

	@BindView(R2.id.recycler) RecyclerView recycler;
	@BindView(R2.id.searchEdit) EditText searchEdit;
	@BindView(R2.id.overlayFrame) FrameLayout overlayFrame;
	@BindView(R2.id.toolbar) Toolbar toolbar;

	private ApplicationsListAdapter adapter;

	public ApplicationsFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View res = inflater.inflate(R.layout.fragment_applications, container, false);;
		ButterKnife.bind(this, res);
		searchEdit.setText(
			ViewModelProviders.of(getActivity())
				.get(ApplicationListViewModel.class)
				.getFilter()
		);
		searchEdit.addTextChangedListener(new TextWatcher() {
			private String searchText = null;
			private Runnable delay = new Runnable() {
				@Override
				public void run() {
					if (isAdded()) {
						if (BuildConfig.DEBUG) {
							Console.logd("search: " + searchText);
						}
						ViewModelProviders.of(getActivity())
								.get(ApplicationListViewModel.class)
								.search(searchText);
					}
				}
			};
			private Handler h = new Handler(Looper.getMainLooper());
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}
			@Override
			public void afterTextChanged(Editable editable) {
				if (BuildConfig.DEBUG) {
					Console.logd("Text changed: " + editable);
				}
				searchText = editable.toString();
				h.removeCallbacks(delay);
				h.postDelayed(delay, 300);
			}
		});
		Menu menu = toolbar.getMenu();
		menu
			.add("Search")
			.setIcon(R.drawable.ic_search_white_24dp)
			.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem menuItem) {
					return false;
				}
			})
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu
			.add("About")
			.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem menuItem) {
					return false;
				}
			})
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		return res;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (adapter == null) {
			adapter = new ApplicationsListAdapter(new OnItemClick() {
				@Override
				public void onItemClick(View v, ApplicationEntity item) {
					Intent it = new Intent(getActivity(), ApplicationDetailsActivity.class);
					it.putExtra(ApplicationDetailsActivity.KEY_APP_ID, item.getId());
					startActivity(it);
				}
			});
		}
		recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		recycler.setAdapter(adapter);
		ApplicationListViewModel viewModel = ViewModelProviders.of(getActivity()).get(ApplicationListViewModel.class);
		viewModel.setup(null);
		viewModel.getApplications().observe(this, new Observer<List<ApplicationEntity>>() {
			@Override
			public void onChanged(@Nullable List<ApplicationEntity> apps) {
				if (apps != null) {
					adapter.setData(apps);
					overlayFrame.setVisibility(View.GONE);
				} else {
					adapter.setData(null);
					overlayFrame.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private interface OnItemClick {
		void onItemClick(View v, ApplicationEntity item);
	}

	private static class ApplicationsListAdapter extends RecyclerView.Adapter<ApplicationsItemHolder> {

		private List<ApplicationEntity> mData;
		private OnItemClick mOnItemClick;

		ApplicationsListAdapter(OnItemClick click) {
			mOnItemClick = click;
		}

		public void setData(List<ApplicationEntity> d) {
			mData = d;
			notifyDataSetChanged();
		}

		@Override
		public ApplicationsItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new ApplicationsItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_application, parent, false));
		}

		@Override
		public void onBindViewHolder(ApplicationsItemHolder holder, int position) {
			final ApplicationEntity entry = mData.get(position);
			holder.text1.setText(entry.getName());
			holder.text2.setText(entry.getId());
			holder.icon1.setImageDrawable(entry.getIcon());
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (mOnItemClick != null) {
						mOnItemClick.onItemClick(view, entry);
					}
				}
			});
		}

		@Override
		public int getItemCount() {
			return mData != null ? mData.size() : 0;
		}
	}

	public static class ApplicationsItemHolder extends RecyclerView.ViewHolder {

		@BindView(R2.id.text1) TextView text1;
		@BindView(R2.id.text2) TextView text2;
		@BindView(R2.id.icon1) ImageView icon1;

		public ApplicationsItemHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
