# FlowState

Трекер личных планов развития. Пользователь создаёт планы (Track) и наполняет их задачами (Task).

## Стек

- Java 21, Spring Boot 4.1.0 (Spring Web, Spring Data JPA, Thymeleaf)
- PostgreSQL 17
- Flyway (миграции базы данных)

## Как запустить

### 1. Подготовка базы

Убедись, что PostgreSQL запущен и база `flowstate` существует:

```sql
CREATE DATABASE flowstate;
```

### 2. Переменная окружения `DB_PASSWORD`

Пароль БД хранится в переменной окружения, а не в коде. Задай её перед запуском.

Windows (PowerShell):

```powershell
setx DB_PASSWORD your_password
```

> Открой новое окно терминала после установки — переменная подхватится только в новом окне.

### 3. Запуск приложения

```powershell
.\mvnw.cmd spring-boot:run
```

При первом старте Flyway создаст таблицы из миграций в `src/main/resources/db/migration/`.

### 4. Проверка

Приложение: **http://localhost:8080**

Создание пользователя:

```http
POST http://localhost:8080/api/users
Content-Type: application/json

{"name": "Маша", "email": "masha@mail.ru"}
```

Ответ — `201 Created` с телом созданного пользователя.

## Структура проекта

```
src/main/java/com/example/flowstate/
├── controller/   # REST-контроллеры
├── service/      # бизнес-логика
├── repository/   # доступ к базе (Spring Data JPA)
└── model/        # сущности: User, Track, Task
```

## REST API

| Метод | URL | Описание |
|---|---|---|
| GET | /api/users | список пользователей |
| POST | /api/users | создать пользователя |
| GET | /api/users/{id} | пользователь по id |
| PUT | /api/users/{id} | обновить пользователя |
| DELETE | /api/users/{id} | удалить пользователя |
| POST | /api/users/{id}/tracks | создать план пользователю |
| GET | /api/tracks | список планов |
| POST | /api/tracks/{id}/tasks | создать задачу в плане |