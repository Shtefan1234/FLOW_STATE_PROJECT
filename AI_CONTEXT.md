# AI_CONTEXT — паспорт проекта FlowState

> **Прочитай этот файл целиком, прежде чем предлагать или делать любые изменения.**
> Он описывает и реальное состояние кода, и согласованный план развития.
> Если что-то в коде расходится с этим файлом — верь файлу, а потом перепроверяй код.

---

## 1. Что за проект

**FlowState** — веб-сайт (НЕ мобильное приложение) для отслеживания прогресса по долгосрочным личным целям: учёба, спорт, языки, здоровье и т.д. Любая цель с чётким дедлайном.

Идея: цель разбивается на конкретные задачи по дням, и весь путь к дедлайну подаётся визуально как «дорожка дней» (вдохновлено Duolingo) — не плоский тудушник, а понятный путь вперёд с ощущением прогресса.

**Сценарий использования:** пользователь создаёт трек («Подготовка к ЕГЭ по информатике», дедлайн — дата экзамена), добавляет задачи на конкретные дни (разных категорий могут быть вперемешку в один день), и каждый день заходит отмечать сделанное.

**Две фичи-отличия:**
1. **Автоперераспределение просроченных задач** — если пользователь пропустил день, план не «ломается»: просроченные задачи сами распределяются по оставшимся дням до дедлайна.
2. **Ударный режим (streak)** — стрик дней подряд, когда в треке был выполнен хотя бы 1 DONE-задача за день + предупреждение, если сегодня есть риск прервать серию.

**НЕ входит в MVP (отложено):** чекин настроения/энергии, эмоциональные маркеры задач, полноценная авторизация (сейчас один пользователь без логина).

---

## 2. Стек

- Java 21
- Spring Boot 4.1.0 (parent) — Spring Web MVC, Spring Data JPA, Thymeleaf, Validation, Actuator (не подключён ещё)
- PostgreSQL 17
- Flyway (миграции БД)
- Maven (`mvnw.cmd`)
- Lombok (используется только для `@RequiredArgsConstructor` в контроллерах; сущности — с ручными геттерами/сеттерами)

**Запуск:** `.\mvnw.cmd spring-boot:run` (порт 8080).
Пароль БД — в переменной окружения `DB_PASSWORD`, НЕ в коде. БД: `flowstate`, пользователь `postgres`.

---

## 3. Модель данных — ТЕКУЩЕЕ СОСТОЯНИЕ (факт, проверено в коде)

### users
| Поле | Тип | Примечание |
|---|---|---|
| id | BIGINT identity | PK |
| name | VARCHAR(255) | |
| email | VARCHAR(255) | |

### tracks
| Поле | Тип | Примечание |
|---|---|---|
| id | BIGINT identity | PK |
| title | VARCHAR(255) | |
| deadline | DATE | необязательный (кейс «привычка без конца»: если нет — дорожка дней до сегодня) |
| created_at | DATE | |
| user_id | BIGINT | FK → users.id, ON DELETE CASCADE |
| current_streak | INT | default 0, для ударного режима (переехало с users по V4) |
| last_active_date | DATE | для ударного режима (переехало с users по V4) |

### tasks
| Поле | Тип | Примечание |
|---|---|---|
| id | BIGINT identity | PK |
| title | VARCHAR(255) | |
| status | VARCHAR(255) | enum **TaskStatus** |
| order_index | INT | |
| category | VARCHAR(255) | enum **TaskCategory** (переехал с Track по V3) |
| date | DATE | день, к которому привязана задача (по V3) |
| track_id | BIGINT | FK → tracks.id, ON DELETE CASCADE |

### Связи
- User 1→N Track (`user.tracks`, `@OneToMany`)
- Track 1→N Task (`track.tasks`, `@OneToMany`)

### Enums
- `TaskCategory`: STUDIES, SPORT, LANGUAGES, OTHER, HEALTH (на Task; класс переименован из TrackCategory)
- `TaskStatus`: PENDING, DONE, SKIPPED
- `DayStatus`: EMPTY, PARTIAL, DONE (статус дня, вычисляется в TrackService, сущности «день» НЕТ)

