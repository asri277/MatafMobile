package com.example.matafmobile;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Ahli_DB_Helper {

    String a_email, b_name, c_alamat, d_poskod, e_bandar, f_negeri, g_noPhone, h_noPejabat, i_jenisIC, j_noIC, k_tarikhLahir,
    l_jantina, m_keturunan, n_lain_lain;

    public Ahli_DB_Helper() {
    }

    public Ahli_DB_Helper(String a_email, String b_name, String c_alamat,
                          String d_poskod, String e_bandar, String f_negeri,
                          String g_noPhone, String h_noPejabat, String i_jenisIC,
                          String j_noIC, String k_tarikhLahir, String l_jantina,
                          String m_keturunan, String n_lain_lain) {
        this.a_email = a_email;
        this.b_name = b_name;
        this.c_alamat = c_alamat;
        this.d_poskod = d_poskod;
        this.e_bandar = e_bandar;
        this.f_negeri = f_negeri;
        this.g_noPhone = g_noPhone;
        this.h_noPejabat = h_noPejabat;
        this.i_jenisIC = i_jenisIC;
        this.j_noIC = j_noIC;
        this.k_tarikhLahir = k_tarikhLahir;
        this.l_jantina = l_jantina;
        this.m_keturunan = m_keturunan;
        this.n_lain_lain = n_lain_lain;
    }

    public String getA_email() {
        return a_email;
    }

    public void setA_email(String a_email) {
        this.a_email = a_email;
    }

    public String getB_name() {
        return b_name;
    }

    public void setB_name(String b_name) {
        this.b_name = b_name;
    }

    public String getC_alamat() {
        return c_alamat;
    }

    public void setC_alamat(String c_alamat) {
        this.c_alamat = c_alamat;
    }

    public String getD_poskod() {
        return d_poskod;
    }

    public void setD_poskod(String d_poskod) {
        this.d_poskod = d_poskod;
    }

    public String getE_bandar() {
        return e_bandar;
    }

    public void setE_bandar(String e_bandar) {
        this.e_bandar = e_bandar;
    }

    public String getF_negeri() {
        return f_negeri;
    }

    public void setF_negeri(String f_negeri) {
        this.f_negeri = f_negeri;
    }

    public String getG_noPhone() {
        return g_noPhone;
    }

    public void setG_noPhone(String g_noPhone) {
        this.g_noPhone = g_noPhone;
    }

    public String getH_noPejabat() {
        return h_noPejabat;
    }

    public void setH_noPejabat(String h_noPejabat) {
        this.h_noPejabat = h_noPejabat;
    }

    public String getI_jenisIC() {
        return i_jenisIC;
    }

    public void setI_jenisIC(String i_jenisIC) {
        this.i_jenisIC = i_jenisIC;
    }

    public String getJ_noIC() {
        return j_noIC;
    }

    public void setJ_noIC(String j_noIC) {
        this.j_noIC = j_noIC;
    }

    public String getK_tarikhLahir() {
        return k_tarikhLahir;
    }

    public void setK_tarikhLahir(String k_tarikhLahir) {
        this.k_tarikhLahir = k_tarikhLahir;
    }

    public String getL_jantina() {
        return l_jantina;
    }

    public void setL_jantina(String l_jantina) {
        this.l_jantina = l_jantina;
    }

    public String getM_keturunan() {
        return m_keturunan;
    }

    public void setM_keturunan(String m_keturunan) {
        this.m_keturunan = m_keturunan;
    }

    public String getN_lain_lain() {
        return n_lain_lain;
    }

    public void setN_lain_lain(String n_lain_lain) {
        this.n_lain_lain = n_lain_lain;
    }
}
