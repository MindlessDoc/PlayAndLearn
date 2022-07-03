package ru.spb.leti.GameWindow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JOptionPane;

import lombok.Getter;
import lombok.Setter;

import ru.spb.leti.pal.Cell;
import ru.spb.leti.pal.DictionaryPair;
import ru.spb.leti.pal.Lesson;
import ru.spb.leti.pal.Move;
import ru.spb.leti.pal.Position;

public class Game {
    @Getter
    private Cell[][] field;// Поле
    @Getter
    private Position fieldSize;// Размеры поля
    @Getter
    private Lesson curLesson;// Текущий урок
    @Getter
    private int numberOfSteps;// Количество этапов в уроке
    @Getter
    private LinkedList<DictionaryPair> lessonMistakes1;// Список ошибок (верные пары левых)
    @Getter
    private LinkedList<DictionaryPair> lessonMistakes2;// Список ошибок (верные пары правых)
    private int numCorrectAnsw;// Кол-во верных ответов на уроке
    private int localMistakes;// Кол-во ошибок на этапе
    private int offset;// Смещение относительно начала словаря
    private LinkedList<Integer> numElemOfMatrix;// Распределение элементов по матрицам
    @Getter @Setter
    private Move lastMove;// Последный ход
    @Setter
    private boolean mixFlag;// Флаг перемешивания (при загрузке этапа)
    private boolean oddFlagForDistribution;// Флаг "плохого" размера поля (нечетная вертикаль)

    public Game(int vertical, int horizontal) {
        setSize(vertical, horizontal);
    }

    /**
     * @return номер текущего этапа
     */
    public int getNumOfCurStep() {
        return !numElemOfMatrix.isEmpty() ? numberOfSteps - numElemOfMatrix.size() + 1 : numberOfSteps;
    }

    /**
     * @return - количество совершенных ошибок за урок
     */
    public int getNumMistakes() {
        return lessonMistakes1.size();
    }

    /**
     * Сеттер размеров игрового поля
     *
     * @param horizontal - ширина
     * @param vertical   - высота
     */
    public void setSize(int vertical, int horizontal) {
        fieldSize = new Position(vertical, horizontal);
        oddFlagForDistribution = vertical % 2 == 1;
    }

    /**
     * Установка нового урока из файла
     *
     * @param file - файл-словарь
     */
    public boolean newLesson(File file) {
        if (curLesson == null) {
            curLesson = new Lesson();
        }
        if (curLesson.init(file)) {
            prepareLesson();
            curLesson.mixDictionary();
            return true;
        } else
            return false;
    }

    /**
     * Подготовка урока к запуску
     */
    private void prepareLesson() {
        lessonMistakes1 = new LinkedList<>();
        lessonMistakes2 = new LinkedList<>();
        numCorrectAnsw = 0;
        offset = 0;
        localMistakes = 0;
        numElemOfMatrix = new LinkedList<>();
        numberOfSteps = CalculateFields();  // рассчёт матричного заполнения
    }

    /**
     * Вычисление оптимального кол-ва элементов в матрицах, для умещения всего урока на "доске"
     */
    private int CalculateFields() {
        int NumElem = curLesson.getDictionary().size();                          // всего элементов
        int MatrixElem = fieldSize.getHorizontal() * fieldSize.getVertical() / 2;         // пар в матрице заданного размера
        int NumOfMatrix = (int) Math.ceil((double) NumElem / MatrixElem);    // количество необходимых матриц
        int AverageNumberOfCells = NumElem;
        if (NumElem > MatrixElem)
            AverageNumberOfCells = NumElem / NumOfMatrix;
        int delta = NumElem - AverageNumberOfCells * NumOfMatrix;
        for (int i = 0; i < NumOfMatrix; i++) {
            int Num = AverageNumberOfCells;                                 // Рассчёт оптимального кол-ва элементов в матрице
            if (delta > 0) {
                Num++;
                delta--;
            }
            numElemOfMatrix.addLast(Num);
        }
        return NumOfMatrix;
    }

