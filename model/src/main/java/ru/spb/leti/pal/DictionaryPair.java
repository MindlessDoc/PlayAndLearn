package ru.spb.leti.pal;

import java.util.Scanner;

import lombok.Getter;

public class DictionaryPair {
    @Getter
    private String first;
    @Getter
    private String second;

    private DictionaryPair(String str1, String str2) {
        first = str1;
        second = str2;
    }

    public static DictionaryPair read1Pair(Scanner scanner) {
        String line = scanner.nextLine();
        String[] strings = line.split("\t");
        if (strings.length == 2)
            return new DictionaryPair(strings[0].replace("\uFEFF", ""), strings[1]);
        else
            return null;
    }

    public static DictionaryPair readPair(Scanner scanner) {
        String line = scanner.nextLine();
        String[] strings = line.split("\t");
        if (strings.length == 2)
            return new DictionaryPair(strings[0], strings[1]);
        else
            return null;
    }

    @Override
    public String toString() {
        return first + "\t" + second;
    }
}

