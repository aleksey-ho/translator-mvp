package aleksey.khokhrin.ru.translator.main.bookmark;

import aleksey.khokhrin.ru.translator.FragmentScoped;
import aleksey.khokhrin.ru.translator.data.source.RepositoryComponent;
import dagger.Component;

@FragmentScoped
@Component(dependencies = RepositoryComponent.class, modules = {BookmarkPresenterModule.class})
public interface BookmarkComponent {
    void inject(BookmarkFragment fragment);
    void inject(FavouritesFragment fragment);
    void inject(HistoryFragment fragment);
}