    /**
     * Контроль установки потока полей урока - устанавливет следующее, если оно существует
     *
     * @return true - если поля ещё есть (и после выполнения очередное поле выставлено), false - если полей урока больше нет
     */
    public boolean nextField() {
        if (!numElemOfMatrix.isEmpty()) {
            field = new Cell[fieldSize.getVertical()][fieldSize.getHorizontal()];
            int NumElem = numElemOfMatrix.getFirst();                       // необходимое кол-во пар в текущей матрице
            int i = 0;
            if (!oddFlagForDistribution) {
                for (int j = 0; j < field.length / 2; j++) {
                    for (int k = 0; k < field[j].length; k++) {
                        if (i < NumElem) {
                            Cell first = new Cell(curLesson.getDictionary().get(offset + i), new Position(j, k), false);
                            Cell second = new Cell(curLesson.getDictionary().get(offset + i), new Position(field.length / 2 + j, k), true); // Для второй(парной) ячейки в матрице
                            i++;
                            field[j][k] = first;
                            field[field.length / 2 + j][k] = second;
                        } else
                            break;
                    }
                }
            } else {
                int j = 0;
                while (j < fieldSize.getVertical() * fieldSize.getHorizontal()) {
                    int row1 = j / fieldSize.getHorizontal();
                    int column1 = j % fieldSize.getHorizontal();
                    j++;
                    int row2 = j / fieldSize.getHorizontal();
                    int column2 = j % fieldSize.getHorizontal();
                    j++;
                    if (i < NumElem) {
                        Cell first = new Cell(curLesson.getDictionary().get(offset + i), new Position(row1, column1), false);
                        Cell second = new Cell(curLesson.getDictionary().get(offset + i), new Position(row2, column2), true); // Для второй(парной) ячейки в матрице
                        i++;
                        field[row1][column1] = first;
                        field[row2][column2] = second;
                    } else
                        break;
                }
            }
            if (mixFlag)
                mixField();
            offset = offset + NumElem;
            return true;
        }
        return false;
    }

    /**
     * Перемешивание поля
     */
    public void mixField() {
        if (field != null) {
            for (int i = 0; i < field.length; i++)
                for (int j = 0; j < field[i].length; j++) {
                    Random rand = new Random();
                    int a = rand.nextInt(field.length);
                    int b = rand.nextInt(field[i].length);
                    while (a == i && b == j) {
                        a = rand.nextInt(field.length);
                        b = rand.nextInt(field[i].length);
                    }
                    Cell tmp = field[a][b];
                    field[a][b] = field[i][j];
                    field[i][j] = tmp;
                    if (field[a][b] != null)
                        field[a][b].setPosition(new Position(a, b));
                    if (field[i][j] != null)
                        field[i][j].setPosition(new Position(i, j));
                }
        }
    }

    /**
     * Сравнение ячеек по поданным координатам
     *
     * @param a - первая ячейка
     * @param b - вторая ячейка
     * @return - true - если ячейки совместимы
     */
    public boolean compareCell(Position a, Position b) {
        Cell first = field[a.getVertical()][a.getHorizontal()];
        Cell second = field[b.getVertical()][b.getHorizontal()];
        boolean compareFlag = isCompatible(first, second);
        setLastMove(new Move(field[a.getVertical()][a.getHorizontal()], field[b.getVertical()][b.getHorizontal()]));
        if (compareFlag) {
            field[a.getVertical()][a.getHorizontal()] = null;
            field[b.getVertical()][b.getHorizontal()] = null;
            numCorrectAnsw++;
            if (numCorrectAnsw == numElemOfMatrix.getFirst()) {
                numElemOfMatrix.removeFirst();
                localMistakes = 0;
                numCorrectAnsw = 0;
                setLastMove(null);
                return true;
            }
        } else {
            localMistakes++;
            if (first.isFlag()) {
                lessonMistakes1.addLast(second.getPair());
                lessonMistakes2.addLast(first.getPair());
            } else {
                lessonMistakes1.addLast(first.getPair());
                lessonMistakes2.addLast(second.getPair());
            }
        }
        return compareFlag;
    }

