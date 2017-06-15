package aleksey.khokhrin.ru.translator.main.bookmark;

import javax.inject.Inject;

import aleksey.khokhrin.ru.translator.data.Translate;
import aleksey.khokhrin.ru.translator.data.source.Repository;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BookmarkPresenter implements BookmarkContract.Presenter {

    private final Repository mRepository;
    BookmarkContract.View mView;

    @Inject
    BookmarkPresenter(Repository repository, BookmarkContract.View tasksView) {
        mRepository = repository;
        mView = tasksView;
    }

    @Override
    public void start() {

    }

    @Override
    public void loadTranslatesInHistory() {
        mRepository.getTranslatesInHistory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(translates -> {
            mView.setHistoryTranslates(translates);
        }, Throwable::printStackTrace);
    }

    @Override
    public void loadTranslatesInFavourites() {
        mRepository.getFavouriteTranslates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(translates -> {
            mView.setFavouritesTranslates(translates);
        }, Throwable::printStackTrace);
    }

    @Override
    public void saveAsFavourite(Translate translate) {
        mRepository.saveAsFavourite(translate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
            mView.setSavedAsFavourite(translate);
        }, Throwable::printStackTrace);
    }

    @Override
    public void removeFromFavourites(Translate translate) {
        mRepository.removeFromFavourites(translate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
            mView.setRemovedFromFavourites(translate);
        }, Throwable::printStackTrace);
    }

    @Override
    public void clearHistory() {
        mRepository.clearHistory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
            HistoryFragmentListenerModel.getInstance().updateHistoryTab();
        }, Throwable::printStackTrace);

    }

    @Override
    public void clearFavourites() {
        mRepository.clearFavourites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    FavouritesFragmentListenerModel.getInstance().updateFavouritesTab();
                }, Throwable::printStackTrace);
    }

}
