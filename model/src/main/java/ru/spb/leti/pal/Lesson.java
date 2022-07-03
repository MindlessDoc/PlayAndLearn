package ru.spb.leti.pal;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JOptionPane;
import lombok.Getter;
import lombok.Setter;

public class Lesson {
    @Getter @Setter
    ArrayList<DictionaryPair> dictionary;
    private String LessonName;

    /**
     * Инициализация урока считыванием пар из файла
     *
     * @param file - файл
     * @return - удачная инициализация - true
     */
    public boolean init(File file) {
        try (Scanner scanner = new Scanner(file, "UTF-8")) {
            dictionary = new ArrayList<>();
            DictionaryPair pair;
            if (scanner.hasNext()) {
                pair = DictionaryPair.read1Pair(scanner);
                if (pair != null)
                {
                    if (isFormatCorrect(pair.getFirst()) && isFormatCorrect(pair.getSecond()))
                        dictionary.add(pair);
                    else {
                        // Сообщение об ошибке в строке словаря ("\e")
                        JOptionPane.showMessageDialog(null, "Неизвестная управляющая конструкция в строке №" + (dictionary.size() + 1), "Ошибка чтения словаря", JOptionPane.ERROR_MESSAGE, null);
                        return false;
                    }
                }
                else
                {
                    // Сообщение об ошибке в словаре
                    JOptionPane.showMessageDialog(null, "Некорректная строка №" + (dictionary.size() + 1), "Ошибка чтения словаря", JOptionPane.ERROR_MESSAGE, null);
                    System.out.println("Ошибка в словаре. Строка №" + (dictionary.size() + 1) + "\n");
                    dictionary.clear();
                    return false;
                }
            }
            while (scanner.hasNext()) {
                pair = DictionaryPair.readPair(scanner);
                if (pair != null)
                {
                    if (isFormatCorrect(pair.getFirst()) && isFormatCorrect(pair.getSecond()))
                        dictionary.add(pair);
                    else {
                        // Сообщение об ошибке в строке словаря ("\e")
                        JOptionPane.showMessageDialog(null, "Неизвестная управляющая конструкция в строке №" + (dictionary.size() + 1), "Ошибка чтения словаря", JOptionPane.ERROR_MESSAGE, null);
                        return false;
                    }
                }
                else
                {
                    // Сообщение об ошибке в словаре
                    JOptionPane.showMessageDialog(null, "Некорректная строка №" + (dictionary.size() + 1), "Ошибка чтения словаря", JOptionPane.ERROR_MESSAGE, null);
                    System.out.println("Ошибка в словаре. Строка №" + (dictionary.size() + 1) + "\n");
                    dictionary.clear();
                    return false;
                }
            }
        } catch (FileNotFoundException e) {
            // Сообщение о ненайденном файле
            JOptionPane.showMessageDialog(null, "Не удалось открыть файл словаря", "Ошибка открытия словаря", JOptionPane.ERROR_MESSAGE, null);
            return false;
        }
        if (dictionary.isEmpty())
        {
            // Сообщение о ненайденном файле
            JOptionPane.showMessageDialog(null, "Пустой словарь", "Ошибка словаря словаря", JOptionPane.ERROR_MESSAGE, null);
            return false;
        }
        setLessonName(file.getName());
        return true;
    }

    public void setLessonName(String lessonName) {
        LessonName = lessonName;
    }

    public String getLessonName() {
        return LessonName;
    }

    /**
     * Инициализация урока из списка
     *
     * @param dictionary - список пар
     * @param lessonName - название урока
     */
    public void LessonFromList(LinkedList<DictionaryPair> dictionary, String lessonName) {
        this.dictionary = LinkedToArrayList(dictionary);
        setLessonName(lessonName);
    }

    private boolean isFormatCorrect(String inputString){
        inputString += ' ';
        char[] input = inputString.toCharArray();
        char lastPushedSymb =' ';
        for (int i=0; i < inputString.length(); i++) {
            if (lastPushedSymb == '\\'){
                switch (input[i]){
                    case '1': case '2': case '3': case '4': case '5': case '6':
                    case 'р': case 'о': case 'з': case 'г': case 'с': case 'ф': case 'ч': case 'е': case 'к':
                    case '\\':
                        lastPushedSymb = ' ';
                        continue;
                    default:
                        return false;
                }

            }
            if (input[i] == '\\'){
                lastPushedSymb = input[i];
            }
        }
        return true;
    }

    /**
     * Создание ArrayList из LinkedList
     *
     * @param Llist - LinkedList
     * @return - ArrayList
     */
    private ArrayList<DictionaryPair> LinkedToArrayList(LinkedList<DictionaryPair> Llist) {
        return new ArrayList<>(Llist);
    }

    /**
     * Перемешивание пар урока
     */
    public void mixDictionary() {
        //Dictionary.sort((o1, o2) -> (new Random()).nextInt() % 3 - 1);
        Collections.shuffle(dictionary);
    }

}