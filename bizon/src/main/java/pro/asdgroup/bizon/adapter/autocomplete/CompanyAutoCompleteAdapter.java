package pro.asdgroup.bizon.adapter.autocomplete;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.model.Company;

/**
 * Created by Voronov Viacheslav on 29.06.2015.
 */
public class CompanyAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private List<Company> mCompanies;

    public CompanyAutoCompleteAdapter(){
        mCompanies = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mCompanies.size();
    }

    @Override
    public Company getItem(int position) {
        return mCompanies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_autocomplete_line, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Company company = getItem(position);

        holder.mNameText.setText(company.getName());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<Company> companies = findCompanies(constraint.toString());
                    filterResults.values = companies;
                    filterResults.count = companies.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0){
                    mCompanies = (List<Company>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    private List<Company> findCompanies(String searchString){
        RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
        return service.searchCompanies(searchString).getCompanies();
    }

    private class ViewHolder {
        TextView mNameText;

        ViewHolder(View view){
            mNameText = (TextView) view.findViewById(R.id.text);
        }
    }
}
