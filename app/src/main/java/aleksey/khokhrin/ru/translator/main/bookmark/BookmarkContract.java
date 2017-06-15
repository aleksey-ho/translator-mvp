package aleksey.khokhrin.ru.translator.main.bookmark;

import java.util.List;

import aleksey.khokhrin.ru.translator.BasePresenter;
import aleksey.khokhrin.ru.translator.data.Translate;

class BookmarkContract {

    interface View {

        void setFavouritesTranslates(List<Translate> translates);

        void setHistoryTranslates(List<Translate> translates);

        void setSavedAsFavourite(Translate translate);

        void setRemovedFromFavourites(Translate translate);

    }

    interface Presenter extends BasePresenter {

        void loadTranslatesInHistory();

        void loadTranslatesInFavourites();

        void saveAsFavourite(Translate translate);

        void removeFromFavourites(Translate translate);

        void clearHistory();

        void clearFavourites();

    }

}
