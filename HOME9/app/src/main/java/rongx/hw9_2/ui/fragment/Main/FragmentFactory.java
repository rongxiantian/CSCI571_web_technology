package rongx.hw9_2.ui.fragment.Main;

import java.util.HashMap;

import rongx.hw9_2.ui.fragment.BaseFragment;

/**
 * Fragment工厂类。
 * Created by LW on 2017/4/20.
 */

public class FragmentFactory {
    private static HashMap<Integer, BaseFragment> mBaseFragments = new HashMap<Integer, BaseFragment>();

    public static BaseFragment createFragment(int pos) {
        BaseFragment baseFragment = mBaseFragments.get(pos);

        if (baseFragment == null) {
            switch (pos) {
                case 0:
                    baseFragment = new SearchFragment();//头条
                    break;
                case 1:
                    baseFragment = new FavouriteFragment();//要闻
                    break;


            }
            mBaseFragments.put(pos, baseFragment);
        }
        return baseFragment;
    }
}
