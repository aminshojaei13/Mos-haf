package com.braveboy.mos_haf.presentation.feature.player.data

enum class Reciter(val id: String, val displayName: String) {
    ALAFASY("Alafasy_128kbps", "مشاری العفاسی"),
    ABDUL_BASIT_MURATTAL("Abdul_Basit_Murattal_128kbps", "عبدالباسط (مرتل)"),
    MINSHAWI_MURATTAL("Minshawy_Murattal_128kbps", "منشاوی (مرتل)"),
    HUSARY_MURATTAL("Husary_128kbps", "الحصری (مرتل)"),
    SUDAIS("Abdurrahmaan_As-Sudais_192kbps", "عبدالرحمن السدیس"),
    SHATRI("Abu_Bakr_Ash-Shaatree_128kbps", "ابوبکر الشاطری"),
    MAHER_MUAIQLY("Maher_AlMuaiqly_64kbps", "ماهر المعیقلی"),
    PARHIZGAR("Parhizgar_48kbps", "شهریار پرهیزگار"),
    MANSURI("Karim_Mansouri_40kbps", "کریم منصوری")
}

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

    fun getAyahUrl(surah: Int, ayah: Int, reciterId: String = "Alafasy_128kbps"): String {
        // هر آیه به صورت 001001.mp3 (3 رقم سوره و 3 رقم آیه)
        val fileName = "%03d%03d.mp3".format(surah, ayah)
        return "https://everyayah.com/data/$reciterId/$fileName"
    }
}
