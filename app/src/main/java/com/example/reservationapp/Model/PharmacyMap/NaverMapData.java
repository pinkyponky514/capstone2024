package com.example.reservationapp.Model.PharmacyMap;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class NaverMapData {
    @SerializedName("dutyAddr")
    @Expose
    private String address;
    @SerializedName("dutyName")
    @Expose
    private String pharmacyname;
    @SerializedName("dutyTel1")
    @Expose
    private String tel;
    @SerializedName("wgs84Lat")
    @Expose
    private double mapx;
    @SerializedName("wgs84Lon")
    @Expose
    private double mapy;

    @SerializedName("dutyTime1s")
    @Expose
    private String MonStartTime;

    @SerializedName("dutyTime1c")
    @Expose
    private String MonCloseTime;

    @SerializedName("dutyTime2s")
    @Expose
    private String TueStartTime;

    @SerializedName("dutyTime2c")
    @Expose
    private String TueCloseTime;

    @SerializedName("dutyTime3s")
    @Expose
    private String WedStartTime;

    @SerializedName("dutyTime3c")
    @Expose
    private String WedCloseTime;

    @SerializedName("dutyTime4s")
    @Expose
    private String ThuStartTime;

    @SerializedName("dutyTime4c")
    @Expose
    private String ThuCloseTime;

    @SerializedName("dutyTime5s")
    @Expose
    private String FriStartTime;

    @SerializedName("dutyTime5c")
    @Expose
    private String FriCloseTime;

    @SerializedName("dutyTime6s")
    @Expose
    private String SatStartTime;

    @SerializedName("dutyTime6c")
    @Expose
    private String SatCloseTime;


    @SerializedName("dutyTime7s")
    @Expose
    private String SunStartTime;

    @SerializedName("dutyTime7c")
    @Expose
    private String SunCloseTime;

    public String getMonStartTime() {
        return MonStartTime;
    }

    public String getMonCloseTime() {
        return MonCloseTime;
    }

    public String getTueStartTime() {
        return TueStartTime;
    }

    public String getTueCloseTime() {
        return TueCloseTime;
    }

    public String getWedStartTime() {
        return WedStartTime;
    }

    public String getWedCloseTime() {
        return WedCloseTime;
    }

    public String getThuStartTime() {
        return ThuStartTime;
    }

    public String getThuCloseTime() {
        return ThuCloseTime;
    }

    public String getFriStartTime() {
        return FriStartTime;
    }

    public String getFriCloseTime() {
        return FriCloseTime;
    }

    public String getSatStartTime() {
        return SatStartTime;
    }

    public String getSatCloseTime() {
        return SatCloseTime;
    }

    public String getSunStartTime() {
        return SunStartTime;
    }

    public String getSunCloseTime() {
        return SunCloseTime;
    }

    public String getAddress() {
        return address;
    }

    public String getPharmacyname() {
        return pharmacyname;
    }

    public String getTel() {
        return tel;
    }

    public double getMapx() {
        return mapx;
    }

    public double getMapy() {
        return mapy;
    }
}

