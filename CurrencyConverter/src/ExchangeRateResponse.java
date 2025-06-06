package com.example.currencyconverter;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class ExchangeRateResponse {
    private String result;
    @SerializedName("documentation")
    private String documentationUrl;
    @SerializedName("terms_of_service")
    private String termsOfServiceUrl;
    @SerializedName("time_last_update_unix")
    private long timeLastUpdateUnix;
    @SerializedName("time_last_update_utc")
    private String timeLastUpdateUtc;
    @SerializedName("time_next_update_unix")
    private long timeNextUpdateUnix;
    @SerializedName("time_next_update_utc")
    private String timeNextUpdateUtc;
    @SerializedName("base_code")
    private String baseCode;
    @SerializedName("conversion_rates")
    private Map<String, Double> conversionRates;

    // Getters
    public String getResult() {
        return result;
    }

    public String getDocumentationUrl() {
        return documentationUrl;
    }

    public String getTermsOfServiceUrl() {
        return termsOfServiceUrl;
    }

    public long getTimeLastUpdateUnix() {
        return timeLastUpdateUnix;
    }

    public String getTimeLastUpdateUtc() {
        return timeLastUpdateUtc;
    }

    public long getTimeNextUpdateUnix() {
        return timeNextUpdateUnix;
    }

    public String getTimeNextUpdateUtc() {
        return timeNextUpdateUtc;
    }

    public String getBaseCode() {
        return baseCode;
    }

    public Map<String, Double> getConversionRates() {
        return conversionRates;
    }

    // Puedes añadir un método toString() para depuración si lo deseas
    @Override
    public String toString() {
        return "ExchangeRateResponse{" +
                "result='" + result + '\'' +
                ", baseCode='" + baseCode + '\'' +
                ", conversionRates=" + conversionRates +
                '}';
    }
}