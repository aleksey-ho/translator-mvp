package aleksey.khokhrin.ru.translator.main.bookmark;

import aleksey.khokhrin.ru.translator.data.Translate;

public interface BookmarkListener {

    void saveAsFavourite(Translate translate);

    void removeFromFavourites(Translate translate);

    void openTranslate(Translate translate);

}
