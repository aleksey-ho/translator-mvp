package aleksey.khokhrin.ru.translator.data.source.remote;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static aleksey.khokhrin.ru.translator.data.source.remote.RemoteDataSource.ENDPOINT;

@Module
public class YandexTranslateApiModule {

    @Singleton
    @Provides
    YandexTranslateApi provideYandexTranslateApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(YandexTranslateApi.class);
    }
}
