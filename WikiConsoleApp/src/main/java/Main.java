// Королев Илья, ПИ19-2. Вариант 1 (Консольное Вики-приложение)
// Для работы с форматом JSON была использована библиотека GSON от Google
// Все инструкции будут выведены в консоль после запуска данного файла

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import java.util.Scanner;

public class Main {

    // вывод выжимки по статье по её названию
    public static void pageByName(String page) throws IOException {

        // вывод в формате POSTMAN'a - необработанный однострочный ответ сервера
        String response = Parser.request("https://en.wikipedia.org/api/rest_v1/page/summary/" + page);
        String text = "";

        Gson gson = new Gson();
        JsonObject json = gson.fromJson(response, JsonObject.class);

        // в response (впоследствии json) нас интересует то, что можно добыть по ключу extract - первый блок или выжимка по статье
        String jsonFormat = (json.get("extract")).toString();
        int size = 100; // растягиваем строку в консоли, чтобы она была читабельна
        // (до этого выставлял значение 60, некоторые куски текста повторяли сами себя)

        text += "\n";
        for(int i=1; i<jsonFormat.length(); i+=1) {
            if (i % size == 0){

                // вытаскиваем из строки нужные символы между двумя индексами
                String substr = jsonFormat.substring(i, (i + size)<jsonFormat.length()? i + size :
                        jsonFormat.length()).replaceFirst(" ", "\n");

                // аналогично
                text +=  jsonFormat.substring(i-size, i) + substr;
            }
        }
        System.out.println(text);
    }

    // основное тело программы: отсюда мы будем вызывать все вспомогательные функции парсера
    public static void main(String[] args) throws IOException {

        // создаём условие начала работы после ввода пользователем "start"
        boolean start = false;
        String go = ""; // в эту переменную "положим" start
        Scanner input = new Scanner(System.in);
        while (!start) {
            System.out.println("Привет! Это консольное приложение для поиска статей в Википедии. " +
                    "Для начала работы приложения введите 'start'");
            go = input.nextLine();
            if(go.equals("start")){ // если пользователь ввёл нужное слово, идём дальше
                start = true;
            }
        }

        boolean check = false;
        while (!check) {
            System.out.println("Что будете искать?");
            String search = input.nextLine(); // аналог поисковой строки Википедии - пользователь вводит запрос
            String url = "https://en.wikipedia.org/w/api.php?action=opensearch&search=" + search + "&format=json";
            try {
                String response = Parser.request(url);
                check = Parser.pageList(response); // pageList в шаблоне switch-case может возвращать true/false
                                                   // в зависимости от результата запроса
            }
            catch (IOException exception){
                check = false;
            }
        }
    }
}