package br.ufla.deg.rodrigodantas.csipsimple.p563;

import android.os.AsyncTask;
import android.os.Environment;

/**
 * Created by Rodrigo on 24/5/16.
 */
public class P563Executer extends AsyncTask<String, Void, String> {


    public static native String p563(String uri);
    static {
        System.loadLibrary("native-audio-jni");
    }

    @Override
    protected String doInBackground(String... params) {
        if(params == null || params.length == 0)
            return "";

        if(!isExternalStorageReadable()){
            System.out.println("\t\t nao consegue ler o device \t\t\t");
            return "";
        }

        String t = p563(params[0]);
        System.out.println(params[0]+" MOS P.563=>" + t);
        return String.valueOf(t);
    }

    /* Checks if external storage is available to at least read */
    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}
