package devilseye.android.firstlab;

import android.app.ListActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AlarmActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayAdapter<AlarmRecord> adapter = new AlarmAdapter(this,
                getAlarmRecord());
        View header = getLayoutInflater().inflate(R.layout.alarm_header, null);
        ListView listView=getListView();
        listView.addHeaderView(header);
        setListAdapter(adapter);
    }

    private List<AlarmRecord> getAlarmRecord() {
        List<AlarmRecord> list = new ArrayList<AlarmRecord>();
        list.add(get(1, getString(R.string.alarm_1_time),
                getString(R.string.alarm_1_message),
                getString(R.string.alarm_1_days),
                PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext())
                        .getBoolean("alarm1", false)));
        list.add(get(2,getString(R.string.alarm_2_time),
                getString(R.string.alarm_2_message),
                getString(R.string.alarm_2_days),
                PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext())
                        .getBoolean("alarm2", false)));
        list.add(get(3,getString(R.string.alarm_3_time),
                getString(R.string.alarm_3_message),
                getString(R.string.alarm_3_days),
                PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext())
                        .getBoolean("alarm3", false)));
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm, menu);
        return true;
    }

    private AlarmRecord get(long id, String time, String message, String days, boolean switcher) {
        return new AlarmRecord(id, time,message,days,switcher);
    }
}
