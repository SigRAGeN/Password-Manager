package com.passwordmanager;

public class PasswordEntry {
    private String nick;
    private String url;
    private String mail;
    private String encryptedPassword;

    public PasswordEntry(String nick, String url, String mail, String encryptedPassword){
        this.nick = nick;
        this.url = url;
        this.mail = mail;
        this.encryptedPassword = encryptedPassword;
    }

    public String getNick(){return nick;}
    public String getUrl(){return url;}
    public String getMail(){return mail;}
    public String getPassword(){return encryptedPassword;}

    @Override
    public String toString(){
        return (nick.toUpperCase() + " ---- " + mail);
    }

}


