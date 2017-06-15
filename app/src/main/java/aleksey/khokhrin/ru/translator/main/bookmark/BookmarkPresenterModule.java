package aleksey.khokhrin.ru.translator.main.bookmark;

import dagger.Module;
import dagger.Provides;

@Module
public class BookmarkPresenterModule {

    private final BookmarkContract.View mView;

    public BookmarkPresenterModule(BookmarkContract.View view) {
        mView = view;
    }

    @Provides
    BookmarkContract.View provideMainContractView() {
        return mView;
    }

}
