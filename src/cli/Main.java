package cli;

import domain.Account;
import domain.Transaction;
import service.BankService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

//тестировочное консольное меню
public class Main {
    private static final Scanner in = new Scanner(System.in);

    private static void menu()
    {
        System.out.println("1. Открыть счёт");
        System.out.println("2. Положить деньги");
        System.out.println("3. Снять деньги");
        System.out.println("4. Показать баланс");
        System.out.println("5. Показать транзакции");
        System.out.println("6. Поиск по реквизитам");
        System.out.println("7. Выход");
    }

    public static void main(String[] args)
    {
        BankService svc = new BankService();
        svc.loadData();
        menu();

        while (true)
        {
            System.out.print("\nВведите пункт меню (0 - показать меню): ");
            String choice = in.nextLine().trim();
            switch (choice)
            {
                case "0": menu(); break;

                case "1":
                    System.out.print("ФИО владельца: ");
                    String owner = in.nextLine().trim();
                    try
                    {
                        Account a = svc.createAccount(owner);
                        System.out.println("\nСчёт создан\n" + a);
                    } catch (Exception e) { System.out.println("Ошибка: " + e.getMessage()); }
                    break;

                case "2":
                case "3":
                    System.out.println("Эта функция ");
                    break;

                case "4":
                    System.out.print("Введите номер счёта: ");
                    try { System.out.println("Баланс: " + svc.getBalance(in.nextLine().trim()) + " RUB"); }
                    catch (Exception e) { System.out.println("Ошибка: " + e.getMessage()); }
                    break;

                case "5":
                case "6":
                    System.out.println("Эта функция поя");
                    break;

                case "7":
                    svc.saveData();
                    System.out.println("Выход");
                    return;

                default: System.out.println("Неверная цифра");
            }
        }
    }
}
