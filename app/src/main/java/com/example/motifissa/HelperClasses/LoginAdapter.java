package com.example.motifissa.HelperClasses;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.motifissa.LoginTabFragment;
import com.example.motifissa.SignupTabFragment;

import org.jetbrains.annotations.NotNull;

public class LoginAdapter extends FragmentPagerAdapter {

    private Context context;
    int totalTabs;

    public LoginAdapter(FragmentManager fm, Context context, int totalTabs){
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

    @NotNull
    public Fragment getItem(int position){
        switch (position){
            case 0:
                return new LoginTabFragment();
            case 1:
                return new SignupTabFragment();
            default:
                return new LoginTabFragment();
        }
    }
}
