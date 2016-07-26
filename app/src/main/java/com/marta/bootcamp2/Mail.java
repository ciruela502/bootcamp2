package com.marta.bootcamp2;

/**
 * Created by marta on 26.07.2016.
 */
public class Mail {

    public String[] adresat = new String[1];
    public String temat;
    public String tresc;

    public Mail(String adresat, String temat, String tresc){
        this.adresat[0] = adresat;//// TODO: 26.07.2016 zmienic gdy bedzie wiecej!
        this.temat = temat;
        this.tresc = tresc;
    }
}
