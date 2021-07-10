package ch.buerki.futurascanner.database.local.objects;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Settings {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "deviceNumber")
    private int deviceNumber;

    @ColumnInfo(name = "serverIp")
    private String serverIp;

    @ColumnInfo(name = "branch")
    private int branch;

    @ColumnInfo(name = "date")
    private String date;

    public Settings(int deviceNumber, String serverIp, int branch, String date) {
        this.deviceNumber = deviceNumber;
        this.serverIp = serverIp;
        this.branch = branch;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(int deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getBranch() {
        return branch;
    }

    public void setBranch(int branch) {
        this.branch = branch;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
