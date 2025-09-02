package com.neverlands.anlc;

/**
 * Перечисление состояний автобоя, аналог AutoboiState.cs
 */
public enum AutoboiState {
    /**
     * Автобой выключен
     */
    AutoboiOff,

    /**
     * Автобой включен
     */
    AutoboiOn,

    /**
     * Автобой в режиме ожидания
     */
    AutoboiWait,

    /**
     * Автобой в режиме ожидания хода
     */
    AutoboiWaitForTurn,

    /**
     * Автобой в режиме ожидания окончания боя
     */
    AutoboiWaitEndOfBoi,

    /**
     * Автобой в режиме ожидания окончания боя с отменой
     */
    AutoboiWaitEndOfBoiCancel
}