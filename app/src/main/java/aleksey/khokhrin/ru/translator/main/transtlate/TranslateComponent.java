package aleksey.khokhrin.ru.translator.main.transtlate;

import aleksey.khokhrin.ru.translator.FragmentScoped;
import aleksey.khokhrin.ru.translator.data.source.RepositoryComponent;
import dagger.Component;

@FragmentScoped
@Component(dependencies = RepositoryComponent.class, modules = {TranslatePresenterModule.class})
public interface TranslateComponent {

    void inject(TranslateFragment fragment);
}
