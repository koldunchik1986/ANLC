# Анализ AppVars.cs

**Расположение:** `abclient/AppVars.cs`

**Назначение:**
Класс `AppVars` служит центральным хранилищем для глобальных статических переменных, настроек и временных данных, используемых во всем приложении ABClient. Он инкапсулирует состояние приложения, обеспечивая доступ к общим данным из различных модулей.

**Ключевые разделы и переменные:**

*   **Информация о приложении:**
    *   `AppVersion (VersionClass)`: Версия приложения.
    *   `Codepage (Encoding)`: Русская кодовая страница (Windows-1251).
    *   `Culture (CultureInfo)`: Русская культура (`ru-RU`).
    *   `EnUsCulture (CultureInfo)`: Английская культура (`en-US`).

*   **Основные компоненты:**
    *   `Profile (UserConfig)`: Рабочий профиль пользователя, содержащий его настройки и данные.
    *   `LocalProxy (WebProxy)`: Локальный прокси-сервер, используемый приложением.
    *   `MainForm (FormMain)`: Главная форма приложения, обеспечивающая доступ к UI-элементам и методам.

*   **Связанные с боем:**
    *   `FightLink (string)`: Ссылка для завершения боя.
    *   `LastBoiLog (string)`: Код последнего обработанного боя.
    *   `LastBoiSostav (string)`: Состав последнего боя.
    *   `LastBoiTravm (string)`: Травматичность последнего боя.
    *   `LastBoiTimer (DateTime)`: Время начала последнего боя.
    *   `FastNeed (bool)`: Флаг, указывающий на необходимость быстрого действия.
    *   `FastId (string)`: Идентификатор используемого предмета/способности для быстрого действия.
    *   `FastNick (string)`: Ник цели для быстрого действия.
    *   `FastCount (int)`: Количество повторений быстрого действия.
    *   `FastNeedAbilDarkTeleport (bool)`: Флаг для использования сумеречного телепорта.
    *   `FastNeedAbilDarkFog (bool)`: Флаг для использования сумеречного тумана.
    *   `FastWaitEndOfBoiActive (bool)`: Флаг активного ожидания окончания боя.
    *   `FastWaitEndOfBoiCancel (bool)`: Флаг отмены ожидания окончания боя.
    *   `AutoAttackToolId (int)`: ID инструмента для автоатаки.
    *   `ThreadWaitForTurn (Thread)`: Поток для ожидания хода.
    *   `PoisonAndWounds (int[])`: Массив для хранения информации об отравлениях и ранах.
    *   `LastMessageAboutTraumaOrPoison (DateTime)`: Время последнего сообщения о травме/отравлении.
    *   `DoPerenap (bool)`: Флаг режима перенападения.
    *   `DoFury (bool)`: Флаг режима свитка осады.
    *   `FastTotemMessageTime (DateTime)`: Время сообщения о тотемном нападении.
    *   `DrinkBlazPotOrElixirFirst (bool)`: Флаг, указывающий, нужно ли сначала пить зелье/эликсир блаженства.
    *   `DrinkDrinkHpMaCount (int)`: Счетчик выпитых зелий HP/MA.

*   **Связанные с картой/навигацией:**
    *   `LocationReal (string)`: Текущее реальное местоположение (может отличаться от профиля в момент перехода).
    *   `PathToMap (const string)`: Путь к рисункам карты.
    *   `AutoMoving (bool)`: Флаг автоматического перемещения.
    *   `AutoMovingNextJump (string)`: Следующий переход в режиме автоперемещения.
    *   `AutoMovingDestinaton (string)`: Пункт назначения автоперемещения.
    *   `AutoMovingJumps (int)`: Количество оставшихся переходов в режиме автоперемещения.
    *   `AutoMovingCityGate (CityGateType)`: Тип городских ворот для автоперемещения.
    *   `AutoMovingMapPath (MapPath)`: Объект, содержащий рассчитанный путь для автоперемещения.
    *   `DoSearchBox (bool)`: Флаг, указывающий, нужно ли искать клад.

*   **Связанные с инвентарем/магазином:**
    *   `RazdelkaResultList (StringCollection)`: Результаты разделки.
    *   `RazdelkaLevelUp (int)`: На сколько поднялось умение разделки.
    *   `RazdelkaTime (DateTime)`: Время, когда можно вывести результаты разделки в чат.
    *   `BulkDropThing (string)`: Предмет для массового выброса.
    *   `BulkDropPrice (string)`: Цена предмета для массового выброса.
    *   `BulkSellThing (string)`: Предмет для массовой продажи.
    *   `BulkSellPrice (int)`: Цена предмета для массовой продажи.
    *   `BulkSellSum (int)`: Сумма массовой продажи.
    *   `WearComplect (string)`: Название комплекта для одевания.
    *   `ShopList (List<ShopEntry>)`: Список предметов в магазине.
    *   `BulkSellOldName (string)`: Название предмета для старой массовой продажи.
    *   `BulkSellOldPrice (string)`: Цена предмета для старой массовой продажи.
    *   `BulkSellOldScript (string)`: Скрипт для старой массовой продажи.

*   **Связанные с чатом:**
    *   `UsersOnline (string)`: Количество пользователей онлайн.
    *   `Chat (string)`: (Вероятно, ссылка на объект чата или его состояние).

