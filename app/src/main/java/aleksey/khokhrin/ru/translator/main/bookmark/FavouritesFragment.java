package aleksey.khokhrin.ru.translator.main.bookmark;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import javax.inject.Inject;

import aleksey.khokhrin.ru.translator.R;
import aleksey.khokhrin.ru.translator.data.Translate;
import aleksey.khokhrin.ru.translator.main.MainListenerModule;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouritesFragment extends Fragment implements BookmarkContract.View,
        BookmarkListener, FavouritesFragmentListenerModel.FavouritesFragmentListener {
    private static final String TAG = FavouritesFragment.class.getSimpleName();

    @Inject
    BookmarkPresenter bookmarkPresenter;

    @BindView(R.id.listViewFavourite)RecyclerView listViewFavourite;
    BookmarkRecyclerAdapter bookmarkRecyclerAdapter;

    @BindView(R.id.favouritesEmpty)
    LinearLayout favouritesEmpty;

    public FavouritesFragment() { }

    public static FavouritesFragment newInstance() {
        return new FavouritesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favourite, container, false);
        ButterKnife.bind(this, root);
        FavouritesFragmentListenerModel.getInstance().setListener(this);

        bookmarkPresenter.loadTranslatesInFavourites();

        return root;
    }

    @Override
    public void setFavouritesTranslates(List<Translate> translates) {
        bookmarkRecyclerAdapter = new BookmarkRecyclerAdapter(getContext(), translates, this);
        listViewFavourite.setAdapter(bookmarkRecyclerAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        listViewFavourite.setLayoutManager(linearLayoutManager);
        favouritesEmpty.setVisibility(translates.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setHistoryTranslates(List<Translate> translates) {
    }

    @Override
    public void setSavedAsFavourite(Translate translate) {
        bookmarkRecyclerAdapter.setSavedAsFavourite(translate);
    }

    @Override
    public void setRemovedFromFavourites(Translate translate) {
        bookmarkRecyclerAdapter.setRemovedFromFavourites(translate);
    }

    @Override
    public void saveAsFavourite(Translate translate) {
        bookmarkPresenter.saveAsFavourite(translate);
    }

    @Override
    public void removeFromFavourites(Translate translate) {
        bookmarkPresenter.removeFromFavourites(translate);
    }

    @Override
    public void openTranslate(Translate translate) {
        MainListenerModule.getInstance().openTranslate(translate);
    }

    @Override
    public void updateFavouritesTab() {
        bookmarkPresenter.loadTranslatesInFavourites();
    }

}
