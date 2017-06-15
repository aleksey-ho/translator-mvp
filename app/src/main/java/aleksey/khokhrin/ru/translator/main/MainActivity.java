package aleksey.khokhrin.ru.translator.main;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import aleksey.khokhrin.ru.translator.R;
import aleksey.khokhrin.ru.translator.TranslatorApplication;
import aleksey.khokhrin.ru.translator.data.Translate;
import aleksey.khokhrin.ru.translator.main.bookmark.BookmarkFragment;
import aleksey.khokhrin.ru.translator.main.bookmark.BookmarkPresenterModule;
import aleksey.khokhrin.ru.translator.main.bookmark.DaggerBookmarkComponent;
import aleksey.khokhrin.ru.translator.main.bookmark.FavouritesFragmentListenerModel;
import aleksey.khokhrin.ru.translator.main.bookmark.HistoryFragmentListenerModel;
import aleksey.khokhrin.ru.translator.main.settings.SettingsFragment;
import aleksey.khokhrin.ru.translator.main.transtlate.DaggerTranslateComponent;
import aleksey.khokhrin.ru.translator.main.transtlate.TranslateFragment;
import aleksey.khokhrin.ru.translator.main.transtlate.TranslatePresenterModule;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainListenerModule.MainListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    TranslateFragment translateFragment;
    BookmarkFragment bookmarkFragment;
    SettingsFragment settingsFragment;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.slidingTabs)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        MainListenerModule.getInstance().setListener(this);

        getSupportActionBar().hide();

        //create fragments
        MainViewPageAdapter adapter = new MainViewPageAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    FavouritesFragmentListenerModel.getInstance().updateFavouritesTab();
                    HistoryFragmentListenerModel.getInstance().updateHistoryTab();
                }
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.GRAVITY_CENTER);
        tabLayout.setSelectedTabIndicatorHeight(4);

        //set drawables for each tab
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_translate);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_bookmark);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_settings);

        translateFragment = adapter.getTranslateFragment();
        DaggerTranslateComponent.builder()
                .repositoryComponent(((TranslatorApplication)getApplicationContext()).getRepositoryComponent())
                .translatePresenterModule(new TranslatePresenterModule(translateFragment))
                .build()
                .inject(translateFragment);

        bookmarkFragment = adapter.getBookmarkFragment();
        DaggerBookmarkComponent.builder()
                .repositoryComponent(((TranslatorApplication) getApplicationContext()).getRepositoryComponent())
                .bookmarkPresenterModule(new BookmarkPresenterModule(bookmarkFragment))
                .build()
                .inject(bookmarkFragment);

    }

    @Override
    public void openTranslate(Translate translate) {
        viewPager.setCurrentItem(0, true);
        translateFragment.setTranslate(translate);
    }

    private class MainViewPageAdapter extends FragmentPagerAdapter {
        List<String> pageTitles = new ArrayList<>();
        int translateFragmentPage = 0,
                bookmarkFragmentPage = 1,
                settingsFragmentPage = 2;

        MainViewPageAdapter(FragmentManager fm) {
            super(fm);
            pageTitles.add("");
            pageTitles.add("");
            pageTitles.add("");
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            if (position == translateFragmentPage) {
                if (translateFragment == null)
                    translateFragment = TranslateFragment.newInstance();
                fragment = translateFragment;
            }
            else if(position == bookmarkFragmentPage) {
                if (bookmarkFragment == null)
                    bookmarkFragment = BookmarkFragment.newInstance();
                fragment = bookmarkFragment;
            }
//            else if(position == settingsFragmentPage) {
            else {
                if (settingsFragment == null)
                    settingsFragment = SettingsFragment.newInstance();
                fragment = settingsFragment;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return pageTitles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitles.get(position);
        }

        TranslateFragment getTranslateFragment() {
            TranslateFragment fragment = (TranslateFragment) getSupportFragmentManager()
                    .findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + translateFragmentPage);
            return fragment != null ? fragment : (TranslateFragment) getItem(translateFragmentPage);
        }

        BookmarkFragment getBookmarkFragment() {
            BookmarkFragment fragment = (BookmarkFragment) getSupportFragmentManager()
                    .findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + bookmarkFragmentPage);
            return fragment != null ? fragment : (BookmarkFragment) getItem(bookmarkFragmentPage);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    view.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    translateFragment.saveTranslate();
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

}
