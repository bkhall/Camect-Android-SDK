package com.camect.android.example.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.camect.android.example.R;
import com.camect.android.example.viewmodels.CameraListPagerViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CameraListPagerFragment extends Fragment {

    public static CameraListPagerFragment newInstance() {
        return new CameraListPagerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera_list_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final CameraListPagerViewModel viewModel = new ViewModelProvider(requireActivity())
                .get(CameraListPagerViewModel.class);

        TabLayout tabs = view.findViewById(R.id.tabs);

        CameraListPagerAdapter adapter = new CameraListPagerAdapter(this);

        ViewPager2 pager = view.findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(adapter.getItemCount() - 1);
        pager.setCurrentItem(viewModel.getCurrentPage());
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                viewModel.setCurrentPage(position);
            }
        });

        final String[] titles = new String[]{"Enabled Cameras", "Disabled Cameras"};

        new TabLayoutMediator(tabs, pager, (tab, position) -> tab.setText(titles[position])).attach();
    }

    private static class CameraListPagerAdapter extends FragmentStateAdapter {

        public CameraListPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return EnabledCameraListFragment.newInstance();
            }

            return DisabledCameraListFragment.newInstance();
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
