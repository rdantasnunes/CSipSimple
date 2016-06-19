package com.csipsimple.pjsip;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Rodrigo on 6/18/16.
 */
public class MOSDialogFragment extends DialogFragment {

    private String mensagem;

    public void setMensagem(String mensagem){
        this.mensagem = mensagem;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mensagem)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                });
                /*.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });*/
        // Create the AlertDialog object and return it
        return builder.create();
    }
}