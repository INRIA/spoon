package com.cristal.ircica.applicationcolis.userinterface.fragments;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cristal.ircica.applicationcolis.R;

/**
 * Created by Itinerance on 03/06/2016.
 */

public class TransporteurFragment extends CompletableFragment{
    RadioGroup transporteurRadioGroup;
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.transporteur_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        transporteurRadioGroup = (RadioGroup)getView().findViewById(R.id.transporteur_radio_group);
        transporteurRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.other_radio_button){
                    changeUndisplayedTransporteurEditTextVisibility(View.VISIBLE);
                }
                else {
                    changeUndisplayedTransporteurEditTextVisibility(View.GONE);
                }
            }
        });
        ((EditText)getView().findViewById(R.id.undisplayed_transporteur_edit_text)).setVisibility(View.GONE);
        ((TextView)getView().findViewById(R.id.undisplayed_transporteur_text_view)).setVisibility(View.GONE);

    }

    /**
     *
     * @return true if all the fields are completed , false otherwise
     */
    public boolean isComplete() {
        if(!((EditText)(getView().findViewById(R.id.no_order))).getText().toString().equals("")
                && !((EditText)getView().findViewById(R.id.no_delivery)).getText().toString().equals("")
                && ((RadioGroup)getView().findViewById(R.id.transporteur_radio_group)).getCheckedRadioButtonId()!=-1
                && !((EditText)getView().findViewById(R.id.nb_parcels)).getText().toString().equals("")){
            return true;
        }
        else
            return false;
    }

    public void resetFields() {
        ((RadioGroup)getView().findViewById(R.id.transporteur_radio_group)).check(-1);
        ((EditText)(getView().findViewById(R.id.no_order))).setText("");
        ((EditText)(getView().findViewById(R.id.no_delivery))).setText("");
        ((EditText)(getView().findViewById(R.id.nb_parcels))).setText("");
    }

    public void changeUndisplayedTransporteurEditTextVisibility(int visibility){
        ((EditText)getView().findViewById(R.id.undisplayed_transporteur_edit_text)).setVisibility(visibility);
        ((TextView)getView().findViewById(R.id.undisplayed_transporteur_text_view)).setVisibility(visibility);
    }
}
