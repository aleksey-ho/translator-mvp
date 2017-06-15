package aleksey.khokhrin.ru.translator.data.source;

import javax.inject.Singleton;

import aleksey.khokhrin.ru.translator.ApplicationModule;
import aleksey.khokhrin.ru.translator.data.source.remote.YandexTranslateApiModule;
import dagger.Component;

@Singleton
@Component(modules = {RepositoryModule.class, ApplicationModule.class, YandexTranslateApiModule.class})
public interface RepositoryComponent {

    Repository getRepository();
}
