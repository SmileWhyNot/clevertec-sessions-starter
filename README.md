# Clevertec Session Manager Spring Boot Starter

Session Manager Spring Boot Starter - это проект стартер, предоставляющий удобные инструменты для работы с сессиями в приложениях Spring Boot. 
Стартер включает в себя аннотацию `@SessionManager` для легкой интеграции сессий в ваши методы.
В проекте также реализован сервис сессий, который предоставляет сессии стартеру, который далее внедряет их в аннотированные методы.

## Как начать

1. **Настройка стартера в файле конфигурации:**

   В файле `application.yml` вашего проекта укажите параметры для стартера:

   ```yaml
   session:
     manager:
       enable: true
       black-list:
         - blockedLogin1
         - blockedLogin2
       black-list-providers:
         - vlad.kuchuk.service.DefaultBlackListProvider
         - vlad.kuchuk.blackList.FileBlackListProvider
       session-provider-url: "http://localhost:8181/api/v1/sessions"
   ```

   Параметр `enable: true` включает стартер. Вы также можете настроить черный список логинов и провайдеры черного списка.
   session-provider-url параметр предоставляет возможность настроить подключение к сервису сессий.

2. **Использование аннотации `@SessionManager`:**

   Примените аннотацию к вашим методам:

   ```java
   @SessionManager(blackList = {PropertySourceHandler.class})
   public void work(Obj obj1, Obj obj2, Obj obj3...){
   }
   ```

   Где `Obj` - это объект, который содержит информацию о сессии (айдишник, логин, время открытия сессии) - объект стартера Session, и еще один объект, который содержит в себе логин - любой класс реализующий интерфейс AuthInfo.

   Аннотация `@SessionManager` позволяет автоматически обращаться к сервису сессий, создавать или получать сессию и внедрять ее в параметр метода (Внутрь объекта Session session).

3. **Кастомизация черного списка:**

   Вы можете создать свой кастомный провайдер черного списка, реализовав интерфейс `BlackListProvider`. Пример использования:

   ```java
   public class CustomBlackListProvider implements BlackListProvider {
       // Реализация методов интерфейса
   }
   ```

   Укажите его в параметрах аннотации `@SessionManager` или в файле `application.yml`:

   ```java
   @SessionManager(blackListProviders = CustomBlackListProvider.class)
   public void customMethod(Obj obj1, Obj obj2, Obj obj3...){
   }
   ```
   
4. **Черный список логинов можно также передать напрямую в аннотацию.** 
Пример использования:

   ```java
    @SessionManager(blackList = {"Alice", "Nata"})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserResponse save(@RequestBody @Valid UserRequest request, Session session) {
    return userService.save(new UserRequest(request.login(), request.password(), request.name()
    , session.openingTime()));
    }
   ```

5. **Сессии предоставляет модуль [session-service](session-service). Он должен быть запущен, для корректной работы стартера.**

6. **Создайте базу данных и настройте подключение в файлах `module-for-testing/../application.yml` и `session-service/../application.yml)`, для корректного выполнения liquibase миграций.** 

7. **В предоставленном сервисе сессий реализована очистка сессий раз в сутки. Частоту очистки сессий можно изменить в файле `session-service/../application.yml)` сервиса сессий** 
Пример:
```yaml
session:
  cleanup:
    enabled: false
    frequency: "0 * * * * ?" # Каждую минуту
```

## Описание проекта

Проект включает три модуля:

1. **session-starter-spring-boot:**
   Стартовый модуль, который предоставляет функциональность стартера. Воспользуйтесь Gradle publishToMavenLocal и далее добавьте зависимость в ваш проект для использования стартера.

2. **session-service:**
   Модуль, предоставляющий сервис сессий. Взаимодействует со стартером и предоставляет сессии по запросу. Обязательно должен быть запущен!
Иначе, должен быть реализован свой провайдер сессий и ссылка на него указана в `session-starter-spring-boot/../application.yml`

3. **module-for-testing:**
   Пример пользовательского проекта, включающего зависимость стартера. Настроен стартер в `application.yml`, представлен пример кастомного провайдера черного списка, получающий заблокированные логины из файла.
Также, предоставлена коллекция Postman в качестве примеров запросов на маппинги, использующие стартер.

## Как воспользоваться

1. **Опубликуйте стартер локально с помощью Gradle - publishToMavenLocal**

1. **Установите зависимость в вашем проекте:**
   ```groovy
   implementation 'vlad.kuchuk:session-starter-spring-boot:1.0.0'
   ```

2. **Настройте стартер в `application.yml`:**
   ```yaml
   session:
     manager:
       enable: true
       black-list:
         - block1
         - block2
       black-list-providers:
         - vlad.kuchuk.service.DefaultBlackListProvider
         - vlad.kuchuk.blackList.FileBlackListProvider
       session-provider-url: "http://localhost:8181/api/v1/sessions"
   ```

3. **Используйте аннотацию `@SessionManager`:**
   ```java
   @SessionManager(blackList = "Alice", blackListProviders = CustomBlackListProvider.class, includeDefaultBlackListSource=false)
   public void customMethod(Obj obj1, Obj obj2, Obj obj3...){
   }
   ```

## Clevertec

Этот проект реализован в качестве задания с курса при компании Clevertec.