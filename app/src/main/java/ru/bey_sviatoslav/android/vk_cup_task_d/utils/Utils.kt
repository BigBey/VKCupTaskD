package ru.bey_sviatoslav.android.vk_cup_task_d.utils

fun Int.getPhotoAddition(): String {

    val preLastDigit = this % 100 / 10
    if (preLastDigit == 1) {
        return "фотографий"
    }

    when (this % 10) {
        1 -> return "фотография"
        2, 3, 4 -> return "фотографии"
        else -> return "фотографий"
    }
}