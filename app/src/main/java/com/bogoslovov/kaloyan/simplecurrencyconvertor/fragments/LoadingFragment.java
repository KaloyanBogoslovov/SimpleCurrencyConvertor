package com.bogoslovov.kaloyan.simplecurrencyconvertor.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.bogoslovov.kaloyan.simplecurrencyconvertor.R;

/**
 * Created by kaloqn on 5/13/17.
 */

public class LoadingFragment extends DialogFragment {

    public LoadingFragment() {}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog alertDialog =
            new AlertDialog.Builder(getActivity()).setView(R.layout.fragment_loading).show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return alertDialog;

    }
}
