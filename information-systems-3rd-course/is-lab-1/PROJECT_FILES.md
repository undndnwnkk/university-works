# Файлы проекта is-lab-1: что где происходит

Разбор каждого файла репозитория по порядку: сборка и запуск, база данных, Java-код по слоям, страницы, тесты.
Для каждого файла указано, что в нём лежит, кто его использует и на что обратить внимание на защите.

Общая картина одной строкой:
**браузер → `FacesServlet` → `*.xhtml` → CDI-бин (`web`) → EJB-сервис (`service`) → репозиторий (`repository`) →
JPA/Hibernate → PostgreSQL**, а изменения уходят обратно всем браузерам через **CDI-событие → `websocket` → `app.js`**.

---

## 1. Сборка, запуск, инфраструктура

### `pom.xml`
Maven-описание проекта.
- `packaging=war`: результат сборки — веб-архив `target/routes.war` (`finalName=routes`). Отсюда и путь
  приложения `/routes/`: WildFly берёт контекст из имени WAR.
- Java 17 (`maven.compiler.release`).
- Зависимости:
  - `jakarta.jakartaee-api` 10.0.0, `provided`. Это только интерфейсы и аннотации Jakarta EE (Faces, CDI, EJB, JPA,
    Bean Validation, WebSocket). Реализации даёт WildFly, в WAR они не кладутся.
  - `hibernate-core` 6.6.3.Final, `provided`. Нужен для компиляции `DatabaseMigration`, который вызывает нативный
    API Hibernate для проверки схемы. Версия совпадает с той, что встроена в WildFly 35.
  - `flyway-core` + `flyway-database-postgresql` 10.22.0, compile. Их в WildFly нет, поэтому они попадают
    в `WEB-INF/lib` внутри WAR.
  - `junit-jupiter`, `assertj-core`, `test`: unit-тесты, в WAR не попадают.
- Плагины: компилятор, `maven-war-plugin` (с `failOnMissingWebXml=false`), `surefire` (запускает тесты при `package`).

### `Dockerfile`
Двухстадийная сборка образа приложения.
1. **Стадия `build`** (`maven:3.9.9-eclipse-temurin-17`):
   - `dependency:go-offline` — скачать зависимости отдельным слоем, чтобы при изменении кода не качать их заново;
   - `mvn package` — компиляция, тесты, сборка `routes.war`. Упал тест — образ не соберётся;
   - `dependency:get` скачивает JDBC-драйвер `postgresql-42.7.5.jar`.
2. **Стадия выполнения** (`quay.io/wildfly/wildfly:35.0.1.Final-jdk17`):
   - кладёт драйвер и `module.xml` в `modules/org/postgresql/main/`: так WildFly видит драйвер как модуль;
   - выполняет `configure.cli` через `jboss-cli.sh` в режиме embed-server. CLI правит `standalone.xml`:
     регистрирует драйвер и datasource. Потом чистит историю конфигов;
   - копирует `routes.war` в `standalone/deployments/`: WildFly развернёт его при старте сам (deployment scanner);
   - `EXPOSE 8080`.

Этот же образ используется для helios: из него целиком копируется готовый WildFly (см. GUIDE.md, раздел 6).

### `compose.yaml`
Локальный запуск двумя контейнерами.
- `db`: `postgres:17-alpine`, база/пользователь/пароль `routes`. Данные лежат в томе `route-data`.
  `healthcheck` с `pg_isready` нужен, чтобы приложение стартовало только после готовности БД.
- `app`: собирается из `Dockerfile`. Переменные `DB_HOST=db`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
  подхватывает datasource. Порт `8080:8080`. `depends_on: condition: service_healthy`.
