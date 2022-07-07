package ru.spb.leti.GameWindow;

import java.awt.Color;
import java.awt.Font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;

import javax.swing.SwingConstants;
import lombok.Setter;

public class StrTransform {
    private static int height;
    @Setter
    private static String fontName = "";

    private static List<Word> formattedWords = new ArrayList<>();
    private static Word bufferWord;
    private static Color defColor = Color.BLACK;
    private static Color curColor = defColor;

    public static void transform(Square button, String input, int squareHeight) {
        System.setProperty("file.encoding", "UTF-8");
        //TODO Почему умножается на 0.8..................
        height = (int) (squareHeight * 0.8);
        toFormattedString(button, input);
        createButtons(button);
        formattedWords.clear();
    }

    private static void formatSwitch(char c, Word word, StringBuffer stringBuffer) {
        int sizeMode = c - '0';
        if (sizeMode >= 1 && sizeMode <= 6) {
            bufferWord.sizeMode = sizeMode;
        } else {
            if (c == '\\') {
                addToViewText(word, stringBuffer.toString() + "<br>");
                word.text += stringBuffer + "\n";
                stringBuffer.delete(0, stringBuffer.toString().length());
            } else {
                curColor = Word.colors.get(c);
                if (curColor == null) {
                    curColor = defColor;
                    System.out.println("Неверный ключ в слове");
                }
                addToViewText(word, stringBuffer.toString());
                word.text += stringBuffer.toString();
                stringBuffer.delete(0, stringBuffer.toString().length());
            }
        }
    }

    private static void addToViewText(Word word, String str) {
        word.viewText += "<span style=" + "color:rgb(" + curColor.getRed() + "," + curColor.getGreen() + ","
                + curColor.getBlue() + ")>" + str + "</span>";
    }

    public static String toFormattedString(Square button, String inputString) {
        curColor = defColor;
        bufferWord = new Word();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < inputString.length(); i++) {
            if (inputString.charAt(i) == '\\' && i < inputString.length() - 1) {
                i++;
                formatSwitch(inputString.charAt(i), bufferWord, stringBuffer);
            } else {
                stringBuffer.append(inputString.charAt(i));
            }
        }
        addToViewText(bufferWord, stringBuffer.toString());

        bufferWord.viewText += "</html>";
        button.getCell().setDisplayedWord(bufferWord.text + stringBuffer);
        formattedWords.add(bufferWord);

        return bufferWord.text;
    }

    private static void createButtons(Square button) {
        button.removeAll();
        JLabel label = new JLabel(bufferWord.viewText);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font(fontName, Font.PLAIN, (int) ((double) bufferWord.sizeMode / 6 * height)));
        button.add(label);
    }

    public static class Word {
        private int sizeMode = 6;
        private String text = "";
        private String viewText = "<html>";

        private static Map<Character, Color> colors = new HashMap<>();

        static {
            colors.put('р', Color.PINK);
            colors.put('о', Color.ORANGE);
            colors.put('з', Color.GREEN);
            colors.put('г', new Color(0, 184, 217));
            colors.put('с', Color.BLUE);
            colors.put('ф', Color.MAGENTA);
            colors.put('е', Color.LIGHT_GRAY);
            colors.put('к', Color.RED);
            colors.put('ч', Color.BLACK);
        }
    }
}