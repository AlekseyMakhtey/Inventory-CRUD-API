
# REST CRUD-сервис Inventory Items с базовой RBAC-авторизацией

## Описание проекта

Inventory Management API — это серверное приложение для управления товарами на складе.

Основные возможности:
- Создание, просмотр, обновление и удаление товаров (CRUD).
- Поиск товаров по названию, SKU и категории.
- Пагинация и сортировка результатов.
- Аутентификация с использованием JWT.
- Разграничение доступа по ролям (ADMINISTRATOR и VIEWER).
- Работа с базой данных PostgreSQL.
- Документация API с помощью Swagger UI.

## Требования

Для запуска проекта необходимы:

- Java 21 или выше
- Maven
- Docker и Docker Compose
- PostgreSQL 16 (если запускать приложение без Docker)



## Запуск проекта через Docker Compose

1. Убедитесь, что Docker Desktop запущен.
2. Откройте терминал в корневой папке проекта.
3. Выполните команду:

   ```bash
   docker compose up -d --build
   ```

4. Дождитесь запуска контейнеров.

Проверить состояние контейнеров можно командой:

```bash
docker compose ps
```

После успешного запуска API будет доступно по адресу:

`http://localhost:8080`

Swagger UI:

`http://localhost:8080/swagger-ui.html`

Для остановки приложения выполните:

```bash
docker compose down
```



## Переменные окружения

Для настройки приложения используется файл `.env`, расположенный в корневой папке проекта.

В нём задаются учётные данные пользователей:

- `ADMIN_USERNAME` — логин администратора.
- `ADMIN_PASSWORD` — пароль администратора.
- `VIEWER_USERNAME` — логин пользователя с ролью VIEWER.
- `VIEWER_PASSWORD` — пароль пользователя с ролью VIEWER.

Файл `.env` не следует добавлять в Git, поскольку он содержит конфиденциальные данные.

## Тестовые учётные записи

При запуске приложения автоматически проверяется наличие пользователей в базе данных.

Предусмотрены две роли:

| Логин | Роль | Права |
|---|---|---|
| `admin` | ADMINISTRATOR | Полный доступ к CRUD |
| `viewer` | VIEWER | Только просмотр товаров |

Пароли задаются в файле `.env` через переменные `ADMIN_PASSWORD` и `VIEWER_PASSWORD`.



## Миграции базы данных

Для управления структурой базы данных используется Flyway.

Файлы миграций находятся в директории:

`src/main/resources/db/migration`

В проекте используются следующие миграции:

- `V1__create_inventory_items.sql` — создание таблицы товаров.
- `V2__create_app_users.sql` — создание таблицы пользователей приложения.

При запуске приложения Flyway автоматически проверяет и применяет миграции, которые ещё не были выполнены.

История выполненных миграций хранится в таблице `flyway_schema_history`.

Миграции выполняются автоматически при запуске приложения. Вручную создавать таблицы не требуется.


## Авторизация

Для доступа к API необходимо получить JWT-токен.

Авторизация выполняется через POST-запрос:

`http://localhost:8080/api/auth/login`

В Postman выберите:

- Метод: POST
- Body → raw → JSON
- Content-Type: application/json

Тело запроса:

```json
{
    "username": "admin",
    "password": "<ADMIN_PASSWORD>"
}
```

Вместо `<ADMIN_PASSWORD>` укажите пароль администратора из файла `.env`.

В ответе сервер вернёт JWT-токен:

```json
{
    "token": "<JWT_TOKEN>"
}
```

Полученный токен необходимо передавать в заголовке `Authorization`:

`Authorization: Bearer <JWT_TOKEN>`

## CRUD-операции с товарами

Во всех запросах, кроме авторизации, необходимо передавать JWT-токен в заголовке `Authorization`.

Полученный токен необходимо передавать в заголовке Authorization:
Authorization: Bearer <token>

## Работа с API через Postman

Базовый URL приложения:

http://localhost:8080

### 1. Авторизация

- Метод: POST
- URL: `http://localhost:8080/api/auth/login`
- Вкладка Body → raw → JSON

Тело запроса:

