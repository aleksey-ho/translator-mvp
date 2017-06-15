package aleksey.khokhrin.ru.translator.data.source.remote;

import aleksey.khokhrin.ru.translator.data.source.remote.model.LanguageModel;
import aleksey.khokhrin.ru.translator.data.source.remote.model.TranslateModel;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YandexTranslateApi {
    @GET("api/v1.5/tr.json/translate")
    Single<TranslateModel> translate(@Query("text") String text,
                                     @Query("lang") String lang,
                                     @Query("key") String key);

    @GET("api/v1.5/tr.json/getLangs")
    Observable<LanguageModel> getLangs(@Query("ui") String ui,
                                       @Query("key") String key);
}
