package ru.chulkova.currencytelegrambot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import ru.chulkova.currencytelegrambot.model.Currency;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Slf4j
public class CurrencyService {

    public static String getCurrencyRate(String message,
                                         Currency model) throws IOException, InterruptedException, ParseException {
        message.trim();
        String result = IOUtils.toString(new URL("https://www.cbr-xml-daily.ru/daily_json.js"), StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(String.valueOf(result));
        model.setDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(json.get("Date").asText()));
        JsonNode currency = json.findValue(message);
        log.info("getCurrencyRate {}", currency);
        model.setAbbreviation(currency.get("CharCode").asText());
        model.setFullNameRU(currency.get("Name").asText());
        model.setRate(currency.get("Value").asDouble());
        return String.format("Official rate of RUB to %s at %s is %s", model.getAbbreviation(),
                getFormatDate(model), model.getRate());
    }

    private static String getFormatDate(Currency model) {
        return new SimpleDateFormat("dd-MM-yyyy").format(model.getDate());
    }
}
