package cli;

import domain.Account;
import domain.Transaction;
import service.BankService;
import java.util.*;

//Консольное меню через карту действий Map<Integer, Runnable>
public class Main {
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args)
    {
        BankService svc = new BankService();
        svc.loadData(); // подтянем прошлые сессии
        // LinkedHashMap сохраняет порядок и отображение меню будет стабильно
        Map<Integer, Runnable> actions = new LinkedHashMap<>();

        actions.put(1, () ->
        {
            System.out.print("ФИО владельца: ");
            String owner = in.nextLine().trim();
            try
            {
                Account a = svc.createAccount(owner);
                System.out.println("\nСчёт создан\n" + a);
            }
            catch (Exception e)
            {
                System.out.println("Ошибка: " + e.getMessage());
            }
        });

        actions.put(2, () ->
        {
            System.out.print("Номер счёта: ");
            String num = in.nextLine().trim();
            System.out.print("Сумма: ");
            String s = in.nextLine().trim();
            if (!s.matches("\\d+"))
            {
                System.out.println("Сумма должна быть числом.");
                return;
            }
            try
            {
                long bal = svc.deposit(num, Long.parseLong(s));
                System.out.println("\nУспешное пополнение\n" + "Баланс: " + bal + " RUB");
            } catch (Exception e)
            {
                System.out.println("Ошибка: " + e.getMessage());
            }
        });

        actions.put(3, () ->
        {
            System.out.print("Номер счёта: ");
            String num = in.nextLine().trim();
            System.out.print("Сумма: ");
            String s = in.nextLine().trim();
            if (!s.matches("\\d+"))
            {
                System.out.println("Сумма должна быть числом");
                return;
            }
            try
            {
                long bal = svc.withdraw(num, Long.parseLong(s));
                System.out.println("\nУспешное списание\n" +"Баланс: " + bal + " RUB");
            } catch (Exception e)
            {
                System.out.println("Ошибка: " + e.getMessage());
            }
        });

        actions.put(4, () ->
        {
            System.out.print("Номер счёта: ");
            String num = in.nextLine().trim();
            try
            {
                System.out.println("Баланс: " + svc.getBalance(num) + " RUB");
            } catch (Exception e)
            {
                System.out.println("Ошибка: " + e.getMessage());
            }
        });

        actions.put(5, () ->
        {
            System.out.print("Номер счёта: ");
            String num = in.nextLine().trim();
            try
            {
                List<Transaction> list = svc.listTransactions(num);
                list.forEach(t -> System.out.println(t + "\n"));
            }
            catch (Exception e)
            {
                System.out.println("Нет данных: " + e.getMessage());
            }
        });

        actions.put(6, () ->
        {
            System.out.print("Запрос (Номер счета/БИК/КПП/ФИО): ");
            String q = in.nextLine();
            try
            {
                List<Account> res = svc.search(q);
                res.forEach(a -> System.out.println(a + "\n"));
            }
            catch (Exception e)
            {
                System.out.println("Счёт не найден");
            }
        });

        actions.put(7, () ->
        {
            svc.saveData(); // финальное сохранение на выходе
            System.out.println("Выход");
            System.exit(0);
        });

        // читаем ввод и исполняем действие
        showMenu();
        while (true)
        {
            System.out.print("\nВведите пункт меню (0 - показать меню): ");
            String raw = in.nextLine().trim();

            if (raw.equals("0"))
            { // просто вывести меню отдельной командой
                showMenu();
                continue;
            }

            try
            {
                int selection = Integer.parseInt(raw); // если не число, то вылезет catch
                Runnable action = actions.get(selection); // достаём действие по номеру
                if (action != null)
                {
                    action.run();
                }
                else
                {
                    System.out.println("Такого пункта нет");
                }
            } catch (NumberFormatException e)
            {
                System.out.println("Введите число от 0 до 7");
            }
        }
    }
    // отдельная функция для показа меню, чтобы не дублировать код
    private static void showMenu()

    {
        System.out.println("1. Открыть счёт");
        System.out.println("2. Положить деньги");
        System.out.println("3. Снять деньги");
        System.out.println("4. Показать баланс");
        System.out.println("5. Показать транзакции");
        System.out.println("6. Поиск по реквизитам");
        System.out.println("7. Выход");
    }
}