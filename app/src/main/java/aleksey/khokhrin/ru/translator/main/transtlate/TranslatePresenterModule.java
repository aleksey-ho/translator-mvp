package aleksey.khokhrin.ru.translator.main.transtlate;

import dagger.Module;
import dagger.Provides;

@Module
public class TranslatePresenterModule {

    private final TranslateContract.View mView;

    public TranslatePresenterModule(TranslateContract.View view) {
        mView = view;
    }

    @Provides
    TranslateContract.View provideMainContractView() {
        return mView;
    }

}
