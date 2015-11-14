package devilseye.android.secondlab;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends ArrayAdapter<ListRecord> {
    private final List<ListRecord> list;
    private final Activity context;

    public ListAdapter(Activity context, List<ListRecord> list) {
        super(context, R.layout.item_list, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView contact;
        protected TextView file;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.item_list, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.contact = (TextView) view.findViewById(R.id.contact);
            viewHolder.file = (TextView) view.findViewById(R.id.file);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.contact.setText(list.get(position).getContact());
        holder.file.setText(list.get(position).getFile());
        return view;
    }
}
