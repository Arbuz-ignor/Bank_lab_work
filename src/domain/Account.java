package domain;

import java.io.Serializable;
import java.util.Objects;


public class Account implements Serializable
{
    // нужен для сериализации, фиксирует версию класса
    private static final long serialVersionUID = 1L;

    private final String number;
    private final String owner;
    private final String bik;
    private final String kpp;      //
    private long balance;


    public Account(String number, String owner, String bik, String kpp, long balance)
    {
        if (balance < 0) throw new IllegalArgumentException("Баланс не может быть отрицательным");
        // Реквизиты фиксируются навсегда, менять их после создания нельзя
        this.number = Objects.requireNonNull(number);
        this.owner  = Objects.requireNonNull(owner);
        this.bik    = Objects.requireNonNull(bik);
        this.kpp    = Objects.requireNonNull(kpp);
        this.balance = balance;

    }


    public String getNumber()  { return number; }
    public String getOwner()   { return owner; }
    public String getBik()     { return bik; }
    public String getKpp()     { return kpp; }
    public long getBalance()   { return balance; }


    public void deposit(long amount)
    {
        if (amount <= 0) throw new IllegalArgumentException("Сумма должна быть положительной");
        balance += amount;
    }

    public void withdraw(long amount)
    {
        if (amount <= 0) throw new IllegalArgumentException("Сумма должна быть положительной");
        if (balance < amount) throw new IllegalArgumentException("Недостаточно средств");
        balance -= amount;
    }


    @Override
    public String toString()
    {
        return "Счёт №: " + number + "\n" +
                "Баланс = " + balance + " RUB\n" +
                "Владелец: " + owner + "\n" +
                "БИК: " + bik + "\n" +
                "КПП: " + kpp;
    }
}