*   **Связанные с рыбалкой:**
    *   `AutoFishCheckUd (bool)`: Флаг проверки УД для авторыбалки.
    *   `AutoFishWearUd (bool)`: Флаг одевания УД для авторыбалки.
    *   `AutoFishCheckUm (bool)`: Флаг проверки УМ для авторыбалки.
    *   `AutoFishHand1 (string)`: Рука 1 для авторыбалки.
    *   `AutoFishHand2 (string)`: Рука 2 для авторыбалки.
    *   `AutoFishHand1D (string)`: Рука 1 (дополнительно) для авторыбалки.
    *   `AutoFishHand2D (string)`: Рука 2 (дополнительно) для авторыбалки.
    *   `AutoFishLikeId (string)`: ID для авторыбалки.
    *   `AutoFishLikeVal (string)`: Значение для авторыбалки.
    *   `AutoFishMassa (string)`: Масса для авторыбалки.
    *   `AutoFishNV (double)`: NV для авторыбалки.
    *   `AutoFishDrink (bool)`: Флаг питья для авторыбалки.
    *   `AutoFishDrinkOnce (bool)`: Флаг однократного питья для авторыбалки.

*   **Другие UI/состояния:**
    *   `ClearExplorerCacheFormMain (ClearExplorerCacheForm)`: Форма очистки кеша.
    *   `MustReload (bool)`: Флаг необходимости перезагрузки.
    *   `LastInitForm (DateTime)`: Время последней инициализации формы.
    *   `AccountError (string)`: Ошибка аккаунта.
    *   `WaitFlash (bool)`: Флаг ожидания Flash.
    *   `DoPromptExit (bool)`: Флаг запроса на выход.
    *   `CacheRefresh (bool)`: Флаг обновления кеша.
    *   `LastMainPhp (DateTime)`: Время последнего запроса main.php.
    *   `ContentMainPhp (string)`: Содержимое main.php.
    *   `Autoboi (AutoboiState)`: Состояние автобоя.
    *   `GuamodCode (string)`: Код Guamod.
    *   `CodePng (byte[])`: PNG-изображение кода.
    *   `Tied (int)`: Усталость.
    *   `LastTied (DateTime)`: Время последней проверки усталости.
    *   `AutoDrink (bool)`: Флаг автопитья.
    *   `SwitchToPerc (bool)`: Флаг переключения на проценты.
    *   `SwitchToFlora (bool)`: Флаг переключения на флору.
    *   `DoShowWalkers (bool)`: Флаг показа ходоков.
    *   `MyCoordOld (string)`: Старые координаты.
    *   `MyLocOld (string)`: Старое местоположение.
    *   `MyCharsOld (Dictionary<string, string>)`: Старые символы.
    *   `MyNevids (int)`: Невидимость.
    *   `MyNevidsOld (int)`: Старая невидимость.
    *   `MyWalkers1 (string)`: Ходоки 1.
    *   `MyWalkers2 (string)`: Ходоки 2.
    *   `LastChList (DateTime)`: Время последнего списка чата.
    *   `AdvArray (string[])`: Массив рекламы.
    *   `AdvIndex (int)`: Индекс рекламы.
    *   `AdvActive (bool)`: Флаг активной рекламы.
    *   `LastAdv (DateTime)`: Время последней рекламы.
    *   `DocumentBodyNullCount (int)`: Счетчик null-значений тела документа.
    *   `CureNeed (bool)`: Флаг необходимости лечения.
    *   `CureNick (string)`: Ник для лечения.
    *   `CureTravm (string)`: Тип травмы для лечения.
    *   `CureNickDone (string)`: Ник, лечение которого завершено.
    *   `CureNickBoi (string)`: Ник боя для лечения.
    *   `UserObrazes (SortedDictionary<string, string>)`: Образы пользователей.
    *   `LastTorgAdv (DateTime)`: Время последней торговой рекламы.
    *   `MovingTime (string)`: Время перемещения.
    *   `PriSelected (bool)`: Флаг выбранного приоритета.
    *   `NamePri (string)`: Название приоритета.
    *   `ValPri (int)`: Значение приоритета.
    *   `LastSwitch (DateTime)`: Время последнего переключения.
    *   `FishNoCaptchaReady (bool)`: Флаг готовности авторыбалки без капчи.
    *   `LicenceExpired (DateTime)`: Время истечения лицензии.
    *   `ServerDateTime (DateTime)`: Время сервера.
    *   `VipFormCompas (FormCompas)`: VIP-форма компаса.
    *   `VipFormAddClan (FormAddClan)`: VIP-форма добавления клана.
    *   `DoChatTip (bool)`: Флаг подсказки чата.
    *   `NextCheckNoConnection (DateTime)`: Время следующей проверки отсутствия соединения.
    *   `BossContacts (SortedList<string, BossContact>)`: Контакты боссов.
    *   `BossSayLastLog (string)`: Последний лог босса.
    *   `NeverTimer (DateTime)`: Блокировочный таймер.
    *   `IdleTimer (DateTime)`: Таймер бездействия.

*   **Статический конструктор:**
    *   Инициализирует `MyCharsOld`, `MyWalkers1`, `MyWalkers2`, `PoisonAndWounds`, `FastTotemMessageTime`, `NextCheckNoConnection`.

*   **Комментарии:**
    *   Многие переменные имеют комментарии, объясняющие их назначение. Некоторые разделы закомментированы (например, `LastExAlchemy`, `LockListMapHerbs`, `ListMapHerbs`, `DirChatLog`, `FileChatLog`).

**Последствия портирования:**
Этот файл является критически важным для понимания глобального состояния и зависимостей приложения C#. Многие из этих статических переменных необходимо будет сопоставить со свойствами в объекте Kotlin `AppVars` или классе данных `Profile`. Некоторые могут быть временными и не требовать прямого портирования, если их использование ограничено конкретными функциями C#, которые не портируются полностью.
