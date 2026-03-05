package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    //контроллер, который будет управлять запросами и делегировать их сервис
    private PostController controller;


    @Override
    //инициализация сервлета: создаём репозиторий, сервис, контроллер
    public void init() throws ServletException {
        // Создаём Spring контекст через аннотации
        var context = new AnnotationConfigApplicationContext();
        // сканируем пакет на @Component, @Service и т.д.
        context.scan("ru.netology");
        context.refresh();

        // Получаем бин контроллера
        this.controller = context.getBean(PostController.class);
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

