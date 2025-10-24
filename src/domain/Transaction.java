package domain;

import java.io.Serializable;

public class Transaction implements Serializable
{
    //тоже нужен для серелизации
    private static final long serialVersionUID = 1L;

    private final String txId;
    private final String number;
    private final long amount;
    private final String time;   // yyyy-MM-dd HH:mm:ss
    private final String type;
    private final long balance_after;

    public Transaction(String txId, String number, long amount, String time, String type, long balance_after)
    {
        this.txId = txId;
        this.number = number;
        this.amount = amount;
        this.time = time;
        this.type = type;
        this.balance_after = balance_after;
    }

    public String getTxId() { return txId; }
    public String getNumber() { return number; }
    public long getAmount() { return amount; }
    public String getTime() { return time; }
    public String getType() { return type; }
    public long getBalanceAfter() { return balance_after; }

    @Override
    public String toString()
    {
        return "Операция: " + txId + "\n" +
                "Счёт: " + number + "\n" +
                "Тип: " + type + "\n" +
                "Сумма: " + amount + " RUB\n" +
                "Баланс после: " + balance_after + " RUB\n" +
                "Дата: " + time;
    }
}
