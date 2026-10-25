package com.donutlauncher.app

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private val minecraftPackage = "com.mojang.minecraftpe"
    private val bg = Color.rgb(12, 16, 13)
    private val surface = Color.rgb(22, 29, 24)
    private val surfaceRaised = Color.rgb(29, 39, 31)
    private val mint = Color.rgb(156, 255, 142)
    private val green = Color.rgb(58, 130, 66)
    private val white = Color.rgb(242, 247, 242)
    private val muted = Color.rgb(157, 174, 159)
    private val outline = Color.rgb(49, 65, 51)

    private val presetViews = mutableMapOf<String, LinearLayout>()
    private lateinit var selectedSummary: TextView
    private lateinit var gameStatus: TextView
    private var selectedPreset = "smooth"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        window.decorView.systemUiVisibility = 0
        selectedPreset = getSharedPreferences("donut_settings", MODE_PRIVATE)
            .getString("preset", "smooth") ?: "smooth"

        val scroll = ScrollView(this).apply {
            setBackgroundColor(bg)
            isFillViewport = false
            clipToPadding = false
            setPadding(0, 0, 0, dp(24))
        }
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(18), dp(20), dp(28))
        }
        scroll.addView(page, ViewGroup.LayoutParams(-1, -2))
        setContentView(scroll)

        buildHeader(page)
        addSpace(page, 22)
        buildHero(page)
        addSpace(page, 16)
        buildGameStatus(page)
        addSpace(page, 26)
        sectionLabel(page, "PERFORMANS PROFİLİ", "Oyun tarzına göre bir profil seç")
        addSpace(page, 12)
        buildPresets(page)
        addSpace(page, 14)
        buildSelectedDetails(page)
        addSpace(page, 14)
        buildCopyButton(page)
        addSpace(page, 28)
        sectionLabel(page, "HIZLI İŞLEMLER", "Oyuna girmeden önce")
        addSpace(page, 12)
        buildQuickActions(page)
        addSpace(page, 28)
        sectionLabel(page, "DAHA AKICI OYUN", "Gerçek fark yaratan küçük dokunuşlar")
        addSpace(page, 12)
        buildTips(page)
        addSpace(page, 18)
        buildDisclaimer(page)
        addSpace(page, 12)
        val footer = label("DONUTLAUNCHER  •  VERSION 1.0.0", 10, muted, Typeface.BOLD).apply {
            gravity = Gravity.CENTER
            letterSpacing = 0.12f
            setPadding(0, dp(10), 0, dp(8))
        }
        page.addView(footer, matchWrap())
        updateGameStatus()
    }

    private fun buildHeader(parent: LinearLayout) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val logo = TextView(this).apply {
            text = "D"
            textSize = 24f
            typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
            gravity = Gravity.CENTER
            setTextColor(bg)
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(mint)
            }
        }
        row.addView(logo, LinearLayout.LayoutParams(dp(48), dp(48)))
        val texts = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        texts.addView(label("DONUT", 19, white, Typeface.BOLD), matchWrap())
        texts.addView(label("LAUNCHER", 10, mint, Typeface.BOLD).apply { letterSpacing = 0.22f }, matchWrap())
        row.addView(texts, LinearLayout.LayoutParams(0, -2, 1f))
        val badge = label("BEDROCK  /  ANDROID", 9, mint, Typeface.BOLD).apply {
            setPadding(dp(10), dp(8), dp(10), dp(8))
            background = rounded(surfaceRaised, 30, outline, 1)
        }
        row.addView(badge)
        parent.addView(row, matchWrap())
    }

    private fun buildHero(parent: LinearLayout) {
        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(22), dp(20), dp(20))
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(Color.rgb(35, 81, 42), Color.rgb(19, 38, 25), Color.rgb(18, 26, 20))
            ).apply { cornerRadius = dp(26).toFloat(); setStroke(dp(1), Color.rgb(61, 105, 63)) }
        }
        val eyebrow = label("MINECRAFT FPS COMPANION", 10, mint, Typeface.BOLD).apply { letterSpacing = 0.12f }
        hero.addView(eyebrow, matchWrap())
        addSpace(hero, 12)
        hero.addView(label("Daha akıcı bir\ndünya.", 31, white, Typeface.BOLD), matchWrap())
        addSpace(hero, 9)
        hero.addView(label("Oyunu aç, sana uygun performans profilini seç ve ayarlarını kolayca uygula.", 14, Color.rgb(210, 226, 211)), matchWrap())
        addSpace(hero, 19)
        val start = actionButton("▶   MINECRAFT'I BAŞLAT", mint, bg)
        start.setOnClickListener { launchMinecraft() }
        hero.addView(start, matchWrap())
        addSpace(hero, 10)
        hero.addView(label("Oyunun orijinal sürümünü kullanır; dosyalarını değiştirmez.", 10, Color.rgb(182, 207, 184)), matchWrap())
        parent.addView(hero, matchWrap())
    }

    private fun buildGameStatus(parent: LinearLayout) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(15), dp(14), dp(15), dp(14))
            background = rounded(surface, 18, outline, 1)
        }
        val dot = TextView(this).apply {
            text = "●"
            textSize = 16f
            setTextColor(mint)
            gravity = Gravity.CENTER
        }
        card.addView(dot, LinearLayout.LayoutParams(dp(22), dp(28)))
        val copy = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), 0, 0, 0)
        }
        copy.addView(label("MINECRAFT DURUMU", 10, muted, Typeface.BOLD), matchWrap())
        gameStatus = label("Yüklü olup olmadığı kontrol ediliyor…", 13, white, Typeface.BOLD)
        copy.addView(gameStatus, matchWrap())
        card.addView(copy, LinearLayout.LayoutParams(0, -2, 1f))
        val refresh = label("YENİLE", 10, mint, Typeface.BOLD).apply {
            setPadding(dp(10), dp(9), dp(10), dp(9))
            background = rounded(surfaceRaised, 12, outline, 1)
            setOnClickListener { updateGameStatus() }
        }
        card.addView(refresh)
        parent.addView(card, matchWrap())
    }

    private fun buildPresets(parent: LinearLayout) {
        addPreset(parent, "smooth", "Akıcı FPS", "Daha fazla akıcılık, daha az görsel yük", "01")
        addSpace(parent, 9)
        addPreset(parent, "balanced", "Dengeli", "Görünüm ve performans arasında denge", "02")
        addSpace(parent, 9)
        addPreset(parent, "quality", "Görsel kalite", "Daha yüksek mesafe ve ayrıntı", "03")
    }

    private fun addPreset(parent: LinearLayout, id: String, title: String, subtitle: String, number: String) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(13), dp(14), dp(13))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                selectedPreset = id
                getSharedPreferences("donut_settings", MODE_PRIVATE).edit().putString("preset", id).apply()
                refreshPresetSelection()
                selectedSummary.text = presetDetails()
            }
        }
        presetViews[id] = row
        val num = TextView(this).apply {
            text = number
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setTextColor(mint)
            background = rounded(Color.rgb(35, 55, 37), 12, Color.TRANSPARENT, 0)
        }
        row.addView(num, LinearLayout.LayoutParams(dp(38), dp(38)))
        val textColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, dp(8), 0)
        }
        textColumn.addView(label(title, 15, white, Typeface.BOLD), matchWrap())
        addSpace(textColumn, 3)
        textColumn.addView(label(subtitle, 11, muted), matchWrap())
        row.addView(textColumn, LinearLayout.LayoutParams(0, -2, 1f))
        val check = label("✓", 18, mint, Typeface.BOLD).apply { gravity = Gravity.CENTER }
        check.tag = "check"
        row.addView(check, LinearLayout.LayoutParams(dp(25), dp(32)))
        parent.addView(row, matchWrap())
        refreshPresetSelection()
    }

    private fun refreshPresetSelection() {
        for ((id, view) in presetViews) {
            val selected = id == selectedPreset
            view.background = rounded(
                if (selected) Color.rgb(30, 49, 32) else surface,
                18,
                if (selected) mint else outline,
                if (selected) 2 else 1
            )
            view.findViewWithTag<TextView>("check")?.visibility = if (selected) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun buildSelectedDetails(parent: LinearLayout) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(15), dp(16), dp(15))
            background = rounded(surface, 18, outline, 1)
        }
        card.addView(label("SEÇİLİ PROFİL ÖNERİSİ", 10, mint, Typeface.BOLD), matchWrap())
        addSpace(card, 8)
        selectedSummary = label(presetDetails(), 13, white)
        card.addView(selectedSummary, matchWrap())
        addSpace(card, 7)
        card.addView(label("Bu bir ayar önerisidir; oyun ayarları otomatik değiştirilmez.", 10, muted), matchWrap())
        parent.addView(card, matchWrap())
    }

    private fun buildCopyButton(parent: LinearLayout) {
        val button = actionButton("AYAR PROFİLİNİ KOPYALA  ↗", surfaceRaised, mint)
        button.background = rounded(surfaceRaised, 16, outline, 1)
        button.setOnClickListener {
            val text = "DonutLauncher • ${presetName()}\n\n${presetDetails()}\n\nNot: Minecraft içindeki Video/Grafik ayarlarından elle uygula. Seçenek isimleri sürüme göre değişebilir."
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("DonutLauncher profil", text))
            Toast.makeText(this, "Profil kopyalandı. Minecraft ayarlarında uygulayabilirsin.", Toast.LENGTH_LONG).show()
        }
        parent.addView(button, matchWrap())
    }

    private fun buildQuickActions(parent: LinearLayout) {
        val settings = secondaryButton("⚙", "Minecraft uygulama ayarları", "Pil ve uygulama izinlerini görüntüle")
        settings.setOnClickListener { openMinecraftAppSettings() }
        parent.addView(settings, matchWrap())
        addSpace(parent, 9)
        val display = secondaryButton("◉", "Ekran ayarları", "Ekran yenileme hızını kontrol et")
        display.setOnClickListener {
            try {
                startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS))
            } catch (_: Exception) {
                Toast.makeText(this, "Ekran ayarları açılamadı.", Toast.LENGTH_SHORT).show()
            }
        }
        parent.addView(display, matchWrap())
    }

    private fun secondaryButton(icon: String, title: String, subtitle: String): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(13), dp(14), dp(13))
            background = rounded(surface, 17, outline, 1)
            isClickable = true
            isFocusable = true
        }
        row.addView(label(icon, 20, mint, Typeface.BOLD).apply { gravity = Gravity.CENTER }, LinearLayout.LayoutParams(dp(36), dp(36)))
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        col.addView(label(title, 14, white, Typeface.BOLD), matchWrap())
        addSpace(col, 3)
        col.addView(label(subtitle, 11, muted), matchWrap())
        row.addView(col, LinearLayout.LayoutParams(0, -2, 1f))
        row.addView(label("›", 25, muted), wrapWrap())
        return row
    }

    private fun buildTips(parent: LinearLayout) {
        val tips = listOf(
            "01" to "Render mesafesi: FPS düşüyorsa 6–8 chunk ile dene.",
            "02" to "Grafikler: Fancy Graphics, bulutlar ve benzeri efektleri kapat; seçenekler sürüme göre değişebilir.",
            "03" to "Isı: Telefon çok ısındığında mola ver; termal kısma FPS'yi düşürebilir.",
            "04" to "Doku paketleri: Yüksek çözünürlüklü paketleri kapatıp oyunu yeniden başlat.",
            "05" to "Akıcılık: Ekranın desteklediği yenileme hızını kullan; daha yüksek Hz tek başına sabit FPS garantilemez."
        )
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(15), dp(5), dp(15), dp(5))
            background = rounded(surface, 18, outline, 1)
        }
        tips.forEachIndexed { index, pair ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.TOP
                setPadding(0, dp(12), 0, dp(12))
            }
            row.addView(label(pair.first, 10, mint, Typeface.BOLD), LinearLayout.LayoutParams(dp(30), -2))
            row.addView(label(pair.second, 12, Color.rgb(218, 227, 219)), LinearLayout.LayoutParams(0, -2, 1f))
            card.addView(row, matchWrap())
            if (index != tips.lastIndex) {
                val line = View(this).apply { setBackgroundColor(outline) }
                card.addView(line, LinearLayout.LayoutParams(-1, dp(1)))
            }
        }
        parent.addView(card, matchWrap())
    }

    private fun buildDisclaimer(parent: LinearLayout) {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(15), dp(14), dp(15), dp(14))
            background = rounded(Color.rgb(29, 31, 23), 16, Color.rgb(84, 79, 44), 1)
        }
        box.addView(label("GERÇEKÇİ FPS NOTU", 10, Color.rgb(255, 224, 135), Typeface.BOLD), matchWrap())
        addSpace(box, 6)
        box.addView(label("Android, başka bir uygulamanın gerçek FPS değerini her cihazda bu uygulamaya açmaz. DonutLauncher oyun motoruna müdahale etmez, FPS artışı vaat etmez ve oyun dosyalarını değiştirmez. Sana ayar profilleri sunar ve Minecraft'ı açar.", 11, Color.rgb(219, 216, 196)), matchWrap())
        parent.addView(box, matchWrap())
    }

    private fun sectionLabel(parent: LinearLayout, title: String, subtitle: String) {
        parent.addView(label(title, 11, mint, Typeface.BOLD).apply { letterSpacing = 0.10f }, matchWrap())
        addSpace(parent, 5)
        parent.addView(label(subtitle, 13, muted), matchWrap())
    }

    private fun launchMinecraft() {
        val launchIntent = packageManager.getLaunchIntentForPackage(minecraftPackage)
        if (launchIntent != null) {
            try {
                startActivity(launchIntent)
            } catch (_: Exception) {
                Toast.makeText(this, "Minecraft açılamadı.", Toast.LENGTH_LONG).show()
            }
        } else {
            AlertDialog.Builder(this)
                .setTitle("Minecraft bulunamadı")
                .setMessage("Minecraft Bedrock bu cihazda yüklü görünmüyor. Oyunun resmî sürümünü yükledikten sonra buradan açabilirsin.")
                .setNegativeButton("Kapat", null)
                .setPositiveButton("Play Store'u aç") { _, _ -> openPlayStore() }
                .show()
        }
    }

    private fun openPlayStore() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$minecraftPackage")))
        } catch (_: Exception) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$minecraftPackage")))
            } catch (_: Exception) {
                Toast.makeText(this, "Play Store açılamadı.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openMinecraftAppSettings() {
        if (packageManager.getLaunchIntentForPackage(minecraftPackage) == null) {
            Toast.makeText(this, "Minecraft bu cihazda yüklü görünmüyor.", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$minecraftPackage")))
        } catch (_: Exception) {
            Toast.makeText(this, "Uygulama ayarları açılamadı.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateGameStatus() {
        val installed = packageManager.getLaunchIntentForPackage(minecraftPackage) != null
        gameStatus.text = if (installed) "Minecraft yüklü • başlatmaya hazır" else "Minecraft bulunamadı • kurulum gerekli"
        gameStatus.setTextColor(if (installed) mint else Color.rgb(255, 203, 125))
    }

    private fun presetName(): String = when (selectedPreset) {
        "balanced" -> "Dengeli"
        "quality" -> "Görsel kalite"
        else -> "Akıcı FPS"
    }

    private fun presetDetails(): String = when (selectedPreset) {
        "balanced" -> "• Render mesafesi: 8–12 chunk ile dene\n• Ağır grafik efektlerini kapalı tut\n• Doku paketi: varsayılan veya hafif paket\n• Takılma olursa render mesafesini kademeli azalt"
        "quality" -> "• Render mesafesi: cihazın kaldırdığı seviyede, 12+ chunk deneyebilirsin\n• İsteğe bağlı görsel efektleri açık tut\n• Yüksek çözünürlüklü paketler daha çok RAM ve GPU kullanır\n• FPS düşerse Dengeli profiline geç"
        else -> "• Render mesafesi: 6–8 chunk\n• Fancy Graphics ve ağır efektleri kapat\n• Varsayılan veya hafif doku paketi kullan\n• Dünyaya girişte takılma sürerse arka plan uygulamalarını kapatıp oyunu yeniden aç"
    }

    private fun actionButton(text: String, fill: Int, textColor: Int): TextView = label(text, 13, textColor, Typeface.BOLD).apply {
        gravity = Gravity.CENTER
        setPadding(dp(16), dp(16), dp(16), dp(16))
        background = rounded(fill, 16, Color.TRANSPARENT, 0)
        isClickable = true
        isFocusable = true
    }

    private fun label(text: String, size: Int, color: Int, style: Int = Typeface.NORMAL): TextView = TextView(this).apply {
        this.text = text
        textSize = size.toFloat()
        setTextColor(color)
        typeface = Typeface.create("sans-serif", style)
        includeFontPadding = true
        setLineSpacing(dp(2).toFloat(), 1.0f)
    }

    private fun rounded(fill: Int, radiusDp: Int, stroke: Int, strokeWidthDp: Int): GradientDrawable = GradientDrawable().apply {
        setColor(fill)
        cornerRadius = dp(radiusDp).toFloat()
        if (strokeWidthDp > 0 && stroke != Color.TRANSPARENT) setStroke(dp(strokeWidthDp), stroke)
    }

    private fun addSpace(parent: LinearLayout, heightDp: Int) {
        parent.addView(View(this), LinearLayout.LayoutParams(1, dp(heightDp)))
    }

    private fun matchWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    private fun wrapWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density + 0.5f).toInt()
}
