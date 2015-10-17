package devilseye.android.firstlab;

public class AlarmRecord {

    private long id;
    private String time;
    private String message;
    private String days;
    private boolean switcher;

    public AlarmRecord(long id,String time, String message, String days, boolean switcher) {
        this.id=id;
        this.time = time;
        this.message=message;
        this.days=days;
        this.switcher = switcher;
    }

    public long getId(){
        return id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public boolean isSwitched() {
        return switcher;
    }

    public void setSwitcher(boolean switcher) {
        this.switcher = switcher;
    }

}
