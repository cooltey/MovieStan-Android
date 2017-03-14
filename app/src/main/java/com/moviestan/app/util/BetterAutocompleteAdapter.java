package com.moviestan.app.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;


public class BetterAutocompleteAdapter extends BaseAdapter implements Filterable {

	private ArrayList<String> mListData;
	private Context mContext;
	private ArrayList<String> resultList = new ArrayList<String>();

	public BetterAutocompleteAdapter(Context context, ArrayList<String> list_data) {
		mContext = context;
		mListData = list_data;
	}

	@Override
	public int getCount() {
		return resultList.size();
	}

	@Override
	public String getItem(int index) {
		return resultList.get(index);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
		}
		((TextView) convertView.findViewById(android.R.id.text1)).setText(getItem(position));
		return convertView;
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					ArrayList<String> items = findItem(mContext, constraint.toString());

					// Assign the data to the FilterResults
					filterResults.values = items;
					filterResults.count = items.size();
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results != null && results.count > 0) {
					resultList = (ArrayList<String>) results.values;
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}};
		return filter;
	}

	/**
	 * Returns a search result for the given book title.
	 */
	private ArrayList<String> findItem(Context context, String item_name) {

		ArrayList<String> tmpList = new ArrayList<String>();

		for(int i = 0; i < mListData.size(); i++){
			if(mListData.get(i).contains(item_name)){
				tmpList.add(mListData.get(i));
			}
		}

		return tmpList;
	}
}
