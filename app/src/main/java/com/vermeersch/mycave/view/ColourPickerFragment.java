package com.vermeersch.mycave.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.larswerkman.lobsterpicker.LobsterPicker;
import com.larswerkman.lobsterpicker.OnColorListener;
import com.vermeersch.mycave.R;

import java.util.zip.Inflater;


public class ColourPickerFragment extends DialogFragment {
    public interface OnColourPickerFragmentListener {
        void onColourSelected(int colour);
        void onLedStripStateChanged(boolean state);

    }

    private OnColourPickerFragmentListener mListener;



    public ColourPickerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

}





    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnColourPickerFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_colour_picker, null);
        LobsterPicker picker = (LobsterPicker)view.findViewById(R.id.colorpicker);
        picker.addOnColorListener(new OnColorListener() {
            @Override
            public void onColorChanged(int color) {
                Log.d("colour", "colour: " + color);
                mListener.onColourSelected(color);
            }

            @Override
            public void onColorSelected(int color) {
                //nothing yet
            }
        });

        builder.setView(view);
        builder

                // Add action buttons
                .setPositiveButton(R.string.on, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onLedStripStateChanged(true);

                    }
                })
                .setNegativeButton(R.string.off, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onLedStripStateChanged(false);
                    }
                });
        return builder.create();
    }

}
