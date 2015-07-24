package pro.asdgroup.bizon.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.model.Company;

/**
 * Created by Voronov Viacheslav on 5/3/2015.
 */
public class CompanyAdapter extends BaseAdapter {

    class ViewHolder {
        TextView name;
        TextView about;

        public ViewHolder(View view){
            name = (TextView) view.findViewById(R.id.name_text);
            about = (TextView) view.findViewById(R.id.description_text);
        }
    }

    private List<Company> mCompanies;

    public CompanyAdapter(List<Company> companies){
        this.mCompanies = companies;
    }

    public void setCompanies(List<Company> companies){
        this.mCompanies = companies;
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
        return mCompanies.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_company, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Company company = getItem(position);

        holder.name.setText(company.getName());
        if (company.getBusiness() != null && !company.getBusiness().trim().isEmpty()) {
            holder.about.setText(company.getBusiness());
        } else {
            holder.about.setText(parent.getContext().getString(R.string.clp_business_is_not_set));
        }

        return convertView;
    }
}
