package com.wt.apkinfo.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.perf.metrics.AddTrace;
import com.hivedi.console.Console;
import com.wt.apkinfo.R;
import com.wt.apkinfo.R2;
import com.wt.apkinfo.entity.ApplicationDetailsEntity;
import com.wt.apkinfo.entity.ComponentInfo;
import com.wt.apkinfo.viewmodel.ApplicationDetailsViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kenumir on 17.09.2017.
 *
 */

public class ApplicationDetailsActivity extends AppCompatActivity {

	public static final String KEY_APP_ID = "application_id";

	@BindView(R2.id.toolbar) Toolbar toolbar;
	@BindView(R2.id.recycler) RecyclerView recycler;

	private String appId = null;
	private AppInfoAdapter mAppInfoAdapter;

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


		ApplicationDetailsViewModel.Factory factory = new ApplicationDetailsViewModel.Factory(getApplication(), appId);
		final ApplicationDetailsViewModel model = ViewModelProviders.of(this, factory).get(ApplicationDetailsViewModel.class);
		model.getApplicationDetails().observe(this, new Observer<ApplicationDetailsEntity>() {
			@Override
			public void onChanged(@Nullable ApplicationDetailsEntity productEntity) {
				model.setProduct(productEntity);
				//Console.loge("productEntity=" + productEntity);
				if (productEntity != null) {
					toolbar.setTitle(productEntity.getName());
					toolbar.setSubtitle(productEntity.getId());
					toolbar.setNavigationIcon(productEntity.getIcon36dp());
					mAppInfoAdapter.setData(productEntity);
				} else {
					Toast.makeText(getApplicationContext(), "Error load application info", Toast.LENGTH_LONG).show();
					finish();
				}
			}
		});
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		mAppInfoAdapter = new AppInfoAdapter();
		recycler.setLayoutManager(new LinearLayoutManager(this));
		recycler.setAdapter(mAppInfoAdapter);

	}

	private static class ItemHeader {
		public String title;
		ItemHeader(String h) {
			title = h;
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

		AppInfoAdapter() {
			data = new ArrayList<>();
		}

		public void setData(ApplicationDetailsEntity entity) {
			data.clear();
			if (entity.permissions != null) {
				data.add(new ItemHeader("Permissions (" + entity.permissions.length + ")"));
			}
			if (entity.activities != null) {
				data.add(new ItemHeader("Activities (" + entity.activities.length + ")"));
				//Collections.addAll(data, entity.activities);
			}
			if (entity.services != null) {
				data.add(new ItemHeader("Services (" + entity.services.length + ")"));
			}
			if (entity.providers != null) {
				data.add(new ItemHeader("Providers (" + entity.providers.length + ")"));
			}
			if (entity.receivers != null) {
				data.add(new ItemHeader("Receivers (" + entity.receivers.length + ")"));
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
				ItemHeader d = (ItemHeader) data.get(position);
				((HeaderHolder) holder).setTitle(d.title);
			} else if (holder instanceof ComponentInfoHolder) {
				ComponentInfo d = (ComponentInfo) data.get(position);
				((ComponentInfoHolder) holder).update(d);
			}
		}

		@Override
		public int getItemCount() {
			return data.size();
		}
	}
}
