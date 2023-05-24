package ru.chulkova.currencytelegrambot.model;

import lombok.Data;

import java.util.Date;

@Data
public class Currency {

    String abbreviation;
    String fullNameRU;
    Date date;
    Double rate;
}