### Ключевое решение
Сущности «день» нет и не планируется. День вычисляется из задач по полю `date` (группировка в `TrackService.getDays`). Статус дня: нет задач → EMPTY; все DONE/SKIPPED → DONE; иначе → PARTIAL.
Диапазон дней: от `createdAt` до `deadline`; если дедлайна нет — до последней даты задач трека (или до сегодня, если задач нет).

---

## 4. Модель данных — ЦЕЛЕВАЯ (реализовано в V3)

Перенос под новую модель **выполнен**: миграция `V3__move_category_to_task.sql` + модели/DTO/мапперы/контроллеры обновлены.

- ✅ **Track**: убрать `category`
- ✅ **Task**: добавить `category` (enum TaskCategory) и `date` (LocalDate)
- ✅ **User**: добавить `currentStreak` (int) и `lastActiveDate` (LocalDate) — позже (по V4) **перенесено на Track**
- ✅ Фильтр по категории перенесён с Track на Task (`GET /api/tasks?category=SPORT`)
- ✅ Новая миграция `V3` (старые V1/V2 не редактировать — применены)
- ✅ Миграция `V4__move_streak_to_track.sql`: `current_streak`/`last_active_date` перенесены с `users` на `tracks` (стрик теперь у трека)

---

## 5. Что уже реализовано

### Архитектура (слои)
```
controller → service → repository → БД
    ↓
mapper (DTO ↔ entity)
```
Пакеты: `controller/`, `service/`, `repository/`, `model/`, `dto/request/`, `dto/response/`, `mapper/`, `exception/`.

### REST API (`/api/...`)
- `/api/users` — полный CRUD + пагинация (`?page=&size=`, Pageable)
- `/api/tracks` — CRUD + пагинация
- `/api/tasks` — CRUD + пагинация + **фильтр по category** (`?category=SPORT`, `findByCategory`)
- Вложенные: `POST /api/users/{id}/tracks`, `POST /api/tracks/{id}/tasks`
- `GET /api/tracks/{id}/days` — **дорожка дней** (шаг 2): `List<DayResponse(date, DayStatus, totalTasks, doneTasks)` от `createdAt` до `deadline` (или до последней даты задач/сегодня, если deadline нет)
- `GET /api/tracks/{id}/progress` — **прогресс трека** (шаг 3): `ProgressResponse(totalTasks, doneTasks, totalDays, doneDays)` — переиспользует `getDays`
- `GET /api/tracks/{id}/streak-status` — **статус стрика** (шаг 5): `StreakStatusResponse(currentStreak, atRisk, pendingTaskToday, hoursLeftToday)`

### Планировщик (шаги 4–5)
- `@EnableScheduling` на `FlowstateApplication`
- `RedistributionService` (`@Scheduled(cron = "0 1 0 * * *")`, ночной job в 00:01): PENDING-задачи с прошедшей датой равномерно распределяются (round-robin) по дням от сегодня до дедлайна; без дедлайна — до последней даты задач трека; трек с прошедшим дедлайном игнорируется
- `StreakService` (`@Scheduled(cron = "59 23 * * *")`, ночной job в 23:59): пересчитывает стрик каждого трека по задачам на сегодня — есть ≥1 DONE → `currentStreak` = +1 (если вчера был активен) или 1 (при разрыве), `lastActiveDate` = сегодня; задач нет → день отдыха (стрик не трогаем); есть задачи, но 0 DONE (включая день со всеми SKIPPED) → `currentStreak` = 0
- **Порядок**: стрик в 23:59 (пока задачи ещё на своих датах), перераспределение в 00:01 (уже после смены суток)
- `TaskRepository.findByStatusAndDateBefore(TaskStatus, LocalDate)`, `TaskRepository.countByTrackIdAndStatusAndDate(Long, TaskStatus, LocalDate)`

