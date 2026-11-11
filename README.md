# Darknet-Launcher

Futuristic Android Launcher — qora (dark) mavzu, neon aksentlar va Jetpack Compose asosidagi bosh ekran ilovasi.

## Imkoniyatlar (v0.2.0)

- **Bosh ekran:** tizim fon rasmi ustida neon soat va sana
- **Dock:** 5 tagacha sevimli ilova (saqlanadi)
- **Ilovalar drawer'i:** bosh ekranda yuqoriga surish (swipe-up) yoki "Ilovalar" yozuvini bosish
- **Qidiruv:** harf yozing, klaviaturadagi qidiruv tugmasi birinchi natijani ochadi
- **Uzoq bosish menyusi:** dock'ga qo'shish/olib tashlash, yashirish, ilova haqida, o'chirish
- **Yashirin ilovalar:** drawer'dagi "Yashirin" tugmasi yashirilganlarni ko'rsatadi
- **Jonli yangilanish:** ilova o'rnatilsa, yangilansa yoki o'chirilsa ro'yxat o'zi yangilanadi
- **Tez ishlash:** ikonkalar keshlanadi, ro'yxat fon oqimida yuklanadi
- Home tugmasi qayta bosilsa drawer yopiladi, "Orqaga" avval qidiruvni tozalaydi

## Yo'l xaritasi

- [ ] Ikonka paketlari va mavzu sozlamalari
- [ ] Dock va drawer ilovalarini tartiblash (drag & drop)
- [ ] Vidjetlar qo'llab-quvvatlashi
- [ ] Imo-ishoralar sozlamasi (double-tap, swipe-down)
- [ ] Bildirishnoma nuqtalari

## Texnologiyalar

Kotlin, Jetpack Compose, Material 3, ViewModel + StateFlow. `minSdk 26`, `targetSdk 35`.

## APK olish

Har bir `main`ga push'dan keyin GitHub Actions debug APK ni quradi va **Releases** bo'limiga joylaydi.

## Qo'lda qurish

1. Loyihani Android Studio'da oching (`File → Open`). Gradle wrapper fayllari Studio tomonidan avtomatik yaratiladi.
2. Qurilma yoki emulyatorda `app` konfiguratsiyasini ishga tushiring.
3. Bosh tugmani bosing va **Darknet Launcher** ni standart launcher sifatida tanlang.

## Loyiha tuzilmasi

```
app/src/main/
├── AndroidManifest.xml            # HOME intent-filter
├── java/uz/darknet/launcher/
│   ├── MainActivity.kt            # kirish nuqtasi, paket o'zgarishlarini tinglash
│   ├── LauncherViewModel.kt       # holat: ilovalar, dock, yashirinlar, drawer
│   ├── LauncherPrefs.kt           # sozlamalarni saqlash
│   ├── AppRepository.kt           # ilovalarni o'qish, ochish, o'chirish
│   ├── LauncherScreen.kt          # bosh ekran, dock, drawer
│   ├── AppIcon.kt                 # ikonka va uzoq bosish menyusi
│   └── ui/Theme.kt                # qora/neon mavzu
└── res/values/                    # matnlar va mavzu
```

## Litsenziya

[MIT](LICENSE)
