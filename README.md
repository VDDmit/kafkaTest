# Уведомления на основе событий с использованием Kafka

## Описание проекта

Проект реализует распределённую систему обработки событий с использованием Kafka для передачи, обработки и хранения уведомлений. Цель проекта — освоить работу с Kafka, включая продюсирование и консюмирование сообщений, использование партиций, настройку групп, а также обработку ошибок.

## Функциональные требования

### 1. Управление уведомлениями
- **Отправка уведомлений через API**.
- **Типы уведомлений**:
  - Email.
  - SMS.
  - Push-уведомления.
- Каждое уведомление должно содержать:
  - ID пользователя.
  - Тип уведомления.
  - Контент.

### 2. Архитектура потоков данных
- Сообщения отправляются в разные топики на основе типа уведомления.
- Консумеры читают сообщения из соответствующих топиков:
  - Консумер для email.
  - Консумер для SMS.
  - Консумер для push-уведомлений.

### 3. Обработка ошибок
- Если обработка сообщения консумером завершается ошибкой:
  - Сообщение отправляется в отдельный топик (dead-letter queue).
  - Логируются подробности ошибки.

### 4. Мониторинг
- Реализовать REST API для мониторинга состояния топиков:
  - Количество непрочитанных сообщений.
  - Список обработанных сообщений.

## Технические требования

### 1. Kafka
- Минимум три топика:
  - `email-topic`
  - `sms-topic`
  - `push-topic`
- **Dead-letter queue** для ошибок.
- Использовать следующие настройки:
  - Партиции для каждого топика (минимум 3).
  - Репликация (минимум фактор репликации 2).

### 2. API
- Использовать Spring Boot для реализации REST API:
  - `POST /api/notifications/send` — отправка уведомления.
  - `GET /api/notifications/stats` — получение статистики.

### 3. Producer
- Один общий продюсер для отправки сообщений в разные топики.

### 4. Consumer
- Разные консумеры для каждого типа уведомлений.
- Группа консумеров для обработки push-уведомлений (как минимум 2 консумера).

### 5. Обработка ошибок
- В случае ошибки при обработке уведомления:
  - Логировать сообщение и причину ошибки.
  - Отправлять сообщение в **dead-letter-topic**.

### 6. Логирование
- Использовать **SLF4J** для детализированного логирования всех операций.

## Инструменты и технологии

- **Kafka**
  - Docker-образ Kafka для развертывания.
  - Kafka UI для управления и мониторинга топиков.
- **Spring Boot**
  - Spring Kafka для взаимодействия с Kafka.
  - Spring Web для реализации REST API.
- **База данных**
  - PostgreSQL для хранения статистики обработанных уведомлений.
- **Тестирование**
  - JUnit и Kafka Test для юнит-тестов и интеграционных тестов.

## Задачи

1. Развернуть Kafka и Kafka UI с использованием Docker.
2. Настроить Spring Boot приложение для взаимодействия с Kafka.
3. Реализовать продюсер для отправки уведомлений.
4. Реализовать консумеры для обработки сообщений.
5. Настроить обработку ошибок и dead-letter queue.
6. Реализовать REST API для отправки уведомлений и мониторинга.
7. Написать тесты для продюсеров и консумеров.

## Дополнительное задание

Добавить функциональность планирования уведомлений. Например, при отправке уведомления задаётся время его отправки, и сообщение помещается в Kafka с использованием задержки.