### Ключевые решения
- **DTO-слои**: request/response как `record`, валидация через `jakarta.validation` (`@NotBlank`, `@Size`, `@Email`, `@NotNull`), `@Valid` в контроллерах
- **Мапперы**: `{Entity}WebMapper` с `toEntity`/`toResponse`
- **Свои исключения**: `UserNotFoundException`, `TrackNotFoundException`, `TaskNotFoundException` (extends RuntimeException, сообщения на русском)
- **GlobalExceptionHandler** (`@RestControllerAdvice`): 404 для «не найдено», 400 для ошибок валидации, ответ `ErrorResponse(status, message)`
- **Стиль контроллера**: `@RestController`, `@RequiredArgsConstructor`, `@Validated`, `@ResponseStatus`, БЕЗ `ResponseEntity`
- **Flyway**: `V1__create_tables.sql` (создание), `V2__cascade_delete.sql` (ON DELETE CASCADE), `V3__move_category_to_task.sql` (категория → Task, date → Task, streak-поля → User), `V4__move_streak_to_track.sql` (streak-поля → Track). Правило: применённые миграции не редактировать, новое = новый файл.

### Thymeleaf UI
- `DashboardController` (`@Controller`, не REST): `GET /` — дашборд
- `templates/dashboard.html` — создание юзера/трека/задачи (задача — с категорией и датой), смена статуса (PENDING↔DONE), удаления
- В шаблонах обращение к связям: `user.tracks`, `track.tasks` (работает через `open-in-view`, включён по умолчанию)

---

## 6. Согласованный план развития (по порядку)

Порядок важен: п.1 — фундамент, остальное на нём стоит.

1. ✅ **Миграция данных под новую модель (V3)** — сделано (см. раздел 4)
2. ✅ **Дни трека** — `GET /api/tracks/{id}/days` — сделано (см. раздел 5): статусы EMPTY/PARTIAL/DONE через `groupingBy`
3. ✅ **Прогресс трека** — `GET /api/tracks/{id}/progress` — сделано (см. раздел 5)

4. ✅ **Фича №1 — автоперераспределение:** `@Scheduled` job (ночью, раз в сутки), находит все PENDING-задачи с прошедшей датой, равномерно распределяет по оставшимся дням до дедлайна (обновляет `date`). Сделано (см. раздел 5).

5. ✅ **Фича №2 — ударный режим (streak):** стрик у трека (`currentStreak`/`lastActiveDate` на Track по V4). Ночной job `StreakService` в 23:59 пересчитывает стрик по задачам на сегодня: есть ≥1 DONE → +1 (или =1 при разрыве); задач нет → день отдыха; есть задачи, но 0 DONE (включая день со всеми SKIPPED) → стрик = 0. `GET /api/tracks/{id}/streak-status` → `{ currentStreak, atRisk, pendingTaskToday, hoursLeftToday }`; бейдж «🔥 N» на дашборде. Сделано (см. раздел 5).

6. **После MVP:** Docker, секреты через env (проверить/завернуть), healthcheck/Actuator, логирование SLF4J, обновить README. Привести MVP к чек-листу требований.

---

## 7. Правила работы с этим проектом

- **Пользователь — начинающий разработчик.** Он пишет код сам, учится по ходу.
- **НЕ пиши код за него.** Объясняй концепции, аннотации, архитектурные решения; давай пошаговый план мелкими шагами, с объяснением «почему так, а не иначе».
- Отвечай на русском.
- Сначала уточни реальное состояние в коде, если сомневаешься, — не полагайся слепо на описание.
- Если предлагаешь изменение модели данных — помни правило Flyway (старые миграции не редактируются, нужен новый файл V-n).

---

## 8. Полезное окружение

- БД: PostgreSQL 17, база `flowstate`, порт 5432, пользователь `postgres`, пароль из `DB_PASSWORD`
- psql НЕ в PATH — полный путь: `C:\Program Files\PostgreSQL\17\bin\psql.exe`
- Приложение: порт 8080, контекст `/`
- UI: `http://localhost:8080/`
- GitHub: `https://github.com/Shtefan1234/FLOW_STATE_PROJECT.git`, ветка `master`
- Git-коммиты делаются только по явной просьбе пользователя
