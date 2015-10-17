package devilseye.android.firstlab;

import java.util.List;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class AlarmAdapter extends ArrayAdapter<AlarmRecord> {

    private final List<AlarmRecord> list;
    private final Activity context;

    public AlarmAdapter(Activity context, List<AlarmRecord> list) {
        super(context, R.layout.alarm_row, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView alarm_time;
        protected TextView alarm_message;
        protected TextView alarm_days;
        protected Switch alarm_switch;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.alarm_row, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.alarm_time = (TextView) view.findViewById(R.id.alarm_time);
            viewHolder.alarm_message = (TextView) view.findViewById(R.id.alarm_message);
            viewHolder.alarm_days = (TextView) view.findViewById(R.id.alarm_days);
            viewHolder.alarm_switch = (Switch) view.findViewById(R.id.alarm_switch);
            viewHolder.alarm_switch
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            AlarmRecord element = (AlarmRecord) viewHolder.alarm_switch.getTag();
                            element.setSwitcher(buttonView.isChecked());
                            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("alarm"+element.getId(), buttonView.isChecked());
                            editor.commit();
                        }
                    });
            view.setTag(viewHolder);
            viewHolder.alarm_switch.setTag(list.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).alarm_switch.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.alarm_time.setText(list.get(position).getTime());
        holder.alarm_message.setText(list.get(position).getMessage());
        holder.alarm_days.setText(list.get(position).getDays());
        holder.alarm_switch.setChecked(list.get(position).isSwitched());
        return view;
    }
}
