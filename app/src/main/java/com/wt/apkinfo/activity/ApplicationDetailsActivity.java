package com.wt.apkinfo.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.metrics.AddTrace;
import com.wt.apkinfo.R;
import com.wt.apkinfo.R2;
import com.wt.apkinfo.dialog.InfoListDialog;
import com.wt.apkinfo.entity.ApplicationDetailsEntity;
import com.wt.apkinfo.entity.ComponentInfo;
import com.wt.apkinfo.util.DateTime;
import com.wt.apkinfo.util.ViewUtil;
import com.wt.apkinfo.viewmodel.ApplicationDetailsViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kenumir on 17.09.2017.
 *
 */

public class ApplicationDetailsActivity extends AppCompatActivity implements InfoListDialog.OnGetData {

	public static final String KEY_APP_ID = "application_id";

	@BindView(R2.id.toolbar) Toolbar toolbar;
	@BindView(R2.id.recycler) RecyclerView recycler;

	private AppInfoAdapter mAppInfoAdapter;
	private ComponentInfo[] selectedData;
	private String appId;
	private MenuItem shareMenuItem;

	@Override
	@AddTrace(name = "ApplicationDetailsActivity_onCreate")
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent() == null) {
			finish();
			return;
		}
		setContentView(R.layout.activity_application_details);
		ButterKnife.bind(this);

		appId = getIntent().getStringExtra(KEY_APP_ID);

		shareMenuItem = toolbar.getMenu().add(R.string.app_details_share).setVisible(false);

		ApplicationDetailsViewModel.Factory factory = new ApplicationDetailsViewModel.Factory(getApplication(), appId);
		final ApplicationDetailsViewModel model = ViewModelProviders.of(this, factory).get(ApplicationDetailsViewModel.class);
		model.getApplicationDetails().observe(this, new Observer<ApplicationDetailsEntity>() {
			@Override
			public void onChanged(final @Nullable ApplicationDetailsEntity productEntity) {
				model.setProduct(productEntity);
				if (productEntity != null) {
					toolbar.setTitle(productEntity.getName());
					toolbar.setSubtitle(productEntity.getId());
					toolbar.setNavigationIcon(productEntity.getIcon36dp());
					mAppInfoAdapter.setData(productEntity);
					shareMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem menuItem) {
							try {
								File srcFile = new File(productEntity.apkFile);
								Intent share = new Intent();
								share.setAction(Intent.ACTION_SEND);
								share.setType("application/vnd.android.package-archive");
								share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(srcFile));
								startActivity(Intent.createChooser(share, getResources().getString(R.string.app_details_share)));
							} catch (Exception e) {
								Crashlytics.logException(e);
							}
							return false;
						}
					});
					shareMenuItem.setVisible(true);

				} else {
					Toast.makeText(getApplicationContext(), R.string.app_details_toast_load_info, Toast.LENGTH_LONG).show();
					finish();
				}
			}
		});
		toolbar.setNavigationIcon(R.drawable.theme_round_icon);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		toolbar.setNavigationContentDescription(appId);
		if (Build.VERSION.SDK_INT >= 21) {
			View navIcon = ViewUtil.findViewWithContentDescription(toolbar, appId);
			if (navIcon != null) {
				navIcon.setTransitionName("transition_" + appId);
			}
		}

		mAppInfoAdapter = new AppInfoAdapter(getResources(), new OnHeaderClick() {
			@Override
			public void onHeaderClick(ItemHeader item) {
				selectedData = item.data;
				InfoListDialog d = InfoListDialog.newInstance(item.title);
				d.show(getSupportFragmentManager(), "dialog_info");
			}
		}, new OnComponentLongClick() {
			@Override
			public void onComponentLongClick(ComponentInfo item) {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("ApkInfo", item.className);
				clipboard.setPrimaryClip(clip);
				Toast.makeText(getApplicationContext(), R.string.app_details_toast_copied_to_clipboard, Toast.LENGTH_SHORT).show();
			}
		});
		recycler.setLayoutManager(new LinearLayoutManager(this));
		recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		recycler.setAdapter(mAppInfoAdapter);

	}

	@Override
	protected void onPause() {
		super.onPause();
		Fragment f = getSupportFragmentManager().findFragmentByTag("dialog_info");
		if (f != null) {
			getSupportFragmentManager()
					.beginTransaction()
					.remove(f)
					.commitNowAllowingStateLoss();
		}
	}

	@Override
	public List<ComponentInfo> onGetData() {
		if (selectedData != null) {
			return new ArrayList<>(Arrays.asList(selectedData));
		}
		return null;
	}

	private static class ItemHeader {
		public String title;
		public ComponentInfo[] data;
		ItemHeader(String h, ComponentInfo[] d) {
			title = h;
			data = d;
		}
	}

	private static class HeaderHolder extends RecyclerView.ViewHolder {
		HeaderHolder(View itemView) {
			super(itemView);
		}
		public void setTitle(String s) {
			((TextView) itemView).setText(s);
		}
	}

	private interface OnHeaderClick {
		void onHeaderClick(ItemHeader item);
	}

	private interface OnComponentLongClick {
		void onComponentLongClick(ComponentInfo item);
	}

	private static class ComponentInfoHolder extends RecyclerView.ViewHolder {
		private TextView text1;
		private TextView text2;
		ComponentInfoHolder(View itemView) {
			super(itemView);
			text1 = itemView.findViewById(R.id.text1);
			text2 = itemView.findViewById(R.id.text2);
		}
		void update(ComponentInfo s) {
			text1.setText(s.name == null || s.name.trim().length() == 0 ? "<empty>" : s.name);
			text2.setText(s.className);
		}
	}

	private static class AppInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

		private List<Object> data;
		private Resources res;
		private OnHeaderClick mOnHeaderClick;
		private OnComponentLongClick mOnComponentLongClick;

		AppInfoAdapter(Resources r, OnHeaderClick cb, OnComponentLongClick cb2) {
			res = r;
			data = new ArrayList<>();
			mOnHeaderClick = cb;
			mOnComponentLongClick = cb2;
		}

		public void setData(ApplicationDetailsEntity entity) {
			data.clear();

			data.add(new ComponentInfo(res.getString(R.string.app_details_version_name), entity.versionName));
			data.add(new ComponentInfo(res.getString(R.string.app_details_version_code), Integer.toString(entity.versionCode)));
			data.add(new ComponentInfo(res.getString(R.string.app_details_installation_time), DateTime.formatFull(entity.firstInstallTime)));
			data.add(new ComponentInfo(res.getString(R.string.app_details_update_time), DateTime.formatFull(entity.lastUpdateTime)));

			if (entity.signatures != null) {
				data.add(new ComponentInfo(res.getString(R.string.app_details_signature), entity.signatures[0]));
			}

			if (entity.metadata != null) {
				data.add(new ItemHeader(res.getString(R.string.app_details_meta, entity.metadata.length), entity.metadata));
			}
			if (entity.permissions != null) {
				data.add(new ItemHeader(res.getString(R.string.app_details_permissions, entity.permissions.length), entity.permissions));
			}
			if (entity.activities != null) {
				data.add(new ItemHeader(res.getString(R.string.app_details_activities, entity.activities.length), entity.activities));
			}
			if (entity.services != null) {
				data.add(new ItemHeader(res.getString(R.string.app_details_services, entity.services.length), entity.services));
			}
			if (entity.receivers != null) {
				data.add(new ItemHeader(res.getString(R.string.app_details_receivers, entity.receivers.length), entity.receivers));
			}
			if (entity.providers != null) {
				data.add(new ItemHeader(res.getString(R.string.app_details_providers, entity.providers.length), entity.providers));
			}

			notifyDataSetChanged();
		}

		@Override
		public int getItemViewType(int position) {
			return data.get(position) instanceof ItemHeader ? 1 : 2;
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			switch (viewType) {
				case 1: return new HeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false));
				case 2: return new ComponentInfoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_component_info, parent, false));
			}
			return null;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			if (holder instanceof HeaderHolder) {
				final ItemHeader d = (ItemHeader) data.get(position);
				((HeaderHolder) holder).setTitle(d.title);
				holder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (mOnHeaderClick != null) {
							mOnHeaderClick.onHeaderClick(d);
						}
					}
				});
			} else if (holder instanceof ComponentInfoHolder) {
				final ComponentInfo d = (ComponentInfo) data.get(position);
				((ComponentInfoHolder) holder).update(d);
				holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View view) {
						if (mOnComponentLongClick != null) {
							mOnComponentLongClick.onComponentLongClick(d);
						}
						return false;
					}
				});
			}
		}

		@Override
		public int getItemCount() {
			return data.size();
		}
	}
}
