package com.company;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        CreateFunction();
        WaysOfComparator();
        CompareJDK7And8();
        TestListAndMap();

        TestStreamPatterns();

        List<Person> persons = GetPersonList();
        persons.stream().map(p -> p.getAge())
                .filter(age -> age > 2)
                .forEach(System.out::println);

        persons.stream()
                .filter(p -> p.getAge() > 2)
                .forEach(System.out::println);

        //peek can used for debugging purpose
        persons.stream().map(p -> p.getAge())
                .peek(System.out::println)
                .filter(age -> age > 2)
                .forEach(System.out::println);

        //will not work, peek is an intermediate call, foreach is a terminal call
        //terminal operation must be called to trigger the processing of stream
        //the call return s a Stream is a intermediate call.
        persons.stream().map(p -> p.getAge())
                .peek(System.out::println)
                .filter(age -> age > 2)
                .peek(System.out::println);

        //skip 2 element, only keep 3 elements
        persons.stream()
                .skip(2)
                .limit(3)
                .filter(p -> p.getAge() > 2)
                .forEach(System.out::println);

        //simple reductions: Match, Find, Reduce
        //anymatch, allmatch, nonematch, terminal operations return boolean
        persons.stream().anyMatch(p -> p.getAge() > 2);
        persons.stream().allMatch(p -> p.getAge() > 2);
        persons.stream().noneMatch(p -> p.getAge() > 2);

        //findfirst, findany
        Optional<Person> opt1 = persons.stream().filter(p -> p.getAge() > 2).findAny();
        Optional<Person> opt2 = persons.stream().filter(p -> p.getAge() > 2).findFirst();
        Optional<Person> opt3 = persons.stream().filter(p -> p.getAge() > 200).findFirst();
        //return optional in case the stream is empty or no value matchs the reduction
        System.out.println(opt1.get());
        System.out.println(opt2.get());
        if (opt3.isPresent())
            System.out.println(opt3.get());


        //Reduce reduction
        //if no identity element is provided, then Optional is returned
        //Associativity is assumed not guaranteed
        int sumOfAges = persons.stream()
                .map(p -> p.getAge())
                .reduce(0, (age1, age2) -> age1 + age2);

        int maxOfAges = persons.stream()
                .map(p -> p.getAge())
                .reduce(0, (age1, age2) -> Integer.max(age1, age2));

        Optional<Integer> opt4 = persons.stream()
                .map(p -> p.getAge())
                .reduce((age1, age2) -> Integer.max(age1, age2));

        //reduce used in parallel operations
        //Identity element, accumulator, combiner (how to combine the persons from CPUs)
        List<Integer> ages =
                persons.stream().reduce(
                                new ArrayList<Integer>(),
                                (list1, p) -> {list1.add(p.getAge());return list1;},
                                (list1, list2) -> {list1.addAll(list2);return list1;}
                        );
        ages.forEach(System.out::println);
    }

    private static void TestStreamPatterns() throws IOException {
        GetPersonList();

        //an empty stream
        Stream.empty();
        //a singleton stream
        Stream.of("one");
        //a stream with several elements
        Stream.of("one", "two", "three");
        //a constant stream
        Stream.generate(() -> "one");
        //a growing stream
        Stream.iterate("+", s -> s + "+");
        //a random Stream
        ThreadLocalRandom.current().ints();
        //a Stream on the letters of a String
        IntStream stream = "hello".chars();
        //a stream on a regular expression
        Stream<String> words = Pattern.compile("[^\\p{javaLetter}]").splitAsStream("book");
        //a stream on the lines of a text file
//        Stream<String> lines = Files.lines(...);

        //first build a Stream.Builder
        Stream.Builder<String> builder = Stream.builder();
        //by chaining the add() method
        builder.add("one").add("two").add("three");
        builder.accept("four");
        Stream<String> stream2 = builder.build();
        stream.forEach(System.out::println);
        //will throw an exception on an add() or accept() call after build method
    }

    private static List<Person> GetPersonList() {
        Person p1 = new Person("h7", "7", 7);
        Person p2 = new Person("a1", "1", 1);
        Person p3 = new Person("c3", "3", 3);
        Person p4 = new Person("d4", "4", 4);
        Person p5 = new Person("b2", "2", 2);
        Person p6 = new Person("e5", "5", 5);
        Person p7 = new Person("f6", "6", 6);
        Person p8 = new Person("j8", "8", 8);

        return Arrays.asList(
                p1, p2, p3, p4, p5, p6, p7, p8
        );
    }

    private static void TestListAndMap() {
        Person p1 = new Person("h7", "7", 7);
        Person p2 = new Person("a1", "1", 1);
        Person p3 = new Person("c3", "3", 3);
        Person p4 = new Person("d4", "4", 4);
        Person p5 = new Person("b2", "2", 2);
        Person p6 = new Person("e5", "5", 5);
        Person p7 = new Person("f6", "6", 6);
        Person p8 = new Person("j8", "8", 8);

        List<Person> personList = Arrays.asList(
                p1, p2, p3, p4, p5, p6, p7, p8
        );

        //personList.removeIf(p -> p.getAge() < 5);
        System.out.println("==============================================");
        personList.replaceAll(person -> new Person(person.getFirstName().toUpperCase(), person.getLastName().toUpperCase(), person.getAge()));
        personList.forEach(System.out::println);
        System.out.println("==============================================");
        personList.sort(Comparator.comparing(Person::getAge).thenComparing(Person::getLastName).reversed());
        personList.forEach(System.out::println);

        City newYork = new City("New York");
        City shangHai = new City("Shanghai");
        City paris = new City("Paris Fr");

        Map<City, List<Person>> map1 = new HashMap<>();
        map1.putIfAbsent(paris, new ArrayList<>());
        map1.get(paris).add(p1);
        map1.computeIfAbsent(newYork, city -> new ArrayList<>()).add(p7);
        map1.computeIfAbsent(newYork, city -> new ArrayList<>()).add(p3);

        Map<City, List<Person>> map2 = new HashMap<>();
        map2.putIfAbsent(paris, new ArrayList<>());
        map2.get(paris).add(p4);
        map2.get(paris).add(p6);
        map2.computeIfAbsent(newYork, city -> new ArrayList<>()).add(p2);
        map2.computeIfAbsent(newYork, city -> new ArrayList<>()).add(p5);
        map2.computeIfAbsent(shangHai, city -> new ArrayList<>()).add(p8);

        map1.forEach((city, person) -> map2.merge(city, person, (personFromMap1, personFromMap2) -> {
            personFromMap2.addAll(personFromMap1);
            return personFromMap2;
        }));
        System.out.println("==============================================");
        map2.forEach((city, person) -> System.out.println(city.getName() + "\t:" + person));
    }

    private static void CompareJDK7And8() {
        //JDK 7, create anonymous class
        Predicate<String> p1 = new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return s.length() < 20;
            }
        };
        //JDK8
        Predicate<String> p2 = s -> s.length() < 20;
    }

    private static void WaysOfComparator() {
        // write your code here
        //Comparator is a functional interface
        Comparator<Person> cmpAge = (p1, p2) -> p2.getAge() - p1.getAge();
        Comparator<Person> cmpFirstName = (p1, p2) -> p2.getFirstName().compareTo(p1.getFirstName());
        Comparator<Person> cmpLastName = (p1, p2) -> p2.getLastName().compareTo(p1.getLastName());

        Function<Person, Integer> f1 = p -> p.getAge();
        Function<Person, String> f2 = p -> p.getFirstName();
        Function<Person, String> f3 = p -> p.getLastName();
        Comparator<Person> cmpPerson = Comparator.comparing(f1);
        Comparator<Person> cmpPersonAge = Comparator.comparing(Person::getAge);
        Comparator<Person> cmpPersonLastName = Comparator.comparing(Person::getLastName);

        Comparator<Person> cmp = cmpPersonAge.thenComparing(cmpPersonLastName);
        Comparator<Person> cmp2 = cmp.thenComparing(Person::getFirstName);
    }

    private static void CreateFunction() {
        Comparator<String> comparator =
                (s1, s2) ->
                        Integer.compare(s1.length(), s2.length());

        Runnable r = () -> {
            int i = 0;
            while (i++ < 10) {
                System.out.print("haha");
            }
        };

        Function<Person, Integer> f1 = person -> person.getAge();
        Function<Person, Integer> f2 = Person::getAge;

        BinaryOperator<Integer> sum = (i1, i2) -> i1 + i2;
        BinaryOperator<Integer> sum2 = Integer::sum;
        BinaryOperator<Integer> max = Integer::max;

        Consumer<String> printer = System.out::println;
        Supplier<Person> personSupplier = Person::new;

        Predicate<Person> ageGT20 = person -> person.getAge() > 20;
    }
}