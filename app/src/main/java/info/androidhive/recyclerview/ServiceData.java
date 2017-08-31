package info.androidhive.recyclerview;

/**
 * Created by sumanth.reddy on 25/02/17.
 */
public class ServiceData {
    private String package_name, app_uuid, app_battery,data_usage_sent,data_usage_receive,updated_at;
    public ServiceData(){

    }
    public ServiceData(String package_name, String app_uuid, String app_battery,String data_usage_sent,String data_usage_receive,String updated_at) {
        this.package_name = package_name;
        this.app_uuid = app_uuid;
        this.app_battery = app_battery;
        this.data_usage_sent = data_usage_sent;
        this.data_usage_receive = data_usage_receive;
        this.updated_at = updated_at;
    }

    //getters
    public String getUpdated_at() {
        return updated_at;
    }
    public String getApp_uuid() {
        return app_uuid;
    }
    public String getApp_battery() {
        return app_battery;
    }
    public String getData_usage_sent() {
        return data_usage_sent;
    }
    public String getData_usage_receive() {
        return data_usage_receive;
    }

    public String getPackage_name() {
        return package_name;
    }

    //setters


    public void setApp_battery(String app_battery) {
        this.app_battery = app_battery;
    }

    public void setApp_uuid(String app_uuid) {
        this.app_uuid = app_uuid;
    }

    public void setData_usage_receive(String data_usage_receive) {
        this.data_usage_receive = data_usage_receive;
    }

    public void setData_usage_sent(String data_usage_sent) {
        this.data_usage_sent = data_usage_sent;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
