package aleksey.khokhrin.ru.translator.langSelector;

import aleksey.khokhrin.ru.translator.FragmentScoped;
import aleksey.khokhrin.ru.translator.data.source.RepositoryComponent;
import dagger.Component;

@FragmentScoped
@Component(dependencies = RepositoryComponent.class, modules = {LangSelectionPresenterModule.class})
public interface LangSelectionComponent {

    void inject(LangSelectionActivity activity);
}
