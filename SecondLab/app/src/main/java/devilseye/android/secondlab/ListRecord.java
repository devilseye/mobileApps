package devilseye.android.secondlab;

public class ListRecord {

    private String contact;
    private String file;

    public ListRecord(String contact, String file) {
        this.contact=contact;
        this.file = file;
    }

    public String getContact(){
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
