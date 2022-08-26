package ru.shift.arg;

import org.apache.commons.cli.*;
import ru.shift.data.DataFile;
import ru.shift.sort.Compare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArgsCommandLine {
    private final Options options = new Options();
    private CommandLine cmd;

    public ArgsCommandLine() {
        Option sortAscending = new Option("a", "Сортировка по возрастанию");
        Option sortDescending = new Option("d", "Сортировка по убыванию");
        OptionGroup sortModeOptionGroup = new OptionGroup();
        sortModeOptionGroup.addOption(sortAscending);
        sortModeOptionGroup.addOption(sortDescending);
        options.addOptionGroup(sortModeOptionGroup);

        Option dataTypeString = new Option("s", "Сортировка строк");
        Option dataTypeInteger = new Option("i", "Сортировка целых чисел");
        OptionGroup dataTypeOptionGroup = new OptionGroup();
        dataTypeOptionGroup.setRequired(true);
        dataTypeOptionGroup.addOption(dataTypeString);
        dataTypeOptionGroup.addOption(dataTypeInteger);
        options.addOptionGroup(dataTypeOptionGroup);
        options.addRequiredOption("o", "outputFile", true, "Имя выходного файла");
        options.addRequiredOption("z", "inputFile", true, "Имена входных файлов");
    }

    public CommandLine getCmd() {
        return cmd;
    }

    public boolean parseCommandLine(String[] args) {
        try {
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(this.getClass().getName(), " ", options,
                    System.lineSeparator() + e.getMessage(), true);
            return false;
        }
        return true;
    }

    private int getNumberLines(File file) {
        try (BufferedReader bf = new BufferedReader(new FileReader(file))) {
            int numberLines = 0;
            while (bf.ready() && bf.readLine() != null) {
                ++numberLines;
            }
            return numberLines;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public List<DataFile> getDataFiles() {
        List<DataFile> dataFiles = new ArrayList<>();
        for (String filePath : cmd.getOptionValues("z")) {
            try {
                File file = new File(filePath);
                dataFiles.add(new DataFile(getNumberLines(file), 0, file));
            } catch (IllegalArgumentException ex) {
                System.out.println("Сортировка слиянием выполнена без данного файла.");
            }
        }
        return dataFiles;
    }

    private String compareStrings(String str1, String str2) {
        if (str1.compareToIgnoreCase(str2) >= 0) {
            if (cmd.hasOption("d")) {
                return str1;
            }
            return str2;
        } else {
            if (cmd.hasOption("d")) {
                return str2;
            }
            return str1;
        }
    }

    private String compareIntegers(String str1, String str2) {
        int number1 = Integer.parseInt(str1);
        int number2 = Integer.parseInt(str2);

        if (number1 >= number2) {
            if (cmd.hasOption("d")) {
                return str1;
            }
            return str2;
        } else {
            if (cmd.hasOption("d")) {
                return str2;
            }
            return str1;
        }
    }

    public Compare getCompareFunction() {
        if (cmd.hasOption("s")) {
            return this::compareStrings;
        }
        return this::compareIntegers;
    }

    public DataFile getFileForWrite() {
        File file = new File(cmd.getOptionValue("o"));
        return new DataFile(0, 0, file);
    }

    public boolean isDigit(String data) {
        if (data == null || data.isEmpty()) return false;
        for (int i = 0; i < data.length(); i++) {
            if (!Character.isDigit(data.charAt(i))) return false;
        }
        return true;
    }
}
