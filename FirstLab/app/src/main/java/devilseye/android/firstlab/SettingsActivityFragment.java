package devilseye.android.firstlab;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import java.util.Calendar;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsActivityFragment extends PreferenceFragment
        implements DatePickerDialog.OnDateSetListener {

    private boolean showInstantBuyDialogFlag;
    private boolean showNotificationsDialogFlag;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        final SwitchPreference instantBuyPref=(SwitchPreference)findPreference("instantBuyPref");
        if (instantBuyPref.isChecked()){
            showInstantBuyDialogFlag=true;
        }
        else
        {
            showInstantBuyDialogFlag = false;
        }
        instantBuyPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (instantBuyPref.isChecked()) {
                    if (showInstantBuyDialogFlag) {
                        showTriggerDialog(instantBuyPref);
                    }
                }
                if ((Boolean)newValue)
                    showInstantBuyDialogFlag=true;
                return true;
            }
        });


        final SwitchPreference notificationsPref=(SwitchPreference)findPreference("notificationsPref");
        if (notificationsPref.isChecked()){
            showNotificationsDialogFlag=true;
        }
        else
        {
            showNotificationsDialogFlag = false;
        }
        notificationsPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (notificationsPref.isChecked()) {
                    if (showNotificationsDialogFlag) {
                        showTriggerDialog(notificationsPref);
                    }
                }
                if ((Boolean)newValue)
                    showNotificationsDialogFlag=true;
                return true;
            }
        });

        final Preference soundPref=findPreference("soundPref");
        soundPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String summary = RingtoneManager.getRingtone(getActivity().getBaseContext(),
                        Uri.parse(newValue.toString()))
                        .getTitle(getActivity().getBaseContext());
                soundPref.setSummary(summary);
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("soundPref", newValue.toString());
                editor.commit();
                return false;
            }
        });
        String summary = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getString("soundPref", "Choose the sound please");
        if (!summary.equals("Choose the sound please")) {
            summary = RingtoneManager.getRingtone(getActivity().getBaseContext(),
                    Uri.parse(summary.toString()))
                    .getTitle(getActivity().getBaseContext());
        }
        soundPref.setSummary(summary);

        final Preference usernamePref=findPreference("usernamePref");
        usernamePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                usernamePref.setSummary(newValue.toString());
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("usernamePref", newValue.toString());
                editor.commit();
                return false;
            }
        });
        usernamePref.setSummary(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getString("usernamePref", "Please enter your name here"));

        Preference datePref = findPreference("datePref");
        datePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDateDialog();
                return false;
            }
        });
        final Calendar calendar = Calendar.getInstance();
        int year=PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getInt("year", calendar.get(Calendar.YEAR));
        int month = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getInt("month", calendar.get(Calendar.MONTH));
        int day = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getInt("day", calendar.get(Calendar.DAY_OF_MONTH));
        DateFormat df=new DateFormat();
        datePref.setSummary(df.format("dd MMM yyyy", new Date(year-1900,month,day)));
    }

    public void showTriggerDialog(final SwitchPreference switchPreference){
        AlertDialog.Builder builder=new AlertDialog.Builder(this.getActivity());
        builder.setTitle(getString(R.string.confHead))
                .setMessage(getString(R.string.confBody))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), null)
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!switchPreference.isChecked()) {
                            String title=switchPreference.getTitle().toString();
                            if (title.equals(getString(R.string.pref_notifications_title))){
                                showNotificationsDialogFlag=false;
                            }
                            if (title.equals(getString(R.string.pref_instantBuy_title))) {
                                showInstantBuyDialogFlag = false;
                            }
                            switchPreference.setChecked(true);
                        }
                    }
                });
        AlertDialog confirmationMessage=builder.create();
        confirmationMessage.show();
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Preference datePref = findPreference("datePref");
        DateFormat df=new DateFormat();
        datePref.setSummary(df.format("dd MMM yyyy", new Date(year-1900,month,day)));
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("day",day);
        editor.putInt("month", month);
        editor.putInt("year", year);
        editor.commit();
    }

    private void showDateDialog(){
        // Use the current date as the default date in the picker
        final Calendar calendar = Calendar.getInstance();
        int year=PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getInt("year", calendar.get(Calendar.YEAR));
        int month = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getInt("month", calendar.get(Calendar.MONTH));
        int day = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getInt("day", calendar.get(Calendar.DAY_OF_MONTH));
        new DatePickerDialog(getActivity(), this, year, month, day).show();
    }
}
