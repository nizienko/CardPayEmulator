package ru.yamoney.test.services.card_pay.acquiring;

import ru.yamoney.test.services.card_pay.Card;

import java.math.BigDecimal;

/**
 * Created by def on 20.03.2016.
 */

public interface BankAcquireService {
    BankAcquireResponse authorize(Card card, BigDecimal sum);
}
