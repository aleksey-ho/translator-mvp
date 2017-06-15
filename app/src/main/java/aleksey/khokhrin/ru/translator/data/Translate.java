package aleksey.khokhrin.ru.translator.data;

import java.util.Date;
import java.util.Objects;

public class Translate {
    private Language languageSource;
    private Language languageTarget;
    private Date date;
    private String textSource;
    private String textTarget;
    private boolean savedInHistory;
    private boolean savedInFavourites;

    public Translate(String textSource, String textTarget, Language languageSource, Language languageTarget,
                     Date date, boolean savedInHistory, boolean savedInFavourites) {
        this.languageSource = languageSource;
        this.languageTarget = languageTarget;
        this.date = date;
        this.textSource = textSource;
        this.textTarget = textTarget;
        this.savedInHistory = savedInHistory;
        this.savedInFavourites = savedInFavourites;
    }

    public Translate() {
    }

    public Translate(String textSource, Language languageSource, Language languageTarget) {
        this.languageSource = languageSource;
        this.languageTarget = languageTarget;
        this.textSource = textSource;
    }

    public Language getLanguageSource() {
        return languageSource;
    }

    public void setLanguageSource(Language languageSource) {
        this.languageSource = languageSource;
    }

    public Language getLanguageTarget() {
        return languageTarget;
    }

    public void setLanguageTarget(Language languageTarget) {
        this.languageTarget = languageTarget;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTextSource() {
        return textSource;
    }

    public void setTextSource(String textSource) {
        this.textSource = textSource;
    }

    public String getTextTarget() {
        return textTarget;
    }

    public void setTextTarget(String textTarget) {
        this.textTarget = textTarget;
    }

    public boolean isSavedInHistory() {
        return savedInHistory;
    }

    public void setSavedInHistory(boolean savedInHistory) {
        this.savedInHistory = savedInHistory;
    }

    public boolean isSavedInFavourites() {
        return savedInFavourites;
    }

    public void setSavedInFavourites(boolean savedInFavourites) {
        this.savedInFavourites = savedInFavourites;
    }

    public static boolean equals(Translate translate1, Translate translate2) {
        return (Objects.equals(translate1.getLanguageSource().getCode(), translate2.getLanguageSource().getCode())
                && Objects.equals(translate1.getLanguageTarget().getCode(), translate2.getLanguageTarget().getCode())
                && Objects.equals(translate1.getTextSource(), translate2.getTextSource()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Translate translate = (Translate) o;

        if (languageSource != null ? !languageSource.equals(translate.languageSource) : translate.languageSource != null)
            return false;
        if (languageTarget != null ? !languageTarget.equals(translate.languageTarget) : translate.languageTarget != null)
            return false;
        return textSource != null ? textSource.equals(translate.textSource) : translate.textSource == null;

    }

    @Override
    public int hashCode() {
        int result = languageSource != null ? languageSource.hashCode() : 0;
        result = 31 * result + (languageTarget != null ? languageTarget.hashCode() : 0);
        result = 31 * result + (textSource != null ? textSource.hashCode() : 0);
        return result;
    }
}
