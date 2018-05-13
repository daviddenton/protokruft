# protokruft

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

Sprinkle on some Protokruft, and you can use it like this:
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

## Get it:
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

    // this block is optional - by default, protokruft will generate dsls for all found protobuf messages
    protokruft {
        packageNames = ["com.mygreatpackage"] // this is "*" by default
        outputClassFile = "myCustomFile" // this is "messageDsl" by default
    }
```