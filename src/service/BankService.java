package service;

import domain.Account;
import domain.Transaction;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.LinkedHashSet;
import java.io.*;
import java.nio.file.*;

public class BankService
{
    // константы вместо магических чисел
    public static final int ACC_NUMBER_LN = 20;
    public static final int BIK_LN = 9;
    public static final int KPP_LN = 9;

    // Основные структуры
    private final Map<String, Account> accounts = new HashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();

    // Индексы для быстрых точных совпадений
    private final Map<String, Account> byNumber = new HashMap<>();
    private final Map<String, List<Account>> byOwner = new HashMap<>();
    private final Map<String, List<Account>> byBik = new HashMap<>();
    private final Map<String, List<Account>> byKpp = new HashMap<>();
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // где лежат файлы
    private final Path dataDir = Path.of("data");
    private final Path accSer  = dataDir.resolve("accounts.ser");
    private final Path txSer   = dataDir.resolve("transactions.ser");

    public void loadData()
    {
        try
        {
            // на случай чистого проекта
            Files.createDirectories(dataDir);
            if (Files.exists(accSer)) {
                try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(accSer))) {
                    @SuppressWarnings("unchecked")
                    List<Account> list = (List<Account>) ois.readObject();
                    for (Account a : list) accounts.put(a.getNumber(), a);
                }
            }
            if (Files.exists(txSer))
            {
                try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(txSer))) {
                    @SuppressWarnings("unchecked")
                    List<Transaction> list = (List<Transaction>) ois.readObject();
                    transactions.addAll(list);
                }
            }
        } catch (Exception e) {
            // произошла ошибка
            System.out.println("Ошибка чтения данных: " + e.getMessage());
        }
        rebuildIndexes();
    }

    public void saveData()
    {
        try {
            Files.createDirectories(dataDir);
            // атомарная запись, сначала tmp, потом move
            Path tmpAcc = Files.createTempFile("acc", ".ser");
            Path tmpTx  = Files.createTempFile("tx",  ".ser");
            try (ObjectOutputStream o1 = new ObjectOutputStream(Files.newOutputStream(tmpAcc));
                 ObjectOutputStream o2 = new ObjectOutputStream(Files.newOutputStream(tmpTx))) {
                o1.writeObject(new ArrayList<>(accounts.values()));
                o2.writeObject(new ArrayList<>(transactions));
            }
            Files.move(tmpAcc, accSer, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            Files.move(tmpTx,  txSer,  StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            System.out.println("Ошибка записи данных: " + e.getMessage());
        }
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
        rebuildIndexes();
        saveData();
        return acc;
    }

    public long getBalance(String number)
    {
        Account a = accounts.get(number);
        if (a == null) throw new NoSuchElementException("Счёт не найден");
        return a.getBalance();
    }

    public long deposit(String number, long amount) {
        Account a = accounts.get(number);
        if (a == null) throw new NoSuchElementException("Счёт не найден");
        a.deposit(amount);
        // фиксируем факт операции
        transactions.add(new Transaction(shortUuid(), number, amount, now(), "Пополнение", a.getBalance()));
        saveData();
        return a.getBalance();
    }

    public long withdraw(String number, long amount)
    {
        Account a = accounts.get(number);
        if (a == null) throw new NoSuchElementException("Счёт не найден");
        a.withdraw(amount);
        transactions.add(new Transaction(shortUuid(), number, amount, now(), "Списание", a.getBalance()));
        saveData();
        return a.getBalance();
    }

    public List<Transaction> listTransactions(String number)
    {
        List<Transaction> list = new ArrayList<>();
        for (Transaction t : transactions)
        {
            if (t.getNumber().equals(number)) list.add(t);
        }
        if (list.isEmpty()) throw new NoSuchElementException("Транзакций нет");
        return list;
    }


    public List<Account> search(String query)
    {
        String q = query == null ? "" : query.trim();
        if (q.isEmpty()) return List.of();
        String qLower = q.toLowerCase();

        // Чтобы не было дублей и сохранялся порядок нахождения
        LinkedHashSet<Account> result = new LinkedHashSet<>();

        // Точное совпадение номера
        Account exact = accounts.get(q);
        if (exact != null) result.add(exact);

        // подстрока по всем полям
        for (Account a : accounts.values())
        {
            if (a.getNumber().contains(q) ||
                    a.getBik().contains(q) ||
                    a.getKpp().contains(q) ||
                    a.getOwner().toLowerCase().contains(qLower))
            {
                result.add(a);
            }
        }
        if (result.isEmpty()) throw new NoSuchElementException("Счёт не найден");
        return new ArrayList<>(result);
    }


    private void rebuildIndexes()
    {
        byNumber.clear(); byOwner.clear(); byBik.clear(); byKpp.clear();
        for (Account a : accounts.values())
        {
            byNumber.put(a.getNumber(), a);
            // computeIfAbsent — если по ключу ещё нет списка, создаёт его и кладёт в map
            byOwner.computeIfAbsent(a.getOwner().toLowerCase(), k -> new ArrayList<>()).add(a);
            byBik.computeIfAbsent(a.getBik(), k -> new ArrayList<>()).add(a);
            byKpp.computeIfAbsent(a.getKpp(), k -> new ArrayList<>()).add(a);
        }
    }

    // пока без хранения и индексов
    private static String randomDigits(int n)
    {
        Random r = new Random();
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(r.nextInt(10));
        return sb.toString();
    }
    protected static String shortUuid() { return UUID.randomUUID().toString().replace("-", "").substring(0, 8); }
    protected static String now() { return LocalDateTime.now().format(TS); }
}
