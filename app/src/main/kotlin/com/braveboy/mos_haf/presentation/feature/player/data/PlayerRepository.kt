package com.braveboy.mos_haf.presentation.feature.player.data

class PlayerRepository {
    fun getPageUrl(pageNumber: Int): String {
        return "http://files.yasinmedia.com/audio/v2/Quran_Abdul_Basit_Murattal_64kbps_page/Page%03d.mp3"
            .format(pageNumber)
    }

    fun getSurahUrl(surah: Int): String {
        return "http://files.yasinmedia.com/audio/v1/Quran_Abdul_Basit_Murattal/%03d.mp3"
            .format(surah)
    }

    fun getJozUrl(joz: Int): String {
        return "http://files.yasinmedia.com/audio/v6/Quran-Tartil-ShakerNejad-Joz/Tartil%20ShakerNejad%20($joz).mp3"
    }

    fun getAyahUrl(surah: Int, ayah: Int): String {
        // محاسبه شماره یکتای آیه در کل قرآن
        val ayahNumber = getGlobalAyahNumber(surah, ayah)

        // استفاده از CDN رایگان Alquran.cloud با کیفیت 128 و صدای Alafasy
        // قابل تعویض با هر قاری دیگر
        return "https://cdn.islamic.network/quran/audio/64/ar.alafasy/$ayahNumber.mp3"
    }

    // تابع کمکی برای تبدیل سوره و آیه به شماره یکتا
    private fun getGlobalAyahNumber(surah: Int, ayah: Int): Int {
        // تعداد آیات هر سوره (تا سوره 114)
        val surahAyahCount = mapOf(
            1 to 7, 2 to 286, 3 to 200, 4 to 176, 5 to 120, 6 to 165, 7 to 206,
            8 to 75, 9 to 129, 10 to 109, 11 to 123, 12 to 111, 13 to 43, 14 to 52,
            15 to 99, 16 to 128, 17 to 111, 18 to 110, 19 to 98, 20 to 135, 21 to 112,
            22 to 78, 23 to 118, 24 to 64, 25 to 77, 26 to 227, 27 to 93, 28 to 88,
            29 to 69, 30 to 60, 31 to 34, 32 to 30, 33 to 73, 34 to 54, 35 to 45,
            36 to 83, 37 to 182, 38 to 88, 39 to 75, 40 to 85, 41 to 54, 42 to 53,
            43 to 89, 44 to 59, 45 to 37, 46 to 35, 47 to 38, 48 to 29, 49 to 18,
            50 to 45, 51 to 60, 52 to 49, 53 to 62, 54 to 55, 55 to 78, 56 to 96,
            57 to 29, 58 to 22, 59 to 24, 60 to 13, 61 to 14, 62 to 11, 63 to 11,
            64 to 18, 65 to 12, 66 to 12, 67 to 30, 68 to 52, 69 to 52, 70 to 44,
            71 to 28, 72 to 28, 73 to 20, 74 to 56, 75 to 40, 76 to 31, 77 to 50,
            78 to 40, 79 to 46, 80 to 42, 81 to 29, 82 to 19, 83 to 36, 84 to 25,
            85 to 22, 86 to 17, 87 to 19, 88 to 26, 89 to 30, 90 to 20, 91 to 15,
            92 to 21, 93 to 11, 94 to 8, 95 to 8, 96 to 19, 97 to 5, 98 to 8,
            99 to 8, 100 to 11, 101 to 11, 102 to 8, 103 to 3, 104 to 9, 105 to 5,
            106 to 4, 107 to 7, 108 to 3, 109 to 6, 110 to 3, 111 to 5, 112 to 4,
            113 to 5, 114 to 6
        )

        var total = 0
        for (i in 1 until surah) {
            total += surahAyahCount[i] ?: 0
        }
        return total + ayah
    }
}