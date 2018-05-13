[![coverage](https://coveralls.io/repos/daviddenton/protokruft/badge.svg?branch=master)](https://coveralls.io/github/daviddenton/protokruft?branch=master)
[![kotlin](https://img.shields.io/badge/kotlin-1.2-blue.svg)](http://kotlinlang.org)
[![build status](https://travis-ci.org/daviddenton/protokruft.svg?branch=master)](https://travis-ci.org/daviddenton/protokruft)
[![bintray version](https://api.bintray.com/packages/daviddenton/maven/protokruft/images/download.svg)](https://bintray.com/daviddenton/maven/protokruft/_latestVersion)

Protokruft is a simple Gradle plugin for generating a Kotlin DSL from generated Java Protobuf message source. It removes the worst of the Java boilerplate that is generated, leaving you with a nice clean syntax in your Kotlin. The target classes are those which extend `GeneratedMessageV3`. By default, the generated classes are put next to the Java files, into the `build/generated/source/proto/main/java` folder.

## What does it do?
Given this proto:
```proto
syntax = "proto3";

option java_package = "org.protokruft.example1";
package AnExample1;

message Car {
    string model = 1;
    Engine engine = 2;
}

message Engine {
    int32 cc = 1;
    int32 bhp = 2;
}
```
The generated Java code from the Google protoc would be used like this:

```kotlin
    val person = Person.newBuilder()
            .setName("Hello Kitty")
            .setAddress(
                    Address.newBuilder()
                            .setNumber(123)
                            .setStreet("Hello Kitty Street")
                            .setPostcode("N304SD")
                            .build())
            .build()
```

Sprinkle on some Protokruft, and you can use it like this to hide the `newBuilder().build()` boilerplate:
```kotlin
    val person = newPerson {
        name = "Hello Kitty"
        address = newAddress {
            number = 123
            street = "Hello Kitty Street"
            postcode = "N304SD"
        }
    }
```
... or like this to scope the `Builder` as `it`: 
```kotlin    
    val personScoped = newPerson.also {
        it.name = "Hello Kitty"
        it.address = newAddress.also {
            it.number = 123
            it.street = "Hello Kitty Street"
            it.postcode = "N304SD"
        }
    }
```

## Use it:
Add the following to your Gradle file:
```groovy
    buildscript {
        dependencies {
            classpath 'org.protokruft:protokruft:0.0.8'
            classpath 'com.google.protobuf:protobuf-gradle-plugin:0.8.3'
        }
    }

    repositories {
        jcenter()
    }

    apply plugin: 'protokruft'

    protokruft {
        packageNames = ["com.mygreatpackage"] // "*" by default
        outputClassFile = "myCustomFile" // "messageDsl" by default
    }

    // allows you to just run generateProtoDsl
    generateProtobufDsl.dependsOn('generateProto')    
```

Then just run: `./gradlew generateProtobufDsl`
