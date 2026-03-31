# Checkout System

System do obsługi koszyka zakupowego z obsługą promocji.

## Features

* Dodawanie produktów do koszyka
* Obliczanie ceny całkowitej
* Obsługa promocji (np. 3 za 90, promocje łączone produktów)
* Testy jednostkowe i integracyjne

## Tech Stack

* Java
* Spring Boot
* Maven
* JUnit

## Run locally

1. Sklonuj repo:
   git clone https://github.com/Zioberus/checkout-system.git

2. Wejdź do folderu:
   cd checkout-system

3. Uruchom aplikację:
   mvn spring-boot:run

## Tests

Uruchomienie testów:

mvn test
Bądź uruchamianie pilku lokalnie

## DATA
Item Normal Price Required Quantity Special Price
A           40             3               30
B           10             2               7.5
C           30             4               20
D           25             2               23.5

## Strategy to calculate final price
1. Promocja parowa (A lub B) z (C lub D)
2. Procmocja iloścowa
3. Pozostałe przedmioty po normalnej cenie
   




