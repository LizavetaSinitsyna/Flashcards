package org.hyperskills.flashcards.main;

import java.io.*;
import java.util.*;
import java.util.Random;


public class Main {

    static Scanner scan = null;
    static String cardName = null;
    static Map<String, String> cards = new LinkedHashMap<>();
    static Map<String, Integer> cardsMistakes = new LinkedHashMap<>();
    static String cardDefinition = null;
    static boolean on = true;
    static String action = null;
    static int counter = 0;
    static ArrayList<String> log = new ArrayList<>();
    static File imp = null;
    static File exp = null;

    public static void main(String[] args) {
        if (args.length == 2) {
            if ("-import".equals(args[0])) {
                imp = new File(args[1]);
                cardsImport(imp);
            } else {
                exp = new File(args[1]);
            }
        } else if (args.length == 4) {
            if ("-import".equals(args[0])) {
                imp = new File(args[1]);
                cardsImport(imp);
            } else {
                exp = new File(args[1]);
            }
            if ("-import".equals(args[2])) {
                imp = new File(args[3]);
                cardsImport(imp);
            } else {
                exp = new File(args[3]);
            }
        }

        while (isOn()) {
            printing("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            scan = new Scanner(System.in);
            action = scanning();
            switch (action) {
                case "add":
                    addCard();
                    break;
                case "remove":
                    removeCard();
                    break;
                case "import":
                    printing("File name:");
                    File fileImp = new File(scanning());
                    cardsImport(fileImp);
                    break;
                case "export":
                    printing("File name:");
                    File fileExp = new File(scanning());
                    cardsExport(fileExp);
                    break;
                case "ask":
                    cardsAsk();
                    break;
                case "exit":
                    on = false;
                    printing("Bye bye!");
                    if (!(exp == null)) {
                        cardsExport(exp);
                    }
                    break;
                case "log":
                    log();
                    break;
                case "hardest card":
                    hardestCard();
                    break;
                case "reset stats":
                    resetStat();
                    break;
                default:
                    break;
            }
        }

    }

    public static void printing(String lineToPrint) {
        System.out.println(lineToPrint);
        log.add(lineToPrint);
    }

    public static String scanning() {
        String scannedLine = scan.nextLine();
        log.add(scannedLine);
        return scannedLine;
    }

    public static void addCard() {
        printing("The card:");
        cardName = scanning();
        if (cards.containsKey(cardName)) {
            printing("The card \"" + cardName + "\" already exists.");
        } else {
            printing("The definition of the card:");
            cardDefinition = scanning();
            if (cards.containsValue(cardDefinition)) {
                printing("The definition \"" + cardDefinition + "\" already exists.");
            } else {
                cards.put(cardName, cardDefinition);
                cardsMistakes.put(cardName, 0);
                printing("The pair (\"" + cardName + "\":\"" + cardDefinition + "\") has been added.");
            }
        }
    }

    public static void removeCard() {
        printing("The card:");
        cardName = scanning();
        if (cards.containsKey(cardName)) {
            cards.remove(cardName);
            cardsMistakes.remove(cardName);
            printing("The card has been removed.");
        } else {
            printing("Can't remove \"" + cardName + "\": there is no such card.");
        }
    }

    public static void cardsExport(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            for (var entry : cards.entrySet()) {
                writer.write(entry.getKey() + " = " + entry.getValue() + " = " + cardsMistakes.get(entry.getKey()) + "\r\n");
                /*for (var mistake : cardsMistakes.entrySet()) {
                    writer.write(mistake.getValue() + "\r\n");
                }*/
                counter++;
            }
            printing(counter + " cards have been saved.");
            counter = 0;
        } catch (IOException e) {
            printing("An exception occurs: " + e.getMessage());
        }
    }

    public static void cardsImport(File file) {
        String[] lineImport;
        try {
            scan = new Scanner(file);
            while (scan.hasNext()) {
                lineImport = scanning().split(" = ");
                cards.put(lineImport[0], lineImport[1]);
                cardsMistakes.put(lineImport[0], Integer.parseInt(lineImport[2]));
                counter++;
            }
            printing(counter + " cards have been loaded.");
            counter = 0;
        } catch (FileNotFoundException e) {
            printing("File not found.");
        }
    }

    public static void cardsAsk() {
        printing("How many times to ask?");
        int times = Integer.parseInt(scanning());
        Random random = new Random();
        String answer;
        String randomKey;
        String key = null;
        Set<String> cardsKeySet = new HashSet<>(cards.keySet());
        try {
            for (int i = 0; i < times; i++) {
                String[] cardsArray = new String[cardsKeySet.size()];
                cardsKeySet.toArray(cardsArray);
                randomKey = cardsArray[random.nextInt(cardsArray.length)];
                printing("Print the definition of \"" + randomKey + "\":");
                answer = scanning();
                if (cards.get(randomKey).equals(answer)) {
                    printing("Correct answer.");
                } else if (cards.containsValue(answer)) {
                    for (var entry : cards.entrySet()) {
                        if (entry.getValue().equals(answer)) {
                            key = entry.getKey();
                        }
                    }
                    printing("Wrong answer. The correct one is \"" + cards.get(randomKey) + "\", , you've just written the definition of \"" + key + "\".");
                    cardsMistakes.replace(randomKey, cardsMistakes.get(randomKey) + 1);
                } else {
                    printing("Wrong answer. The correct one is \"" + cards.get(randomKey) + "\".");
                    cardsMistakes.replace(randomKey, cardsMistakes.get(randomKey) + 1);
                }
                //cardsKeySet.remove(cardTemp);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            printing("There is no added cards. Please add at least one card.");
        }
    }

    public static void log() {
        printing("File name:");
        File file = new File(scanning());
        try (FileWriter writer = new FileWriter(file)) {
            for (String ioConsole : log) {
                writer.write(ioConsole + "\r\n");
            }
            printing("The log has been saved.");
        } catch (IOException e) {
            printing("An exception occurs: " + e.getMessage());
        }
    }

    public static void hardestCard() {
        ArrayList<String> keysWithMaxOfMistakes = new ArrayList<>();
        int maxNumberOfMistakes = 0;
        for (var mistakes : cardsMistakes.entrySet()) {
            if (mistakes.getValue() > maxNumberOfMistakes) {
                maxNumberOfMistakes = mistakes.getValue();
                keysWithMaxOfMistakes.clear();
                keysWithMaxOfMistakes.add(mistakes.getKey());
            } else if (mistakes.getValue() == maxNumberOfMistakes) {
                keysWithMaxOfMistakes.add(mistakes.getKey());
            }
        }
        if (maxNumberOfMistakes == 0) {
            printing("There are no cards with errors.");
        } else {
            if (keysWithMaxOfMistakes.size() == 1) {
                printing("The hardest cards is \"" + keysWithMaxOfMistakes.get(0) + "\". You have " + maxNumberOfMistakes + " errors answering it.");
            } else {
                String joinedCards = String.join("\", \"", keysWithMaxOfMistakes);
                printing("The hardest cards are \"" + joinedCards + "\". You have " + maxNumberOfMistakes + " errors answering them.");
            }

        }
    }

    public static void resetStat() {
        for (var entry : cardsMistakes.entrySet()) {
            cardsMistakes.replace(entry.getKey(), 0);
        }
        printing("Card statistics has been reset.");
    }

    public static boolean isOn() {
        return on;
    }
}
