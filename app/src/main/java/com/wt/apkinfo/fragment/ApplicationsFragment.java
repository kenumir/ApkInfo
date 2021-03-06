package com.wt.apkinfo.fragment;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SearchEvent;
import com.hivedi.console.Console;
import com.wt.apkinfo.BuildConfig;
import com.wt.apkinfo.R;
import com.wt.apkinfo.activity.ApplicationDetailsActivity;
import com.wt.apkinfo.activity.RateAppActivity;
import com.wt.apkinfo.entity.ApplicationEntity;
import com.wt.apkinfo.util.ImageLoader;
import com.wt.apkinfo.util.IntentHelper;
import com.wt.apkinfo.util.UserEngagement;
import com.wt.apkinfo.viewmodel.ApplicationListViewModel;

import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class ApplicationsFragment extends Fragment {

	private RecyclerView recycler;
	private FrameLayout overlayFrame;
	private LinearLayout overlayNoApps;
	private Toolbar toolbar;

	private ApplicationsListAdapter adapter;
	private String searchText = null;
	private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchAction = () -> {
		if (isAdded()) {
			if (BuildConfig.DEBUG) {
				Console.logd("search: " + searchText);
			}
			Answers.getInstance().logSearch(new SearchEvent().putQuery(searchText));
			ViewModelProviders.of(ApplicationsFragment.this)
					.get(ApplicationListViewModel.class)
					.search(searchText);
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
		recycler = res.findViewById(R.id.recycler);
		overlayFrame = res.findViewById(R.id.overlayFrame);
		overlayNoApps = res.findViewById(R.id.overlayNoApps);
		toolbar = res.findViewById(R.id.toolbar);

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

		menu.add("Export applications info").setOnMenuItemClickListener(item -> {
			new Thread(){
				@Override
				public void run() {
					final StringBuilder sb = new StringBuilder();
					sb.append("app_id;app_name;version;version_name;installer_package;first_install_timestamp;siganture").append("\n");
					try {
						Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
						mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
						PackageManager pm = getActivity().getPackageManager();
						List<ResolveInfo> pkgAppsList = pm.queryIntentActivities(mainIntent, 0);
						if (pkgAppsList != null && pkgAppsList.size() > 0) {
							for(ResolveInfo ri : pkgAppsList) {
								// package, name, version, version name, installer package, first install timestamp
								String appId = ri.activityInfo.packageName;
								sb.append(appId).append(";");
								sb.append(ri.activityInfo.loadLabel(pm).toString()).append(";");
								PackageInfo pi = pm.getPackageInfo(appId, PackageManager.GET_ACTIVITIES);
								sb.append(pi.versionCode).append(";");
								sb.append(pi.versionName).append(";");
								sb.append(pm.getInstallerPackageName(appId)).append(";");
								sb.append(pi.firstInstallTime).append(";");

								String sig = "";
								try {
									pi = pm.getPackageInfo(appId, PackageManager.GET_SIGNATURES);
									if (pi.signatures != null) {
										MessageDigest md = MessageDigest.getInstance("SHA");
										Signature ai = pi.signatures[0];
										md.update(ai.toByteArray());
										StringBuilder s = new StringBuilder();
										for (byte b : md.digest()) {
											s.append(":").append(String.format("%02x", b));
										}
										sig = s.substring(1).toUpperCase(Locale.US);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								sb.append(sig).append("\n");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					new Handler(Looper.getMainLooper()).post(() -> {
						Intent intent2 = new Intent(); intent2.setAction(Intent.ACTION_SEND);
						intent2.setType("text/plain");
						intent2.putExtra(Intent.EXTRA_TEXT, sb.toString() );
						startActivity(Intent.createChooser(intent2, "Info"));
					});
				}
			}.start();
			return false;
		});

		menu.add(R.string.main_menu_about)
			.setOnMenuItemClickListener(menuItem -> {
				if (getActivity() != null) {
					new MaterialDialog.Builder(getActivity())
							.title(R.string.about_title)
							.content(getResources().getString(R.string.about_desc, BuildConfig.VERSION_NAME))
							.positiveText(R.string.label_close)
							.neutralText(R.string.about_open)
							.onNeutral((dialog, which) -> IntentHelper.openInBrowser(getActivity(), "https://twitter.com/kenumir"))
							.build()
							.show();
				}
				return false;
			})
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		menu.add(R.string.rate_title)
                .setOnMenuItemClickListener(item -> {
					startActivity(new Intent(getActivity(), RateAppActivity.class));
					return false;
				})
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		if (BuildConfig.DEBUG) {
		    menu.add("[DEV] Test Crash").setOnMenuItemClickListener(item -> {
				Crashlytics.getInstance().crash();
				return false;
			});
        }
		return res;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (adapter == null) {
			adapter = new ApplicationsListAdapter((v, item, holder) -> {
				if (getActivity() != null) {
					ApplicationDetailsActivity.start(getActivity(), item.id, item.name, holder.icon1);
					UserEngagement.incUserRateConditionValue(getActivity());
				}
			});
		}
		recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		recycler.setAdapter(adapter);
		ApplicationListViewModel viewModel = ViewModelProviders.of(this).get(ApplicationListViewModel.class);
		viewModel.setup(null);
		viewModel.getApplications().observe(this, apps -> {
			if (apps != null) {
				adapter.setData(apps);
				overlayFrame.setVisibility(View.GONE);
				overlayNoApps.setVisibility(apps.size() > 0 ? View.GONE : View.VISIBLE);
			} else {
				adapter.setData(null);
				overlayFrame.setVisibility(View.GONE);
				overlayNoApps.setVisibility(View.VISIBLE);
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
            return true;
        } else if (searchMenuItem.isActionViewExpanded()) {
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
			holder.itemView.setOnClickListener(view -> {
				if (mOnItemClick != null) {
					mOnItemClick.onItemClick(view, entry, holder);
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

		public TextView text1;
		public TextView text2;
		public ImageView icon1;

		public ApplicationsItemHolder(View itemView) {
			super(itemView);
			text1 = itemView.findViewById(R.id.text1);
			text2 = itemView.findViewById(R.id.text2);
			icon1 = itemView.findViewById(R.id.icon1);
		}

		public void update(ApplicationEntity entry) {
			ViewCompat.setTransitionName(icon1, "transition_" + entry.id);
			//icon1.setTransitionName("transition_" + entry.id);
		}
	}
}