    /**
     * Проверяет совместимость ячеек
     *
     * @param first  - первая ячейка
     * @param second - вторая ячейка
     * @return - true - если совместимы
     */
    private boolean isCompatible(Cell first, Cell second) {
        ArrayList<String> left = new ArrayList<>();
        ArrayList<String> right = new ArrayList<>();
        String firstStr;
        String secondStr;
        boolean flag1 = false;
        boolean flag2 = false;
        if (first.isFlag()) {
            // Если первая ячейка отображает правое слово
            firstStr = toNormalString(first.getPair().getSecond());
            secondStr = toNormalString(second.getPair().getFirst());
            for (int i = 0; i < curLesson.getDictionary().size(); i++) {
                String str1 = toNormalString(curLesson.getDictionary().get(i).getFirst());
                String str2 = toNormalString(curLesson.getDictionary().get(i).getSecond());
                if (firstStr.equals(str2)) {
                    left.add(str1);
                }
                if (secondStr.equals(str1)) {
                    right.add(str2);
                }
            }
            for (String a : left) {
                if (a.equals(secondStr)) {
                    flag1 = true;
                    break;
                }
                flag1 = false;
            }
            for (String a : right) {
                if (a.equals(firstStr)) {
                    flag2 = true;
                    break;
                }
                flag2 = false;
            }
        } else {
            // Если первая ячейка отображает левое слово
            firstStr = toNormalString(first.getPair().getFirst());
            secondStr = toNormalString(second.getPair().getSecond());
            for (int i = 0; i < curLesson.getDictionary().size(); i++) {
                String str1 = toNormalString(curLesson.getDictionary().get(i).getFirst());
                String str2 = toNormalString(curLesson.getDictionary().get(i).getSecond());
                if (firstStr.equals(str1)) {
                    right.add(str2);
                }
                if (secondStr.equals(str2)) {
                    left.add(str1);
                }
            }
            for (String a : left) {
                if (a.equals(firstStr)) {
                    flag1 = true;
                    break;
                }
                flag1 = false;
            }
            for (String a : right) {
                if (a.equals(secondStr)) {
                    flag2 = true;
                    break;
                }
                flag2 = false;
            }
        }
        return flag1 && flag2;
    }

    /**
     * Отмена последнего хода
     * - верного - уменьшение кол-ва верных ответов
     * - неверного - удаление из таблицы ошибок (уменьшение кол-ва ошибок)
     */
    public void undo() {
        if (lastMove != null) {
            Position f = lastMove.getFirst().getPosition();
            Position s = lastMove.getSecond().getPosition();
            field[f.getVertical()][f.getHorizontal()] = lastMove.getFirst();
            field[s.getVertical()][s.getHorizontal()] = lastMove.getSecond();
            if (isCompatible(lastMove.getFirst(), lastMove.getSecond())) {
                numCorrectAnsw--;
            } else {
                lessonMistakes1.removeLast();
                lessonMistakes2.removeLast();
                localMistakes--;
            }
            setLastMove(null);
        }
    }


    /**
     * Сохранение совершенных ошибок в файл
     */
    public void SaveMistakes(String fileName) {
        if (curLesson != null) {
            StringBuilder build = new StringBuilder();
            build.append(fileName + "-");
            build.append(curLesson.getLessonName());
            build.setLength(build.length() - 4);
            try {
                MistakesToFile(lessonMistakes1, build.toString() + "(err1).txt");
                MistakesToFile(lessonMistakes2, build.toString() + "(err2).txt");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Не удалось сохранить ошибки", "Ошибка сохранения ошибок", JOptionPane.ERROR_MESSAGE, null);
            }
        }
    }

