package com.example.storekeeper.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.storekeeper.returnFragments.fromEmployee;
import com.example.storekeeper.returnFragments.toSupplier;

public class return_PagerAdapter extends FragmentStateAdapter {
    public return_PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new fromEmployee();
            case 1:
                return new toSupplier();
            default:
                return new fromEmployee();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
