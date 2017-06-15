package aleksey.khokhrin.ru.translator.main.bookmark;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import aleksey.khokhrin.ru.translator.R;
import aleksey.khokhrin.ru.translator.TranslatorApplication;
import aleksey.khokhrin.ru.translator.data.Translate;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookmarkFragment extends Fragment implements BookmarkContract.View {
    private static final String TAG = BookmarkFragment.class.getSimpleName();

    HistoryFragment historyFragment;
    FavouritesFragment favouritesFragment;

    private BookmarkFragmentViewPageAdapter adapter;

    @Inject
    BookmarkPresenter bookmarkPresenter;

    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.bookmarkViewPager) ViewPager viewPager;

    @OnClick(R.id.buttonClearHistory)
    void clearBookmarks() {
        int currentItem = viewPager.getCurrentItem();
        String message = currentItem == 0 ? getString(R.string.delete_history_question) :
                getString(R.string.delete_favourites_question);

        AlertDialog.Builder messageBox = new AlertDialog.Builder(getContext());
        messageBox.setTitle(currentItem == 0 ? R.string.view_pager_history : R.string.view_pager_favourites);
        messageBox.setMessage(message);
        messageBox.setPositiveButton(R.string.yes, (dialog, which) -> {
            if (currentItem == 0) {
                bookmarkPresenter.clearHistory();
            }
            else {
                bookmarkPresenter.clearFavourites();
            }
        });
        messageBox.setNegativeButton(R.string.cancel, null);
        messageBox.show();
    }

    public BookmarkFragment() { }

    public static BookmarkFragment newInstance() {
        return new BookmarkFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bookmark, container, false);
        ButterKnife.bind(this, root);

        FragmentActivity context = (FragmentActivity) getContext();
        adapter = new BookmarkFragmentViewPageAdapter(context.getSupportFragmentManager());

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                FavouritesFragmentListenerModel.getInstance().updateFavouritesTab();
                HistoryFragmentListenerModel.getInstance().updateHistoryTab();
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        historyFragment = adapter.getHistoryFragment();
        DaggerBookmarkComponent.builder()
                .repositoryComponent(((TranslatorApplication) getContext().getApplicationContext()).getRepositoryComponent())
                .bookmarkPresenterModule(new BookmarkPresenterModule(historyFragment))
                .build()
                .inject(historyFragment);

        favouritesFragment = adapter.getFavouritesFragment();
        DaggerBookmarkComponent.builder()
                .repositoryComponent(((TranslatorApplication) getContext().getApplicationContext()).getRepositoryComponent())
                .bookmarkPresenterModule(new BookmarkPresenterModule(favouritesFragment))
                .build()
                .inject(favouritesFragment);

        return root;
    }

    @Override
    public void setFavouritesTranslates(List<Translate> translates) {
    }

    @Override
    public void setHistoryTranslates(List<Translate> translates) {
    }

    @Override
    public void setSavedAsFavourite(Translate translate) {
    }

    @Override
    public void setRemovedFromFavourites(Translate translate) {
    }

    private class BookmarkFragmentViewPageAdapter extends FragmentPagerAdapter {
        List<String> pageTitles = new ArrayList<>();
        int historyFragmentPage = 0, favouritesFragmentPage = 1;

        BookmarkFragmentViewPageAdapter(FragmentManager fm) {
            super(fm);
            pageTitles.add(getString(R.string.view_pager_history));
            pageTitles.add(getString(R.string.view_pager_favourites));
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            if (position == historyFragmentPage) {
                if (historyFragment == null)
                    historyFragment = HistoryFragment.newInstance();
                fragment = historyFragment;
            }
            else { //if favouritesFragmentPage
                if (favouritesFragment == null)
                    favouritesFragment = FavouritesFragment.newInstance();
                fragment = favouritesFragment;
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

        HistoryFragment getHistoryFragment() {
            HistoryFragment fragment = (HistoryFragment) getFragmentManager()
                    .findFragmentByTag("android:switcher:" + R.id.bookmarkViewPager + ":" + historyFragmentPage);
            return fragment != null ? fragment : (HistoryFragment) getItem(historyFragmentPage);
        }

        FavouritesFragment getFavouritesFragment() {
            FavouritesFragment fragment = (FavouritesFragment) getFragmentManager()
                    .findFragmentByTag("android:switcher:" + R.id.bookmarkViewPager + ":" + favouritesFragmentPage);
            return fragment != null ? fragment : (FavouritesFragment) getItem(favouritesFragmentPage);
        }
    }

}
