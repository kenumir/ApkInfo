package com.wt.apkinfo.fragment;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
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
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hivedi.console.Console;
import com.wt.apkinfo.BuildConfig;
import com.wt.apkinfo.R;
import com.wt.apkinfo.R2;
import com.wt.apkinfo.activity.ApplicationDetailsActivity;
import com.wt.apkinfo.activity.MainActivity;
import com.wt.apkinfo.activity.StartAlarmReceiver;
import com.wt.apkinfo.entity.ApplicationEntity;
import com.wt.apkinfo.util.IntentHelper;
import com.wt.apkinfo.viewmodel.ApplicationListViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ApplicationsFragment extends Fragment {

	@BindView(R2.id.recycler) RecyclerView recycler;
	@BindView(R2.id.overlayFrame) FrameLayout overlayFrame;
	@BindView(R2.id.toolbar) Toolbar toolbar;

	private ApplicationsListAdapter adapter;
	private String searchText = null;

	public ApplicationsFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View res = inflater.inflate(R.layout.fragment_applications, container, false);
		ButterKnife.bind(this, res);

		if (savedInstanceState != null) {
			searchText = savedInstanceState.getString("searchText");
		} else {
			ApplicationListViewModel model = ViewModelProviders.of(getActivity()).get(ApplicationListViewModel.class);
			String filter = model.getFilter();
			if (filter != null) {
				model.search(null);
			}
		}

		Menu menu = toolbar.getMenu();

		SearchView search = new SearchView(toolbar.getContext());
		search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			private Handler handler = new Handler(Looper.getMainLooper());
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
			@Override
			public boolean onQueryTextSubmit(String query) {
				searchText = query;
				handler.removeCallbacks(delay);
				handler.postDelayed(delay, 300);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				searchText = newText;
				handler.removeCallbacks(delay);
				handler.postDelayed(delay, 300);
				return false;
			}
		});

		MenuItem searchMenuItem = menu.add(R.string.main_menu_search)
			.setIcon(R.drawable.ic_search_white_24dp)
			.setActionView(search);
		searchMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		if (searchText != null) {
			search.setQuery(searchText, false);
			searchMenuItem.expandActionView();
		}

		menu
			.add(R.string.main_menu_about)
			.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem menuItem) {
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
					return false;
				}
			})
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		menu.add("Test Alarm").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				new MaterialDialog.Builder(getActivity())
						.title("Select alarm time")
						.items("1 min", "2 min", "3 min", "5 min", "10 min")
						.itemsCallback(new MaterialDialog.ListCallback() {
							@Override
							public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
								String s = text.subSequence(0, text.toString().indexOf(" ")).toString().trim();
								long time = (60_000 * Integer.valueOf(s)) + System.currentTimeMillis();
								Console.logi("TIME: " + time);


								Context ctx = getActivity().getApplicationContext();

								Intent it = new Intent(ctx, MainActivity.class);
								Intent mIntent = new Intent(ctx, StartAlarmReceiver.class);
								PendingIntent pi = PendingIntent.getBroadcast(ctx, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
								AlarmManager mAlarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

								PendingIntent alarmEditInfo = PendingIntent.getActivity(ctx, 2, it, PendingIntent.FLAG_UPDATE_CURRENT);
								mAlarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(System.currentTimeMillis() + (60_000 * time), alarmEditInfo), pi);

								dialog.dismiss();
							}
						})
						.build()
						.show();
				return false;
			}
		});
		return res;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (adapter == null) {
			adapter = new ApplicationsListAdapter(new OnItemClick() {
				@Override
				public void onItemClick(View v, ApplicationEntity item, ApplicationsItemHolder holder) {
					Intent it = new Intent(getActivity(), ApplicationDetailsActivity.class);
					it.putExtra(ApplicationDetailsActivity.KEY_APP_ID, item.getId());
					if (Build.VERSION.SDK_INT >= 21) {
						View decorView = getActivity().getWindow().getDecorView();
						View statusBar = decorView.findViewById(android.R.id.statusBarBackground);
						View navigationBar = decorView.findViewById(android.R.id.navigationBarBackground);

						List<Pair<View, String>> el = new ArrayList<>();
						el.add(Pair.create((View) holder.icon1, "transition_" + item.id));
						if (statusBar != null) {
							el.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
						}
						if (navigationBar != null) {
							el.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
						}
						ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), el.toArray(new Pair[el.size()]));
						startActivity(it, options.toBundle());
					} else {
						startActivity(it);
					}
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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("searchText", searchText);
		super.onSaveInstanceState(outState);
	}

	private interface OnItemClick {
		void onItemClick(View v, ApplicationEntity item, ApplicationsItemHolder holder);
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
		public void onBindViewHolder(final ApplicationsItemHolder holder, int position) {
			final ApplicationEntity entry = mData.get(position);
			holder.text1.setText(entry.getName());
			holder.text2.setText(entry.getId());
			holder.icon1.setImageDrawable(entry.getIcon());
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (mOnItemClick != null) {
						mOnItemClick.onItemClick(view, entry, holder);
					}
				}
			});
			holder.update(entry);
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

		public void update(ApplicationEntity entry) {
			ViewCompat.setTransitionName(icon1, "transition_" + entry.id);
			//icon1.setTransitionName("transition_" + entry.id);
		}
	}
}
