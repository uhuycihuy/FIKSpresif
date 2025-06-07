package com.example.fikspresif;

public class Db_Contract {
    public static String ip = "192.168.1.12"; //di ganti sesuai IP server laptop masing2
    public static final String urlRegister = "http://" + ip + "/fikspresif/api_register.php";
    public static final String urlLogin = "http://" + ip + "/fikspresif/api_login.php";
    public static final String urlGetAccount = "http://" + ip + "/fikspresif/api_get_account.php";
    public static  final  String urlEditUser = "http://" + ip + "/fikspresif/api_edit_user.php";
    public static  final  String urlAddAspirasi = "http://" + ip + "/fikspresif/api_add_aspirasi.php";
    public static final String urlGetAspirasiById = "http://" + ip + "/fikspresif/api_get_aspirasi_byid.php";
    public static final String urlGetAllAspirasi = "http://" + ip + "/fikspresif/api_get_aspirasi.php";
    public static final String urlEditAspirasi = "http://" + ip + "/fikspresif/api_edit_aspirasi.php";
    public static final String urlDeleteAspirasi = "http://" + ip + "/fikspresif/api_delete_aspirasi.php";

}