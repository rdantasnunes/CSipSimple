package br.ufla.deg.rodrigodantas.csipsimple.util;

import android.util.Log;

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
            Log.d("parseToFloat: ",strValue);
            return -1f;
        }
    }
}
