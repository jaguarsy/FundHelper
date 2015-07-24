package com.bitcage.fundhelper.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bitcage.fundhelper.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JohnnyCage on 2015-7-24.
 */
public class AutoCompleteAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private ArrayFilter mFilter;
    private ArrayList<String> mOriginalValues;
    private List<String> mObjects;
    private final Object mLock = new Object();
    private int maxMatch = 10;

    public AutoCompleteAdapter(Context context, ArrayList<String> mOriginalValues, int maxMatch) {
        this.context = context;
        this.mOriginalValues = mOriginalValues;
        this.maxMatch = maxMatch;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public Object getItem(int position) {
        //此方法有误，尽量不要使用
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView tv;
    }

    public ArrayList<String> getAllItems() {
        return mOriginalValues;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.simple_list_item_for_autocomplete, null);
            holder.tv = (TextView) convertView.findViewById(R.id.simple_item_0);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv.setText(mObjects.get(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
                synchronized (mLock) {
                    ArrayList<String> list = new ArrayList<String>(mOriginalValues);
                    results.values = list;
                    results.count = list.size();
                    return results;
                }
            } else {
                String prefixString = prefix.toString().toLowerCase();

                final int count = mOriginalValues.size();

                final ArrayList<String> newValues = new ArrayList<String>(count);

                for (int i = 0; i < count; i++) {
                    final String value = mOriginalValues.get(i);
                    final String valueText = value.toLowerCase();

                    if (valueText.contains(prefixString)) {
                        newValues.add(value);
                    }
                    if (maxMatch > 0) {
                        if (newValues.size() > maxMatch - 1) {
                            break;
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mObjects = (List<String>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}
