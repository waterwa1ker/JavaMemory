# JavaMemory

![Static Badge](https://img.shields.io/badge/Java-blue)
![Static Badge](https://img.shields.io/badge/Visualvm-blue)
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

#### Работа с логами и их визуализация

Для того, чтобы вывод работы Garbage Collector записывался в логи необходимо прописать следующий VM options:

![vm-options](./images/vm-options.png)

Примерное содержимое файла:

```bash
[0.005s][info][gc] Using G1
[0.753s][info][gc] GC(0) Pause Young (Normal) (G1 Evacuation Pause) 23M->6M(236M) 5.732ms
[1.148s][info][gc] GC(1) Pause Young (Concurrent Start) (CodeCache GC Threshold) 31M->8M(236M) 7.941ms
[1.148s][info][gc] GC(2) Concurrent Mark Cycle
[1.156s][info][gc] GC(2) Pause Remark 10M->10M(54M) 2.140ms
[1.158s][info][gc] GC(2) Pause Cleanup 10M->10M(54M) 0.110ms
[1.159s][info][gc] GC(2) Concurrent Mark Cycle 10.854ms
[2.142s][info][gc] GC(3) Pause Young (Prepare Mixed) (G1 Evacu
```

Для визуализации логов будем использовать [GCViewer](https://github.com/chewiebug/GCViewer/wiki/Changelog)

Запуск GCViewer:

```bash
java -jar {GCVIEWER_FILENAME}.jar
```

Находим наш лог, полуаем следующий картину:

![gcviewer-insta](./images/gcviewer-insta.png)

**Красный столбик** - общее количество выделенного памяти в куче.

**Синим цветом** - затраченная память.

Вызовем метод и посмотрим на распределение памяти:

![gcviewer-after](./images/gcviewer-after.png)

Видно, что потребление памяти стало больше, и сборщик мусора **не будет** очищать выделенную память для статического списка.

## Внутренние классы

Дополним класс Leak, добавив внутренний класс InnerLeak:

```java
   public InnerLeak createInnerLeak() {
        return new InnerLeak();
    }

    public class InnerLeak {

    }
```

Добавим эндпоинт с созданием 10 экземпляров класса InnerLeak:

```java
@GetMapping("/inner")
    public void createInnerInstance() throws InterruptedException {
        for (int i = 0; i < ITERATION_COUNT; ++i) {
            Leak leak = new Leak();
            innerLeaks.add(leak.createInnerLeak());
            Thread.sleep(SECOND_IN_MILLISECONDS);
        }
    }
```

Смотрим на распределение памяти: видно, что после вызова эндпоинта (отмечено курсором) было выделено 10 раз память на создание объекта, но сборщик не очищает память.

![inner-leaks](./images/inner-leaks.png)

## Ключи в Map

Добавим метод, который добавляет в словарь 10 значений, ключами которого является объект класса LeakKey:

```java
public class LeakKey {

    private String value;

    public LeakKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
```
Добавляем метод, который заполняет наш словарь:

```java
private Map<LeakKey, Double> map = new HashMap<>();

    public void addValues() {
        for (int i = 0; i < ITERATION_COUNT; ++i) {
            map.put(new LeakKey("Value"), Math.random());
        }
    }
```

Важным моментом является наличие переопределенных equals-hashcode в классе LeakKey. Распределение памяти будет очень сильно отличаться в двух разных сценариях:

Без переопределенных методов:

![map-without-equals-hashcode](./images/map-without-equals-hashcode.png)

С переопределенными методами:

![map-with-equals-hashcode](./images/map-with-equals-hashcode.png)

**Без переопределенных методов** в словарь будет добавлено 10 элементов с одинаковыми значениями, **с переопределенными** - предыдущий элемент будет заменен (с очисткой памяти).

## References

Благодарность этому [материалу](https://www.youtube.com/watch?v=IUUoMVaXzas) и [статье](https://javarush.com/groups/posts/6518-kofe-breyk-264-utechki-pamjati-java-kak-ikh-obnaruzhitjh-i-predotvratitjh).
