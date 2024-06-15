# Todo List App

## Opis projektu
Todo List App to aplikacja na Androida pozwalająca na zarządzanie zadaniami. Aplikacja oferuje funkcje takie jak dodawanie, edytowanie, usuwanie zadań oraz możliwość dołączania zdjęć do zadań i wysyłania powiadomień push o statusie zadań.

## Technologie
- Kotlin
- Jetpack Compose
- MVVM (Model-View-ViewModel)
- Android Jetpack (LiveData, ViewModel)

## Funkcjonalności
- Dodawanie nowych zadań
- Edytowanie istniejących zadań
- Usuwanie zadań
- Dołączanie zdjęć do zadań
- Wyświetlanie listy zadań
- Wysyłanie powiadomień push o statusie zadań

## Instrukcje uruchomienia projektu

### Wymagania
- Android Studio Flamingo | 2022.2.1 lub nowszy
- Emulator Androida lub fizyczne urządzenie z systemem Android 5.0 lub nowszym

### Kroki

1. **Klonowanie repozytorium**
    ```sh
    git clone https://github.com/GeorgeousDev/Kotlin-App.git
    ```

2. **Otworzenie projektu w Android Studio**
    - Uruchom Android Studio.
    - Wybierz `Open an existing project`.
    - Wskaż lokalizację folderu `todo-list-app`.

3. **Zbudowanie projektu**
    - Upewnij się, że masz zainstalowane wszystkie wymagane zależności.
    - Kliknij przycisk `Build` lub uruchom `./gradlew build` z linii komend.

4. **Uruchomienie aplikacji**
    - Podłącz urządzenie z Androidem lub uruchom emulator.
    - Kliknij przycisk `Run` lub użyj skrótu klawiaturowego `Shift+F10`.

### Uprawnienia
Aplikacja wymaga następujących uprawnień:
- Kamera: do robienia zdjęć
- Powiadomienia: do wysyłania powiadomień push

### Uwagi
- Upewnij się, że nadałeś aplikacji odpowiednie uprawnienia, gdy pojawi się prośba o ich przyznanie.
