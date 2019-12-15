package com.wt.apkinfo.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hivedi.era.ERA;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wt.apkinfo.R;
import com.wt.apkinfo.dialog.InfoListDialog;
import com.wt.apkinfo.entity.ApplicationDetailsEntity;
import com.wt.apkinfo.entity.ComponentInfo;
import com.wt.apkinfo.util.BitmapUtil;
import com.wt.apkinfo.util.DateTime;
import com.wt.apkinfo.util.ImageLoader;
import com.wt.apkinfo.util.ViewUtil;
import com.wt.apkinfo.viewmodel.ApplicationDetailsViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kenumir on 17.09.2017.
 *
 */

public class ApplicationDetailsActivity extends AppCompatActivity implements InfoListDialog.OnGetData, Target {

	public static final String KEY_APP_ID = "application_id";
	public static final String KEY_APP_NAME = "application_name";

	public static void start(@NonNull Activity ctx, @NonNull String appId, @NonNull String appName, @NonNull View holderImage) {
		Intent it = new Intent(ctx, ApplicationDetailsActivity.class);
		it.putExtra(ApplicationDetailsActivity.KEY_APP_ID, appId);
		it.putExtra(ApplicationDetailsActivity.KEY_APP_NAME, appName);

		// Bug in activity transitions, fixed in android 6.x
		// https://issuetracker.google.com/issues/37121916
		if (Build.VERSION.SDK_INT >= 23) {
			View decorView = ctx.getWindow().getDecorView();
			View statusBar = decorView.findViewById(android.R.id.statusBarBackground);
			View navigationBar = decorView.findViewById(android.R.id.navigationBarBackground);
			List<Pair<View, String>> el = new ArrayList<>();
			el.add(Pair.create(holderImage, "transition_" + appId));
			if (statusBar != null) {
				el.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
			}
			if (navigationBar != null) {
				el.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
			}
			//noinspection unchecked
			ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(ctx, el.toArray(new Pair[0]));
			ctx.startActivity(it, options.toBundle());
		} else {
			ctx.startActivity(it);
		}
	}

