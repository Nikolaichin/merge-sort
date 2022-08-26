package ru.shift;

import ru.shift.arg.ArgsCommandLine;
import ru.shift.data.DataFile;
import ru.shift.sort.Compare;

import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        ArgsCommandLine programArgsCommandLine = new ArgsCommandLine();

        if (!programArgsCommandLine.parseCommandLine(args)) {
            System.exit(0);
        }

        Compare compare = programArgsCommandLine.getCompareFunction();
        List<DataFile> dataFiles = programArgsCommandLine.getDataFiles();
        DataFile dataFile = null;
        Optional<DataFile> optionalDataFile;

        DataFile fileForWrite = programArgsCommandLine.getFileForWrite();

        while (!(optionalDataFile = dataFiles.stream()
                .filter(f -> f.getCurrentLineNumber() < f.getNumberLines())
                .findFirst()).equals(Optional.empty())) {

            dataFile = optionalDataFile.get();

            int number = dataFile.getCurrentLineNumber() + 1;
            String data = dataFile.readData(number);

            if (!programArgsCommandLine.isDigit(data) && programArgsCommandLine.getCmd().hasOption("i")) {
                dataFile.setCurrentLineNumber(dataFile.getCurrentLineNumber() + 1);
                continue;
            }

            String result = "";
            for (DataFile file : dataFiles) {
                if (dataFile != file && file.getCurrentLineNumber() + 1 <= file.getNumberLines()) {
                    int currentLineNumber = file.getCurrentLineNumber();
                    while (currentLineNumber < file.getNumberLines()) {
                        String currentData = file.readData(currentLineNumber + 1);
                        if (!programArgsCommandLine.isDigit(currentData) && programArgsCommandLine.getCmd().hasOption("i")) {
                            currentLineNumber++;
                            continue;
                        }

                        result = compare.getResult(data, currentData);
                        if (!result.equalsIgnoreCase(data)) {
                            number = currentLineNumber + 1;

                            dataFile = file;
                            data = result;
                        }
                        currentLineNumber++;
                    }
                }
            }
            dataFile.setCurrentLineNumber(number);
            fileForWrite.writeData(data);
        }
    }
}


