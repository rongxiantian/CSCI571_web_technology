package rongx.hw9_2.ui.fragment.PlaceDetail;

import java.util.HashMap;

import rongx.hw9_2.ui.fragment.BaseFragment;

public class FragmentFactory_PD {
    private static HashMap<Integer, BaseFragment> mBaseFragments = new HashMap<Integer, BaseFragment>();

    public static BaseFragment createFragment(int pos) {
        BaseFragment baseFragment = mBaseFragments.get(pos);

        if (baseFragment == null) {
            switch (pos) {
                case 0:
                    baseFragment = new InfoFragment();//头条
                    break;
                case 1:
                    baseFragment = new PhotoFragment();//要闻
                    break;
                case 2:
                    baseFragment = new MapFragment();//头条
                    break;
                case 3:
                    baseFragment = new ReviewFragment();//要闻
                    break;
            }
            mBaseFragments.put(pos, baseFragment);
        }
        return baseFragment;
    }
}
