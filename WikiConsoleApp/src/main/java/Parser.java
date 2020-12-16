import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.io.IOException;
import java.io.InputStreamReader;
// по условию задачи используем библиотеку HttpURLConnection
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.*;
import java.io.BufferedReader;

public class Parser {

    // Делаем запрос к серверу и полученный ответ конвертируем в String
    public static String request(String url)throws IOException {
        url = url.replaceAll(" ", "_");
        URL connectUrl = new URL(url);
        // Открываем соединение по нужному URL
        HttpURLConnection connection = (HttpURLConnection)connectUrl.openConnection();
        connection.setRequestMethod("GET"); // нам нужно только получить ответ сервера

        // Данные загружаем в буфер и построчно читаем их
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String response = br.readLine();
        br.close();

        return response;
    }

    // Вывод списка статей по сделанному запросу
    public static Boolean pageList(String response) throws IOException {

        Gson gson = new Gson();

        ArrayList jsonList = gson.fromJson(response, ArrayList.class);
        // создаём список из полученного ответа сервера и обрабатываем его с помощью GSON
        ArrayList pageList = new ArrayList((Collection) jsonList.get(1));

        switch (pageList.size()){ // статей по запросу может быть от 0 до 10, обрабатываем эти случаи

            default:
                String output = String.format("Выберите номер статьи от 1 до %d", pageList.size());
                System.out.println(output);
                System.out.println(pageList); // выводим список, если статей несколько

                boolean check = false;
                while (!check) {
                    Scanner input = new Scanner(System.in); // запрашиваем выбор статьи пользователем
                    int num = input.nextInt(); // пользователь вводит номер нужной статьи
                    if(num <= pageList.size()){
                        String name = (pageList.get(num-1)).toString(); // вытаскиваем название статьи по индексу
                        Main.pageByName(name);
                        check = true;
                    }
                    else{
                        System.out.println("Введите число из предложенного дипазона");
                    }
                }
                return true;
            case 1: // если статья всего одна, то сразу выводим её extract
                Main.pageByName(pageList.get(0).toString());
                return true;

            // Если статей по запросу нет
            case 0:
                System.out.println("Ничего не найдено");
                return false;
        }
    }
}