    /**
     * @param a        - сохраняемый список
     * @param fileName - имя файла
     */
    private void MistakesToFile(LinkedList<DictionaryPair> a, String fileName) throws IOException {
        if (a != null) {
            //BufferedWriter out = null;
            try (Writer out = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)) {
                for (DictionaryPair err : a)
                    out.write(err.getFirst() + "\t" + err.getSecond() + "\n");
            }
        }
    }

    /**
     * Создает урок из поданного списка
     *
     * @param list    - список для создания урока
     * @param numType - тип списка
     */
    public void MistakesToLesson(LinkedList<DictionaryPair> list, NumMistakeType numType) {
        StringBuilder build = new StringBuilder();
        build.append(curLesson.getLessonName());
        build.setLength(build.length() - 4);
        switch (numType) {
            case FIRST: {
                build.append("(err1).txt");
                break;
            }
            case SECOND: {
                build.append("(err2).txt");
                break;
            }
            case BOTH: {
                build.append("(allerr).txt");
                break;
            }
        }
        curLesson.LessonFromList(list, build.toString());
        prepareLesson();
    }

    public enum NumMistakeType {
        FIRST,
        SECOND,
        BOTH
    }

    /**
     * Сохранение текущего прогресса, для дальнейшего продолжение
     * // принимает fileName поданное пользователем?
     */
    public boolean SaveProgress(String fileName, int TimeSec) {
        String saveName = fileName + ".savepr";
        try (Writer save = new OutputStreamWriter(new FileOutputStream(saveName), StandardCharsets.UTF_8)) {
            save.write(curLesson.getLessonName() + "\n");                   // Название урока
            save.write(curLesson.getDictionary().size() + " " + offset + "\n");      // Кол-во слов словаря, смещение в словаре
            for (int i = 0; i < curLesson.getDictionary().size(); i++)          // Пары словарь
                save.write(curLesson.getDictionary().get(i).toString() + "\n");
            save.write(numElemOfMatrix.size() + " ");               // Количество матриц
            for (int a : numElemOfMatrix)                               // Матричное распределение
                save.write(a + " ");
            save.write("\n");
            save.write(fieldSize.getVertical() + " " + fieldSize.getHorizontal()); // Размер поля
            save.write("\n");
            for (Cell[] arr : field) // Запись поля
            {
                for (Cell a : arr) {
                    if (a != null) { // Индекс пары в словаре + флаг
                        if (a.isFlag())
                            save.write(curLesson.getDictionary().indexOf(a.getPair()) + " 1 ");
                        else
                            save.write(curLesson.getDictionary().indexOf(a.getPair()) + " 0 ");
                    } else
                        save.write("-1 0 "); // Если ячейка пуста, то -1
                }
                save.write("\n");
            }
            SaveLastMove(save);
            save.write("\n");
            save.write(TimeSec + " " + numCorrectAnsw + " " + lessonMistakes1.size() + "\n");  // Значение таймера, кол-во верных ответов, ошибок
            for (DictionaryPair a : lessonMistakes1) // Сохранение первого списка ошибок
                save.write(curLesson.getDictionary().indexOf(a) + " ");
            save.write("\n");
            for (DictionaryPair a : lessonMistakes2) // Сохранение второго списка ошибок
                save.write(curLesson.getDictionary().indexOf(a) + " ");
            save.write("\n");
        } catch (IOException e) {
            // Сообщение о неудачном сохранении
            JOptionPane.showMessageDialog(null, "Не удалось сохранить прогресс текущего урока", "Ошибка сохранения прогресса", JOptionPane.ERROR_MESSAGE, null);
            return false;
        }
        return true;
    }

    /**
     * Сохраняет последний ход игрока
     *
     * @param save - FileWriter
     * @throws IOException - Input/Output Exception
     */
    private void SaveLastMove(Writer save) throws IOException {
        if (lastMove != null) {
            Cell cell = lastMove.getFirst();
            if (cell.isFlag())
                save.write(curLesson.getDictionary().indexOf(cell.getPair()) + " 1 ");
            else
                save.write(curLesson.getDictionary().indexOf(cell.getPair()) + " 0 ");
            save.write(cell.getPosition().toString() + " ");
            cell = lastMove.getSecond();
            if (cell.isFlag())
                save.write(curLesson.getDictionary().indexOf(cell.getPair()) + " 1 ");
            else
                save.write(curLesson.getDictionary().indexOf(cell.getPair()) + " 0 ");
            save.write(cell.getPosition().toString() + " ");
        } else
            save.write("-1"); // В случае, если последнего хода нет
    }

    /**
     * Загрузка сохраненного прогресса
     * // принимает fileName поданное пользователем?
     */
    public int LoadProgress(String fileName) {
        int TimeSec;
        try (Scanner load = new Scanner(new File(fileName), "UTF-8")) {
            curLesson = new Lesson();
            curLesson.setLessonName(load.nextLine());               // Название урока
            curLesson.setDictionary(new ArrayList<>());
            prepareLesson();
            int SizeDict = load.nextInt();                          // Кол-во слов словаря
            offset = load.nextInt();                                // Смещение в словаре
            load.nextLine();
            for (int i = 0; i < SizeDict; i++)                      // Пары словаря
                curLesson.getDictionary().add(DictionaryPair.readPair(load));
            numberOfSteps = load.nextInt();                         // Кол-во шагов
            numElemOfMatrix.clear();
            for (int i = 0; i < numberOfSteps; i++)                     // Матричное распределение
                numElemOfMatrix.addLast(load.nextInt());
            load.nextLine();
            fieldSize.setVertical(load.nextInt());
            fieldSize.setHorizontal(load.nextInt());
            load.nextLine();
            field = new Cell[fieldSize.getVertical()][fieldSize.getHorizontal()];
            for (int i = 0; i < fieldSize.getVertical(); i++)                      // Считывание поля
            {
                for (int j = 0; j < fieldSize.getVertical(); j++) {
                    int index = load.nextInt();
                    int flag = load.nextInt();
                    if (index != -1) {                                                        // Индекс пары в словаре + флаг
                        if (flag == 1)
                            field[i][j] = new Cell(curLesson.getDictionary().get(index), new Position(i, j), true);
                        else
                            field[i][j] = new Cell(curLesson.getDictionary().get(index), new Position(i, j), false);
                    } else
                        field[i][j] = null;
                }
            }
            lastMove = readSaveMove(load);          // Последний сделанный ход
            load.nextLine();
            TimeSec = load.nextInt();
            numCorrectAnsw = load.nextInt();
            int NumAllMistakes = load.nextInt();
            for (int i = 0; i < NumAllMistakes; i++)                         // Загрука первого списка ошибок
                lessonMistakes1.add(curLesson.getDictionary().get(load.nextInt()));
            load.nextLine();
            for (int i = 0; i < NumAllMistakes; i++)                         // Загрузка второго списка ошибок
                lessonMistakes2.add(curLesson.getDictionary().get(load.nextInt()));
        } catch (IOException | InputMismatchException e) {
            return -1;
        }
        return TimeSec;
    }

    private String toNormalString(String inputString) {
        char[] input = inputString.toCharArray();
        String output = "";
        char lastPushedSymb = ' ';
        for (int i = 0; i < inputString.length(); i++) {
            if (lastPushedSymb == '\\') {
                switch (input[i]) {
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case 'р':
                    case 'о':
                    case 'з':
                    case 'г':
                    case 'с':
                    case 'ф':
                    case 'ч':
                    case 'е':
                    case 'к':
                    case '\\':
                        // if (i != 1 && input[i+1] != '\\')
                        //output += '';
                        lastPushedSymb = ' ';
                        continue;
                }
            }
            if (input[i] == '\\') {
                lastPushedSymb = input[i];
                continue;
            }
            output += input[i];
        }
        return output;
    }

    /**
     * Считывает сохраненный последний ход игрока из потока
     *
     * @param load - сканнер потока
     * @return - объект класса Move
     */
    private Move readSaveMove(Scanner load) {
        Move move = null;
        int index = load.nextInt();
        if (index != -1) {
            move = new Move();
            int flag = load.nextInt();
            int row = load.nextInt();
            int column = load.nextInt();
            if (flag == 1)
                move.setFirst(new Cell(curLesson.getDictionary().get(index), new Position(row, column), true));
            else
                move.setFirst(new Cell(curLesson.getDictionary().get(index), new Position(row, column), false));
            index = load.nextInt();
            flag = load.nextInt();
            row = load.nextInt();
            column = load.nextInt();
            if (flag == 1)
                move.setSecond(new Cell(curLesson.getDictionary().get(index), new Position(row, column), true));
            else
                move.setSecond(new Cell(curLesson.getDictionary().get(index), new Position(row, column), false));
        }
        return move;
    }
}

