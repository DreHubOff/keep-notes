package com.jksol.keep.notes.core.model

import androidx.annotation.Keep

@Keep
enum class NoteColor(val day: Long, val night: Long) {
    Peach(0xFFFDD5B9, 0xFF9A511B),
    Caramel(0xFFFEF0B9, 0xFF7E4903),
    Mint(0xFFC1E9D7, 0xFF264D3B),
    Aqua(0xFFC0F2EC, 0xFF0C635D),
    Rose(0xFFF9C8E0, 0xFF6D394F),
    Blush(0xFFFCC5CF, 0xFF992336),
    Lavender(0xFFF4C7FA, 0xFF750E8B),
    Lilac(0xFFDCCEFC, 0xFF5A35B5),
    Sky(0xFFB7E4F8, 0xFF256476),
    Periwinkle(0xFFD0D1FB, 0xFF3F42A4),
    Lime(0xFFDAF0B9, 0xFF497800),
    Denim(0xFFC4D9FC, 0xFF482E5B),
    Emerald(0xFFBAF3E0, 0xFF0B6547),
}