package aleksey.khokhrin.ru.translator.data.source;

import android.content.Context;

import javax.inject.Singleton;

import aleksey.khokhrin.ru.translator.data.source.local.LocalDataSource;
import aleksey.khokhrin.ru.translator.data.source.remote.RemoteDataSource;
import aleksey.khokhrin.ru.translator.data.source.remote.YandexTranslateApi;
import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Singleton
    @Provides
    @Local
    DataSource provideTasksLocalDataSource(Context context) {
        return new LocalDataSource(context);
    }

    @Singleton
    @Provides
    @Remote
    DataSource provideTasksRemoteDataSource(YandexTranslateApi yandexTranslateApi) {
        return new RemoteDataSource(yandexTranslateApi);
    }
}
