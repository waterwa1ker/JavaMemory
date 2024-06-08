# JavaMemory

![Static Badge](https://img.shields.io/badge/java-blue)
![Static Badge](https://img.shields.io/badge/visualvm-blue)
![Static Badge](https://img.shields.io/badge/GCViewer-blue)

## Кейсы с неправильным использованием памяти

Для отображения использования памяти, работы Garbage Collector будем использовать [VisualVM](https://visualvm.github.io/download.html). Для визуализации логов [GCViewer](https://github.com/chewiebug/GCViewer/wiki/Changelog).

### Static поля

Создадим класс Leak, который будет добавлять в статический список множество элементов:

```java
public class Leak {

    private static final List<Double> leaks = new ArrayList<>();

    public void addNumbers() {
        for (int i = 0; i < 10_000_000; ++i) {
            leaks.add(Math.random());
        }
    }

}
```

Для удобства используем плагин в IntelliJ Idea [VisualVM](https://plugins.jetbrains.com/plugin/7115-visualvm-launcher/):

Так же эндпоинт, который вызывает метод addNumbers:

```java
@RestController
public class SimpleController {

    @GetMapping
    public void leak() {
        Leak leak = new Leak();
        leak.addNumbers();
    }

}
```

Запускаем приложение, смотрим распределение памяти до вызова метода addNumbers():

![before-memory-allocation](./images/before-memory-allocation.png)

После вызова localhost:8080:

![after-memory-allocation](./images/after-memory-allocation.png)

Видно, что сборщик мусора не освобождает память, так как static поля действуют на **протяжении всего действия приложения**
