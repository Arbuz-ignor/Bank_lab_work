package service;

import domain.Account;
import domain.Transaction;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BankService
{
    // константы вместо магических чисел
    public static final int ACC_NUMBER_LN = 20;
    public static final int BIK_LN = 9;
    public static final int KPP_LN = 9;

    private final Map<String, Account> accounts = new HashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void loadData()
    {
        // пока ничего серелизация позже появится
    }
    public void saveData()
    {
        // пока ничего серелизация позже появится
    }

    public Account createAccount(String owner)
    {
        if (owner == null || owner.isBlank())
            throw new IllegalArgumentException("ФИО не может быть пустым");
        // генерим уникальные реквизиты
        String bik = randomDigits(BIK_LN);
        String kpp = randomDigits(KPP_LN);
        String number;
        do { number = randomDigits(ACC_NUMBER_LN); } while (accounts.containsKey(number));
        Account acc = new Account(number, owner.trim(), bik, kpp, 0);
        accounts.put(number, acc);
        return acc;
    }

    public long getBalance(String number)
    {
        Account a = accounts.get(number);
        if (a == null) throw new NoSuchElementException("Счёт не найден");
        return a.getBalance();
    }

    // заглушки
    public long deposit(String n, long amt) { throw new UnsupportedOperationException("ещё не реализовано"); }
    public long withdraw(String n, long amt) { throw new UnsupportedOperationException("ещё не реализовано"); }
    public List<Transaction> listTransactions(String n) { return List.of(); }
    public List<Account> search(String q) { return List.of(); }

    // пока без хранения и индексов
    private static String randomDigits(int n) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(r.nextInt(10));
        return sb.toString();
    }
    protected static String shortUuid() { return UUID.randomUUID().toString().replace("-", "").substring(0, 8); }
    protected static String now() { return LocalDateTime.now().format(TS); }
}
