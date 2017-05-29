package com.cristal.ircica.applicationcolis.userinterface.fragments;


import android.support.v4.app.Fragment;

/**Define a fragment able to say if all its fields are completed
 * Created by Itinerance on 14/06/2016.
 */
public abstract class CompletableFragment extends Fragment {
    public abstract boolean isComplete();

}
