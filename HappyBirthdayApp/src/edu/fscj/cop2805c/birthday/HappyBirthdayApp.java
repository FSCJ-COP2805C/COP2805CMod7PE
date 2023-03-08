// HappyBirthdayApp.java
// D. Singletary
// 1/29/23
// wish multiple users a happy birthday

// D. Singletary
//// 2/26/23
//// Added Stream and localization code

// D. Singletary
// 3/7/23
// Changed to thread-safe queue
// Moved buildCard to BirthdayCard class
// Instantiating the BirthdayCardProcessor object
// added test data for multi-threading tests

package edu.fscj.cop2805c.birthday;

import edu.fscj.cop2805c.dispatch.Dispatcher;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;


// utility class with constant list of desired timezones (US/)
class DesiredTimeZones {
    public static final ArrayList<String> ZONE_LIST = new ArrayList<>();

    public DesiredTimeZones() {
        String[] availableTimezones = TimeZone.getAvailableIDs();
        for (String s : availableTimezones) {
            if (s.length() >= 3 && s.substring(0, 3).equals("US/")) {
                ZONE_LIST.add(s);
            }
        }
    }

    // show the list of zones as a numeric menu
    public void showMenu() {
        int menuCount = 1;
        for (String s : ZONE_LIST)
            System.out.println(menuCount++ + ". " + s);
    }
}

// main mpplication class
public class HappyBirthdayApp implements BirthdayGreeter, Dispatcher<BirthdayCard>  {
    private ArrayList<User> birthdays = new ArrayList<>();
    // Use a thread-safe Queue<LinkedList> to act as message queue for the dispatcher
    ConcurrentLinkedQueue safeQueue = new ConcurrentLinkedQueue(
           new LinkedList<BirthdayCard>()
    );

    private Stream<BirthdayCard> stream = safeQueue.stream();

    public HappyBirthdayApp() { }

    // dispatch the card using the dispatcher
    public void dispatch(BirthdayCard bc) {
        this.safeQueue.add(bc);
    }

    // send the card
    public void sendCard(BirthdayCard bc) {
        // dispatch the card
        // dispatch(bc);
        // show an alternative dispatch using a lambda
        Dispatcher<BirthdayCard> d = (c)-> {
            //this.queue.add(c);
            this.safeQueue.add(c);
        };
        d.dispatch(bc);
    }

    // show prompt msg with no newline
    public static void prompt(String msg) {
        System.out.print(msg + ": ");
    }

    public void generateCards() {

        for (User u : birthdays) {
            System.out.println(u.getName());
            // see if today is their birthday
            // if not, show sorry message
            if (!BirthdayCard.isBirthday(u))
                System.out.println("Sorry, today is not their birthday.");
                // otherwise build the card
            else {
                String msg = "";
                try {
                    // load the property and create the localized greeting
                    ResourceBundle res = ResourceBundle.getBundle(
                            "edu.fscj.cop2805c.birthday.Birthday", u.getLocale());
                    String happyBirthday = res.getString("HappyBirthday");

                    // format and display the date
                    DateTimeFormatter formatter =
                            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
                    formatter =
                            formatter.localizedBy(u.getLocale());
                    msg = u.getBirthday().format(formatter) + "\n";

                    // add the localized greeting
                    msg += happyBirthday + " " + u.getName() + "\n" +
                            BirthdayCard.WISHES;
                } catch (java.util.MissingResourceException e) {
                    System.err.println(e);
                    msg = "Happy Birthday, " + u.getName() + "\n" +
                            BirthdayCard.WISHES;
                }
                BirthdayCard bc = new BirthdayCard(u, msg);
                sendCard(bc);
            }
        }
        birthdays.clear(); // clear the list
    }

    // add multiple birthdays
    public void addBirthdays(User... users) {
        for (User u : users) {
            birthdays.add(u);
        }
    }

    // main program
    public static void main(String[] args) {

        // create a list of desired timezones to use for our app,
        // we'll use the US/ zones
        DesiredTimeZones dzt = new DesiredTimeZones();

        HappyBirthdayApp hba = new HappyBirthdayApp();

        // start the processor thread
        BirthdayCardProcessor processor = new BirthdayCardProcessor(hba.safeQueue);

        // test the varargs method by creating multiple birthdays and adding them.
        // names were generated using the random name generator at
        //     http://random-name-generator.info/
        // be sure to test the positive case (today is someone's birthday
        // as well as negative)

        // use current date for testing, adjust where necessary
        ZonedDateTime currentDate = ZonedDateTime.now();

        // negative test
        User u1 = new User("Dianne", "Romero", "Dianne.Romero@email.test",
                new Locale("en"), currentDate.minusDays(1));

        // positive tests
        // test with odd length full name and english locale
        User u2 = new User("Sally", "Ride", "Sally.Ride@email.test",
                new Locale("en"), currentDate);

        // test french locale
        User u3 = new User("René", "Descartes", "René.Descartes@email.test",
                new Locale("fr"), currentDate);

        // test with even length full name and german locale
        User u4 = new User("Johannes", "Brahms", "Johannes.Brahms@email.test",
                new Locale("de"), currentDate);

        // test chinese locale
        User u5 = new User("Charles", "Kao", "Charles.Kao@email.test",
                new Locale("zh"), currentDate);

        hba.addBirthdays(u1, u2, u3, u4, u5);
        hba.generateCards();

        // wait for a bit
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            System.out.println("sleep interrupted! " + ie);
        }

        hba.addBirthdays(u1, u2, u3, u4, u5);
        hba.generateCards();

        // wait for a bit
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            System.out.println("sleep interrupted! " + ie);
        }

        processor.endProcessing();
    }
}