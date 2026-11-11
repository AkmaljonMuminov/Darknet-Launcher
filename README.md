# Darknet-Launcher

Futuristic Android Launcher — qora (dark) mavzu, neon aksentlar va Jetpack Compose asosidagi bosh ekran ilovasi.

## Imkoniyatlar (hozirgi holat)

- Barcha o'rnatilgan ilovalarni 4 ustunli gridda ko'rsatadi
- Ilovalar bo'yicha tezkor qidiruv
- Qora/neon mavzu
- Ilovani bosib ishga tushirish
- Bosh ekran (HOME) launcheri sifatida o'rnatiladi

## Yo'l xaritasi

- [ ] Ilova ikonkalarini keshlash va tezlashtirish
- [ ] Sevimli ilovalar paneli (dock)
- [ ] Ilovalarni yashirish va guruhlash
- [ ] Ikonka paketlari va mavzu sozlamalari
- [ ] Imo-ishoralar (swipe up — qidiruv)
- [ ] Vidjetlar qo'llab-quvvatlashi

## Texnologiyalar

Kotlin, Jetpack Compose, Material 3. `minSdk 26`, `targetSdk 35`.

## Ishga tushirish

1. Loyihani Android Studio'da oching (`File → Open`). Gradle wrapper fayllari Studio tomonidan avtomatik yaratiladi.
2. Qurilma yoki emulyatorda `app` konfiguratsiyasini ishga tushiring.
3. Bosh tugmani bosing va **Darknet Launcher** ni standart launcher sifatida tanlang.

## Loyiha tuzilmasi

```
app/src/main/
├── AndroidManifest.xml          # HOME intent-filter
├── java/uz/darknet/launcher/
│   ├── MainActivity.kt          # kirish nuqtasi
│   ├── AppRepository.kt         # o'rnatilgan ilovalarni o'qish
│   ├── LauncherScreen.kt        # qidiruv va grid
│   └── ui/Theme.kt              # qora/neon mavzu
└── res/values/                  # matnlar va mavzu
```

## Litsenziya

[MIT](LICENSE)
