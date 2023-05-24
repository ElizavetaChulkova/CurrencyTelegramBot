package ru.chulkova.currencytelegrambot;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.chulkova.currencytelegrambot.config.BotConfig;
import ru.chulkova.currencytelegrambot.model.Currency;

import java.io.IOException;
import java.text.ParseException;

@Component
@AllArgsConstructor
@Slf4j
public class CurrencyBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private static final String ERROR_MESSAGE = "We have not found such currency. \n" +
            "Enter the currency whose official exchange rate you want " +
            "to know in relation to RUB. \n For example, USD.";
    private static final String START_MESSAGE = "Enter the currencies whose official exchange rate \n" +
            "you want to know in relation to RUB. \n " + "For example: USD";

    @Override
    public void onUpdateReceived(Update update) {
        Currency currencyModel = new Currency();
        String currency = "";
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start":
                    log.info("onUpdateReceived /start");
                    startReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default:
                    try {
                        log.info("default getCurrencyRate");
                        currency = CurrencyService.getCurrencyRate(messageText, currencyModel);
                    } catch (IOException e) {
                        sendMessage(chatId, ERROR_MESSAGE);
                    } catch (InterruptedException | ParseException e) {
                        throw new RuntimeException(e);
                    }
                    sendMessage(chatId, currency);
            }
        }
    }

    private void startReceived(Long chatId, String name) {
        String answer = "Hello, " + name + "! \n" + START_MESSAGE;
        sendMessage(chatId, answer);
        log.info("sendMessage name {}", name);

    }

    private void sendMessage(Long chatId, String text) {
        SendMessage send = new SendMessage();
        send.setChatId(String.valueOf(chatId));
        send.setText(text);
        try {
            execute(send);
        } catch (TelegramApiException ignored) {
        }
    }

    @Override
    public String getBotUsername() {
        return config.getUserName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }
}
