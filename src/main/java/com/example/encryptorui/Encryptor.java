package com.example.encryptorui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

// в классе возникает множество RuntimeException
// эти исключения перехватываются в интерфейсе
public class Encryptor {
    // символы, которые шифруем
    private final static String alphabet = " !\",-.:?АБВГДЕЖЗИКЛМНОПРСТУФХЦЧШЩЫЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюяё";

    public static void encrypt(String filePath, int key, String fileSuffix)
    {
        // получаем таблицу соответсвия символов
        HashMap<Character, Character> translator = createTranslator(key);
        // читаем текст из файла
        String text = readTextFromFile(filePath);
        // кодируем текст с использованием карты
        String encryptedText = encryptString(text, translator);
        // записываем результат в файл
        writeToFile(encryptedText, filePath, fileSuffix);
    }

    // расшифровка текста = шифрование с отрицательным ключем
    public static void decrypt(String filePath, int key, String fileSuffix) {
        encrypt(filePath, -key, fileSuffix);
    }

    public static void decryptBruteForce(String filePath, String fileSuffix) {
        // читаем текст из файла
        String text = readTextFromFile(filePath);
        // брутфорсим текст
        String decryptedText = bruteForceString(text);
        // записываем результат в файл
        writeToFile(decryptedText, filePath, fileSuffix);
    }

    public static void decryptStatistic(String filePath, String sampleFilePath,  String fileSuffix) {
        // читаем текст из файла
        String sampleText = readTextFromFile(sampleFilePath);
        // читаем текст из файла
        String text = readTextFromFile(filePath);
        // собираем статистику по образцу
        ArrayList<Map.Entry<Character, Integer>> sampleStatistic = countSymbols(sampleText);
        // собираем статистику по зашифрованному тексту
        ArrayList<Map.Entry<Character, Integer>> textStatistic = countSymbols(text);
        // получаем таблицу соответсвия символов
        HashMap<Character, Character> translator = createTranslator(textStatistic, sampleStatistic);
        // кодируем текст с использованием карты
        String encryptedText = encryptString(text, translator);
        // записываем результат в файл
        writeToFile(encryptedText, filePath, fileSuffix);
    }

    // шифрую строку посимвольно
    private static String encryptString(String text, HashMap<Character, Character> translator) {
        StringBuilder sb = new StringBuilder();

        // для каждого символа достаю его замену из HashMap
        for(char c:text.toCharArray()) {
            var newCharacter = translator.get(c);
            sb.append(newCharacter == null ? c : newCharacter);
        }

        return sb.toString();
    }

    private static String bruteForceString(String text) {
        for (int i = 0; i < alphabet.length(); i++) {
            HashMap<Character, Character> translator = createTranslator(i);
            String decryptedText = encryptString(text, translator);
            // проверяем, что в расшифрованном тексте есть знаки препинания и после знака припинания никогда не встречается криллическая буква
            if(!Pattern.compile("(.*)[;.,!?:][А-Яа-я](.*)").matcher(decryptedText).find() &&
                    Pattern.compile("[;.,!?:]").matcher(decryptedText).find()) {
                return decryptedText;
            }
        }
        // если после перебора всех ключей не нашлось правильного текста, бросаем исключение
        throw new RuntimeException("Не удалось расшифровать текст перебором");
    }

    // собираю статистику по кол-ву использований символов
    private static ArrayList<Map.Entry<Character, Integer>> countSymbols(String text) {
        HashMap<Character, Integer> symbolsCountMap = new HashMap<>();
        // иду по всем символам нашего алфавита
        for (char c:alphabet.toCharArray()) {
            int count = 0;
            // иду по всей строке
            // видел красивый вариант с stream, но это мы еще не проходили
            // count = text.chars().filter(ch -> ch == c).count();
            for (int i = 0; i < text.length(); i++) {
                if(text.charAt(i) == c) {
                    count++;
                }
            }
            // символ и кол-во вхождений символа в тексте
            symbolsCountMap.put(c, count);
        }

        // делаю из HashMap ArrayList, чтобы его можно было сортировать
        ArrayList<Map.Entry<Character, Integer>> symbolsCountList = new ArrayList<>(symbolsCountMap.entrySet());
        // сортирую по значению
        symbolsCountList.sort(Map.Entry.comparingByValue());

        return symbolsCountList;
    }

    // читаем текст из файла в строку, не работает для больших файлов
    // в любой непонятно ситуации бросаем RuntimeException
    private static String readTextFromFile(String filePath) {
        Path path = Path.of(filePath);
        String result;
        if(Files.notExists(path) || Files.isDirectory(path)) {
            throw new RuntimeException("Файл не найден");
        }

        try {
            result = Files.readString(path);
        }
        catch(OutOfMemoryError e) {
            throw new RuntimeException("Файл слишком большой");
        }
        catch(SecurityException e) {
            throw new RuntimeException("Файл недоступен по причине безопасность");
        }
        catch(IOException e) {
            throw new RuntimeException("Некорректное содержимое файла");
        }

        if(result.length() == 0) {
            throw new RuntimeException("Файл пуст");
        }

        return result;
    }

    // создаю таблицу соответствия символов для шифрования/расшивровки на основе ключа
    private static HashMap<Character, Character> createTranslator(int key) {
        HashMap<Character, Character> translator = new HashMap<>();
        char[] alphabetArray = alphabet.toCharArray();
        key %= alphabetArray.length;

        // иду по списку символов для шифрования
        for (int i = 0; i < alphabetArray.length; i++) {

            // номер символа, который заменит текущий символ
            int j = i + key;

            // если вышли за границы массива - идем на второй круг
            if(j < 0) {
                j += alphabetArray.length;
            } else if(j >= alphabetArray.length) {
                j %= alphabetArray.length;
            }

            translator.put(alphabetArray[i], alphabetArray[j]);
        }

        return translator;
    }

    // создаю таблицу соответствия символов для шифрования/расшивровки на основе статистики использования символов
    private static HashMap<Character, Character> createTranslator(ArrayList<Map.Entry<Character, Integer>> textStatistic, ArrayList<Map.Entry<Character, Integer>> sampleStatistic) {
        if(textStatistic.size() != sampleStatistic.size()) {
            // такого случиться не должно, так как в обоих списках есть все символы из alphabet
            throw new RuntimeException("Расшифровка невозможно - в зашифрованном тексте и в образце используются разные алфавиты");
        }
        HashMap<Character, Character> translator = new HashMap<>();

        // иду по двум спискам. так как они отсортированы по кол-во вхождений символов, в таблицу соответствия пишу символы, находящиеся на одинаковой позиции в списке
        for (int i = 0; i < textStatistic.size(); i++) {
            translator.put(textStatistic.get(i).getKey(), sampleStatistic.get(i).getKey());
        }

        return translator;
    }

    // запись результата в файл
    private static void writeToFile(String text, String filePath, String fileSuffix) {
        Path path = constructPath(filePath, fileSuffix);

        try {
            Files.write(path, text.getBytes());
        }
        catch(IOException e) {
            throw new RuntimeException("Произошла ошибка при записи в файл");
        }


    }

    // собираю путь для файла, в который буду писать результат
    private static Path constructPath(String filePath, String fileSuffix) {
        Path path = Path.of(filePath);
        String fileName = path.getFileName().toString();
        String fileExtension = fileName.substring(fileName.indexOf("."));
        String fileNameWithoutExtension = fileName.substring(0, fileName.indexOf("."));

        return Path.of(path.getParent().toString(), fileNameWithoutExtension + fileSuffix + fileExtension);
    }
}
