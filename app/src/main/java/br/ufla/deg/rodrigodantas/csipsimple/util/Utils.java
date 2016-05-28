package br.ufla.deg.rodrigodantas.csipsimple.util;

/**
 * Created by Rodrigo on 5/27/16.
 */
public final class Utils {

    private Utils(){
        super();
    }

    public static float parseToFloat(String strValue){
        try{
            return Float.parseFloat(strValue);
        }catch (NumberFormatException n){
            return -1f;
        }
    }
}
