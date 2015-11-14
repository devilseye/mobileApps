package devilseye.android.secondlab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import de.timroes.android.listview.EnhancedListView;

public class MainActivity extends Activity {

    final static int PICK_FILE = 1;
    final static int PICK_DICT = 2;
    final static int PICK_CONTACT = 3;

    boolean SEND = false;
    List<ListRecord> records;
    String currentContact;
    String currentFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            Uri uri=intent.getClipData().getItemAt(0).getUri();
            if (uri!=null) {
                SEND=true;
                String pathToFile = uri.getPath();
                if (pathToFile.length() > 30) {
                    pathToFile = "..." + pathToFile.substring(pathToFile.length() - 30, pathToFile.length());
                }
                currentFile = pathToFile;
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactPickerIntent, PICK_CONTACT);
            }
        }

        setContentView(R.layout.activity_main);
        EnhancedListView listView = (EnhancedListView) findViewById(R.id.itemList);
        records=readFromFile();
        if (records==null){
            records=new ArrayList<ListRecord>();
            writeToFile(records);
        }
        final ArrayAdapter<ListRecord> listAdapter = new ListAdapter(this,records);
        listView.setAdapter(listAdapter);
        listView.setDismissCallback(new EnhancedListView.OnDismissCallback() {
            @Override
            public EnhancedListView.Undoable onDismiss(EnhancedListView listView, final int position) {
                final ListRecord item = listAdapter.getItem(position);
                listAdapter.remove(item);
                return new EnhancedListView.Undoable() {
                    @Override
                    public void undo() {
                        listAdapter.insert(item, position);
                        EnhancedListView listView = (EnhancedListView) findViewById(R.id.itemList);
                        listView.setAdapter(listAdapter);
                    }

                    @Override
                    public String getTitle() {
                        return getString(R.string.deleted) + item.getContact();
                    }

                    @Override
                    public void discard() {
                        writeToFile(records);
                    }
                };
            }
        });
        listView.setRequireTouchBeforeDismiss(false);
        listView.enableSwipeToDismiss();
    }

    private void writeToFile(List<ListRecord> records) {
        try {
            OutputStream out = new FileOutputStream(getFilesDir().getPath()+"/fileList");
            writeJsonStream(out, records);
            out.flush();
            out.close();
        } catch(IOException ex) {
            Log.d("IO:",ex.getMessage());
        }
    }

    public void writeJsonStream(OutputStream out, List<ListRecord> records) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("  ");
        writeRecordsArray(writer, records);
        writer.close();
    }

    public void writeRecordsArray(JsonWriter writer, List<ListRecord> records) throws IOException {
        writer.beginArray();
        for (ListRecord record : records) {
            writeRecord(writer, record);
        }
        writer.endArray();
    }

    public void writeRecord(JsonWriter writer, ListRecord record) throws IOException {
        writer.beginObject();
        writer.name("contact").value(record.getContact());
        writer.name("file").value(record.getFile());
        writer.endObject();
    }

    private List<ListRecord> readFromFile(){
        List<ListRecord> records=null;
        try {
            InputStream in = new FileInputStream(getFilesDir().getPath()+"/fileList");
            records=readJsonStream(in);
            in.close();
        } catch (IOException ex) {
            Log.d("IO:",ex.getMessage());
        }
        return records;
    }

    public List<ListRecord> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readRecordsArray(reader);
        }
        finally {
            reader.close();
        }
    }

    public List<ListRecord> readRecordsArray(JsonReader reader) throws IOException {
        List<ListRecord> records = new ArrayList<ListRecord>();

        reader.beginArray();
        while (reader.hasNext()) {
            records.add(readRecord(reader));
        }
        reader.endArray();
        return records;
    }

    public ListRecord readRecord(JsonReader reader) throws IOException {
        String contact = null;
        String file = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("contact")) {
                contact = reader.nextString();
            } else if (name.equals("file")) {
                file = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new ListRecord(contact,file);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void addToList(String contact, String file){
        records.add(new ListRecord(contact,file));
        final ArrayAdapter<ListRecord> listAdapter = new ListAdapter(this,records);
        EnhancedListView listView = (EnhancedListView) findViewById(R.id.itemList);
        listView.setAdapter(listAdapter);
        writeToFile(records);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_addNew){
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(contactPickerIntent, PICK_CONTACT);
        }

        if (id == R.id.action_about) {
            showAboutDialog();
        }

        if (id == R.id.action_exit) {
            showExitDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    public void showExitDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(getString(R.string.exitHead))
                .setMessage(getString(R.string.exitBody))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                })
                .setNegativeButton(getString(R.string.no), null);
        AlertDialog exitMessage=builder.create();
        exitMessage.show();
    }

    public void showAboutDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(getString(R.string.aboutHead))
                .setMessage(getString(R.string.aboutBody))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.ok), null);
        AlertDialog exitMessage=builder.create();
        exitMessage.show();
    }

    public void showSelectDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        final String[] items={getString(R.string.selectFile), getString(R.string.selectDictaphone)};
        builder.setTitle(getString(R.string.selectHead))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("*/*");
                                String title = getResources().getString(R.string.chooser_title);
                                Intent chooser = Intent.createChooser(intent, title);
                                if (intent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(chooser, PICK_FILE);
                                }
                            } catch (Exception ex) {
                                Context context = getApplicationContext();
                                CharSequence text = getString(R.string.errorNoApp);
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        } else {
                            try {
                                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                                startActivityForResult(intent, PICK_DICT);
                            } catch (Exception ex) {
                                Context context = getApplicationContext();
                                CharSequence text = getString(R.string.errorNoApp);
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        }
                    }
                })
                .setCancelable(true);
        AlertDialog selectMessage=builder.create();
        selectMessage.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_CONTACT: {
                if (data!=null) {
                    Uri savedUri = data.getData();
                    if (savedUri!=null) {
                        retrieveContactName(savedUri);
                        if (SEND) {
                            addToList(currentContact, currentFile);
                            SEND = false;
                        } else {
                            showSelectDialog();
                        }
                    }
                }
                break;
            }
            case PICK_DICT: {
                if (data!=null) {
                    Uri savedUri = data.getData();
                    if (savedUri != null) {
                        String pathToFile=savedUri.getPath();
                        if (pathToFile.length()>30){
                            pathToFile="..."+pathToFile.substring(pathToFile.length()-30,pathToFile.length());
                        }
                        addToList(currentContact,pathToFile);
                    }
                }
                break;
            }
            case PICK_FILE: {
                if (data!=null) {
                    Uri savedUri = data.getData();
                    if (savedUri != null) {
                        String pathToFile=savedUri.getPath();
                        if (pathToFile.length()>30){
                            pathToFile="..."+pathToFile.substring(pathToFile.length()-30,pathToFile.length());
                        }
                        addToList(currentContact,pathToFile);
                    }
                }
                break;
            }
            default: {break;}
        }
    }

    private void retrieveContactName(Uri uriContact) {
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);
        if (cursor.moveToFirst()) {
            currentContact = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        cursor.close();
    }
}
