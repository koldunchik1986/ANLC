# Анализ map.js

**Расположение:** `abclient/Js/map.js`

**Назначение:**
Этот JavaScript-файл отвечает за отображение карты в клиенте ABClient, взаимодействие с элементами карты, обработку перемещений и отображение различной информации, связанной с игровым миром. Он активно использует интерфейс `window.external` для взаимодействия с C#-логикой приложения.

**Ключевые особенности:**

*   **Инициализация карты:** При загрузке страницы `map.js` инициализирует параметры карты, такие как ширина, высота и масштаб, получая их из C# через `window.external.GetHalfMapWidth()`, `window.external.GetHalfMapHeight()` и `window.external.GetMapScale()`.
*   **Отображение карты:** Функции `view_map()` и `showMap()` динамически создают HTML-элементы для отображения карты, включая ячейки, изображения и текстовые наложения.
*   **Взаимодействие с C#:** Файл содержит многочисленные вызовы к `window.external`, что позволяет JavaScript получать данные из C# и вызывать методы C# для выполнения различных действий, таких как:
    *   Получение информации о состоянии игры (`UsersOnline()`, `IsAutoFish()`, `DoHerbAutoCut()`, `ShowOverWarning()`).
    *   Управление элементами интерфейса (`SetNeverTimer()`, `InsertGuaDiv()`, `SetAutoFishMassa()`, `SetFishNoCaptchaReady()`).
    *   Обработка действий игрока (`MoveTo()`, `TraceCut()`).
    *   Получение данных о ячейках карты (`IsCellExists()`, `CellAltText()`, `CellDivText()`, `GenMoveLink()`).
    *   Получение текстовой информации (`MapText()`).
    *   Управление инвентарем и ресурсами (`HerbsList()`, `CheckPri()`, `FishOverload()`).
*   **Обработка перемещений:** Функции `move()`, `loadMap()`, `freeMap()` и `loadPath()` отвечают за плавное перемещение по карте, динамическую подгрузку и выгрузку частей карты.
*   **Таймеры и события:** Файл управляет игровыми таймерами и реагирует на различные события, такие как завершение навигации или изменение состояния игры.

**Вызовы `window.external`:**

Ниже приведен список всех обнаруженных вызовов `window.external` в `map.js` с кратким описанием их предполагаемого назначения:

*   `window.external.GetHalfMapWidth()`: Получает половину ширины карты.
*   `window.external.GetHalfMapHeight()`: Получает половину высоты карты.
*   `window.external.GetMapScale()`: Получает масштаб карты.
*   `window.external.UsersOnline()`: Возвращает HTML-строку с информацией о количестве пользователей онлайн.
*   `window.external.DoHerbAutoCut()`: Проверяет, включена ли функция автоматического сбора трав.
*   `window.external.IsCellExists(dx, dy)`: Проверяет, существует ли ячейка карты с заданными координатами.
*   `window.external.CellAltText(dx, dy, scale)`: Возвращает альтернативный текст для ячейки карты.
*   `window.external.MoveTo(window.external.GenMoveLink(dx, dy))`: Перемещает персонажа в указанное место, используя сгенерированную ссылку.
*   `window.external.CellDivText(dx, dy, scale, img.onclick, false, isframe)`: Возвращает HTML-строку для отображения информации о ячейке карты.
*   `window.external.SetNeverTimer(time_left_sec)`: Устанавливает таймер.
*   `window.external.MapText()`: Возвращает текст, связанный с картой.
*   `window.external.HerbsList(abcingr)`: Передает список трав в C#-приложение.
*   `window.external.SetAutoFishMassa(ingr[3] + '/' + ingr[4])`: Устанавливает массу для авторыбалки.
*   `window.external.CheckPri(ingr[i][1], ingr[i][2])`: Проверяет приоритет.
*   `window.external.InsertGuaDiv(ingr[1])`: Вставляет HTML-код, возможно, связанный с капчей или специальными элементами интерфейса.
*   `window.external.FishOverload()`: Уведомляет C#-приложение о перегрузке рыбы.
*   `window.external.IsAutoFish()`: Проверяет, включена ли функция авторыбалки.
*   `window.external.SetFishNoCaptchaReady()`: Уведомляет C#-приложение о готовности к авторыбалке без капчи.
*   `window.external.ShowOverWarning()`: Проверяет, нужно ли показывать предупреждение о перегрузке.
*   `window.external.TraceCut(name)`: Отслеживает действие "срезать" (вероятно, сбор ресурсов).
*   `window.external.GenMoveLink(x, y)`: Генерирует ссылку для перемещения на указанные координаты.

**Связь с Android-портом:**

Все эти вызовы `window.external` должны быть реализованы в Android-приложении как методы Kotlin, аннотированные `@JavascriptInterface`. Логика, выполняемая этими методами в C#, должна быть перенесена в соответствующие Kotlin-классы. Особое внимание следует уделить методам, возвращающим HTML-строки, так как они напрямую влияют на отображение в WebView.
