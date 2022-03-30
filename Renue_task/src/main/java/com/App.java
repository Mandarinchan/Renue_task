package com;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class App {

    private static final String fileName = "airports.dat";

    public static void main(String[] args) throws IOException, InterruptedException, CsvValidationException {
        int columnIndex = getColumnIndex(args);


        TreeMap<String, ArrayList<String>> airports_three_map =
                sortAirportsByColumns(columnIndex);



        long start_find = System.currentTimeMillis();

        Scanner sc = new Scanner(System.in);
        System.out.println("Пожалуйста, введите строку:");
        String wordSearch = sc.nextLine();

        List<String> resultSearch = new ArrayList<>(findAirports(airports_three_map, wordSearch));

        long finish_find = System.currentTimeMillis();

        long timeResult = finish_find - start_find;


        if (resultSearch.size() > 0) {
            System.out.println(String.join("\n", resultSearch));
        }

        System.out.println();
        System.out.println("////////////////////////////////////////////////////");
        System.out.println("Количество найденных строк: " + resultSearch.size());
        System.out.println("Время, затраченное на поиск: " + timeResult + "мс");
        System.out.println("////////////////////////////////////////////////////");


    }

    private static int getColumnIndex(String[] args) throws IOException {
        int colIndex = 0;
        if (args.length > 0) {
            try {
                colIndex = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Число задаваемое в файле доожно быть целым!");
                System.exit(-1);
            }
        } else {
            InputStream settings = ClassLoader.getSystemResourceAsStream("application.yml");// если здесь заменить на fileName то выдает ошибку, незнаю почему
            try {
                Properties prop = new Properties();
                prop.load(settings);
                colIndex = Integer.parseInt(prop.getProperty("column_search"));
            } catch (NullPointerException e) {
                System.out.println("Не найден файл с настройками приложения application.yml");
                System.exit(-1);
            } catch (NumberFormatException e) {
                System.out.println("Свойство column (application.yml) не найдено или не может быть преобразовано в целое цисло");
                System.exit(-1);
            }
        }

        if (colIndex < 1) {
            System.out.println("Номер колонки должен быть больше или равен 1");
            System.exit(-1);
        }

        colIndex--;
        return colIndex;
    }

    private static ArrayList<String> findAirports(TreeMap<String, ArrayList<String>> airports_three_map, String search) {
        ArrayList<String> result = new ArrayList<>();
        for (String key : airports_three_map.keySet()) {
            if (key.startsWith(search)) {
                result.addAll(airports_three_map.get(key));
            } else if (key.compareTo(search) > 0){
                return result;
            }
        }
        return result;
    }




    private static TreeMap<String, ArrayList<String>> sortAirportsByColumns(int colIndex) throws IOException, CsvValidationException {

        try {
            InputStream file = ClassLoader.getSystemResourceAsStream(fileName);
            InputStreamReader ir = new InputStreamReader(file, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(ir);
            CSVReader reader = new CSVReader(bufferedReader);

            TreeMap<String, ArrayList<String>> airports_three_map = new TreeMap<>();
            String[] line;

            while ((line = reader.readNext()) != null) {
                String key = line[colIndex];
                if (colIndex >= line.length) {
                    return null;
                }


                if (!airports_three_map.containsKey(key)) {
                    airports_three_map.put(key, new ArrayList<>());
                }
                airports_three_map.get(key).add(String.join(", ", line));
            }

            reader.close();
            return airports_three_map;

        } catch (NullPointerException e) {
            System.out.println("Файл с именем << " + fileName + ">> не найден");
            System.exit(-1);
            return null;
        }




    }



}