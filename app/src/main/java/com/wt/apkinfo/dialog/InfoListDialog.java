package com.wt.apkinfo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wt.apkinfo.R;
import com.wt.apkinfo.R2;
import com.wt.apkinfo.entity.ComponentInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kenumir on 24.09.2017.
 *
 */

public class InfoListDialog extends DialogFragment {

	private static final String KEY_TITLE = "title";

	public interface OnGetData {
		List<ComponentInfo> onGetData();
	}

	public static InfoListDialog newInstance(String title) {
		InfoListDialog d = new InfoListDialog();
		Bundle args = new Bundle();
		args.putString(KEY_TITLE, title);
		d.setArguments(args);
		return d;
	}

	private OnGetData mOnGetData;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mOnGetData = (OnGetData) context;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = getArguments() != null ? getArguments().getString(KEY_TITLE, "List") : "List";
		MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
				.title(title)
				.positiveText(R.string.label_close)
				.adapter(new ListItemsAdapter(mOnGetData.onGetData()), new LinearLayoutManager(getActivity()))
				.build();
		RecyclerView recycler = dialog.getRecyclerView();
		recycler.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
		return dialog;
	}

	public static class ListItemHolder extends RecyclerView.ViewHolder {

		@BindView(R2.id.text1) TextView text1;
		@BindView(R2.id.text2) TextView text2;

		public ListItemHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

	private static class ListItemsAdapter extends RecyclerView.Adapter<ListItemHolder> {
		List<ComponentInfo> data;
		public ListItemsAdapter(List<ComponentInfo> d) {
			data = d;
		}

		@Override
		public ListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new ListItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_component_info, parent, false));
		}

		@Override
		public void onBindViewHolder(ListItemHolder holder, int position) {
			ComponentInfo d = data.get(position);
			holder.text1.setText(d.name);
			holder.text2.setText(d.className);
		}

		@Override
		public int getItemCount() {
			return data != null ? data.size() : 0;
		}
	}

}
