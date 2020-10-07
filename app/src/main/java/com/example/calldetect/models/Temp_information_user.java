package com.example.calldetect.models;

public class Temp_information_user {

    private  String login;
    private  String user_name;

    public Temp_information_user(){
        this.login = null;
        this.user_name = null;
    }

    public Temp_information_user(String m_login, String m_name){
        this.login = m_login;
        this.user_name = m_name;
    }
    public String getLogin() {return login;}

    public void setLogin(String login) {this.login = login;}

    public String getUser_name() { return user_name; }

    public void setUser_name(String password) {this.user_name = password;}
}
