package com.aspire.dicmp.component.guava;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.or;
import static com.google.common.collect.Sets.newHashSet;

/**
 * 使用Guava简化集合的使用
 * Copyright © 2008   卓望公司
 * package: com.aspire.dicmp.component.guava
 * fileName: GuavaForCollections.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/09/20 11:08
 */
public class GuavaForCollections {

    public static void main(String[] args) {

        // 使用Maps构建map
        Map<String, Map<Long, List<String>>> map = Maps.newHashMap();
        List<String> list = Lists.newArrayList();
        Set<String> set = newHashSet();

        // 构建不可变list，map
        ImmutableMap<String, String> immutableMapmap = ImmutableMap.of("key1", "value1", "key2", "value2");
        ImmutableList<String> immutableListlist = ImmutableList.of("a", "b", "c", "dd", "ee");

        // Files读取文件
//        File file = new File("a.txt");
//        List<String> lines = null;
//        try {
//            lines = Files.readLines(file, Charsets.UTF_8);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // 基本类型比较
        System.out.println(Ints.compare(3, 6));

        // list转换成数组
//        int[] array2 = Ints.toArray(list);

        // CharMatcher
        // 移除字符串
        System.out.println(CharMatcher.whitespace().removeFrom("aspire to inspire"));
        // 移除字符串中从a-h的所有字母
        System.out.println(CharMatcher.inRange('a', 'h').removeFrom("aspire to inspire"));


        // Joiner
        String[] subdirs = {"usr", "local", "lib"};
        System.out.println(Joiner.on("/").join(subdirs));

        int[] numbers = {1, 2, 3, 4, 5};
        System.out.println(Joiner.on(",").join(Ints.asList(numbers)));


        //Splitter 字符串切割
        String testString = "foo , what,,,more,";
        System.out.println(Splitter.on(",").omitEmptyStrings().trimResults().split(testString));

        // 数组最大值
        int[] array = {1, 2, 3, 4, 5};
        System.out.println(Ints.indexOf(array, 6));
        System.out.println(Ints.max(array));
        System.out.println(Ints.min(array));

        //函数式编程
        ImmutableMap<String, Double> eurPriceMap = ImmutableMap.of("sunke", 454.98, "bnmyni", 993.4);
        Map usdPriceMap = Maps.transformValues(eurPriceMap, new Function<Double, Double>() {
            double eurToUsd = 1.4888;

            @Override
            public Double apply(@Nullable Double aDouble) {
                return eurToUsd * aDouble;
            }
        });

        System.out.println(usdPriceMap);


        // 集合过滤 也可以通过实现Predicate的方式扩展过滤条件
        List<String> names = Lists.newArrayList("Aleksander", "Jaran", "Integrasco", "Guava", "Java");
        Iterable<String> filtered = Iterables.filter(names, or(equalTo("Aleksander")));
        System.out.println(filtered);


        List<Person> persons = Lists.newArrayList(new Person("ke", "sun"), new Person("xg", "kang"));
        Comparator<Person> byLastName = new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                return o1.getLastName().compareTo(o2.getLastName());
            }
        };

        Comparator<Person> byFirstName = new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                return o1.getFirstName().compareTo(o2.getFirstName());
            }
        };
        // 排序后的列表
        System.out.println(Ordering.from(byLastName).compound(byFirstName).reverse().sortedCopy(persons));


        //  集合交并差
        HashSet setA = newHashSet(1, 2, 3, 4, 5);
        HashSet setB = newHashSet(4, 5, 6, 7, 8);
        System.out.println("集合交并差结果");
        System.out.println(Sets.union(setA, setB));
        System.out.println(Sets.difference(setA, setB));
        System.out.println(Sets.intersection(setA, setB));

        //Preconditions 校验
//        int count = 20;
//        Person p = null;
//        Preconditions.checkArgument(count < 30, "count太大了");
//        Preconditions.checkNotNull(p);

        // 一个key对应多个value的情况
        Multimap<Person, BlogPost> multimap = ArrayListMultimap.create();
        multimap.put(new Person("sun", "ke"), new BlogPost("blog 1"));
        multimap.put(new Person("sun", "ke"), new BlogPost("blog 2"));
        multimap.forEach((k, v) -> System.out.println(k.getFirstName() + k.getLastName() + ":" + v.getContent()));


        List<Map<String, String>> muList = Lists.newArrayList();
        ImmutableMap.of("type", "blog", "key2", "value2");
        if (true) {
            throw new RuntimeException();
        }


        System.out.println("");
    }


}

class Person {
    private String firstName;

    private String lastName;

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

class BlogPost {

    private String content;

    public BlogPost(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
