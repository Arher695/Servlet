package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PostRepository {
    private final Map<Long, Post> posts = new ConcurrentHashMap<>();
    private final AtomicLong lastId = new AtomicLong(0);

    public PostRepository() {
        // Можно добавить тестовые данные при необходимости
    }

    public Map<Long, Post> all() {
        return Map.copyOf(posts); // неизменяемая копия для потокобезопасности
    }

    public Optional<Post> getById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            // Создание нового поста
            long newId = lastId.incrementAndGet();
            final var newPost = new Post(newId, post.getContent());
            posts.put(newId, newPost);
            return newPost;
        } else {
            // Обновление существующего
            if (!posts.containsKey(post.getId())) {
                throw new NotFoundException("Post with id " + post.getId() + " not found");
            }
            posts.put(post.getId(), post);
            return post;
        }
    }

    public void removeById(long id) {
        if (!posts.containsKey(id)) {
            throw new NotFoundException("Post with id " + id + " not found");
        }
        posts.remove(id);
    }
}
