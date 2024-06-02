package com.example.glucoguardapp.model


class InsulinCalculator {

    private var result = 0.0
    private var totalResult = 0.0

    fun calculate(weight: Double, carbohydrate: Double) {
        if (weight >= 45 && weight < 49) {
            result = carbohydrate / 16
            totalResult = result
        } else if (weight >= 49 && weight < 58) {
            result = carbohydrate / 15
            totalResult = result
        } else if (weight >= 58 && weight < 63) {
            result = carbohydrate / 14
            totalResult = result
        } else if (weight >= 63 && weight < 67) {
            result = carbohydrate / 13
            totalResult = result
        } else if (weight >= 67 && weight < 76) {
            result = carbohydrate / 12
            totalResult = result
        } else if (weight >= 76 && weight < 81) {
            result = carbohydrate / 11
            totalResult = result
        } else if (weight >= 81 && weight < 85) {
            result = carbohydrate / 10
            totalResult = result
        } else if (weight >= 85 && weight < 90) {
            result = carbohydrate / 9
            totalResult = result
        } else if (weight >= 90 && weight < 99) {
            result = carbohydrate / 8
            totalResult = result
        } else if (weight >= 99 && weight < 108) {
            result = carbohydrate / 7
            totalResult = result
        } else if (weight >= 108) {
            result = carbohydrate / 6
            totalResult = result
        } else if (weight < 45) {
            result = carbohydrate / 17
            totalResult = result
        }
    }

    fun result(): Double {
        return totalResult
    }
}