{
    "username": "admin",
    "password": "<ADMIN_PASSWORD>"
}

В ответе сервер возвращает JWT-токен.

### 2. Настройка авторизации

Для последующих запросов:

- Откройте вкладку Authorization.
- В поле Auth Type выберите Bearer Token.
- Вставьте полученный JWT-токен.

### 3. Создание товара

- Метод: POST
- URL: `http://localhost:8080/api/items`
- Authorization: Bearer Token
- Body → raw → JSON

Тело запроса:

{
    "name": "Wireless Mouse",
    "sku": "MOUSE-001",
    "quantity": 25,
    "price": 19.99,
    "category": "ELECTRONICS",
    "location": "Warehouse A",
    "description": "Wireless computer mouse"
}

Ожидаемый статус: 201 Created.

### 4. Получение списка товаров

- Метод: GET
- URL: `http://localhost:8080/api/items`
- Authorization: Bearer Token

Ожидаемый статус: 200 OK.

### 5. Получение товара по ID

- Метод: GET
- URL: `http://localhost:8080/api/items/1`
- Authorization: Bearer Token

Вместо `1` укажите ID существующего товара.

Ожидаемый статус: 200 OK.

### 6. Обновление товара

- Метод: PUT
- URL: `http://localhost:8080/api/items/1`
- Authorization: Bearer Token
- Body → raw → JSON

Тело запроса:

{
    "name": "Updated Wireless Mouse",
    "sku": "MOUSE-001",
    "quantity": 30,
    "price": 24.99,
    "category": "ELECTRONICS",
    "location": "Warehouse B",
    "description": "Updated wireless computer mouse"
}

Ожидаемый статус: 200 OK.

### 7. Удаление товара

- Метод: DELETE
- URL: `http://localhost:8080/api/items/1`
- Authorization: Bearer Token

Ожидаемый статус: 204 No Content.

Операции создания, обновления и удаления доступны только роли ADMINISTRATOR.

## Фильтрация, пагинация и сортировка

API поддерживает поиск товаров по названию, SKU и категории.

### Фильтрация

Примеры параметров:

- `name` — поиск по названию (частичное совпадение, без учёта регистра).
- `sku` — поиск по SKU (частичное совпадение, без учёта регистра).
- `category` — фильтрация по категории.

Пример URL:

`http://localhost:8080/api/items?name=mouse&category=ELECTRONICS`

### Пагинация

Для управления страницами используются параметры:

- `_page` — номер страницы, начиная с 0.
- `_size` — количество товаров на странице (от 1 до 100).

Пример:

`http://localhost:8080/api/items?_page=0&_size=10`

### Сортировка

Параметр `_sort` задаёт поле сортировки.

Поддерживаемые поля:

`id`, `name`, `sku`, `quantity`, `price`, `category`, `location`.

Пример:

`http://localhost:8080/api/items?_sort=name`

Все параметры можно комбинировать.

Пример полного запроса:

`http://localhost:8080/api/items?name=mouse&category=ELECTRONICS&_page=0&_size=10&_sort=name`


## Запуск тестов

Для запуска всех автоматических тестов выполните команду в терминале из корневой папки проекта:

```bash
mvn test
```

В проекте используются:

- JUnit 5 — для написания тестов.
- Mockito — для изоляции зависимостей при тестировании сервисного слоя.
- MockMvc — для тестирования REST-контроллеров.

Тесты проверяют CRUD-операции, обработку ошибок, валидацию и разграничение доступа по ролям.

## Swagger UI

Документация API доступна через Swagger UI:

`http://localhost:8080/swagger-ui.html`

Swagger UI позволяет просматривать доступные REST-эндпоинты и выполнять запросы к API.

Для авторизации нажмите кнопку **Authorize** и вставьте JWT-токен, полученный через `/api/auth/login`.

## Используемые технологии

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Security
- JWT (JSON Web Token)
- Spring Data JPA / Hibernate
- PostgreSQL 16
- Flyway (миграции базы данных)
- Maven
- Swagger / OpenAPI
- Docker и Docker Compose
- JUnit 5
- Mockito
- MockMvc

