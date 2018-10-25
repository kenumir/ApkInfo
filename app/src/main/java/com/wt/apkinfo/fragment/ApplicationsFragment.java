package com.wt.apkinfo.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SearchEvent;
import com.hivedi.console.Console;
import com.wt.apkinfo.BuildConfig;
import com.wt.apkinfo.R;
import com.wt.apkinfo.R2;
import com.wt.apkinfo.activity.ApplicationDetailsActivity;
import com.wt.apkinfo.dialog.RateAppDialog;
import com.wt.apkinfo.entity.ApplicationEntity;
import com.wt.apkinfo.util.ImageLoader;
import com.wt.apkinfo.util.IntentHelper;
import com.wt.apkinfo.util.UserEngagement;
import com.wt.apkinfo.viewmodel.ApplicationListViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ApplicationsFragment extends Fragment {

	@BindView(R2.id.recycler) RecyclerView recycler;
	@BindView(R2.id.overlayFrame) FrameLayout overlayFrame;
	@BindView(R2.id.overlayNoApps) LinearLayout overlayNoApps;
	@BindView(R2.id.toolbar) Toolbar toolbar;

	private ApplicationsListAdapter adapter;
	private String searchText = null;
	private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchAction = new Runnable() {
        @Override
        public void run() {
            if (isAdded()) {
                if (BuildConfig.DEBUG) {
                    Console.logd("search: " + searchText);
                }
                Answers.getInstance().logSearch(new SearchEvent().putQuery(searchText));
                ViewModelProviders.of(ApplicationsFragment.this)
                        .get(ApplicationListViewModel.class)
                        .search(searchText);
            }
        }
    };
    private SearchView search;
    private MenuItem searchMenuItem;

	public ApplicationsFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View res = inflater.inflate(R.layout.fragment_applications, container, false);
		ButterKnife.bind(this, res);

		if (savedInstanceState != null) {
			searchText = savedInstanceState.getString("searchText");
		} else {
			ApplicationListViewModel model = ViewModelProviders.of(this).get(ApplicationListViewModel.class);
			String filter = model.getFilter();
			if (filter != null) {
				model.search(null);
			}
		}

		Menu menu = toolbar.getMenu();

		search = new SearchView(toolbar.getContext());
		search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				searchText = query;
                searchHandler.removeCallbacks(searchAction);
                searchHandler.postDelayed(searchAction, 300);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				searchText = newText;
                searchHandler.removeCallbacks(searchAction);
                searchHandler.postDelayed(searchAction, 300);
				return false;
			}
		});

		searchMenuItem = menu.add(R.string.main_menu_search)
			.setIcon(R.drawable.ic_search_white_24dp)
			.setActionView(search);
		searchMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		if (searchText != null) {
			search.setQuery(searchText, false);
			searchMenuItem.expandActionView();
		}

		menu.add(R.string.main_menu_about)
			.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem menuItem) {
					if (getActivity() != null) {
						new MaterialDialog.Builder(getActivity())
								.title(R.string.about_title)
								.content(getResources().getString(R.string.about_desc, BuildConfig.VERSION_NAME))
								.positiveText(R.string.label_close)
								.neutralText(R.string.about_open)
								.onNeutral(new MaterialDialog.SingleButtonCallback() {
									@Override
									public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
										IntentHelper.openInBrowser(getActivity(), "https://plus.google.com/u/0/+Micha%C5%82Szwarc");
									}
								})
								.build()
								.show();
					}
					return false;
				}
			})
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		menu.add(R.string.rate_title)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (getFragmentManager() != null) {
                            RateAppDialog d = new RateAppDialog();
                            d.setTargetFragment(ApplicationsFragment.this, 1);
                            d.show(getFragmentManager(), "rate");
                        }
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
				public void onItemClick(View v, ApplicationEntity item, ApplicationsItemHolder holder) {
					if (getActivity() != null) {
						ApplicationDetailsActivity.start(getActivity(), item.id, item.name, holder.icon1);
                        UserEngagement.incUserRateConditionValue(getActivity());
					}
				}
			});
		}
		recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		recycler.setAdapter(adapter);
		ApplicationListViewModel viewModel = ViewModelProviders.of(this).get(ApplicationListViewModel.class);
		viewModel.setup(null);
		viewModel.getApplications().observe(this, new Observer<List<ApplicationEntity>>() {
			@Override
			public void onChanged(@Nullable List<ApplicationEntity> apps) {
				if (apps != null) {
					adapter.setData(apps);
					overlayFrame.setVisibility(View.GONE);
					overlayNoApps.setVisibility(apps.size() > 0 ? View.GONE : View.VISIBLE);
				} else {
					adapter.setData(null);
					overlayFrame.setVisibility(View.GONE);
					overlayNoApps.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		outState.putString("searchText", searchText);
		super.onSaveInstanceState(outState);
	}

	public boolean onBackAction() {
        if (searchText != null && searchText.length() > 0) {
			search.setQuery(null, true);
			searchMenuItem.collapseActionView();
            return true;
        }
        return false;
	}

	private interface OnItemClick {
		void onItemClick(View v, ApplicationEntity item, ApplicationsItemHolder holder);
	}

	private static class ApplicationsListAdapter extends RecyclerView.Adapter<ApplicationsItemHolder> {

		private List<ApplicationEntity> mData;
		private OnItemClick mOnItemClick;
		private ImageLoader mImageLoader;

		ApplicationsListAdapter(OnItemClick click) {
			mOnItemClick = click;
            mImageLoader = ImageLoader.get();
		}

		public void setData(List<ApplicationEntity> d) {
			mData = d;
			notifyDataSetChanged();
		}

		@NonNull
		@Override
		public ApplicationsItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			return new ApplicationsItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_application, parent, false));
		}

		@Override
		public void onBindViewHolder(@NonNull final ApplicationsItemHolder holder, int position) {
			final ApplicationEntity entry = mData.get(position);
			holder.text1.setText(entry.getName());
			holder.text2.setText(entry.getId());
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (mOnItemClick != null) {
						mOnItemClick.onItemClick(view, entry, holder);
					}
				}
			});

            mImageLoader.load(entry.getIconUri(), holder.icon1);

			holder.update(entry);
		}

		@Override
		public int getItemCount() {
			return mData != null ? mData.size() : 0;
		}
	}

	@SuppressWarnings("WeakerAccess")
	public static class ApplicationsItemHolder extends RecyclerView.ViewHolder {

		@BindView(R2.id.text1) TextView text1;
		@BindView(R2.id.text2) TextView text2;
		@BindView(R2.id.icon1) ImageView icon1;

		public ApplicationsItemHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		public void update(ApplicationEntity entry) {
			ViewCompat.setTransitionName(icon1, "transition_" + entry.id);
			//icon1.setTransitionName("transition_" + entry.id);
		}
	}
}
