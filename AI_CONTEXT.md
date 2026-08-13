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
2. **Ударный режим (streak)** — стрик дней подряд с полностью закрытыми задачами + предупреждение, если сегодня есть риск прервать серию.

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
| current_streak | INT | default 0, для ударного режима |
| last_active_date | DATE | для ударного режима |

### tracks
| Поле | Тип | Примечание |
|---|---|---|
| id | BIGINT identity | PK |
| title | VARCHAR(255) | |
| deadline | DATE | необязательный (кейс «привычка без конца»: если нет — дорожка дней до сегодня) |
| created_at | DATE | |
| user_id | BIGINT | FK → users.id, ON DELETE CASCADE |

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

---

## 4. Модель данных — ЦЕЛЕВАЯ (реализовано в V3)

Перенос под новую модель **выполнен**: миграция `V3__move_category_to_task.sql` + модели/DTO/мапперы/контроллеры обновлены.

- ✅ **Track**: убрать `category`
- ✅ **Task**: добавить `category` (enum TaskCategory) и `date` (LocalDate)
- ✅ **User**: добавить `currentStreak` (int) и `lastActiveDate` (LocalDate)
- ✅ Фильтр по категории перенесён с Track на Task (`GET /api/tasks?category=SPORT`)
- ✅ Новая миграция `V3` (старые V1/V2 не редактировать — применены)

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
- `GET /api/tracks/{id}/days` — **дорожка дней** (шаг 2): `List<DayResponse(date, DayStatus, totalTasks, doneTasks)` от `createdAt` до `deadline` (или до сегодня, если deadline нет)

### Ключевые решения
- **DTO-слои**: request/response как `record`, валидация через `jakarta.validation` (`@NotBlank`, `@Size`, `@Email`, `@NotNull`), `@Valid` в контроллерах
- **Мапперы**: `{Entity}WebMapper` с `toEntity`/`toResponse`
- **Свои исключения**: `UserNotFoundException`, `TrackNotFoundException`, `TaskNotFoundException` (extends RuntimeException, сообщения на русском)
- **GlobalExceptionHandler** (`@RestControllerAdvice`): 404 для «не найдено», 400 для ошибок валидации, ответ `ErrorResponse(status, message)`
- **Стиль контроллера**: `@RestController`, `@RequiredArgsConstructor`, `@Validated`, `@ResponseStatus`, БЕЗ `ResponseEntity`
- **Flyway**: `V1__create_tables.sql` (создание), `V2__cascade_delete.sql` (ON DELETE CASCADE), `V3__move_category_to_task.sql` (категория → Task, date → Task, streak-поля → User). Правило: применённые миграции не редактировать, новое = новый файл.

### Thymeleaf UI
- `DashboardController` (`@Controller`, не REST): `GET /` — дашборд
- `templates/dashboard.html` — создание юзера/трека/задачи (задача — с категорией и датой), смена статуса (PENDING↔DONE), удаления
- В шаблонах обращение к связям: `user.tracks`, `track.tasks` (работает через `open-in-view`, включён по умолчанию)

---

## 6. Согласованный план развития (по порядку)

Порядок важен: п.1 — фундамент, остальное на нём стоит.

1. ✅ **Миграция данных под новую модель (V3)** — сделано (см. раздел 4)
2. ✅ **Дни трека** — `GET /api/tracks/{id}/days` — сделано (см. раздел 5): статусы EMPTY/PARTIAL/DONE через `groupingBy`
3. ⬜ **Прогресс трека** — `GET /api/tracks/{id}/progress`: выполнено задач/всего, закрыто дней/всего.

4. **Фича №1 — автоперераспределение:** `@Scheduled` job (ночью, раз в сутки), находит все PENDING-задачи с прошедшей датой, равномерно распределяет по оставшимся дням до дедлайна (обновляет `date`).

5. **Фича №2 — ударный режим (streak):**
   - при закрытии последней задачи дня → обновить `currentStreak`/`lastActiveDate` (инкремент, если вчера был активен; сброс на 1, если разрыв)
   - тот же ночной job проверяет незакрытые задачи за вчера → при наличии сброс `currentStreak` на 0
   - `GET /api/tracks/{id}/streak-status` → `{ currentStreak, atRisk, pendingTasksToday, hoursLeftToday }` для баннера-предупреждения

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
