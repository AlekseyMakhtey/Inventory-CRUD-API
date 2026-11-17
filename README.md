# Inventory Management API

REST API для управления складскими товарами, построенный на Java и Spring Boot. Проект включает CRUD-операции, JWT-аутентификацию, разграничение доступа, отдельный сервис сбора статистики и мониторинг.

## Содержание

- [Описание проекта](#описание-проекта)
- [Возможности](#возможности)
- [Структура Git-веток](#структура-git-веток)
- [Технологический стек](#технологический-стек)
- [Архитектура](#архитектура)
- [Требования](#требования)
- [Запуск проекта](#запуск-проекта)
- [Переменные окружения](#переменные-окружения)
- [Аутентификация и авторизация](#аутентификация-и-авторизация)
- [Работа с Inventory API через Postman](#работа-с-inventory-api-через-postman)
- [Фильтрация, пагинация и сортировка](#фильтрация-пагинация-и-сортировка)
- [Statistics Service](#statistics-service)
- [Мониторинг и метрики](#мониторинг-и-метрики)
- [Миграции базы данных](#миграции-базы-данных)
- [Тестирование](#тестирование)
- [Swagger UI](#swagger-ui)

## Описание проекта

Inventory Management API — серверное приложение для управления товарами на складе.

Основная часть приложения предоставляет REST API для выполнения CRUD-операций с товарами. Доступ к защищённым ресурсам осуществляется с помощью JWT-токенов.

Дополнительно реализован отдельный микросервис статистики, который периодически получает данные из Inventory API, агрегирует их и сохраняет результаты в собственной базе данных.

Проект также включает мониторинг приложения с помощью Micrometer и Prometheus, механизм повторных запросов при временных ошибках и два варианта разграничения доступа: RBAC и PBAC.

## Возможности

### Inventory API

- Создание, получение, обновление и удаление товаров.
- Поиск по названию и SKU.
- Фильтрация по категории.
- Пагинация и сортировка.
- Валидация входных данных.
- Обработка ошибок с соответствующими HTTP-статусами.
- JWT-аутентификация.
- Разграничение доступа.

### Дополнительные возможности

- Отдельный Spring Boot Statistics Service.
- Автоматический ежедневный сбор статистики в 02:00 UTC.
- Подсчёт общего количества товаров и количества товаров по категориям.
- Хранение статистики в отдельной PostgreSQL.
- Получение статистики за выбранный период.
- Retry с exponential backoff для повторных запросов.
- Сбор метрик с помощью Micrometer.
- Экспорт метрик в формате Prometheus.

## Структура Git-веток

В проекте используются три основные ветки, позволяющие отдельно рассматривать базовую реализацию и дополнительные возможности.

| Ветка | Содержание |
|---|---|
| `main` | Основной Inventory CRUD API с JWT и RBAC |
| `feature` | Statistics Service, Prometheus и Retry |
| `auth` | Все возможности `feature` плюс переход на PBAC |

**Рекомендуемая ветка для просмотра полной реализации:** `auth`.

Переключение между ветками:

```bash
git switch main
git switch feature
git switch auth
```

## Технологический стек

| Технология | Назначение |
|---|---|
| Java 21 | Основной язык разработки |
| Spring Boot 4 | Основа приложения |
| Spring Web MVC | REST API |
| Spring Security | Аутентификация и авторизация |
| JWT | Токены доступа |
| Spring Data JPA | Работа с базой данных |
| Hibernate | ORM |
| PostgreSQL 16 | Хранение данных |
| Flyway | Миграции базы данных |
| Maven | Сборка проекта |
| OpenAPI / Swagger | Документация API |
| Docker Compose | Контейнеризация и запуск сервисов |
| JUnit 5 | Автоматизированное тестирование |
| Mockito | Модульное тестирование |
| MockMvc | Тестирование REST-контроллеров |
| Micrometer | Сбор метрик |
| Prometheus | Мониторинг |
| Resilience4j Retry | Повторные запросы при ошибках |

## Архитектура

Проект состоит из двух Spring Boot приложений и двух баз данных PostgreSQL.

**Основные компоненты:**

1. Inventory API — основной сервис управления товарами, порт `8080`.
2. Inventory PostgreSQL — база данных товаров и пользователей, порт `5432`.
3. Statistics Service — отдельный сервис сбора и предоставления статистики, порт `8081`.
4. Statistics PostgreSQL — отдельная база данных для хранения статистических снимков, порт `5433`.

Statistics Service обращается к Inventory API, получает список товаров, рассчитывает агрегированные показатели и сохраняет результаты в собственной базе данных.

## Требования

Для запуска проекта необходимы:

- Java 21 или выше.
- Docker Desktop.
- Docker Compose.
- Git.

Для запуска через Docker Compose устанавливать PostgreSQL и Maven отдельно не требуется.

## Запуск проекта

### 1. Клонирование репозитория

```bash
git clone https://github.com/AlekseyMakhtey/Inventory-CRUD-API.git
cd Inventory-CRUD-API
```

### 2. Выбор ветки

Для запуска полной версии проекта:

```bash
git switch auth
```

### 3. Настройка переменных окружения

Создайте файл `.env` в корневой директории проекта и задайте необходимые учётные данные.

Пример:

```env
APP_BOOTSTRAP_ADMIN_USERNAME=admin
APP_BOOTSTRAP_ADMIN_PASSWORD=your_admin_password

APP_BOOTSTRAP_VIEWER_USERNAME=viewer
APP_BOOTSTRAP_VIEWER_PASSWORD=your_viewer_password
```

### 4. Запуск Docker Compose

Убедитесь, что Docker Desktop запущен.

Выполните:

```bash
docker compose up -d --build
```

Команда соберёт образы и запустит все сервисы, описанные в `docker-compose.yml`.

### 5. Проверка состояния контейнеров

```bash
docker compose ps
```

Для просмотра логов:

```bash
docker compose logs -f
```

### 6. Доступ к сервисам

| Сервис | Адрес |
|---|---|
| Inventory API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Statistics Service | http://localhost:8081 |
| Statistics Health | http://localhost:8081/actuator/health |
| Prometheus Metrics | http://localhost:8081/actuator/prometheus |

### Остановка проекта

```bash
docker compose down
```

Для остановки с удалением контейнеров и сетей, но с сохранением данных в именованных volumes, используйте ту же команду.

Чтобы также удалить данные баз данных:

```bash
docker compose down -v
```

## Переменные окружения

Для настройки bootstrap-пользователей используются:

| Переменная | Назначение |
|---|---|
| `APP_BOOTSTRAP_ADMIN_USERNAME` | Логин администратора |
| `APP_BOOTSTRAP_ADMIN_PASSWORD` | Пароль администратора |
| `APP_BOOTSTRAP_VIEWER_USERNAME` | Логин пользователя с ограниченными правами |
| `APP_BOOTSTRAP_VIEWER_PASSWORD` | Пароль пользователя с ограниченными правами |

Параметры подключения к базам данных и другие настройки приложения также задаются через конфигурацию Docker Compose и переменные окружения.

Не храните реальные пароли и секреты в публичном репозитории.

## Аутентификация и авторизация

Для доступа к защищённым эндпоинтам Inventory API используется JWT.

### Получение JWT-токена

**Postman request:**

- Method: `POST`
- URL: `http://localhost:8080/api/auth/login`
- Body → raw → JSON

```json
{
    "username": "admin",
    "password": "<ADMIN_PASSWORD>"
}
```

В ответ сервер возвращает токен:

```json
{
    "token": "<JWT_TOKEN>"
}
```

Для последующих запросов необходимо использовать заголовок:

```http
Authorization: Bearer <JWT_TOKEN>
```

### RBAC
Ветка `main` использует разграничение доступа на основе ролей.

Предусмотрены две роли:

| Роль | Возможности |
|---|---|
| `ADMINISTRATOR` | Полный доступ к CRUD |
| `VIEWER` | Просмотр товаров |

### PBAC

Ветка `auth` использует разграничение доступа на основе конкретных разрешений.

Реализованы следующие permissions:

| Permission | Возможность |
|---|---|
| `ITEM_READ` | Просмотр товаров |
| `ITEM_CREATE` | Создание товаров |
| `ITEM_UPDATE` | Обновление товаров |
| `ITEM_DELETE` | Удаление товаров |

Права назначаются пользователям и хранятся в базе данных в отдельных таблицах `permissions` и `user_permissions`.

В стандартной конфигурации:

- `admin` получает все четыре permissions.
- `viewer` получает только `ITEM_READ`.

Таким образом, проверка доступа выполняется на основании разрешений пользователя, а не его роли.

## Работа с Inventory API через Postman

Базовый URL:

```text
http://localhost:8080
```

### 1. Создание товара

- Method: `POST`
- URL: `http://localhost:8080/api/items`
- Authorization: Bearer Token
- Body → raw → JSON

```json
{
    "name": "Wireless Mouse",
    "sku": "MOUSE-001",
    "quantity": 25,
    "price": 19.99,
    "category": "ELECTRONICS",
    "location": "Warehouse A",
    "description": "Wireless computer mouse"
}
```

Ожидаемый статус: `201 Created`.

Требуемое разрешение: `ITEM_CREATE`.

### 2. Получение списка товаров

- Method: `GET`
- URL: `http://localhost:8080/api/items`
- Authorization: Bearer Token

Ожидаемый статус: `200 OK`.

Требуемое разрешение: `ITEM_READ`.

### 3. Получение товара по ID

- Method: `GET`
- URL: `http://localhost:8080/api/items/1`
- Authorization: Bearer Token

Вместо `1` укажите ID существующего товара.

Ожидаемый статус: `200 OK`.

Требуемое разрешение: `ITEM_READ`.

### 4. Обновление товара

- Method: `PUT`
- URL: `http://localhost:8080/api/items/1`
- Authorization: Bearer Token
- Body → raw → JSON

```json
{
    "name": "Updated Wireless Mouse",
    "sku": "MOUSE",
    "quantity": 30,
    "price": 24.99,
    "category": "ELECTRONICS",
    "location": "Warehouse B",
    "description": "Updated wireless mouse"
}
```

Ожидаемый статус: `200 OK`.

Требуемое разрешение: `ITEM_UPDATE`.

### 5. Удаление товара

- Method: `DELETE`
- URL: `http://localhost:8080/api/items/1`
- Authorization: Bearer Token

Ожидаемый статус: `204 No Content`.

Требуемое разрешение: `ITEM_DELETE`.

### Проверка разграничения доступа

При попытке выполнить операцию без необходимого разрешения сервер возвращает:

```http
403 Forbidden
```

Например, пользователь `viewer` может выполнять GET-запросы, но не может создавать, обновлять или удалять товары.

## Фильтрация, пагинация и сортировка

API поддерживает фильтрацию, постраничную выдачу и сортировку результатов.

### Фильтрация

Доступные параметры:

| Параметр | Назначение |
|---|---|
| `name` | Поиск по названию |
| `sku` | Поиск по SKU |
| `category` | Фильтрация по категории |

Пример:

```text
GET http://localhost:8080/api/items?name=mouse&category=ELECTRONICS
```

### Пагинация

Используются параметры:

- `_page` — номер страницы, начиная с 0.
- `_size` — количество элементов на странице (от 1 до 100).

Пример:

```text
GET http://localhost:8080/api/items?_page=0&_size=10
```

### Сортировка

Параметр `_sort` задаёт поле сортировки.

Поддерживаемые поля:

```text
id, name, sku, quantity, price, category, location
```

Пример:

```text
GET http://localhost:8080/api/items?_sort=name
```

Параметры можно комбинировать:

```text
GET http://localhost:8080/api/items?name=mouse&category=ELECTRONICS&_page=0&_size=10&_sort=name
```

## Statistics Service

Statistics Service — отдельное Spring Boot приложение, предназначенное для сбора и хранения статистики по товарам.

### Основные возможности

- Ежедневный автоматический сбор данных в 02:00 UTC.
- Получение списка товаров из Inventory API.
- Расчёт общего количества товаров.
- Подсчёт количества товаров по категориям.
- Сохранение статистических снимков в отдельной PostgreSQL.
- Получение сохранённой статистики за выбранный временной период.

### Пример статистического снимка

```json
{
    "collectedAt": "2026-11-16T17:05:09.190606",
    "totalCount": 11,
    "countPerCategory": {
        "CLOTHING": 1,
        "GROCERY": 1,
        "FURNITURE": 1,
        "ELECTRONICS": 8
    }
}
```

Статистика сохраняется отдельно от основной базы Inventory API, что позволяет разделить ответственность между сервисами.

## Мониторинг и метрики

Для мониторинга `Statistics Service` используются `Micrometer` и `Prometheus`.

Доступные endpoints:

**Health**

```text
GET http://localhost:8081/actuator/health
```

**Prometheus metrics**

```text
GET http://localhost:8081/actuator/prometheus
```
## Retry и обработка временных ошибок

Для повышения устойчивости взаимодействия между сервисами используется `Resilience4j Retry`.

Механизм повторных запросов настроен для операций авторизации и получения товаров через `Inventory API`.

Основные параметры:

- До 3 попыток выполнения запроса.
- `Exponential backoff` — увеличение задержки между повторными попытками.
- `HTTP 4xx` исключены из повторных попыток.

Это позволяет повторять запросы при временных сбоях, не выполняя бесполезные повторы при клиентских ошибках.

## Миграции базы данных

Для управления схемой баз данных используется `Flyway`.

Миграции `Inventory API` находятся в:

```text
src/main/resources/db/migration
```

В ветке `auth` используются:

| Миграция | Назначение |
|---|---|
| `V1__create_inventory_items.sql` | Создание таблицы товаров |
| `V2__create_app_users.sql` | Создание таблицы пользователей |
| `V3__create_permissions.sql` | Создание permissions и связей пользователей с разрешениями |

Миграции применяются автоматически при запуске приложения.

История выполненных миграций хранится в таблице `flyway_schema_history`.

Statistics Service имеет собственные миграции и отдельную базу данных.

## Тестирование

Для запуска автоматических тестов из корневой директории проекта:

**Windows PowerShell:**

```powershell
.\mvnw.cmd clean test
```

**Linux / macOS:**

```bash
./mvnw clean test
```

В проекте используются:

- `JUnit 5` — написание тестов.
- `Mockito` — изоляция зависимостей.
- `MockMvc` — тестирование REST-контроллеров.
- `Spring Boot Test` — интеграционное тестирование приложения.

Тесты `Inventory API` проверяют:

- CRUD-операции.
- HTTP-статусы и обработку ошибок.
- Валидацию входных данных.
- Разграничение доступа.
- Запрет операций без соответствующих permissions.

Последний успешный запуск тестов в ветке `auth`:

```text
Tests run: 19, Failures: 0, Errors: 0, Skipped: 0

BUILD SUCCESS
```

## Swagger UI

Документация Inventory API доступна по адресу:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Swagger UI позволяет просматривать REST-эндпоинты, параметры запросов и схемы данных, а также выполнять запросы непосредственно из браузера.

Для авторизации нажмите **Authorize** и укажите JWT-токен, полученный через `/api/auth/login`.

---

## GitHub

Репозиторий проекта:

[Inventory-CRUD-API](https://github.com/AlekseyMakhtey/Inventory-CRUD-API)

Основные ветки:

- `main` — базовая реализация с RBAC.
- `feature` — статистика, мониторинг и Retry.
- `auth` — полная версия с PBAC.