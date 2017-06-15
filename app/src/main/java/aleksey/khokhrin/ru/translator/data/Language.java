package aleksey.khokhrin.ru.translator.data;

import java.util.Calendar;
import java.util.Date;

public class Language {
    private String code;
    private String name;
    private Date sourceLastUseDate;
    private Date targetLastUseDate;
    // can be used in future to show "Frequently used languages"
    private int usageCounter;

    public Language(String code, String name, Date sourceLastUseDate, Date targetLastUseDate, int usageCounter) {
        this.code = code;
        this.name = name;
        this.sourceLastUseDate = sourceLastUseDate;
        this.targetLastUseDate = targetLastUseDate;
        this.usageCounter = usageCounter;
    }

    public Language(int languageId, String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getSourceLastUseDate() {
        return sourceLastUseDate;
    }

    public void setSourceLastUseDate(Date sourceLastUseDate) {
        this.sourceLastUseDate = sourceLastUseDate;
    }

    public Date getTargetLastUseDate() {
        return targetLastUseDate;
    }

    public void setTargetLastUseDate(Date targetLastUseDate) {
        this.targetLastUseDate = targetLastUseDate;
    }

    public int getUsageCounter() {
        return usageCounter;
    }

    public void setUsageCounter(int usageCounter) {
        this.usageCounter = usageCounter;
    }

    public void updateUsage(LangSelectionDirection direction) {
        usageCounter++;
        if (direction == LangSelectionDirection.SOURCE)
            sourceLastUseDate = Calendar.getInstance().getTime();
        else
            targetLastUseDate = Calendar.getInstance().getTime();
    }
}
