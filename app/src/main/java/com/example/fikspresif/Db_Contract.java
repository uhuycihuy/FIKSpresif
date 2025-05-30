package com.example.fikspresif;

public class Db_Contract {
    public static String ip = "10.0.2.2"; //di ganti sesuai IP server laptop masing2
    public static final String urlRegister = "http://" + ip + "/fikspresif/api_register.php";
    public static final String urlLogin = "http://" + ip + "/fikspresif/api_login.php";
    public static final String urlGetAccount = "http://" + ip + "/fikspresif/api_get_account.php";
}