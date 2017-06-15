package aleksey.khokhrin.ru.translator.langSelector;

import dagger.Module;
import dagger.Provides;

@Module
public class LangSelectionPresenterModule {

    private final LangSelectionContract.View mView;

    public LangSelectionPresenterModule(LangSelectionContract.View view) {
        mView = view;
    }

    @Provides
    LangSelectionContract.View provideMainContractView() {
        return mView;
    }

}
