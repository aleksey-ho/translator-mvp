package aleksey.khokhrin.ru.translator.main.bookmark;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class HistoryFragment extends Fragment implements BookmarkContract.View,
        BookmarkListener, HistoryFragmentListenerModel.HistoryFragmentListener {
    private static final String TAG = HistoryFragment.class.getSimpleName();

    @Inject
    BookmarkPresenter bookmarkPresenter;

    @BindView(R.id.listViewHistory)RecyclerView listViewHistory;
    BookmarkRecyclerAdapter bookmarkRecyclerAdapter;

    @BindView(R.id.historyEmpty)
    LinearLayout historyEmpty;

    public HistoryFragment() { }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, root);
        HistoryFragmentListenerModel.getInstance().setListener(this);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bookmarkPresenter.loadTranslatesInHistory();
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
    public void setFavouritesTranslates(List<Translate> translates) {
    }

    @Override
    public void setHistoryTranslates(List<Translate> translates) {
        bookmarkRecyclerAdapter = new BookmarkRecyclerAdapter(getContext(), translates, this);
        listViewHistory.setAdapter(bookmarkRecyclerAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        listViewHistory.setLayoutManager(linearLayoutManager);
        historyEmpty.setVisibility(translates.size() > 0 ? View.GONE : View.VISIBLE);
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
    public void updateHistoryTab() {
        bookmarkPresenter.loadTranslatesInHistory();
    }

}
