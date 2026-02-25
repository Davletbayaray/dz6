import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


interface IPaymentStrategy {
    void pay(double amount);
}

class CardPayment implements IPaymentStrategy {
    public void pay(double amount) {
        System.out.println("[Карта] " + amount + " тг көлемінде төлем қабылданды.");
    }
}

class PayPalPayment implements IPaymentStrategy {
    public void pay(double amount) {
        System.out.println("[PayPal] " + amount + " тг көлемінде төлем PayPal арқылы расталды.");
    }
}

class CryptoPayment implements IPaymentStrategy {
    public void pay(double amount) {
        System.out.println("[Crypto] " + amount + " тг көлемінде крипто-транзакция жасалды.");
    }
}


class PaymentContext {
    private IPaymentStrategy strategy;

    public void setStrategy(IPaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void executePayment(double amount) {
        if (strategy == null) {
            System.out.println("Қате: Төлем әдісі таңдалмаған!");
        } else {
            strategy.pay(amount);
        }
    }
}



interface IObserver {
    void update(String currency, double rate);
}

interface ISubject {
    void attach(IObserver observer);
    void detach(IObserver observer);
    void notifyObservers();
}


class CurrencyExchange implements ISubject {
    private List<IObserver> observers = new ArrayList<>();
    private double usdRate;

    public void setUsdRate(double rate) {
        this.usdRate = rate;
        notifyObservers(); // Бағам өзгергенде хабарлау
    }

    public void attach(IObserver observer) {
        observers.add(observer);
    }

    public void detach(IObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (IObserver observer : observers) {
            observer.update("USD", usdRate);
        }
    }
}

class MobileAppObserver implements IObserver {
    public void update(String cur, double rate) {
        System.out.println("[Хабарлама] Мобильді қосымша: " + cur + " бағамы өзгерді: " + rate + " тг.");
    }
}

class BankSystemObserver implements IObserver {
    public void update(String cur, double rate) {
        System.out.println("[Банк Жүйесі] Орталық банк жаңа бағамды бекітті: " + rate);
    }
}

class BrokerObserver implements IObserver {
    public void update(String cur, double rate) {
        System.out.println(rate > 500 ? "[Брокер] Бағам тым жоғары! Сату керек." : "[Брокер] Сатып алуға қолайлы уақыт.");
    }
}


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.println("----- 1. STRATEGY PATTERN (Төлем) -----");
        PaymentContext paymentProcessor = new PaymentContext();

        System.out.println("Төлем әдісін таңдаңыз: 1-Карта, 2-PayPal, 3-Крипто");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1": paymentProcessor.setStrategy(new CardPayment()); break;
            case "2": paymentProcessor.setStrategy(new PayPalPayment()); break;
            case "3": paymentProcessor.setStrategy(new CryptoPayment()); break;
            default: System.out.println("Қате таңдау!"); break;
        }
        paymentProcessor.executePayment(50000.0);


        System.out.println("\n----- 2. OBSERVER PATTERN (Мониторинг) -----");
        CurrencyExchange exchange = new CurrencyExchange();

        IObserver phone = new MobileAppObserver();
        IObserver bank = new BankSystemObserver();
        IObserver broker = new BrokerObserver();

        exchange.attach(phone);
        exchange.attach(bank);
        exchange.attach(broker);

        System.out.print("Жаңа USD бағамын енгізіңіз (мысалы 485.0): ");
        if (scanner.hasNextDouble()) {
            double newRate = scanner.nextDouble();
            exchange.setUsdRate(newRate);
        }

        System.out.println("\nБрокерді жазылымнан шығару және бағамды 510.0-ге өзгерту...");
        exchange.detach(broker);
        exchange.setUsdRate(510.0);

        scanner.close();
    }
}