	private Toolbar toolbar;
	private RecyclerView recycler;
	private AppInfoAdapter mAppInfoAdapter;
	private ComponentInfo[] selectedData;
	private MenuItem shareMenuItem, appInfoMenu, playStoreMenu, runMenu;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent() == null) {
			finish();
			return;
		}
		setContentView(R.layout.activity_application_details);

		toolbar = findViewById(R.id.toolbar);
		recycler = findViewById(R.id.recycler);

		final String appId = getIntent().getStringExtra(KEY_APP_ID);
		final String appName = getIntent().getStringExtra(KEY_APP_NAME);

		shareMenuItem = toolbar.getMenu().add(R.string.app_details_share).setVisible(false);
        appInfoMenu = toolbar.getMenu().add(R.string.app_details_info).setVisible(false);
		playStoreMenu = toolbar.getMenu().add(R.string.app_details_play_store).setVisible(false);
		runMenu = toolbar.getMenu().add(R.string.app_details_run).setVisible(false);

		ApplicationDetailsViewModel.Factory factory = new ApplicationDetailsViewModel.Factory(getApplication(), appId);
		final ApplicationDetailsViewModel model = ViewModelProviders.of(this, factory).get(ApplicationDetailsViewModel.class);
		model.getApplicationDetails().observe(this, new Observer<ApplicationDetailsEntity>() {
			@Override
			public void onChanged(final @Nullable ApplicationDetailsEntity productEntity) {
				model.setProduct(productEntity);
				if (productEntity != null) {
					toolbar.setTitle(productEntity.getName());
					toolbar.setSubtitle(productEntity.getId());
					toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

					ImageLoader.get().load(productEntity.getIconUri(), ApplicationDetailsActivity.this);

					mAppInfoAdapter.setData(productEntity);
					supportStartPostponedEnterTransition();
					shareMenuItem.setOnMenuItemClickListener(menuItem -> {
						ApkListActivity.start(ApplicationDetailsActivity.this, appId, appName, new File(productEntity.apkFile).getParent());

						/*
						try {
							File dir = new File(productEntity.apkFile).getParentFile();

							File srcFile = new File(productEntity.apkFile);
							Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".fileprovider", srcFile);
							Intent share = new Intent();
							share.setAction(Intent.ACTION_SEND);
							share.setType("application/vnd.android.package-archive");
							share.putExtra(Intent.EXTRA_STREAM, photoURI);
							share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
							startActivity(Intent.createChooser(share, getResources().getString(R.string.app_details_share)));
							Console.logi("file=" + srcFile);
						} catch (Exception e) {
							Console.loge("e=" + e, e);
							Crashlytics.logException(e);
						}
						 */
						return false;
					}).setVisible(true);
                    appInfoMenu.setOnMenuItemClickListener(menuItem -> {
						ERA.log("Open app info: " + appId);
						try {
							Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
							intent.setData(Uri.parse("package:" + appId));
							startActivity(intent);
						} catch (ActivityNotFoundException e) {
							ERA.logException(e);
						}
						return false;
					}).setVisible(true);
					playStoreMenu.setOnMenuItemClickListener(menuItem -> {
						ERA.log("Open Play Store: " + appId);
						try {
							startActivity(
									new Intent(
											Intent.ACTION_VIEW,
											Uri.parse("market://details?id=" + appId)
									)
							);
							return true;
						} catch (Exception e) {
							try {
								startActivity(
										new Intent(
												Intent.ACTION_VIEW,
												Uri.parse("https://play.google.com/store/apps/details?id=" + appId)
										)
								);
								return true;
							} catch (Exception e2) {
								ERA.logException(e2);
							}
						}
						return false;
					}).setVisible(true);
					runMenu.setOnMenuItemClickListener(menuItem -> {
						ERA.log("Run: " + appId);
						Intent runIntent = getPackageManager().getLaunchIntentForPackage(appId);
						if (runIntent != null) {
							startActivity(runIntent);
						} else {
							Toast.makeText(getApplicationContext(), R.string.app_details_toast_no_launcher_activity, Toast.LENGTH_SHORT).show();
						}
						return false;
					}).setVisible(true);
				} else {
					Toast.makeText(getApplicationContext(), R.string.app_details_toast_load_info, Toast.LENGTH_LONG).show();
					finish();
				}
			}
		});
		toolbar.setTitle(appName);
		toolbar.setSubtitle(appId);
		toolbar.setNavigationIcon(R.drawable.theme_round_icon);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				supportFinishAfterTransition();
			}
		});
		toolbar.setNavigationContentDescription(appId);
		if (Build.VERSION.SDK_INT >= 23) {
			supportPostponeEnterTransition();
			View navIcon = ViewUtil.findViewWithContentDescription(toolbar, appId);
			if (navIcon != null) {
				navIcon.setTransitionName("transition_" + appId);
			}
			Transition fade = new Fade();
			fade.excludeTarget(android.R.id.statusBarBackground, true);
			fade.excludeTarget(android.R.id.navigationBarBackground, true);
			getWindow().setExitTransition(fade);
			getWindow().setEnterTransition(fade);
		}

		mAppInfoAdapter = new AppInfoAdapter(getResources(), new OnHeaderClick() {
			@Override
			public void onHeaderClick(ItemHeader item) {
				selectedData = item.data;
				// IllegalStateException:  Can not perform this action after onSaveInstanceState
				ERA.log("isFinishing=" + isFinishing());
				InfoListDialog.Companion
						.newInstance(item.title)
						.show(getSupportFragmentManager(), "dialog_info");
			}
		}, item -> {
			ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			if (clipboard != null) {
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

	@Override
	public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
		toolbar.setNavigationIcon(BitmapUtil.bitmapToDrawable(this, bitmap));
	}

	@Override
	public void onBitmapFailed(Drawable errorDrawable) {
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
	}

	@Override
	public void onPrepareLoad(Drawable placeHolderDrawable) {
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
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
			data.add(new ComponentInfo(res.getString(R.string.app_details_target_sdk), Integer.toString(entity.targetSdkVersion)));
			if (entity.minSdkVersion > 0) {
				data.add(new ComponentInfo(res.getString(R.string.app_details_min_sdk), Integer.toString(entity.minSdkVersion)));
			}
			data.add(new ComponentInfo(
					res.getString(R.string.app_details_installation_update_time),
					DateTime.formatFull(entity.firstInstallTime) + "/" + DateTime.formatFull(entity.lastUpdateTime)
			));

			if (entity.signatures != null) {
				data.add(new ComponentInfo(res.getString(R.string.app_details_signature), entity.signatures[0]));
			}

			data.add(new ComponentInfo("Data Directory", entity.dataDir));
			data.add(new ComponentInfo("Native Library Directory", entity.nativeLibraryDir));
			data.add(new ComponentInfo("Installer Package", TextUtils.isEmpty(entity.installerPackage) ?
					res.getString(R.string.app_details_none) :
					entity.installerPackage));

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

		@NonNull
		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			switch (viewType) {
				case 1: return new HeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false));
				default:
				case 2: return new ComponentInfoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_component_info, parent, false));
			}
			//return null;
		}

		@Override
		public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
			if (holder instanceof HeaderHolder) {
				final ItemHeader d = (ItemHeader) data.get(position);
				((HeaderHolder) holder).setTitle(d.title);
				holder.itemView.setOnClickListener(view -> {
					if (mOnHeaderClick != null) {
						mOnHeaderClick.onHeaderClick(d);
					}
				});
			} else if (holder instanceof ComponentInfoHolder) {
				final ComponentInfo d = (ComponentInfo) data.get(position);
				((ComponentInfoHolder) holder).update(d);
				holder.itemView.setOnLongClickListener(view -> {
					if (mOnComponentLongClick != null) {
						mOnComponentLongClick.onComponentLongClick(d);
					}
					return false;
				});
			}
		}

		@Override
		public int getItemCount() {
			return data.size();
		}
	}
}
