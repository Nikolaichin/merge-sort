package ru.shift.data;

import java.io.*;

public class DataFile {
    private int numberLines;

    private int currentLineNumber;

    private File file;

    public DataFile(int numberLines, int currentLineNumber, File file) {
        this.numberLines = numberLines;
        this.currentLineNumber = currentLineNumber;
        this.file = file;
    }

    public int getNumberLines() {
        return numberLines;
    }

    public int getCurrentLineNumber() {
        return currentLineNumber;
    }

    public void setCurrentLineNumber(int currentLineNumber) {
        this.currentLineNumber = currentLineNumber;
    }

    public String readData(int numberLine) {
        if (numberLine <= numberLines) {
            try (BufferedReader bf = new BufferedReader(new FileReader(file))) {
                int currentNumberLine = 0;
                String lineRead = "";
                while (currentNumberLine < numberLine) {
                    lineRead = bf.readLine();
                    ++currentNumberLine;
                }
                return lineRead;
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        return "";
    }

    public void writeData(String str) {
        if (str != null && !str.isEmpty()) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))) {
                if (currentLineNumber > 0) {
                    bufferedWriter.newLine();
                }
                bufferedWriter.write(str);
                ++currentLineNumber;
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    @Override
    public String toString() {
        return "numberLines " + this.numberLines +
                " currentLineNumber " + this.currentLineNumber +
                " file" + this.file.getName();
    }
}
