package com.example.opqr.overall_situation;


public class User {

    private String Identification;
    private String UserPhoneNum;
    private String UserPassword;
    private String UserName;
    private int uid=-1;
    public User(String Identification, String userAccount, String userPassword,String UserName) {
        this.Identification = Identification;
        this.UserPhoneNum = userAccount;
        this.UserPassword = userPassword;
        this.UserName=UserName;
    }
    public  User(){};
    public boolean UserIsempty()
    {
        if(UserName==null||UserPassword==null||UserPhoneNum==null||Identification==null)
            return true;
        else if(UserName=="null"||UserPassword=="null"||UserPhoneNum=="null"||Identification=="null")
            return true;
        else
            return false;
    }
public void setuser(User temp)
{
    this.Identification=temp.getIdentification();
    this.UserPhoneNum=temp.getUserPhoneNum();
    this.UserPassword= temp.getUserPassword();
    this.UserName=temp.getUserName();
}
    public String getUserName(){return  UserName;};
    public String getIdentification() {
        return Identification;
    }
    public String getUserPhoneNum() {
        return UserPhoneNum;
    }
    public String getUserPassword() {
        return UserPassword;
    }
    public void setUserPhoneNum(String userAccount) {
        this.UserPhoneNum = userAccount;
    }
    public void setUserPassword(String userPassword) {
        this.UserPassword = userPassword;
    }
    public void setIdentification(String id) {
        this.Identification = id;
    }
    public void setUserName(String userName){this.UserName=userName;}
    public void setUid(int i)
    {
        uid=i;
    }
    public int getUid()
    {
        return uid;
    }
    public void clear()
    {
        this.Identification=null;
        this.UserName=null;
        this.UserPassword=null;
        this.UserPhoneNum=null;
    }
}