- Скриптов инициализации БД здесь нет: таблицы создаёт Flyway при старте приложения (issue #11).
- `docker compose down -v` удаляет том, и при следующем старте база создаётся с нуля.

### `docker/wildfly/configure.cli`
Скрипт `jboss-cli`, который выполняется при сборке образа.
- `embed-server --server-config=standalone.xml` поднимает WildFly «внутри» CLI без сети, только для правки конфигурации.
- `jdbc-driver=postgresql:add(...)` регистрирует драйвер из модуля `org.postgresql`, включая XA-класс.
- `data-source add --name=RouteDS --jndi-name=java:/jdbc/RouteDS ...` создаёт пул соединений:
  - URL `jdbc:postgresql://${env.DB_HOST:db}:${env.DB_PORT:5432}/${env.DB_NAME:routes}`. Выражения `${env.X:default}`
    WildFly вычисляет при каждом старте, поэтому один и тот же сервер работает и в Docker, и на helios
    с другими переменными;
  - пул 2–15 соединений, проверка соединения при выдаче и фоновая каждые 5 секунд.
- JNDI-имя `java:/jdbc/RouteDS` дальше используется в `persistence.xml` и `DatabaseMigration`.

### `docker/wildfly/modules/org/postgresql/main/module.xml`
Описание модуля JBoss Modules для драйвера: ресурс `postgresql.jar` и зависимости от системных модулей
(`java.sql`, `jakarta.transaction.api` и т.д.). В WildFly библиотеки изолированы по модулям, и драйвер должен быть
модулем, чтобы datasource уровня сервера мог его загрузить.

### `.env.example`
Пример переменных для подключения к кафедральной БД на helios (`DB_HOST=pg`, `DB_NAME=studs`, `DB_USER=sXXXXXX`).
Приложение этот файл само не читает. Это шпаргалка, какие переменные выставить перед запуском WildFly.

### `.gitignore`
Не коммитить `target/`, файлы IDE, `.env` (там был бы пароль), `data/`, логи.

### `README.md`
Пустой. Его содержимое удалили на GitHub в коммите `3d50a67`.

### `docs/*.png`
Картинки интерфейса: исходный концепт (`ui-concept.png`) и скриншоты отрисованных страниц (обычный вид, модальное
окно, мобильная ширина). В коде не используются.

---

## 2. База данных

### `src/main/resources/db/migration/V1__schema.sql`
Первая миграция Flyway: вся схема. Flyway применяет её один раз и записывает в `flyway_schema_history`.
Все команды идемпотентны (`IF NOT EXISTS`, `CREATE OR REPLACE`), чтобы безопасно применяться поверх схемы,
созданной раньше вручную.

**Таблицы:**
- `locations`:
  - `id` — `INTEGER GENERATED BY DEFAULT AS IDENTITY`, id выдаёт БД;
  - `x INTEGER`, `y DOUBLE PRECISION`, `z REAL` — все `NOT NULL`;
  - `name VARCHAR(755)` + `CHECK` на длину.
- `routes`:
  - `id` — IDENTITY;
  - `name` `NOT NULL` + `CHECK (btrim(name) <> '')`, то есть пустая строка тоже запрещена;
  - `coordinate_x`, `coordinate_y` — сюда маппится `@Embeddable Coordinates`;
  - `creation_date` `DEFAULT CURRENT_TIMESTAMP`;
  - `from_location_id NOT NULL REFERENCES locations` и `to_location_id REFERENCES locations` (может быть NULL);
  - `distance REAL` + `CHECK (distance > 1)`;
  - `rating INTEGER` + `CHECK (rating > 0)`.
- `route_links (source_route_id, related_route_id)` — служебная таблица «маршрут ссылается на маршрут» для операции
  удаления с перепривязкой. Удаление источника каскадно чистит его ссылки (`ON DELETE CASCADE`), а удаление цели
  запрещено (`RESTRICT`), пока ссылки не перенесены.

**Индексы:** `lower(name)` для поиска, `rating` для спецопераций, `(from_location_id, to_location_id)` для поиска
между локациями.

**Функции PL/pgSQL**, которые вызывает Java-код:
- `delete_route_with_replacement(p_route_id, p_replacement_id)`:
  - проверяет, что маршрут и замена существуют и что маршрут не заменяет сам себя;
  - если на маршрут есть ссылки в `route_links`, а замена не указана, — ошибка;
  - иначе переносит ссылки на замену (`INSERT ... SELECT ... ON CONFLICT DO NOTHING`), удаляет старые ссылки
    и сам маршрут;
  - ошибки — `RAISE EXCEPTION` с русским текстом, их видно в интерфейсе.
- `delete_one_route_by_rating(p_rating)` — спецоперация 1: берёт первый по id маршрут с таким рейтингом с блокировкой
  `FOR UPDATE`, выбирает замену (первый другой маршрут) и вызывает функцию выше. Возвращает id удалённого или NULL.
- `max_rating_route()` — спецоперация 2: `SETOF routes`, один маршрут с максимальным рейтингом.
- `routes_rating_less_than(p_rating)` — спецоперация 3: все маршруты с рейтингом меньше заданного.
- `find_routes_between(p_from, p_to, p_sort)` — спецоперация 4:
  - динамический `ORDER BY`;
  - защита от SQL-инъекции: `p_sort` проверяется по белому списку, имя колонки подставляется через `format('%I')`,
    значения передаются через `USING`.
- `add_route_between(...)` — спецоперация 5: проверяет имя, distance и rating, делает `INSERT ... RETURNING id`.

### `src/main/resources/db/migration/V2__seed_data.sql`
Демонстрационные данные: 5 локаций (id 1–5) и 5 маршрутов (id 101–105).
- `OVERRIDING SYSTEM VALUE` позволяет вставить явные id в IDENTITY-колонку.
- `ON CONFLICT DO NOTHING` — повторная вставка не падает.
- `setval(pg_get_serial_sequence(...))` сдвигает последовательность после максимального id, иначе следующий
  `INSERT` получил бы уже занятый id.

### `src/main/resources/META-INF/persistence.xml`
Persistence unit `routesPU`:
- `transaction-type="JTA"` — транзакциями управляет контейнер (EJB);
- `jta-data-source` = `java:/jdbc/RouteDS` — datasource из `configure.cli`;
- явно перечислены сущности `Route` и `Location`;
- `jakarta.persistence.schema-generation.database.action=none` и `hibernate.hbm2ddl.auto=none`: Hibernate схему
  не создаёт и при старте не проверяет. Схема — забота Flyway, а проверка выполняется после миграций
  в `DatabaseMigration`;
- `hibernate.jdbc.time_zone=Europe/Moscow` — в каком поясе читать и писать `LocalDateTime`.

Провайдер явно не указан: WildFly по умолчанию подставляет Hibernate.

---

## 3. Java-код (`src/main/java/ru/itmo/routes`)

### 3.1. `domain` — JPA-сущности

#### `domain/Route.java`
Главная сущность, таблица `routes`.
- `@Id @GeneratedValue(strategy = IDENTITY) Integer id` — id выдаёт БД.
- `name`: `@NotBlank`, `@Column(nullable = false)`. Сеттер делает `trim()`.
- `coordinates`: `@Embedded @Valid @NotNull`. Объект встраивается в ту же таблицу, `@Valid` включает проверку
  вложенных полей. Инициализирован `new Coordinates()`.
- `creationDate`: `@NotNull`, `@Column(updatable = false)`. Значение ставит `@PrePersist initializeCreationDate()`
  перед `INSERT`, при `UPDATE` колонка не перезаписывается.
- `from`: `@ManyToOne(fetch = EAGER, optional = false) @JoinColumn(from_location_id)` + `@NotNull`.
- `to`: `@ManyToOne(fetch = EAGER) @JoinColumn(to_location_id)`, может быть `null`.
  EAGER: локации нужны почти всегда, и маппинг в DTO не упирается в ленивую загрузку.
- `distance`: `@DecimalMin(value = "1.0", inclusive = false)` — строго больше 1.
- `rating`: `@NotNull @Positive`.
- `equals/hashCode` по id: сущности сравниваются по идентичности в БД, `hashCode` — константа класса, чтобы объект
  не «терялся» в `HashSet` после присвоения id.

#### `domain/Coordinates.java`
`@Embeddable`: значение без собственной идентичности (value object).
- `Double x` → колонка `coordinate_x`, `Long y` → `coordinate_y`, оба `@NotNull`.
- Пустой конструктор нужен JPA, второй — для удобства.

#### `domain/Location.java`
Сущность, таблица `locations`.
- `id` — IDENTITY.
- `int x`, `double y` — примитивы, поэтому null невозможен.
- `Float z` — `@NotNull`.
- `name` — `@Size(max = 755)`. Сеттер превращает пустую строку в `null`.
- `getDisplayName()` возвращает имя или «Локация #id», если имени нет.

### 3.2. `dto` — объекты для веб-слоя (issue #10)

Бины и страницы работают только с этими классами, сущности наружу из сервиса не выходят.
Все DTO — обычные классы с геттерами, а не `record`: Expression Language в Jakarta EE 10 (EL 5) не умеет читать
свойства record-ов.

#### `dto/RouteDto.java`
Маршрут для отображения (таблица, карточка, результаты спецопераций): id, name, `CoordinatesDto`, creationDate,
`LocationDto from/to`, distance, rating. Все поля `final`, сеттеров нет: изменить маршрут через DTO нельзя.

#### `dto/CoordinatesDto.java`
Координаты внутри `RouteDto`: `x`, `y`. Благодаря ему на страницах осталось `#{route.coordinates.x}`.

#### `dto/LocationDto.java`
Локация: id, x, y, z, name и `getDisplayName()`, такой же, как в сущности. Используется в выпадающих списках
и на карточке.

#### `dto/RouteForm.java`
То, что вводит пользователь при создании или изменении маршрута.
- Поля: `name`, `coordinateX`, `coordinateY`, `fromId`, `toId`, `distance`, `rating`. Ссылки на локации хранятся
  как id, а не как объекты.
- Значения по умолчанию для новой формы: координаты 0, distance 1.1, rating 1.
- На полях те же ограничения Bean Validation, что и в сущности. JSF при отправке формы сам вызывает валидатор
  по аннотациям свойства, к которому привязан `h:inputText`, поэтому ошибки показываются до вызова сервиса.
- `static RouteForm of(RouteDto)` заполняет форму данными существующего маршрута для редактирования.

### 3.3. `repository` — доступ к БД

#### `repository/RouteRepository.java`
`@ApplicationScoped` CDI-бин, один на приложение. `@PersistenceContext(unitName = "routesPU") EntityManager em`:
контейнер внедряет потокобезопасный прокси, привязанный к текущей JTA-транзакции.
- `findById` — `em.find`.
- `findPage(filter, sort, ascending, offset, limit)`:
  - JPQL `select r from Route r join r.from f left join r.to t where ...` — фильтр по `lower(...) like %строка%`
    сразу по названию маршрута и названиям обеих локаций;
  - сортировка: ключ из UI проходит через `switch` в разрешённое выражение (`r.name`, `f.name`, ...). Строка
    пользователя в запрос не попадает, инъекции нет;
  - пагинация `setFirstResult/setMaxResults` превращается в `LIMIT/OFFSET` в SQL.
- `count(filter)` — то же условие, `count(r)`, нужен для числа страниц.
- `findAll()` — все маршруты по имени, для списка «маршрут-замена» в окне удаления.
- `save` — `em.persist`, `update` — `em.merge`.
- `deleteWithReplacement` — `select delete_route_with_replacement(?1, ?2)`, native query.
- Спецоперации — native queries к функциям:
  - `ratingLessThan`, `maxRating`, `findBetween` — `createNativeQuery("select * from fn(...)", Route.class)`.
    Функции возвращают `SETOF routes`, и Hibernate маппит строки на сущность по именам колонок;
  - `deleteOneByRating`, `addBetween` — скалярный результат (`getSingleResult`, `Number → int`).
- `normalized` — `trim().toLowerCase()`, `null` → `""`.

#### `repository/LocationRepository.java` (issue #13)
Тот же стиль, только локации.
- `findAll()` — JPQL, сортировка по имени (`coalesce(l.name, '')`), затем по id.
- `reference(id)` — `em.getReference`: прокси без запроса в БД. Достаточно для `route.setFrom(...)`, Hibernate
  возьмёт только id для внешнего ключа. `null` для `null`, так как «Куда» может быть не выбрано.

### 3.4. `service` — бизнес-логика и транзакции

#### `service/RouteService.java`
`@Stateless` EJB. Каждый публичный метод по умолчанию выполняется в транзакции (`TransactionAttribute.REQUIRED`):
контейнер открывает её при входе и коммитит при выходе, а при `RuntimeException` откатывает.
- Внедрены `RouteRepository`, `LocationRepository` и `Event<RouteChanged> changes`.
- Чтение (`page`, `count`, `find`, `all`, `maxRating`, `ratingLessThan`, `findBetween`) — вызов репозитория
  и маппинг в DTO через `RouteMapper`.
- `create(RouteForm)`: новая сущность → `RouteMapper.copy(form, route)` → `bindLocations` → `persist` →
  `changes.fire(new RouteChanged("created", id))` → `RouteDto`.
- `update(id, RouteForm)`: найти сущность (нет — `IllegalArgumentException("Маршрут не найден")`), скопировать
  поля и локации, `merge`, событие `updated`.
- `delete(id, replacementId)`: запрет замены на себя, вызов функции БД, событие `deleted`.
- `deleteOneByRating`: событие, только если что-то удалено.
- `addBetween`: `trim` имени, функция БД, событие `created`.
- `bindLocations`: `fromId` обязателен, ссылки на локации берутся через `LocationRepository.reference`.

Событие публикуется внутри транзакции, а слушатель срабатывает после коммита (см. `ChangeNotifier`).

#### `service/LocationService.java` (issue #13)
`@Stateless` EJB, `all()` возвращает `List<LocationDto>`. Отсюда бины берут локации для выпадающих списков.

#### `service/RouteMapper.java` (issue #10)
`final`-класс со статическими методами, видимость package-private: им пользуются только сервисы.
- `toDto(Route)`, `toDto(Location)` (`null` → `null`), `toRouteDtos`, `toLocationDtos`.
- `copy(RouteForm, Route)` переносит имя, координаты (новый `Coordinates`), distance и rating. Локации не трогает:
  их ставит сервис.

#### `service/RouteChanged.java` (issue #12)
`record RouteChanged(String action, Integer id)` — CDI-событие «маршрут создан/изменён/удалён».
Здесь record допустим: это не EL, а обычный Java-объект.

### 3.5. `migration` (issue #11)

#### `migration/DatabaseMigration.java`
- `@Singleton @Startup` — контейнер создаёт бин один раз при развёртывании, до первого запроса.
- `@TransactionAttribute(NOT_SUPPORTED)` — метод выполняется без JTA-транзакции: Flyway сам управляет транзакциями
  миграций.
- Внедрены `@Resource(lookup = "java:/jdbc/RouteDS") DataSource` и `@PersistenceUnit EntityManagerFactory`.
- `@PostConstruct migrate()`:
  1. `configure(dataSource).migrate()` — Flyway применяет новые `V*__*.sql` из `classpath:db/migration`;
  2. `entityManagerFactory.unwrap(SessionFactory.class).getSchemaManager().validateMappedObjects()` — Hibernate
     сверяет маппинг сущностей со схемой. Если что-то не совпадает, будет исключение, и деплой упадёт.
- `static configure(DataSource)`:
  - `baselineOnMigrate(true)` + `baselineVersion("0")` — если в схеме уже есть таблицы, но нет истории Flyway,
    ставится отметка «версия 0» и применяются V1, V2;
  - метод отдельный, чтобы проверить конфигурацию в тесте без БД.

### 3.6. `web` — CDI-бины страниц

Все три — `@Named` (доступны в EL по имени класса с маленькой буквы) и `@ViewScoped` (живут, пока пользователь
на одной странице; переживают Ajax-запросы, но не переход на другую страницу). Должны быть `Serializable`:
view-скоуп хранится в сессии.

#### `web/RouteListBean.java` → `index.xhtml`
Состояние главной: текущая страница `routes`, все `locations`, `allRoutes` (кандидаты на замену), `filter`,
`sort`/`ascending`, `page`/`pageSize = 8`, `total`, флаги модалок, `editingId`, `deleteId`, `replacementId`,
`draft` (`RouteForm`).
- `@PostConstruct init()` — загрузка локаций, всех маршрутов и первой страницы.
- `reload()` — `count` → число страниц → поправить `page`, если она вылезла за конец (например, после удаления) →
  загрузить страницу.
- `applyFilter` (с первой страницы), `nextPage`/`previousPage`.
- `sortBy(field)` — повторный клик по той же колонке меняет направление, новая колонка сортируется по возрастанию.
- `openCreate()` — новая `RouteForm`, «Откуда» по умолчанию первая локация, открыть модалку.
- `openEdit(id)` — `RouteForm.of(service.find(id))`, открыть модалку.
- `save()` — `create` или `update` в зависимости от `editingId`, закрыть окно, перечитать данные, сообщение.
  Исключение сервиса или БД превращается в сообщение `rootMessage` (текст самой глубокой причины).
- `askDelete(id)` / `delete()` — окно подтверждения и удаление с заменой.
- `getPageCount`, `getFirstRowNumber`, `getLastRowNumber` — для подписи «Записи 1–8 из N».

#### `web/RouteDetailBean.java` → `route-view.xhtml`
- `id` приходит из URL через `f:viewParam`.
- `load()` вызывается `f:viewAction` только на первом GET (`isPostback` → выход). Грузит локации и маршрут,
  если маршрута нет — сообщение «Маршрут не найден».
- `openEdit()` — `RouteForm.of(route)`, открыть модалку.
- `save()` — `service.update(id, draft)`, обновить `route`, сообщение. Без `try/catch`: ошибка уйдёт на страницу ошибки.
- **Известный баг:** сохранение отсюда не работает. В `route-view.xhtml` у `f:viewParam` стоит `required="true"`,
  а при Ajax-постбэке параметра `id` в запросе нет. Валидация падает с «j_idt1: Validation Error: Value is required».

#### `web/SpecialOperationsBean.java` → `special.xhtml`
Поля форм (`rating`, `fromId`, `toId`, `sort`, `name`, `coordinateX/Y`, `distance`, `newRating`) и `results`.
- `deleteByRating` — сообщение «Удалён маршрут #N» или «не найден», результаты очищаются.
- `showMax`, `showLessThan`, `findBetween` — заполняют `results`.
- `addBetween` — создаёт маршрут и сразу показывает маршруты между этими локациями.

### 3.7. `websocket` — синхронизация клиентов

#### `websocket/ChangeNotifier.java` (issue #12)
`@ApplicationScoped`, метод `onRouteChanged(@Observes(during = TransactionPhase.AFTER_SUCCESS) RouteChanged event)`.
CDI вызывает его только после успешного коммита транзакции, в которой событие было опубликовано. При откате вызова
не будет, поэтому клиенты не получат уведомление о несуществующем изменении. Внутри — `RouteEvents.broadcast`.

#### `websocket/RouteEvents.java`
`@ServerEndpoint("/ws/routes")` — WebSocket-endpoint, полный адрес `ws://host/routes/ws/routes`.
- Контейнер создаёт экземпляр на каждое соединение, поэтому общий список сессий —
  `static Set<Session> SESSIONS = ConcurrentHashMap.newKeySet()` (потокобезопасный).
- `@OnOpen` добавляет сессию, `@OnClose` убирает.
- `broadcast(action, id)` собирает JSON `{"action":"created","id":106}` и отправляет всем `getBasicRemote().sendText`.
  Закрытые сессии и сессии с ошибкой отправки удаляются тут же (`removeIf`).

---

## 4. Веб-часть (`src/main/webapp`)

### `WEB-INF/web.xml`
- `jakarta.faces.PROJECT_STAGE = Production` — без отладочных сообщений Faces.
- `FacesServlet` на `*.xhtml`, `load-on-startup=1`.
- Стартовая страница — `index.xhtml`.
- Ошибки 404 и 500 ведут на `/error.xhtml`.

### `WEB-INF/beans.xml`
Включает CDI с `bean-discovery-mode="annotated"`: бинами становятся только классы с аннотациями скоупа
(`@ViewScoped`, `@ApplicationScoped` и т.п.) или EJB.

### `WEB-INF/faces-config.xml`
Локаль по умолчанию `ru`: в ней форматируются даты (например, «27 октября 2026») и подбираются стандартные сообщения.

### `WEB-INF/templates/app.xhtml`
Общий Facelets-шаблон для всех страниц.
- `<h:head>`: точка вставки `metadata` (для `f:metadata` карточки), `title`, подключение `css/app.css`.
- Боковое меню: «Маршруты» и «Спецоперации» через `h:link`. Активный пункт подсвечивается
  по `#{view.viewId}`.
- Верхняя полоса «Данные синхронизируются».
- `<ui:insert name="content"/>` — сюда страницы вставляют своё содержимое.
- `app.js` подключается в конец `body`.

`WEB-INF` недоступен из браузера напрямую, поэтому шаблон нельзя открыть по URL.

### `index.xhtml` — главная (бин `routeListBean`)
`ui:composition` по шаблону. Всё внутри одной формы `routesForm`.
- **«＋ Новый маршрут»** — `h:commandButton` + `f:ajax execute="@this" render=":routesForm:modalArea"`:
  Ajax-запрос вызывает `openCreate()` и перерисовывает только область модалок.
- **Поиск** — `h:inputText id="filter"` с `f:ajax event="keyup" delay="350" listener="#{routeListBean.applyFilter}"`.
  Запрос уходит через 350 мс после последнего нажатия, перерисовываются таблица и пагинатор.
- **Таблица** `h:dataTable` по `routeListBean.routes` (это `RouteDto`):
  - колонки ID, Название (ссылка на карточку), Координаты «x; y», Из, В, Расстояние (`f:convertNumber`), Рейтинг,
    Создан (`f:convertDateTime`), Действия;
  - заголовки ID, Название, Из, В, Расстояние, Рейтинг — `h:commandLink` с `sortBy('...')`;
  - действия: «Открыть» (`h:link` на `route-view?id=`), «Изменить» (`openEdit(id)`), «Удалить» (`askDelete(id)`);
  - если строк нет — блок «Ничего не найдено».
- **Пагинатор** `pager`: «Назад/Вперёд» (выключаются на краях), номер страницы, «Записи a–b из N».
- **`modalArea`** — две модалки, видимость через `ui:fragment rendered`:
  - **редактор**:
    - поля привязаны к `draft.*` (`RouteForm`): `required`, `f:validateDoubleRange`/`f:validateLongRange`,
      выпадающие списки локаций с координатами в подписи, «Куда» с пунктом «Не выбрано» (`noSelectionOption`);
    - «Отмена» — `immediate="true"`: закрыть без валидации;
    - «Создать»/«Сохранить» — `execute="@form" render="@form"`;
    - ошибки — `h:messages` внутри окна;
  - **подтверждение удаления**: выбор «маршрута-замены» из `allRoutes` (сам удаляемый `itemDisabled`) и кнопка «Удалить».
- Ошибки диапазона показываются дважды: английское сообщение от `f:validate*Range` и русское от Bean Validation.

### `route-view.xhtml` — карточка (бин `routeDetailBean`)
- `f:metadata`: `f:viewParam name="id"` кладёт `?id=` из URL в `routeDetailBean.id`, `f:viewAction` вызывает `load()`.
- Карточка: id, имя, рейтинг. «Линия маршрута» — блоки «Откуда»/«Куда» с координатами x, y, z локаций
  и расстоянием между ними. Сетка атрибутов: координаты объекта, расстояние, рейтинг, дата создания.
- «Маршрут не найден», если `route == null`.
- Модалка `detailModal` с той же формой, что на главной, но привязанной к `routeDetailBean.draft`.
  Сохранение не работает, см. баг в `RouteDetailBean`.

### `special.xhtml` — спецоперации (бин `specialOperationsBean`)
Форма `specialForm`.
- Панель «Операции с рейтингом»: поле `opRating` и три кнопки — «меньше», «максимальный», «удалить один».
  У каждой свой `f:ajax execute` (например, `execute="opRating"`), поэтому другие поля формы не валидируются.
- Панель «Маршруты между локациями»: «Откуда», «Куда», сортировка (значения совпадают с белым списком функции БД),
  «Найти маршруты». В `<details>` «Добавить новый маршрут» — имя, координаты, distance, rating.
- `results` — таблица найденных маршрутов (ссылки на карточки) или «Результаты появятся здесь».
  Все кнопки перерисовывают `results` и сообщения `specialMessages`.

### `error.xhtml`
Страница для 404/500 по общему шаблону: «Не удалось открыть страницу» и ссылка на главную.

### `resources/css/app.css`
Все стили, около 180 строк:
- раскладка: `app-shell` (сетка «меню + контент»), `sidebar`, `topbar`, `page`;
- кнопки (`button.primary/secondary`, `danger-button`), поиск, таблица (`data-table`, `sort-link`, `row-actions`),
  пагинатор;
- уведомления `toast.success/error`, модалки (`modal-layer` — затемнение, `modal.drawer` — выезжающая панель справа,
  `confirm-modal`), поля форм;
- карточка маршрута (`detail-card`, `route-line`, `location-node`), спецоперации (`operations-layout`, `operation-panel`);
- адаптив под мобильную ширину.

Подключается через `h:outputStylesheet library="css"`, поэтому URL вида `/routes/jakarta.faces.resource/app.css.xhtml?ln=css`.

### `resources/js/app.js`
WebSocket-клиент, обёрнут в IIFE, чтобы не засорять глобальную область.
- Адрес собирается из текущего: `ws:`/`wss:` по протоколу страницы, хост, путь приложения + `/ws/routes`.
- `onmessage`: таймер на 700 мс сбрасывается при каждом новом сообщении (debounce: пачка событий даёт одну
  перезагрузку). По таймеру — `location.reload()`, но только если не открыта модалка (`.modal-layer`), чтобы
  не потерять ввод.
- `onclose`: переподключение через 2,5 с, например если сервер перезапускали.
- Своя вкладка тоже получает своё событие и перезагружается, из-за этого сообщение «Маршрут создан» пропадает.

---

## 5. Тесты (`src/test/java/ru/itmo/routes`)

Запускаются при `mvn package`, а значит, при каждой сборке Docker-образа. БД и сервер приложений не нужны.

- `domain/DomainConstraintsTest.java` проверяет аннотации на сущностях:
  - `@NotBlank` на имени, `@NotNull` на координатах и «Откуда», `@DecimalMin("1.0")` на distance,
    `@Positive` на rating;
  - id генерируется как `IDENTITY`;
  - сеттеры обрезают пробелы, пустое имя локации превращается в `null`.
- `service/RouteMapperTest.java`:
  - сущность → DTO, включая локацию и `to = null`;
  - форма → сущность, включая `trim` имени через сеттер;
  - в `RouteForm` те же ограничения, что в сущности (issue #10).
- `migration/MigrationScriptsTest.java`: файлы `V1__schema.sql` и `V2__seed_data.sql` лежат в classpath, Flyway
  настроен на `classpath:db/migration` (issue #11).
- `websocket/ChangeNotifierTest.java`: у параметра `onRouteChanged` стоит `@Observes(during = AFTER_SUCCESS)`
  (issue #12).

---

## 6. Локальные файлы вне git

- `GUIDE.md` — гайд по запуску, деплою на helios, теории и issues. Добавлен в `.git/info/exclude`, в коммит не попадёт.
- `is-lab-1.postman_collection.json` — коллекция Postman для всех действий (JSF Ajax-запросы с автоматическим ViewState).
- `PROJECT_FILES.md` — этот файл.
- `target/` (если собирать Maven-ом локально) — результат сборки, в `.gitignore`.
