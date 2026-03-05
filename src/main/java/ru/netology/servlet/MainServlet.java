package ru.netology.servlet;

import com.google.gson.Gson;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MainServlet extends HttpServlet {
    //контроллер, который будет управлять запросами и делегировать их сервис
    private PostController controller;


    @Override
    //инициализация сервлета: создаём репозиторий, сервис, контроллер
    public void init() {
        final var repository = new PostRepository();
        final var service = new PostService(repository);
        controller = new PostController(service);
    }

    @Override
    // обработка http-запросов, определение маршрута и метода
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {
            //получаем URI из запроса
            final var path = req.getRequestURI();
            //получаем метод запроса(get, post, delete и т.д.)
            final var method = req.getMethod();
            // primitive routing
            //обработка маршрута для всех запросов
            if (method.equals("GET") && path.equals("/api/posts")) {
                //запрос для получения всех постов
                controller.all(resp);
                return;
            }
            // извлекаем ID из URL


            if (method.equals("GET") && path.matches("/api/posts/\\d+")) {
                // easy way
                //запрос на получение поста по ID
                // извлекаем ID из URL
                long parseID = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
                controller.getById(parseID, resp);
                return;
            }
            if (method.equals("POST") && path.equals("/api/posts")) {
                //запрос на создание или обновление нового поста
                controller.save(req.getReader(), resp);
                return;
            }
            if (method.equals("DELETE") && path.matches("/api/posts/\\d+")) {
                // easy way
                //запрос на удаление поста по ID
                long parseID = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
                controller.removeById(parseID, resp);
                return;
            }
            //если не удалось найти подходящий маршрут - возвращается 404 ошибка
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            //печатаем ошибку в случае исключения
            e.printStackTrace();
            //возвращаем ошибку сервера
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

