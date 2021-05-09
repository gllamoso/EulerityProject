package dev.gtcl.eulerityproject;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class Util {
    public static void showDialogFragment(DialogFragment fragment, FragmentManager fragmentManager){
        if(fragment == null || fragment.isAdded()){
            return;
        }
        fragment.show(fragmentManager, fragment.getTag());
    }
}
