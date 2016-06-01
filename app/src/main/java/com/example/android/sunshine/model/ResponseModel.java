package com.example.android.sunshine.model;

public class ResponseModel {

    public City city;
    public int cnt;
    public String cod;
    public String message;
    public java.util.List<Weather> list;


    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public java.util.List<Weather> getList() {
        return list;
    }

    public void setList(java.util.List<Weather> list) {
        this.list = list;
    }
}
