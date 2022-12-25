package com.benayoub.aghithni;

public class UserData {
    public String Fullname,Username,EmailId,Gender,Password;
    public UserData(){
    }
    public UserData(String fullname, String username, String emailId, String gender,String password) {
        Fullname = fullname;
        Username = username;
        EmailId = emailId;
        Gender = gender;
        Password=password;
    }

    public String getFullname() {
        return Fullname;
    }

    public void setFullname(String fullname) {
        Fullname = fullname;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmailId() {
        return EmailId;
    }

    public void setEmailId(String emailId) {
        EmailId = emailId;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
