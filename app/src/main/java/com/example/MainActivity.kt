package com.example

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// ==========================================
// MODELS & GRAPHICS STRUCTURES
// ==========================================

enum class BrushType(val label: String, val iconEmoji: String) {
    GLOW("Glow Pen", "✨"),
    RAINBOW("Rainbow", "🌈"),
    CRAYON("Crayon", "🖍️"),
    SPARKLE("Sparkle", "🌟")
}

data class PlayroomColor(
    val color: Color,
    val name: String,
    val labelEmoji: String
)

val PlayroomColors = listOf(
    PlayroomColor(Color(0xFFFF5E7E), "Strawberry", "🍓"),
    PlayroomColor(Color(0xFFFFA502), "Orange", "🍊"),
    PlayroomColor(Color(0xFFFFD124), "Banana", "🍌"),
    PlayroomColor(Color(0xFF2ED573), "Watermelon", "🍉"),
    PlayroomColor(Color(0xFF1E90FF), "BlueBerry", "🫐"),
    PlayroomColor(Color(0xFF9E00FF), "Plum Glow", "🍇"),
    PlayroomColor(Color(0xFFFF8AAE), "Bubble Pink", "🍭")
)

data class ColorInfo(
    val name: String,
    val colorVal: Color,
    val emoji: String,
    val speech: String
)

val PlayroomColorsList = listOf(
    ColorInfo("Red", Color(0xFFEA2027), "🍎", "Red like an Apple"),
    ColorInfo("Green", Color(0xFF2ECC71), "🌳", "Green like a Tree"),
    ColorInfo("Blue", Color(0xFF0652DD), "🐳", "Blue like the Ocean"),
    ColorInfo("Pink", Color(0xFFFDA7DF), "🌸", "Pink like a Flower"),
    ColorInfo("Yellow", Color(0xFFFFC312), "☀️", "Yellow like the Sun"),
    ColorInfo("Black", Color(0xFF1E272E), "🎩", "Black like a Hat"),
    ColorInfo("White", Color(0xFFFFFFFF), "☁️", "White like a Cloud"),
    ColorInfo("Orange", Color(0xFFEE5A24), "🍊", "Orange like an Orange"),
    ColorInfo("Purple", Color(0xFF9B59B6), "🍇", "Purple like Grapes"),
    ColorInfo("Brown", Color(0xFF8B4513), "🍫", "Brown like Chocolate"),
    ColorInfo("Teal", Color(0xFF12CBC4), "💎", "Teal like a Diamond"),
    ColorInfo("Gray", Color(0xFF7F8C8D), "🐭", "Gray like a Mouse")
)

enum class PlayroomTab(val label: String, val iconEmoji: String) {
    NURSERY_BOOK("Nursery Book", "📚"),
    ROUND_EXPLORER("Round Wheel", "🧭"),
    DRAW_BOARD("Draw Studio", "✏️"),
    PLAYROOM_GAMES("Games", "🎮")
}

data class DrawStroke(
    val points: List<Offset>,
    val color: Color,
    val brush: BrushType,
    val thickness: Float = 16f
)

// Custom Number Info for the Number nursery book
data class NumberInfo(
    val value: Int,
    val word: String,
    val emoji: String,
    val phonics: String
)

val NumberRepository = listOf(
    NumberInfo(1, "One", "🍎", "One"),
    NumberInfo(2, "Two", "🧸🧸", "Two"),
    NumberInfo(3, "Three", "🐱🐱🐱", "Three"),
    NumberInfo(4, "Four", "🍀🍀🍀🍀", "Four"),
    NumberInfo(5, "Five", "🍒🍒🍒🍒🍒", "Five"),
    NumberInfo(6, "Six", "🐝🐝🐝🐝🐝🐝", "Six"),
    NumberInfo(7, "Seven", "🌈🌈🌈🌈🌈🌈🌈", "Seven"),
    NumberInfo(8, "Eight", "🐙🐙🐙🐙🐙🐙🐙🐙", "Eight"),
    NumberInfo(9, "Nine", "🎈🎈🎈🎈🎈🎈🎈🎈🎈", "Nine"),
    NumberInfo(10, "Ten", "⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️", "Ten"),
    NumberInfo(11, "Eleven", "⚽⚽⚽⚽⚽⚽⚽⚽⚽⚽⚽", "Eleven"),
    NumberInfo(12, "Twelve", "🍨🍨🍨🍨🍨🍨🍨🍨🍨🍨🍨🍨", "Twelve"),
    NumberInfo(13, "Thirteen", "🦋🦋🦋🦋🦋🦋🦋🦋🦋🦋🦋🦋🦋", "Thirteen"),
    NumberInfo(14, "Fourteen", "🧁🧁🧁🧁🧁🧁🧁🧁🧁🧁🧁🧁🧁🧁", "Fourteen"),
    NumberInfo(15, "Fifteen", "🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀🚀", "Fifteen"),
    NumberInfo(16, "Sixteen", "🦖🦖🦖🦖🦖🦖🦖🦖🦖🦖🦖🦖🦖🦖🦖🦖", "Sixteen"),
    NumberInfo(17, "Seventeen", "🍩🍩🍩🍩🍩🍩🍩🍩🍩🍩🍩🍩🍩🍩🍩🍩🍩", "Seventeen"),
    NumberInfo(18, "Eighteen", "🦁🦁🦁🦁🦁🦁🦁🦁🦁🦁🦁🦁🦁🦁🦁🦁🦁🦁", "Eighteen"),
    NumberInfo(19, "Nineteen", "🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉", "Nineteen"),
    NumberInfo(20, "Twenty", "🚗🚗🚗🚗🚗🚗🚗🚗🚗🚗🚗🚗🚗🚗🚗🚗🚗🚗🚗🚗", "Twenty")
)

// Memory match card representation
data class MemoryCard(
    val id: Int,
    val content: String, // could be letter or its emoji
    val matchKey: String, // the letter itself
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)

// Floating ball representation for the new bubbles upward game
data class FloatingBall(
    val id: Int,
    val char: Char,
    val xPercent: Float,
    val yPercent: Float,
    val speed: Float,
    val color: Color,
    val sizeDp: Int
)

// ==========================================
// VIEWMODEL FOR CONSOLIDATING APPLICATION STATE
// ==========================================

class PlayroomViewModel : ViewModel() {
    private val _currentTab = MutableStateFlow(PlayroomTab.NURSERY_BOOK)
    val currentTab: StateFlow<PlayroomTab> = _currentTab.asStateFlow()

    fun setTab(tab: PlayroomTab) {
        _currentTab.value = tab
    }

    // Theme selection: 0 = Light, 1 = Dark, 2 = System Default
    private val _themeMode = MutableStateFlow(2)
    val themeMode: StateFlow<Int> = _themeMode.asStateFlow()

    fun setThemeMode(mode: Int) {
        _themeMode.value = mode
    }

    // Number of stars gathered
    private val _completedLetters = MutableStateFlow<Set<Char>>(emptySet())
    val completedLetters: StateFlow<Set<Char>> = _completedLetters.asStateFlow()

    fun completeLetter(char: Char) {
        _completedLetters.value = _completedLetters.value + char
    }

    // Interactive popups status
    var activePopupLetter = mutableStateOf<LetterInfo?>(null)
    var activePopupNumber = mutableStateOf<NumberInfo?>(null)

    // Drawing Board session configurations
    var selectedDrawingLetter = mutableStateOf(LetterRepository.letters[0])
    var selectedDrawingNumber = mutableStateOf(NumberRepository[0])
    var drawingWithLetterMode = mutableStateOf(true) // true = letters, false = numbers

    // Drawing lines persistent list
    val paintStrokes = mutableStateListOf<DrawStroke>()
    val currentStrokePoints = mutableStateListOf<Offset>()
    var activePenColor = mutableStateOf(PlayroomColors[0].color)
    var activePenBrush = mutableStateOf(BrushType.GLOW)

    fun clearDrawingPaths() {
        paintStrokes.clear()
        currentStrokePoints.clear()
    }
}

// ==========================================
// MAIN ACTIVITY CLASS (SPEECH ENGINE INTEGRATION)
// ==========================================

class MainActivity : ComponentActivity() {
    private var tts: TextToSpeech? = null
    private val ttsReady = mutableStateOf(false)
    private val viewModel: PlayroomViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Speak engine
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setSpeechRate(0.85f) // perfect tempo for young learners
                tts?.setPitch(1.3f)      // cheerful kids cartoon pitch tone
                ttsReady.value = true
                
                // Greeting Phonics intro
                sayWord("Welcome to Moiz Education App! Best Education app for age upto 8 years. App Developed by G.Abbas and Dedicated to Sindh Public School, Larkana")
            }
        }

        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val darkTheme = when (themeMode) {
                0 -> false
                1 -> true
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = darkTheme) {
                val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize().testTag("app_scaffold"),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        BottomNavBar(
                            currentTab = currentTab,
                            onTabSelected = { tab ->
                                viewModel.setTab(tab)
                                sayWord(tab.label)
                            }
                        )
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                             // High priority Mascot Title Header Card (Only shown on Nursery Book tab to save space on mobile phones)
                             if (currentTab == PlayroomTab.NURSERY_BOOK) {
                                 HeaderMascotCard(
                                     themeMode = themeMode,
                                     onThemeSelect = { mode ->
                                         viewModel.setThemeMode(mode)
                                         val modeSpeech = when (mode) {
                                             0 -> "Light Theme selected"
                                             1 -> "Dark Theme selected"
                                             else -> "System default theme selected"
                                         }
                                         sayWord(modeSpeech)
                                     },
                                     onGreetClick = {
                                         sayWord("Welcome to Moiz Education App! Best Education app for age upto 8 years. App Developed by G.Abbas and Dedicated to Sindh Public School, Larkana")
                                     }
                                 )
                             }

                            // Crossfade transitions between tabs
                            Crossfade(
                                targetState = currentTab,
                                modifier = Modifier.weight(1f),
                                label = "TabTransition"
                            ) { tab ->
                                when (tab) {
                                    PlayroomTab.NURSERY_BOOK -> {
                                        NurseryBookTab(
                                            onLetterTap = { letter ->
                                                viewModel.activePopupLetter.value = letter
                                                sayWord("${letter.char} for ${letter.word}")
                                                viewModel.completeLetter(letter.char)
                                            },
                                            onNumberTap = { num ->
                                                viewModel.activePopupNumber.value = num
                                                sayWord(num.word)
                                            },
                                            onSpeak = { sayWord(it) }
                                        )
                                    }
                                    PlayroomTab.ROUND_EXPLORER -> {
                                        RoundWheelExplorer(
                                            onSpeak = { sayWord(it) }
                                        )
                                    }
                                    PlayroomTab.DRAW_BOARD -> {
                                        DrawStudio(
                                            viewModel = viewModel,
                                            onSpeak = { sayWord(it) }
                                        )
                                    }
                                    PlayroomTab.PLAYROOM_GAMES -> {
                                        GamesSandbox(
                                            onSpeak = { sayWord(it) }
                                        )
                                    }
                                }
                            }

                            // Elegant signature credits at the footer of all screens
                            Spacer(modifier = Modifier.height(10.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp, start = 14.dp, end = 14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "App Developed by G.Abbas",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Dedicated to Sindh Public School, Larkana 🏢❤️",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Child pop-up dialog overlays mapping
                        val popupLet = viewModel.activePopupLetter.value
                        if (popupLet != null) {
                            NurseryPopupCard(
                                title = popupLet.char.toString(),
                                word = "for " + popupLet.word,
                                emoji = popupLet.emoji,
                                colorAccent = Color(0xFFFF7675),
                                onDismiss = { viewModel.activePopupLetter.value = null },
                                onSpeakAgain = { sayWord("${popupLet.char} for ${popupLet.word}!") }
                            )
                        }

                        val popupNum = viewModel.activePopupNumber.value
                        if (popupNum != null) {
                            NurseryPopupCard(
                                title = popupNum.value.toString(),
                                word = popupNum.word,
                                emoji = popupNum.emoji,
                                colorAccent = Color(0xFF0984E3),
                                onDismiss = { viewModel.activePopupNumber.value = null },
                                onSpeakAgain = { sayWord(popupNum.word) }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun sayWord(text: String) {
        if (ttsReady.value) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "MoizWorldSpeech")
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}

// ==========================================
// CUTE MASCOT HEADER ROW BRANDING
// ==========================================

@Composable
fun HeaderMascotCard(
    themeMode: Int,
    onThemeSelect: (Int) -> Unit,
    onGreetClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .shadow(6.dp, RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                )
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGreetClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circle border Mascot Moiz Avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_moiz),
                        contentDescription = "Moiz World Mascot",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Moiz Education App 👦✨",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Best App for age upto 8 years!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Pre-School, Nursery, KG, One etc. • Education - Fun - Entertainment",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            // Thick elegant custom divider Spacer
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
            )
            Spacer(modifier = Modifier.height(10.dp))

            // App Dark Theme Switcher Pills Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Theme Palette 🎨",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val themes = listOf("☀️ Light", "🌙 Dark", "⚙️ System")
                    themes.forEachIndexed { index, name ->
                        val isSelected = themeMode == index
                        val containerBg = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        val contentCol = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(containerBg)
                                .clickable { onThemeSelect(index) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("theme_option_$index"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = contentCol
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// BOTTOM NAVIGATION SYSTEM
// ==========================================

@Composable
fun BottomNavBar(
    currentTab: PlayroomTab,
    onTabSelected: (PlayroomTab) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp, start = 12.dp, end = 12.dp)
            .shadow(6.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayroomTab.values().forEach { tab ->
                val isSelected = currentTab == tab
                val backgroundTint = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent
                val textTint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(backgroundTint)
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("tab_button_${tab.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = tab.iconEmoji,
                            fontSize = 22.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = textTint
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 1: THE CUTE NURSERY BOOK (LETTERS & NUMBERS)
// ==========================================

@Composable
fun NurseryBookTab(
    onLetterTap: (LetterInfo) -> Unit,
    onNumberTap: (NumberInfo) -> Unit,
    onSpeak: (String) -> Unit
) {
    var subTabMode by remember { mutableStateOf(0) } // 0: A-Z, 1: 1-10, 2: Colors, 3: Poems

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        // Sub-selector Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { subTabMode = 0 },
                modifier = Modifier
                    .height(44.dp)
                    .testTag("subtab_alphabets"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (subTabMode == 0) Color(0xFFFF7675) else Color(0xFFE2E8F0),
                    contentColor = if (subTabMode == 0) Color.White else Color(0xFF2D3748)
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
            ) {
                Text("🌸 Alphabets", fontSize = 14.sp, fontWeight = FontWeight.Black)
            }

            Button(
                onClick = { subTabMode = 1 },
                modifier = Modifier
                    .height(44.dp)
                    .testTag("subtab_numbers"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (subTabMode == 1) Color(0xFF0984E3) else Color(0xFFE2E8F0),
                    contentColor = if (subTabMode == 1) Color.White else Color(0xFF2D3748)
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
            ) {
                Text("🔢 Numbers Book", fontSize = 14.sp, fontWeight = FontWeight.Black)
            }

            Button(
                onClick = { subTabMode = 2 },
                modifier = Modifier
                    .height(44.dp)
                    .testTag("subtab_colors"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (subTabMode == 2) Color(0xFF2ECC71) else Color(0xFFE2E8F0),
                    contentColor = if (subTabMode == 2) Color.White else Color(0xFF2D3748)
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
            ) {
                Text("🎨 Color Fun", fontSize = 14.sp, fontWeight = FontWeight.Black)
            }

            Button(
                onClick = { subTabMode = 3 },
                modifier = Modifier
                    .height(44.dp)
                    .testTag("subtab_poems"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (subTabMode == 3) Color(0xFF9B59B6) else Color(0xFFE2E8F0),
                    contentColor = if (subTabMode == 3) Color.White else Color(0xFF2D3748)
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
            ) {
                Text("🎵 Kids Poems", fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid lists or custom screens based on selected book category
        when (subTabMode) {
            0 -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 80.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("alphabet_book_grid"),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(LetterRepository.letters) { item ->
                        ClickableGridItem(
                            title = item.char.toString(),
                            emoji = item.emoji,
                            desc = item.word,
                            isAlphabet = true,
                            onTap = { onLetterTap(item) }
                        )
                    }
                }
            }
            1 -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 85.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("numbers_book_grid"),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(NumberRepository) { item ->
                        ClickableGridItem(
                            title = item.value.toString(),
                            emoji = getSingleEmojiForNumber(item.value),
                            desc = item.word,
                            isAlphabet = false,
                            onTap = { onNumberTap(item) }
                        )
                    }
                }
            }
            2 -> {
                ColorsAndShapesSubTab(onSpeak = onSpeak)
            }
            3 -> {
                NurseryPoemsSubTab(onSpeak = onSpeak)
            }
        }
    }
}

// ==========================================
// SUB-TAB: LEARN 12 RAINBOW COLORS & COLORING SHAPES
// ==========================================
@Composable
fun ColorsAndShapesSubTab(
    onSpeak: (String) -> Unit
) {
    var activeColorIndex by remember { mutableStateOf(0) }
    val activeColor = PlayroomColorsList[activeColorIndex]

    // We allow kids to choose a template to paint:
    // 0: Apple, 1: Butterfly, 2: Rocket
    var activeShapeTemplate by remember { mutableStateOf(0) }

    // Store local colors for shape parts.
    val applePartsColor = remember { mutableStateMapOf("Body" to Color(0xFFEA2027), "Stem" to Color(0xFF8B4513), "Leaf" to Color(0xFF2ECC71)) }
    val butterflyPartsColor = remember { mutableStateMapOf("Wings" to Color(0xFF9B59B6), "Body" to Color(0xFFFFC312), "Antennas" to Color(0xFFEE5A24)) }
    val rocketPartsColor = remember { mutableStateMapOf("Nose" to Color(0xFF0652DD), "Body" to Color(0xFF95A5A6), "Thrusters" to Color(0xFFEE5A24), "Window" to Color(0xFF12CBC4)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        // Heading Colors learn
        Text(
            text = "Learn 12 Rainbow Colors! 🎨🌈",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF2C3E50),
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Text(
            text = "Tap active color card to vocalize names & learn matching items!",
            fontSize = 11.sp,
            color = Color(0xFF7F8C8D),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Grid of 12 beautiful colors
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PlayroomColorsList.forEachIndexed { idx, colorInfo ->
                val isSelected = activeColorIndex == idx
                Card(
                    modifier = Modifier
                        .size(height = 68.dp, width = 85.dp)
                        .border(
                            width = if (isSelected) 3.5.dp else 1.5.dp,
                            color = if (isSelected) Color(0xFF2C3E50) else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            activeColorIndex = idx
                            onSpeak(colorInfo.speech)
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colorInfo.colorVal.copy(alpha = 0.15f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(4.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = colorInfo.emoji, fontSize = 21.sp)
                        Text(
                            text = colorInfo.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (colorInfo.name == "White") Color.DarkGray else colorInfo.colorVal
                        )
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))

        // Shape Painting Title
        Text(
            text = "Fruit & Animal Coloring Fun! 🍎🦋🚀",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF2C3E50)
        )
        Text(
            text = "Choose a shape template below, tap a palette color, then click outline parts to fill!",
            fontSize = 11.sp,
            color = Color(0xFF7F8C8D),
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Template selector chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("🍎 Sweet Apple", "🦋 Big Butterfly", "🚀 Cosmic Rocket").forEachIndexed { index, label ->
                val isSelected = activeShapeTemplate == index
                Button(
                    onClick = {
                        activeShapeTemplate = index
                        onSpeak("Let's paint the " + label.substring(3))
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFF2ECC71) else Color(0xFFF1F2F6),
                        contentColor = if (isSelected) Color.White else Color.DarkGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        // The Painting Canvas Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .shadow(4.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFBFD)),
            border = BorderStroke(2.dp, Color(0xFFE2E8F0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                // Render custom vector outline based on chosen template
                when (activeShapeTemplate) {
                    0 -> { // Apple Fruit
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                // Leaf Part
                                Box(
                                    modifier = Modifier
                                        .size(width = 62.dp, height = 36.dp)
                                        .shadow(1.dp, RoundedCornerShape(18.dp))
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(applePartsColor["Leaf"] ?: Color.Green)
                                        .border(2.5.dp, Color.White, RoundedCornerShape(18.dp))
                                        .clickable {
                                            applePartsColor["Leaf"] = activeColor.colorVal
                                            onSpeak("Painted leaf " + activeColor.name)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Green Leaf 🌿", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.White)
                                }

                                // Stem Part
                                Box(
                                    modifier = Modifier
                                        .size(width = 24.dp, height = 48.dp)
                                        .shadow(1.dp, RoundedCornerShape(6.dp))
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(applePartsColor["Stem"] ?: Color(0xFF8B4513))
                                        .border(2.5.dp, Color.White, RoundedCornerShape(6.dp))
                                        .clickable {
                                            applePartsColor["Stem"] = activeColor.colorVal
                                            onSpeak("Painted stem " + activeColor.name)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Stem", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.White)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Apple Body
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .shadow(2.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(applePartsColor["Body"] ?: Color.Red)
                                    .border(3.5.dp, Color.White, CircleShape)
                                    .clickable {
                                        applePartsColor["Body"] = activeColor.colorVal
                                        onSpeak("Painted apple body " + activeColor.name)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("APPLE BODY 🍎", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    Text("Tap to fill paint", fontSize = 8.sp, color = Color.White.copy(alpha = 0.8f))
                                }
                            }
                        }
                    }
                    1 -> { // Butterfly
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left Wing
                                Box(
                                    modifier = Modifier
                                        .size(width = 85.dp, height = 110.dp)
                                        .shadow(1.dp, RoundedCornerShape(40.dp))
                                        .clip(RoundedCornerShape(40.dp))
                                        .background(butterflyPartsColor["Wings"] ?: Color.Magenta)
                                        .border(2.5.dp, Color.White, RoundedCornerShape(40.dp))
                                        .clickable {
                                            butterflyPartsColor["Wings"] = activeColor.colorVal
                                            onSpeak("Painted left wing " + activeColor.name)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Left Wing 🦋", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Butterfly Central Body
                                Box(
                                    modifier = Modifier
                                        .size(width = 38.dp, height = 130.dp)
                                        .shadow(1.dp, RoundedCornerShape(18.dp))
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(butterflyPartsColor["Body"] ?: Color.Yellow)
                                        .border(2.5.dp, Color.White, RoundedCornerShape(18.dp))
                                        .clickable {
                                            butterflyPartsColor["Body"] = activeColor.colorVal
                                            onSpeak("Painted center body " + activeColor.name)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Body 🐝", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.DarkGray, textAlign = TextAlign.Center)
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Right Wing
                                Box(
                                    modifier = Modifier
                                        .size(width = 85.dp, height = 110.dp)
                                        .shadow(1.dp, RoundedCornerShape(40.dp))
                                        .clip(RoundedCornerShape(40.dp))
                                        .background(butterflyPartsColor["Wings"] ?: Color.Magenta)
                                        .border(2.5.dp, Color.White, RoundedCornerShape(40.dp))
                                        .clickable {
                                            butterflyPartsColor["Wings"] = activeColor.colorVal
                                            onSpeak("Painted right wing " + activeColor.name)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Right Wing 🦋", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
                                }
                            }
                        }
                    }
                    2 -> { // Rocket
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Rocket nose cone
                            Box(
                                modifier = Modifier
                                    .size(width = 54.dp, height = 40.dp)
                                    .shadow(1.dp, RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                                    .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                                    .background(rocketPartsColor["Nose"] ?: Color.Blue)
                                    .border(2.5.dp, Color.White, RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                                    .clickable {
                                        rocketPartsColor["Nose"] = activeColor.colorVal
                                        onSpeak("Painted nose cone " + activeColor.name)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Nose 🚀", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }

                            // Rocket Body cabin
                            Box(
                                modifier = Modifier
                                    .size(width = 68.dp, height = 90.dp)
                                    .shadow(1.dp, RoundedCornerShape(4.dp))
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(rocketPartsColor["Body"] ?: Color.Gray)
                                    .border(2.5.dp, Color.White, RoundedCornerShape(4.dp))
                                    .clickable {
                                        rocketPartsColor["Body"] = activeColor.colorVal
                                        onSpeak("Painted capsule body " + activeColor.name)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                // Cabin window inside rocket
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .shadow(1.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(rocketPartsColor["Window"] ?: Color.Cyan)
                                        .border(2.dp, Color.White, CircleShape)
                                        .clickable {
                                            rocketPartsColor["Window"] = activeColor.colorVal
                                            onSpeak("Painted cabin window " + activeColor.name)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Window 💎", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                }
                            }

                            // Thrusters / Engine Fire
                            Box(
                                modifier = Modifier
                                    .size(width = 44.dp, height = 30.dp)
                                    .shadow(1.dp, RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp))
                                    .clip(RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp))
                                    .background(rocketPartsColor["Thrusters"] ?: Color.Red)
                                    .border(2.5.dp, Color.White, RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp))
                                    .clickable {
                                        rocketPartsColor["Thrusters"] = activeColor.colorVal
                                        onSpeak("Painted rocket fire " + activeColor.name)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Fire 🔥", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Reset and speak designs bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    applePartsColor["Body"] = Color(0xFFEA2027)
                    applePartsColor["Leaf"] = Color(0xFF2ECC71)
                    applePartsColor["Stem"] = Color(0xFF8B4513)

                    butterflyPartsColor["Wings"] = Color(0xFF9B59B6)
                    butterflyPartsColor["Body"] = Color(0xFFFFC312)
                    butterflyPartsColor["Antennas"] = Color(0xFFEE5A24)

                    rocketPartsColor["Nose"] = Color(0xFF0652DD)
                    rocketPartsColor["Body"] = Color(0xFF95A5A6)
                    rocketPartsColor["Thrusters"] = Color(0xFFEE5A24)
                    rocketPartsColor["Window"] = Color(0xFF12CBC4)

                    onSpeak("Colors reset to default outlines!")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF718096)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1.0f)
                    .height(40.dp)
            ) {
                Text("Reset Outline Colors 🔄", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    onSpeak("Wow! That looks absolutely beautiful and creative! Good job!")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1.0f)
                    .height(40.dp)
            ) {
                Text("Speak My Artwork! 🗣️✨", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// SUB-TAB: KIDS INTERACTIVE NURSERY POEMS RECITER
// ==========================================
data class KidsPoem(
    val title: String,
    val icon: String,
    val bgGradient: List<Color>,
    val lines: List<String>
)

val KidsPoemsList = listOf(
    KidsPoem(
        title = "Twinkle Twinkle Little Star 🌟",
        icon = "⭐",
        bgGradient = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A)),
        lines = listOf(
            "Twinkle, twinkle, little star,",
            "How I wonder what you are!",
            "Up above the world so high,",
            "Like a diamond in the sky.",
            "Twinkle, twinkle, little star,",
            "How I wonder what you are!"
        )
    ),
    KidsPoem(
        title = "Humpty Dumpty Sat on a Wall 🥚",
        icon = "🧱",
        bgGradient = listOf(Color(0xFFFFECEF), Color(0xFFFFCCD5)),
        lines = listOf(
            "Humpty Dumpty sat on a wall,",
            "Humpty Dumpty had a great fall;",
            "All the king's horses and all the king's men,",
            "Couldn't put Humpty together again."
        )
    ),
    KidsPoem(
        title = "Baa Baa Black Sheep 🐑",
        icon = "🐑",
        bgGradient = listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1)),
        lines = listOf(
            "Baa, baa, black sheep,",
            "Have you any wool?",
            "Yes, sir, yes, sir,",
            "Three bags full;",
            "One for the master,",
            "And one for the dame,",
            "And one for the little boy",
            "Who lives down the lane."
        )
    ),
    KidsPoem(
        title = "The Itsy Bitsy Spider 🕷️",
        icon = "🌧️",
        bgGradient = listOf(Color(0xFFE6FFFA), Color(0xFFB2F5EA)),
        lines = listOf(
            "The itsy bitsy spider climbed up the waterspout.",
            "Down came the rain and washed the spider out.",
            "Out came the sun and dried up all the rain,",
            "And the itsy bitsy spider climbed up the spout again."
        )
    )
)

@Composable
fun NurseryPoemsSubTab(
    onSpeak: (String) -> Unit
) {
    var selectedPoemIndex by remember { mutableStateOf(0) }
    val poem = KidsPoemsList[selectedPoemIndex]

    var currentPlayingLine by remember { mutableStateOf(-1) }
    val coroutineScope = rememberCoroutineScope()
    var playbackJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    // Clean up playback when leaving
    DisposableEffect(Unit) {
        onDispose {
            playbackJob?.cancel()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        // Explanations Header
        Text(
            text = "Nursery Rhymes & Poems 🎵🌟",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF2C3E50),
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Text(
            text = "Tap any nursery rhyme to read and trigger highlighted text-to-speech recite-along!",
            fontSize = 11.sp,
            color = Color(0xFF7F8C8D),
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Row select horizontal lists
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KidsPoemsList.forEachIndexed { index, p ->
                val isSelected = selectedPoemIndex == index
                Card(
                    modifier = Modifier
                        .size(height = 68.dp, width = 140.dp)
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) Color(0xFF9B59B6) else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            playbackJob?.cancel()
                            currentPlayingLine = -1
                            selectedPoemIndex = index
                            onSpeak("Selected " + p.title)
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFF3E8FF) else Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = p.icon, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = p.title.replace(" 🌟", "").replace(" 🥚", "").replace(" 🐑", "").replace(" 🕷️", ""),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2D3748)
                        )
                    }
                }
            }
        }

        // Active Poem Card Layout
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(2.5.dp, Color(0xFFF3E8FF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header of active rhyme
                Text(
                    text = poem.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF9B59B6)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // The Lyric sheet block
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                    border = BorderStroke(1.dp, Color(0xFFEADBFF))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        poem.lines.forEachIndexed { idx, line ->
                            val isHighlighted = currentPlayingLine == idx
                            Surface(
                                color = if (isHighlighted) Color(0xFFFFD124) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
                            ) {
                                Text(
                                    text = line,
                                    fontSize = if (isHighlighted) 15.sp else 13.sp,
                                    fontWeight = if (isHighlighted) FontWeight.Black else FontWeight.Medium,
                                    color = if (isHighlighted) Color.Black else Color(0xFF4A5568),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Recite Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            playbackJob?.cancel()
                            playbackJob = coroutineScope.launch {
                                for (i in poem.lines.indices) {
                                    currentPlayingLine = i
                                    val lyric = poem.lines[i]
                                    onSpeak(lyric)
                                    delay(4000) // Keep reading with steady delays
                                }
                                currentPlayingLine = -1
                                onSpeak("Aww, beautiful reading! Tap again to repeat!")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9B59B6)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(46.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("📢 Play", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            playbackJob?.cancel()
                            currentPlayingLine = -1
                            onSpeak("Paused.")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(0.8f)
                            .height(46.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Stop", tint = Color.DarkGray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pause ⏸️", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                }
            }
        }
    }
}

// Helper to get matching single emoji for counting
fun getSingleEmojiForNumber(value: Int): String {
    return when (value) {
        1 -> "🍎"
        2 -> "🧸"
        3 -> "🐱"
        4 -> "🍀"
        5 -> "🍒"
        6 -> "🐝"
        7 -> "🌈"
        8 -> "🐙"
        9 -> "🎈"
        10 -> "⭐️"
        11 -> "⚽"
        12 -> "🍨"
        13 -> "🦋"
        14 -> "🧁"
        15 -> "🚀"
        16 -> "🦖"
        17 -> "🍩"
        18 -> "🦁"
        19 -> "🍉"
        20 -> "🚗"
        else -> "✨"
    }
}

// Side-by-side count arrangement layout for kids to count easily
@Composable
fun NumberEmojisDisplay(
    value: Int,
    emojiSingle: String,
    maxPerRow: Int = 5
) {
    val itemsToShow = value.coerceIn(1, 20)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
    ) {
        val rows = (itemsToShow + maxPerRow - 1) / maxPerRow
        for (r in 0 until rows) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 1.dp)
            ) {
                val cols = minOf(maxPerRow, itemsToShow - r * maxPerRow)
                repeat(cols) {
                    Text(
                        text = emojiSingle,
                        fontSize = if (value > 10) 13.sp else if (value > 5) 15.sp else 19.sp,
                        modifier = Modifier.padding(horizontal = 1.5.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ClickableGridItem(
    title: String,
    emoji: String,
    desc: String,
    isAlphabet: Boolean,
    onTap: () -> Unit
) {
    // Determine vibrant kid-friendly color
    val colorIndex = title.firstOrNull()?.code ?: title.length
    val bgColors = listOf(
        Color(0xFFFFD2D2), // Soft Strawberry
        Color(0xFFD6EEFF), // Soft Blue
        Color(0xFFD2FFD2), // Soft Green
        Color(0xFFFFF0D2), // Soft yellow
        Color(0xFFEADBFF), // Soft violet
        Color(0xFFFFE3F0)  // Soft pink
    )
    val cardColor = bgColors[colorIndex % bgColors.size]

    val borderColors = listOf(
        Color(0xFFFF7675),
        Color(0xFF0984E3),
        Color(0xFF2ECC71),
        Color(0xFFF1C40F),
        Color(0xFF9B59B6),
        Color(0xFFFD79A8)
    )
    val ringColor = borderColors[colorIndex % borderColors.size]

    Card(
        modifier = Modifier
            .aspectRatio(0.95f)
            .shadow(4.dp, RoundedCornerShape(22.dp))
            .clickable { onTap() }
            .testTag("book_item_$isAlphabet" + "_$title"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(2.5.dp, ringColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF2C3E50),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            if (isAlphabet) {
                Text(
                    text = emoji,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            } else {
                val numVal = title.toIntOrNull() ?: 1
                NumberEmojisDisplay(
                    value = numVal,
                    emojiSingle = getSingleEmojiForNumber(numVal)
                )
            }
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = desc,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF7F8C8D),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==========================================
// DIALOG OVERLAY POPUP FOR LEARNING DETAILS
// ==========================================

@Composable
fun NurseryPopupCard(
    title: String,
    word: String,
    emoji: String,
    colorAccent: Color,
    onDismiss: () -> Unit,
    onSpeakAgain: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .padding(16.dp)
                .shadow(12.dp, RoundedCornerShape(32.dp))
                .testTag("nursery_detail_popup"),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(4.dp, colorAccent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Exit button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFF1F2F6), CircleShape)
                            .testTag("popup_dismiss_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.DarkGray
                        )
                    }
                }

                // Gigantic highlighted letter/number symbol
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(colorAccent.copy(alpha = 0.15f), CircleShape)
                        .border(3.dp, colorAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 62.sp,
                        fontWeight = FontWeight.Black,
                        color = colorAccent,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Gigantic image/emoji or side-by-side cute counting display
                val isNumber = title.toIntOrNull() != null
                Box(
                    modifier = Modifier
                        .size(if (isNumber) 180.dp else 130.dp)
                        .shadow(2.dp, RoundedCornerShape(24.dp))
                        .background(Color(0xFFF8F9FA), RoundedCornerShape(24.dp))
                        .padding(if (isNumber) 10.dp else 0.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isNumber) {
                        val numVal = title.toInt()
                        NumberEmojisDisplay(
                            value = numVal,
                            emojiSingle = getSingleEmojiForNumber(numVal)
                        )
                    } else {
                        Text(
                            text = emoji,
                            fontSize = 76.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Nursery book formatting label
                Text(
                    text = word,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2D3748)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Interactive Audio speaker
                Button(
                    onClick = onSpeakAgain,
                    colors = ButtonDefaults.buttonColors(containerColor = colorAccent),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("popup_speak_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Speak Aloud",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sound Aloud! 🔊", fontSize = 16.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

// ==========================================
// TAB 2: THE CIRCULAR EXPLORER WHEEL
// ==========================================

@Composable
fun RoundWheelExplorer(
    onSpeak: (String) -> Unit
) {
    val letters = LetterRepository.letters
    var selectedIndex by remember { mutableStateOf(0) }
    val selectedLetter = letters[selectedIndex]
    
    var isAutoplayActive by remember { mutableStateOf(false) }

    // Manual speech when user changes letter (only if autoplay is not running to avoid race speech)
    LaunchedEffect(selectedIndex) {
        if (!isAutoplayActive) {
            onSpeak("${selectedLetter.char} for ${selectedLetter.word}")
        }
    }

    // Autoplay A-Z reader sequence loop
    LaunchedEffect(isAutoplayActive) {
        if (isAutoplayActive) {
            while (isAutoplayActive) {
                val activeL = letters[selectedIndex]
                onSpeak("${activeL.char} is for ${activeL.word}")
                kotlinx.coroutines.delay(2600) // kid-friendly pacing
                if (isAutoplayActive) {
                    selectedIndex = (selectedIndex + 1) % 26
                }
            }
        }
    }

    // Vibrant colors matching keys in the image
    val keyStyler = remember {
        listOf(
            Color(0xFFFF5252) to Color(0xFFFFF1F1), // Red
            Color(0xFF2979FF) to Color(0xFFE3F2FD), // Blue
            Color(0xFF00E676) to Color(0xFFE8F5E9), // Green
            Color(0xFFFFB300) to Color(0xFFFFF8E1), // Yellow/Gold
            Color(0xFFFF9100) to Color(0xFFFFF3E0), // Orange
            Color(0xFFD500F9) to Color(0xFFF3E5F5), // Purple
            Color(0xFFF50057) to Color(0xFFFCE4EC)  // Magenta
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ALPHABET WORLD 🎨✨",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF2C3E50),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Tap any letter below or try the Autoplay Reader!",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7F8C8D),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // LARGE CENTRAL SHOWCASE CARD (Style like in the image but rectangular form)
        val borderStyle = keyStyler[selectedIndex % keyStyler.size]
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(180.dp)
                .shadow(12.dp, RoundedCornerShape(28.dp))
                .testTag("explorer_center_showcase"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(4.dp, borderStyle.first)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .border(2.5.dp, borderStyle.first.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                    .background(borderStyle.second.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Big Rounded Letter box (Like image center)
                    Card(
                        modifier = Modifier
                            .size(110.dp)
                            .shadow(6.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(3.dp, borderStyle.first)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = selectedLetter.char.toString(),
                                fontSize = 68.sp,
                                fontWeight = FontWeight.Black,
                                color = borderStyle.first
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = selectedLetter.emoji,
                            fontSize = 62.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = selectedLetter.word,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2C3E50),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Selected Letter",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7F8C8D),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // THE AUTOPLAY A-Z READER CONTROL ROW
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(52.dp)
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isAutoplayActive) Color(0xFFE8F5E9) else Color(0xFFFFF9DB)
            ),
            border = BorderStroke(
                2.dp,
                if (isAutoplayActive) Color(0xFF2ECC71) else Color(0xFFF1C40F)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isAutoplayActive) "🎙️ Reading Letter: " else "🎵 Letter Play: ",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2C3E50)
                    )
                    Text(
                        text = selectedLetter.char.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = borderStyle.first
                    )
                }

                Button(
                    onClick = {
                        isAutoplayActive = !isAutoplayActive
                        if (isAutoplayActive) {
                            onSpeak("Starting A to Z Autoplay Reading!")
                        } else {
                            onSpeak("Autoplay Paused")
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAutoplayActive) Color(0xFFE74C3C) else Color(0xFF2ECC71)
                    ),
                    modifier = Modifier.height(34.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = if (isAutoplayActive) "Pause ⏸️" else "Autoplay A-Z 🔊▶️",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // GRID OF ALL 26 ALPHABET TILE KEYS (REPLACES THE WHEEL)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFFFFDF5), RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFFFFECEF), RoundedCornerShape(24.dp))
                .padding(10.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(26) { i ->
                    val letterItem = letters[i]
                    val isHighlighted = i == selectedIndex
                    val itemStyle = keyStyler[i % keyStyler.size]

                    // Pulse or bold border if selected
                    val customBg = if (isHighlighted) itemStyle.second else Color.White
                    val customBorderColor = itemStyle.first
                    val customBorderSize = if (isHighlighted) 3.5.dp else 1.8.dp

                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .shadow(if (isHighlighted) 6.dp else 2.dp, RoundedCornerShape(14.dp))
                            .clickable {
                                isAutoplayActive = false // pause autocomplete on click
                                selectedIndex = i
                            }
                            .testTag("key_node_${letterItem.char}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = customBg),
                        border = BorderStroke(customBorderSize, customBorderColor)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letterItem.char.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = itemStyle.first
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 3: THE WATERMARK FREE-DRAW board (REMOVE INK LIMIT)
// ==========================================

@Composable
fun DrawStudio(
    viewModel: PlayroomViewModel,
    onSpeak: (String) -> Unit
) {
    val strokes = viewModel.paintStrokes
    val currentPoints = viewModel.currentStrokePoints
    val activeColor = viewModel.activePenColor.value
    val activeBrush = viewModel.activePenBrush.value
    val activeMode = viewModel.drawingWithLetterMode.value

    val activeLetter = viewModel.selectedDrawingLetter.value
    val activeNumber = viewModel.selectedDrawingNumber.value

    val overlayLetterText = if (activeMode) activeLetter.char.toString() else activeNumber.value.toString()
    val subTextMark = if (activeMode) "for " + activeLetter.word else "for " + activeNumber.word
    val emojiMark = if (activeMode) activeLetter.emoji else activeNumber.emoji

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // Mode switch + Guide letter dropdown row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    viewModel.drawingWithLetterMode.value = !activeMode
                    viewModel.clearDrawingPaths()
                    onSpeak("Changed target background guide!")
                },
                modifier = Modifier
                    .weight(1.1f)
                    .height(44.dp)
                    .testTag("draw_mode_toggle"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00CEC9))
            ) {
                Text(
                    text = if (activeMode) "🌟 Switch Numbers" else "🌸 Switch Letters",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Dropdown selection horizontal carousel
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, Color(0xFF00CEC9), RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                if (activeMode) {
                    var isDropdownExpanded by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { isDropdownExpanded = true }
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Letter: ${activeLetter.char} ▾",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2C3E50)
                        )
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            LetterRepository.letters.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text("${item.char} (${item.word})") },
                                    onClick = {
                                        viewModel.selectedDrawingLetter.value = item
                                        viewModel.clearDrawingPaths()
                                        isDropdownExpanded = false
                                        onSpeak("Let's draw letter ${item.char}")
                                    }
                                )
                            }
                        }
                    }
                } else {
                    var isDropdownExpanded by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { isDropdownExpanded = true }
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Number: ${activeNumber.value} ▾",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2C3E50)
                        )
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            NumberRepository.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text("${item.value} (${item.word})") },
                                    onClick = {
                                        viewModel.selectedDrawingNumber.value = item
                                        viewModel.clearDrawingPaths()
                                        isDropdownExpanded = false
                                        onSpeak("Let's draw number ${item.value}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Chalkboard Drawing Canvas Area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(26.dp))
                .testTag("draw_studio_chalkboard"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2C3E50)), // Chalkboard slate
            border = BorderStroke(3.dp, Color(0xFF7F8C8D))
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Background faint Watermark guidelines of targets
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = overlayLetterText,
                            fontSize = 280.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0x11FFFFFF), // faint watermark
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "$subTextMark $emojiMark",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0x1AFFFFFF),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Drawing Pointer Canvas component
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(activeColor, activeBrush) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        currentPoints.add(offset)
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        currentPoints.add(change.position)
                                    },
                                    onDragEnd = {
                                        if (currentPoints.isNotEmpty()) {
                                            strokes.add(
                                                DrawStroke(
                                                    points = currentPoints.toList(),
                                                    color = activeColor,
                                                    brush = activeBrush
                                                )
                                            )
                                            currentPoints.clear()
                                        }
                                    }
                                )
                            }
                            .testTag("pen_canvas")
                    ) {
                        // Drawing large beautiful dashed tracing guide
                        val dashedStroke = Stroke(
                            width = 12f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 15f), 0f),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                        val guideColor = Color(0xCCFFFA65) // Ultra-bright neon yellow chalk guide color

                        if (activeMode) {
                            // Draw alphabet strokes
                            activeLetter.styles.firstOrNull()?.strokes?.forEach { stroke ->
                                if (stroke.points.size >= 2) {
                                    val path = Path().apply {
                                        val startX = stroke.points.first().x * size.width
                                        val startY = stroke.points.first().y * size.height
                                        moveTo(startX, startY)
                                        for (idx in 1..stroke.points.lastIndex) {
                                            val pX = stroke.points[idx].x * size.width
                                            val pY = stroke.points[idx].y * size.height
                                            lineTo(pX, pY)
                                        }
                                    }
                                    drawPath(path = path, color = guideColor, style = dashedStroke)
                                }
                            }
                        } else {
                            // Draw custom number strokes
                            val numberStrokesList = when (activeNumber.value) {
                                1 -> listOf(listOf(0.5f to 0.2f, 0.5f to 0.8f))
                                2 -> listOf(listOf(0.35f to 0.3f, 0.5f to 0.2f, 0.65f to 0.3f, 0.35f to 0.8f, 0.65f to 0.8f))
                                3 -> listOf(listOf(0.35f to 0.25f, 0.65f to 0.25f, 0.5f to 0.5f, 0.65f to 0.5f, 0.65f to 0.75f, 0.35f to 0.75f))
                                4 -> listOf(
                                    listOf(0.5f to 0.2f, 0.3f to 0.6f, 0.7f to 0.6f),
                                    listOf(0.6f to 0.45f, 0.6f to 0.8f)
                                )
                                5 -> listOf(listOf(0.65f to 0.22f, 0.38f to 0.22f, 0.38f to 0.5f, 0.65f to 0.5f, 0.65f to 0.78f, 0.35f to 0.78f))
                                6 -> listOf(listOf(0.6f to 0.22f, 0.4f to 0.52f, 0.6f to 0.52f, 0.6f to 0.78f, 0.4f to 0.78f, 0.4f to 0.52f))
                                7 -> listOf(listOf(0.35f to 0.22f, 0.65f to 0.22f, 0.42f to 0.78f))
                                8 -> listOf(listOf(0.5f to 0.5f, 0.35f to 0.35f, 0.5f to 0.2f, 0.65f to 0.35f, 0.5f to 0.5f, 0.35f to 0.65f, 0.5f to 0.8f, 0.65f to 0.65f, 0.5f to 0.5f))
                                9 -> listOf(
                                    listOf(0.5f to 0.5f, 0.35f to 0.5f, 0.35f to 0.22f, 0.65f to 0.22f, 0.65f to 0.5f, 0.5f to 0.5f),
                                    listOf(0.65f to 0.3f, 0.65f to 0.78f)
                                )
                                10 -> listOf(
                                    listOf(0.35f to 0.25f, 0.35f to 0.75f),
                                    listOf(0.65f to 0.3f, 0.55f to 0.3f, 0.55f to 0.7f, 0.75f to 0.7f, 0.75f to 0.3f, 0.65f to 0.3f)
                                )
                                else -> emptyList()
                            }
                            
                            numberStrokesList.forEach { points ->
                                if (points.size >= 2) {
                                    val path = Path().apply {
                                        val startX = points.first().first * size.width
                                        val startY = points.first().second * size.height
                                        moveTo(startX, startY)
                                        for (idx in 1..points.lastIndex) {
                                            val pX = points[idx].first * size.width
                                            val pY = points[idx].second * size.height
                                            lineTo(pX, pY)
                                        }
                                    }
                                    drawPath(path = path, color = guideColor, style = dashedStroke)
                                }
                            }
                        }

                        // Render previously confirmed paths
                        strokes.forEach { stroke ->
                            if (stroke.points.size >= 2) {
                                val strokePath = Path().apply {
                                    val pointsList = stroke.points
                                    moveTo(pointsList.first().x, pointsList.first().y)
                                    var prev = pointsList.first()
                                    for (idx in 1 until pointsList.size) {
                                        val current = pointsList[idx]
                                        val midX = (prev.x + current.x) / 2
                                        val midY = (prev.y + current.y) / 2
                                        quadraticTo(prev.x, prev.y, midX, midY)
                                        prev = current
                                    }
                                    lineTo(prev.x, prev.y)
                                }

                                when (stroke.brush) {
                                    BrushType.GLOW -> {
                                        drawPath(
                                            path = strokePath,
                                            color = stroke.color.copy(alpha = 0.45f),
                                            style = Stroke(width = 30f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                        )
                                        drawPath(
                                            path = strokePath,
                                            color = Color.White,
                                            style = Stroke(width = 10f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                        )
                                    }
                                    BrushType.RAINBOW -> {
                                        drawPath(
                                            path = strokePath,
                                            color = stroke.color,
                                            style = Stroke(width = stroke.thickness, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                        )
                                        drawPath(
                                            path = strokePath,
                                            color = Color(0xFFFF7675),
                                            style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                        )
                                    }
                                    BrushType.CRAYON -> {
                                        drawPath(
                                            path = strokePath,
                                            color = stroke.color.copy(alpha = 0.7f),
                                            style = Stroke(width = stroke.thickness, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                        )
                                    }
                                    BrushType.SPARKLE -> {
                                        drawPath(
                                            path = strokePath,
                                            color = Color.Yellow.copy(alpha = 0.5f),
                                            style = Stroke(width = 24f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                        )
                                        drawPath(
                                            path = strokePath,
                                            color = stroke.color,
                                            style = Stroke(width = 12f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                        )
                                    }
                                }
                            }
                        }

                        // Render currently active live stroke paths
                        if (currentPoints.size >= 2) {
                            val currentPath = Path().apply {
                                val pointsList = currentPoints
                                moveTo(pointsList.first().x, pointsList.first().y)
                                var prev = pointsList.first()
                                for (idx in 1 until pointsList.size) {
                                    val current = pointsList[idx]
                                    val midX = (prev.x + current.x) / 2
                                    val midY = (prev.y + current.y) / 2
                                    quadraticTo(prev.x, prev.y, midX, midY)
                                    prev = current
                                }
                                lineTo(prev.x, prev.y)
                            }
                            when (activeBrush) {
                                BrushType.GLOW -> {
                                    drawPath(
                                        path = currentPath,
                                        color = activeColor.copy(alpha = 0.4f),
                                        style = Stroke(width = 30f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )
                                    drawPath(
                                        path = currentPath,
                                        color = Color.White,
                                        style = Stroke(width = 10f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )
                                }
                                BrushType.RAINBOW -> {
                                    drawPath(
                                        path = currentPath,
                                        color = activeColor,
                                        style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )
                                    drawPath(
                                        path = currentPath,
                                        color = Color(0xFFFFD253),
                                        style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )
                                }
                                BrushType.CRAYON -> {
                                    drawPath(
                                        path = currentPath,
                                        color = activeColor.copy(alpha = 0.7f),
                                        style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )
                                }
                                BrushType.SPARKLE -> {
                                    drawPath(
                                        path = currentPath,
                                        color = Color.Yellow.copy(alpha = 0.5f),
                                        style = Stroke(width = 24f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )
                                    drawPath(
                                        path = currentPath,
                                        color = activeColor,
                                        style = Stroke(width = 12f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Color Pallet picker line with nice round bubbles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayroomColors.forEach { pColor ->
                val isSelected = activeColor == pColor.color
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(pColor.color)
                        .border(
                            width = if (isSelected) 3.dp else 0.dp,
                            color = Color.White,
                            shape = CircleShape
                        )
                        .clickable {
                            viewModel.activePenColor.value = pColor.color
                            onSpeak("Selected color ${pColor.name}")
                        }
                        .testTag("draw_color_${pColor.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = pColor.labelEmoji, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Brush style picker + Clear button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BrushType.values().forEach { style ->
                val isSelected = activeBrush == style
                Button(
                    onClick = {
                        viewModel.activePenBrush.value = style
                        onSpeak("Brush changed to ${style.label}")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("draw_brush_${style.name}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFFFF7675) else Color(0xFFF1F2F6),
                        contentColor = if (isSelected) Color.White else Color(0xFF2C3E50)
                    )
                ) {
                    Text(text = "${style.iconEmoji} ${style.label}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            IconButton(
                onClick = {
                    viewModel.clearDrawingPaths()
                    onSpeak("Cleared drawing board!")
                },
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFFFFECEF), RoundedCornerShape(10.dp))
                    .testTag("draw_clear_btn"),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear board",
                    tint = Color(0xFFD63031)
                )
            }
        }
    }
}

// ==========================================
// TAB 4: TODDLER GAMES HUB
// ==========================================

@Composable
fun GamesSandbox(
    onSpeak: (String) -> Unit
) {
    var gameMode by remember { mutableStateOf(0) } // 0: Game Picker, 1: Balloon Pop, 2: Feed Moiz, 3: Memory Match, 4: Multiple Quiz

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        if (gameMode == 0) {
            // Displays picker lobby cards
            Text(
                text = "Playroom Games 🎮🌟",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF2C3E50)
            )
            Text(
                text = "Pick an interactive game with Moiz!",
                fontSize = 12.sp,
                color = Color(0xFF7F8C8D),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                GameLobbyCard(
                    title = "🎈 Balloon Phonics Pop",
                    desc = "Pop matching balloons of the target letter with visual effects!",
                    containerColor = Color(0xFFD6EEFF),
                    onPlay = {
                        gameMode = 1
                        onSpeak("Tap the balloon with the target letter to pop!")
                    },
                    tag = "balloon_game_lobby"
                )

                GameLobbyCard(
                    title = "🧁 Feed Hungry Moiz!",
                    desc = "Help feed Moiz tasty treats starting with specified phonics!",
                    containerColor = Color(0xFFFFECEF),
                    onPlay = {
                        gameMode = 2
                        onSpeak("Feed Moiz treats starting with the letter!")
                    },
                    tag = "feed_game_lobby"
                )

                GameLobbyCard(
                    title = "🃏 Memory Match Cards",
                    desc = "Flip matching cards pairing letters with their Nursery Book objects!",
                    containerColor = Color(0xFFEADBFF),
                    onPlay = {
                        gameMode = 3
                        onSpeak("Find matching pairs of cards!")
                    },
                    tag = "memory_game_lobby"
                )

                GameLobbyCard(
                    title = "❓ Nursery Phonics Quiz",
                    desc = "Interactive multiple choices quiz to challenge kid skills!",
                    containerColor = Color(0xFFFFF0D2),
                    onPlay = {
                        gameMode = 4
                        onSpeak("Test items starting with the letters")
                    },
                    tag = "quiz_game_lobby"
                )

                GameLobbyCard(
                    title = "🔮 Floating Phonics Balls",
                    desc = "Pop balls floating upwards containing and pronouncing letters to practice sound combinations!",
                    containerColor = Color(0xFFE6FFFA),
                    onPlay = {
                        gameMode = 5
                        onSpeak("Tap floating balls to pop them and speak their letter sound!")
                    },
                    tag = "floating_balls_lobby"
                )

                GameLobbyCard(
                    title = "✏️ Draw & Match Lines",
                    desc = "Draw connections from letters A-D to their cute matching objects to earn your trophy!",
                    containerColor = Color(0xFFFFF0FE),
                    onPlay = {
                        gameMode = 6
                        onSpeak("Draw lines to connect letters on the left to their matched items on the right!")
                    },
                    tag = "draw_match_lobby"
                )
            }
        } else {
            // Screen-friendly scrollable Column to support compact/small mobile phone screens beautifully
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Display back to selector and embed sandbox game
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            gameMode = 0
                            onSpeak("Let's pick another adventure!")
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFF1F2F6), CircleShape)
                            .testTag("game_back_to_lobby")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.DarkGray
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Go to Games Lobby",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF7F8C8D)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                when (gameMode) {
                    1 -> BalloonPopGame(onSpeak = onSpeak)
                    2 -> FeedMoizGame(onSpeak = onSpeak)
                    3 -> MemoryMatchGame(onSpeak = onSpeak)
                    4 -> MultiChoiceQuizGame(onSpeak = onSpeak)
                    5 -> FloatingPhonicsBallsGame(onSpeak = onSpeak)
                    6 -> DrawLinesMatchingGame(onSpeak = onSpeak)
                }
            }
        }
    }
}

@Composable
fun GameLobbyCard(
    title: String,
    desc: String,
    containerColor: Color,
    onPlay: () -> Unit,
    tag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .testTag(tag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(2.dp, Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF2C3E50))
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, fontSize = 13.sp, color = Color(0xFF5B6977))
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onPlay,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF2C3E50)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(42.dp),
                border = BorderStroke(1.5.dp, Color(0xFFBDC3C7))
            ) {
                Text("Start Learning! 🚀", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// GAME 1: BALLOON PHONICS POP
// ==========================================

@Composable
fun BalloonPopGame(
    onSpeak: (String) -> Unit
) {
    var letterTargets by remember { mutableStateOf(('A'..'Z').toList().shuffled()) }
    var activeTargetIndex by remember { mutableStateOf(0) }
    val activeChar = letterTargets.getOrNull(activeTargetIndex) ?: 'A'
    val activeLetter = LetterRepository.letters.find { it.char == activeChar } ?: LetterRepository.letters[0]

    // Create 3 balloon text labels (1 correct, 2 randomized helpers)
    var choices by remember(activeChar) {
        mutableStateOf(
            listOf(activeChar)
                .plus(('A'..'Z').filter { it != activeChar }.shuffled().take(2))
                .shuffled()
        )
    }

    var feedbackString by remember { mutableStateOf("Look at the target and pop the correct balloon! 🍿🎈") }
    var popTriggeredIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(activeChar) {
        onSpeak("Find letter ${activeChar} in the balloons!")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .shadow(3.dp, RoundedCornerShape(24.dp))
            .testTag("balloon_game_canvas"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEBF8FF)),
        border = BorderStroke(1.5.dp, Color(0xFF90CDF4))
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Balloon Phonics Pop! 🎈",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF2B6CB0)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // GORGEOUS TARGET CARD - Tells children exactly what to find visually without complex reading
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(16.dp))
                    .clickable { onSpeak("Find letter $activeChar!") }
                    .padding(8.dp)
                    .border(1.5.dp, Color(0xFF90CDF4), RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = "🔊 TAP TO HEAR: FIND ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E3A8A)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Card(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(4.dp, CircleShape),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(2.5.dp, Color(0xFF2B6CB0))
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = activeChar.toString(),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2B6CB0)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = feedbackString,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A5568),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Balloon row selectors of choice items
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                choices.forEachIndexed { idx, choice ->
                    val isPopped = popTriggeredIndex == idx
                    
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .shadow(if (isPopped) 0.dp else 4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                if (isPopped) Color.Transparent else when (idx) {
                                    0 -> Color(0xFFFF7675)
                                    1 -> Color(0xFF3498DB)
                                    else -> Color(0xFF2ECC71)
                                }
                            )
                            .clickable {
                                if (popTriggeredIndex != null) return@clickable
                                if (choice == activeChar) {
                                    popTriggeredIndex = idx
                                    feedbackString = "🌟 POPPED! Correct! ${activeLetter.char} is for ${activeLetter.word} ${activeLetter.emoji}! 🎉"
                                    onSpeak("Correct! ${activeLetter.char} for ${activeLetter.word}!")

                                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                        activeTargetIndex = (activeTargetIndex + 1) % letterTargets.size
                                        popTriggeredIndex = null
                                        feedbackString = "Get ready to match the next letter! 🎈"
                                    }, 2500)
                                } else {
                                    feedbackString = "Oops, try again! Tap the balloon for $activeChar! 😉"
                                    onSpeak("Try again!")
                                }
                            }
                            .testTag("balloon_item_$choice"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!isPopped) {
                            Text(
                                text = choice.toString(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        } else {
                            Text("💥 pop!", fontSize = 20.sp, color = Color(0xFFE17055))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Progress tracking
            Text(
                text = "Score milestone progress: ${activeTargetIndex} Popped! ⭐",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF718096)
            )
        }
    }
}

// ==========================================
// GAME 2: FEED HUNGRY MOIZ!
// ==========================================

@Composable
fun FeedMoizGame(
    onSpeak: (String) -> Unit
) {
    val itemsPool = LetterRepository.letters
    var levelIndex by remember { mutableStateOf(0) }
    val activeLevelLetter = itemsPool[levelIndex % itemsPool.size]

    var choiceDishes by remember(activeLevelLetter.char) {
        val matchesText = activeLevelLetter.word.uppercase()
        val matchFood = "$matchesText ${activeLevelLetter.emoji}"
        
        val filteredOpponents = itemsPool.filter { it.char != activeLevelLetter.char }
        val chosenOpponents = filteredOpponents.shuffled().take(2)
        val list = mutableListOf(matchFood)
        
        chosenOpponents.forEach { item ->
            list.add("${item.word.uppercase()} ${item.emoji}")
        }
        mutableStateOf(list.shuffled())
    }

    var feedingSuccessTrigger by remember { mutableStateOf(false) }
    var promptCaption by remember { mutableStateOf("A treat starting with '${activeLevelLetter.char}'!") }

    LaunchedEffect(activeLevelLetter.char) {
        onSpeak("Moiz wants a treat starting with ${activeLevelLetter.char}!")
        promptCaption = "A treat starting with '${activeLevelLetter.char}'!"
        feedingSuccessTrigger = false
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .testTag("feed_game_canvas"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
        border = BorderStroke(1.5.dp, Color(0xFFFEB2B2))
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Feed Hungry Mascot Moiz! 🧁👦",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFC53030)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Big mascot feedback animation
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(3.dp, if (feedingSuccessTrigger) Color(0xFF2ECC71) else Color(0xFFE2E8F0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_moiz),
                    contentDescription = "Moiz Mascot feeding animation",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (feedingSuccessTrigger) "Moiz: 'YUMMY! Thank you! 😋'" else "Moiz: 'I am hungry! Quick!'",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = if (feedingSuccessTrigger) Color(0xFF27AE60) else Color(0xFFC53030)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // EXTREMELY VISUAL CHEERFUL PROMPT CARD FOR KIDS
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clickable { onSpeak("Feed Moiz a treat starting with the letter ${activeLevelLetter.char}!") }
                    .shadow(4.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
                border = BorderStroke(2.5.dp, Color(0xFFFEB2B2))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🔊 FEED MOIZ: ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFC53030)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Card(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(4.dp, CircleShape),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(3.dp, Color(0xFFC53030))
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = activeLevelLetter.char.toString(),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFC53030)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = promptCaption,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7F8C8D),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Render options dishes
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                choiceDishes.forEach { choice ->
                    val isCorrect = choice.startsWith(activeLevelLetter.word.uppercase())
                    
                    Button(
                        onClick = {
                            if (feedingSuccessTrigger) return@Button
                            if (isCorrect) {
                                feedingSuccessTrigger = true
                                promptCaption = "AMAZING! Fed Moiz ${activeLevelLetter.word}!"
                                onSpeak("Yummy! Thank you for the ${activeLevelLetter.word}!")

                                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                    levelIndex = (levelIndex + 1) % itemsPool.size
                                    feedingSuccessTrigger = false
                                }, 2500)
                            } else {
                                promptCaption = "Oops! That food doesn't start with ${activeLevelLetter.char}!"
                                onSpeak("No, try again!")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dish_item_$choice"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (feedingSuccessTrigger && isCorrect) Color(0xFF2ECC71) else Color.White,
                            contentColor = Color(0xFF2C3E50)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFE2E8F0))
                    ) {
                        Text(choice, fontSize = 13.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

// ==========================================
// GAME 3: MEMORY MATCH CARDS (NEW ADVENTURE GAME!)
// ==========================================

@Composable
fun MemoryMatchGame(
    onSpeak: (String) -> Unit
) {
    // Letters & items pairs to match (6 pairs, randomized each round!)
    var sourcePairs by remember {
        mutableStateOf(
            LetterRepository.letters.filter { it.emoji.isNotEmpty() }.shuffled().take(6).map { it.char.toString() to it.emoji }
        )
    }

    val cardsList = remember(sourcePairs) {
        mutableStateListOf<MemoryCard>().also { list ->
            var identifierId = 0
            sourcePairs.forEach { pair ->
                list.add(MemoryCard(identifierId++, pair.first, pair.first))
                list.add(MemoryCard(identifierId++, pair.second, pair.first))
            }
            list.shuffle()
        }
    }

    var selectedFirstIndex by remember { mutableStateOf<Int?>(null) }
    var selectedSecondIndex by remember { mutableStateOf<Int?>(null) }
    var gameplayInfoText by remember { mutableStateOf("Match 6 pairs of Letters and Pictures! 🃏") }
    
    var isPeekingAll by remember { mutableStateOf(false) }
    var showCardsBeforePlaying by remember { mutableStateOf(false) }
    var freezeTapClicks by remember { mutableStateOf(false) }

    val allMatchedCount = cardsList.count { it.isMatched }
    val scope = rememberCoroutineScope()

    // Helper to generate a new set of 6 random pairs from the entire LetterRepository list
    fun startNewSubsetGame() {
        selectedFirstIndex = null
        selectedSecondIndex = null
        val pool = LetterRepository.letters.filter { it.emoji.isNotEmpty() }.shuffled()
        sourcePairs = pool.take(6).map { it.char.toString() to it.emoji }
        gameplayInfoText = "Deck shuffled! Find the matches! 🌟"
        // Also trigger silent peek
        scope.launch {
            gameplayInfoText = "Memorizing cards... 🧠👁️"
            isPeekingAll = true
            freezeTapClicks = true
            delay(2800)
            isPeekingAll = false
            freezeTapClicks = false
            gameplayInfoText = "Find matching cards! 🃏"
        }
    }

    // Peek helper that shows all cards temporarily, spoken and visually
    fun triggerPeekSequence(silent: Boolean = false) {
        scope.launch {
            if (!silent) {
                onSpeak("Take a peek! Memorize the cards!")
            }
            gameplayInfoText = "Memorizing cards... 🧠👁️"
            isPeekingAll = true
            freezeTapClicks = true
            delay(2800) // 2.8 seconds peeking time
            isPeekingAll = false
            freezeTapClicks = false
            gameplayInfoText = "Now find the matches! 🃏"
        }
    }

    // Run peek sequence automatically when game loads
    LaunchedEffect(Unit) {
        triggerPeekSequence(silent = true)
    }

    LaunchedEffect(allMatchedCount) {
        if (allMatchedCount == 12) {
            gameplayInfoText = "FANTASTIC! You matched all pairs! 🌟🎉"
            onSpeak("Amazing work! You found all matches! Tap Play Next Level for more!")
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .testTag("memory_game_match_canvas"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
        border = BorderStroke(1.5.dp, Color(0xFFE9D8FD))
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Memory Game 🃏🌸",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF6B46C1)
                )

                // Row of helper actions
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Show Cards (Practice Mode)
                    Button(
                        onClick = {
                            showCardsBeforePlaying = !showCardsBeforePlaying
                            if (showCardsBeforePlaying) {
                                onSpeak("Showing cards! Study them before playing!")
                            } else {
                                onSpeak("Play mode started! Find the card matches!")
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showCardsBeforePlaying) Color(0xFF48BB78) else Color(0xFFF7FAFC),
                            contentColor = if (showCardsBeforePlaying) Color.White else Color(0xFF4A5568)
                        ),
                        modifier = Modifier.height(30.dp).border(1.dp, Color(0xFFCBD5E0), RoundedCornerShape(10.dp)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = if (showCardsBeforePlaying) "🫣 Hide" else "👁️ Show",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Peek Peek Button
                    Button(
                        onClick = {
                            triggerPeekSequence()
                        },
                        enabled = !isPeekingAll && !showCardsBeforePlaying,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9F7AEA)),
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "👀 Peek",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    // Refresh Button
                    IconButton(
                        onClick = {
                            startNewSubsetGame()
                        },
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry",
                            tint = Color(0xFF6B46C1),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = gameplayInfoText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Rows of 3 Cards each (Enlarged and perfectly spaced without scrollbars)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val rowCount = 4
                val colCount = 3
                for (r in 0 until rowCount) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (c in 0 until colCount) {
                            val index = r * colCount + c
                            if (index < cardsList.size) {
                                val card = cardsList[index]
                                val isRevealed = card.isFaceUp || card.isMatched || isPeekingAll || showCardsBeforePlaying

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1.15f) // Spacious, prominent touch size
                                        .shadow(3.dp, RoundedCornerShape(14.dp))
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isRevealed) Color.White else Color(0xFF6B46C1))
                                        .border(2.5.dp, if (card.isMatched) Color(0xFF2ECC71) else Color(0xFFD6B4FC), RoundedCornerShape(14.dp))
                                        .clickable {
                                            if (isRevealed || freezeTapClicks) return@clickable

                                            // Flip up clicked target card with copy-on-write
                                            val flippedCard = card.copy(isFaceUp = true)
                                            cardsList[index] = flippedCard
                                            onSpeak(flippedCard.content)

                                            if (selectedFirstIndex == null) {
                                                selectedFirstIndex = index
                                            } else if (selectedSecondIndex == null) {
                                                selectedSecondIndex = index
                                                freezeTapClicks = true

                                                val firstIdx = selectedFirstIndex!!
                                                val secondIdx = index
                                                val firstCard = cardsList[firstIdx]

                                                if (firstCard.matchKey == flippedCard.matchKey) {
                                                    cardsList[firstIdx] = firstCard.copy(isMatched = true)
                                                    cardsList[secondIdx] = flippedCard.copy(isMatched = true)
                                                    gameplayInfoText = "Hurray! Matched letter ${flippedCard.matchKey}! 🎉"
                                                    onSpeak("Match!")
                                                    selectedFirstIndex = null
                                                    selectedSecondIndex = null
                                                    freezeTapClicks = false
                                                } else {
                                                    gameplayInfoText = "Oops! Keep looking!"
                                                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                                        // safety bounds checking
                                                        if (firstIdx < cardsList.size && secondIdx < cardsList.size) {
                                                            val c1 = cardsList[firstIdx]
                                                            val c2 = cardsList[secondIdx]
                                                            cardsList[firstIdx] = c1.copy(isFaceUp = false)
                                                            cardsList[secondIdx] = c2.copy(isFaceUp = false)
                                                        }
                                                        selectedFirstIndex = null
                                                        selectedSecondIndex = null
                                                        freezeTapClicks = false
                                                        gameplayInfoText = "Find matching cards!"
                                                    }, 1300)
                                                }
                                            }
                                        }
                                        .testTag("memory_card_$index"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isRevealed) {
                                        Text(text = card.content, fontSize = 28.sp, fontWeight = FontWeight.Black)
                                    } else {
                                        Text(text = "❓", fontSize = 22.sp, color = Color.White)
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Beautiful next level button at the bottom of cards when passing
            if (allMatchedCount == 12) {
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = {
                        startNewSubsetGame()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .height(48.dp)
                        .shadow(4.dp, RoundedCornerShape(14.dp))
                        .testTag("memory_next_level_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF48BB78)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "PLAY NEXT LEVEL 🎯🎉",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ==========================================
// GAME 4: MULTIPLE CHOICES PHONICS QUIZ
// ==========================================

@Composable
fun MultiChoiceQuizGame(
    onSpeak: (String) -> Unit
) {
    var quizRoundIdx by remember { mutableStateOf(0) }
    val quizLetter = LetterRepository.letters[quizRoundIdx % LetterRepository.letters.size]

    var quizChoices by remember(quizLetter.char) {
        val list = mutableListOf(quizLetter.word)
        val filtered = LetterRepository.letters.filter { it.char != quizLetter.char }
        filtered.shuffled().take(2).forEach { item ->
            list.add(item.word)
        }
        mutableStateOf(list.shuffled())
    }

    var isChoiceSuccessful by remember { mutableStateOf(false) }
    var quizPromptCaption by remember { mutableStateOf("Can you discover the object pairing with '${quizLetter.char}'?") }

    LaunchedEffect(quizLetter.char) {
        onSpeak("Can you discover the object for letter ${quizLetter.char}?")
        quizPromptCaption = "Can you discover the object for '${quizLetter.char}'?"
        isChoiceSuccessful = false
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .testTag("quiz_game_canvas"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEFCBF)),
        border = BorderStroke(1.5.dp, Color(0xFFFAF089))
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Toddler Phonics Quiz! ❓🧠",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF975A16)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Massive mystery Box
            Box(
                modifier = Modifier
                    .size(105.dp)
                    .background(Color.White, CircleShape)
                    .border(3.dp, Color(0xFFECC94B), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = quizLetter.char.toString(),
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF975A16)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // EXTREMELY VISUAL CHEERFUL PROMPT CARD FOR KIDS
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clickable { onSpeak("Discover the object starting with letter ${quizLetter.char}!") }
                    .shadow(4.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF0)),
                border = BorderStroke(2.5.dp, Color(0xFFFAF089))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🔊 WHICH IS FOR: ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF975A16)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Card(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(4.dp, CircleShape),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(3.dp, Color(0xFF975A16))
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = quizLetter.char.toString(),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF975A16)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = quizPromptCaption,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7F8C8D),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Render option selector buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quizChoices.forEach { option ->
                    val isCorrect = option == quizLetter.word
                    
                    Button(
                        onClick = {
                            if (isCorrect) {
                                isChoiceSuccessful = true
                                quizPromptCaption = "FANTASTIC! Selected ${quizLetter.word} ${quizLetter.emoji}!"
                                onSpeak("Great job! ${quizLetter.char} is for ${quizLetter.word}!")

                                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                    quizRoundIdx = (quizRoundIdx + 1) % LetterRepository.letters.size
                                    isChoiceSuccessful = false
                                }, 2500)
                            } else {
                                quizPromptCaption = "No, that starts with another letter! Try again!"
                                onSpeak("Almost! Try again!")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("quiz_option_$option"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isChoiceSuccessful && isCorrect) Color(0xFF2ECC71) else Color.White,
                            contentColor = Color(0xFF2C3E50)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFE2E8F0))
                    ) {
                        Text(option, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// GAME 5: FLOATING BALLS POPPING GAME
// ==========================================

@Composable
fun FloatingPhonicsBallsGame(
    onSpeak: (String) -> Unit
) {
    var score by remember { mutableStateOf(0) }
    var gameStarted by remember { mutableStateOf(true) }
    var gameSpeedFactor by remember { mutableStateOf(1.1f) } // 1f: Normal, 1.8f: Speedy, 0.6f: Slow

    val ballsList = remember { mutableStateListOf<FloatingBall>() }
    val particleEffects = remember { mutableStateListOf<Pair<Offset, Color>>() }

    val alphabetPool = remember { LetterRepository.letters.map { it.char } }
    val colorsList = remember {
        listOf(
            Color(0xFFFF7675), // Soft red
            Color(0xFF74B9FF), // Soft blue
            Color(0xFF55E6C1), // Soft green
            Color(0xFFFECA57), // Yellow gold
            Color(0xFFFF9FF3), // Pink lollipop
            Color(0xFFD980FA), // Lavender plum
            Color(0xFF48DBFB)  // Cyan sky
        )
    }

    // Helper to request spawning a single custom random ball
    fun spawnBall(fromY: Float = 110f) {
        val randomChar = alphabetPool.random()
        val randomX = (10..90).random().toFloat()
        val randomSpeed = (12..25).random() / 10f
        val randomColor = colorsList.random()
        val randomSize = (56..72).random()
        val id = (0..100000).random()
        ballsList.add(FloatingBall(id, randomChar, randomX, fromY, randomSpeed, randomColor, randomSize))
    }

    // Initialize 5 colorful balls on startup
    LaunchedEffect(Unit) {
        if (ballsList.isEmpty()) {
            for (i in 0 until 5) {
                // Stagger spawn heights so they don't all float up in a single cluster clump
                spawnBall(fromY = 110f + (i * 22f))
            }
        }
    }

    // Game ticks loop running smoothly in a coroutine
    LaunchedEffect(gameStarted, gameSpeedFactor) {
        while (gameStarted) {
            delay(16) // ~60fps rendering frame intervals
            val iterator = ballsList.listIterator()
            val toBeSpawnedCount = mutableListOf<Float>()
            while (iterator.hasNext()) {
                val ball = iterator.next()
                val nextY = ball.yPercent - (ball.speed * gameSpeedFactor)
                if (nextY < -15f) {
                    iterator.remove() // remove ball floating completely off screen
                    // queue respawning fresh ball at randomized height offset
                    toBeSpawnedCount.add(110f + (0..15).random().toFloat())
                } else {
                    iterator.set(ball.copy(yPercent = nextY))
                }
            }
            // Spawn replacements for lost ones
            toBeSpawnedCount.forEach { height ->
                spawnBall(height)
            }

            // Slowly fade out/clear popped particles
            if (particleEffects.isNotEmpty()) {
                if (particleEffects.size > 15) {
                    particleEffects.removeAt(0)
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .testTag("floating_balls_canvas_container"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6FFFA)),
        border = BorderStroke(1.5.dp, Color(0xFFB2F5EA))
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Floating Phonics Balls 🔮✨",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF008080)
                    )
                    Text(
                        text = "Tap balls to pop, hear letters, and learn!",
                        fontSize = 11.sp,
                        color = Color(0xFF4A5568)
                    )
                }

                // Beautiful Score badge
                Box(
                    modifier = Modifier
                        .background(Color(0xFF319795), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Score: $score 🏆",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Speed Control Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ball Speed: ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F766E)
                )
                Spacer(modifier = Modifier.width(6.dp))

                listOf(
                    "Slow" to 0.6f,
                    "Medium" to 1.1f,
                    "Fast" to 1.8f
                ).forEach { (label, speed) ->
                    val isSelected = gameSpeedFactor == speed
                    Button(
                        onClick = {
                            gameSpeedFactor = speed
                            onSpeak("Speed updated to $label!")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFF008080) else Color.White,
                            contentColor = if (isSelected) Color.White else Color(0xFF4A5568)
                        ),
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(28.dp)
                            .border(1.dp, Color(0xFFCBD5E0), RoundedCornerShape(8.dp)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // The Canvas visual physics container
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF0F172A)) // Slate neon background
                    .border(2.dp, Color(0xFF319795), RoundedCornerShape(18.dp))
                    .testTag("balls_drawing_canvas_arena")
            ) {
                val containerWidth = maxWidth
                val containerHeight = maxHeight

                // Custom popping explosion loop
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Draw popped particles
                    particleEffects.forEach { effect ->
                        drawCircle(
                            color = effect.second.copy(alpha = 0.7f),
                            radius = 24f,
                            center = effect.first
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.9f),
                            radius = 10f,
                            center = effect.first
                        )
                    }
                }

                // Render dynamic tactile floating balls using Box overlay list to support rich styling & custom fonts beautifully
                ballsList.forEach { ball ->
                    // Convert percentages to dynamic layout coordinates
                    val offsetX = (ball.xPercent / 100f) * containerWidth.value
                    val offsetY = (ball.yPercent / 100f) * containerHeight.value

                    Box(
                        modifier = Modifier
                            .offset(
                                x = (offsetX - (ball.sizeDp / 2f)).dp,
                                y = (offsetY - (ball.sizeDp / 2f)).dp
                            )
                            .size(ball.sizeDp.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(ball.color.copy(alpha = 0.4f), ball.color)
                                )
                            )
                            .border(2.5.dp, Color.White.copy(alpha = 0.9f), CircleShape)
                            .clickable {
                                // POP THE BALL!
                                score += 1
                                val letterItem = LetterRepository.letters.firstOrNull { it.char == ball.char }
                                val speakText = if (letterItem != null) {
                                    "${ball.char}! ${letterItem.char} is for ${letterItem.word}!"
                                } else {
                                    "${ball.char} popped!"
                                }
                                onSpeak(speakText)

                                // Add pop burst particles at popped location coordinate
                                val particlePt = Offset(
                                    x = (ball.xPercent / 100f) * constraints.maxWidth.toFloat(),
                                    y = (ball.yPercent / 100f) * constraints.maxHeight.toFloat()
                                )
                                particleEffects.add(particlePt to ball.color)

                                // Replace with a fresh spawned ball at bottom
                                ballsList.remove(ball)
                                spawnBall()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ball.char.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                // Backdrop instruction watermarks
                if (ballsList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Loading Floating Phonics Balls...",
                            color = Color.LightGray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Level bottom tools
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        spawnBall()
                        onSpeak("Spawned more alphabet balls!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF319795)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Add More Balls (🎈)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        score = 0
                        ballsList.clear()
                        for (i in 0 until 5) {
                            spawnBall(fromY = 110f + (i * 22f))
                        }
                        onSpeak("Score reset. Let's start fresh!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF718096)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Reset Score", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// GAME 6: DRAW LINES CONNECTING GAME (A-D to Objects)
// ==========================================
@Composable
fun DrawLinesMatchingGame(
    onSpeak: (String) -> Unit
) {
    // 4 matches: A -> 🍎 Apple, B -> ⚽ Ball, C -> 🐱 Cat, D -> 🐶 Dog
    data class MatchNode(
        val char: Char,
        val label: String,
        val emoji: String,
        val targetLabel: String
    )

    val sourceNodes = remember {
        listOf(
            MatchNode('A', "Letter A", "🍎", "Apple"),
            MatchNode('B', "Letter B", "⚽", "Ball"),
            MatchNode('C', "Letter C", "🐱", "Cat"),
            MatchNode('D', "Letter D", "🐶", "Dog")
        )
    }

    // Right-side object nodes (shuffled on start)
    val rightNodes = remember {
        listOf(
            MatchNode('C', "Letter C", "🐱", "Cat"),
            MatchNode('A', "Letter A", "🍎", "Apple"),
            MatchNode('D', "Letter D", "🐶", "Dog"),
            MatchNode('B', "Letter B", "⚽", "Ball")
        )
    }

    // Keep track of matched links (map of Char -> Boolean indicating if successfully connected)
    val linksCompleted = remember { mutableStateMapOf<Char, Boolean>() }

    // Active selection for Tap-based connection selection
    var selectedLeftChar by remember { mutableStateOf<Char?>(null) }
    var selectedRightChar by remember { mutableStateOf<Char?>(null) }

    // Confetti particles to pop dynamically on success
    var showConfetti by remember { mutableStateOf(false) }

    // Check if game is completed
    val isCompleted = linksCompleted.size == 4

    if (isCompleted && !showConfetti) {
        showConfetti = true
        LaunchedEffect(Unit) {
            onSpeak("Excellent! Excellent! You matched all letters and objects perfectly!")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Draw Lines: Phonics Matching! ✏️✨",
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF2C3E50)
        )
        Text(
            text = "Draw lines or tap a Letter and then tap its matching object!",
            fontSize = 11.sp,
            color = Color(0xFF7F8C8D),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // The game play area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(352.dp)
                .shadow(4.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFDFE)),
            border = BorderStroke(2.dp, Color(0xFFE2E8F0))
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val layoutWidth = maxWidth

                // Custom matching board draw behind canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw lines for matched links
                    sourceNodes.forEachIndexed { sIdx, sNode ->
                        if (linksCompleted[sNode.char] == true) {
                            // Find sNode index on the left
                            val leftY = (sIdx * 82 + 50) * density
                            val leftX = 72 * density

                            // Find sNode matching item position on the right list
                            val rIdx = rightNodes.indexOfFirst { it.char == sNode.char }
                            val rightY = (rIdx * 82 + 50) * density
                            val rightX = (layoutWidth.value - 86) * density

                            // Draw thick gorgeous neon line with a beautiful custom primary glow
                            drawLine(
                                color = when (sNode.char) {
                                    'A' -> Color(0xFFEA2027)
                                    'B' -> Color(0xFF0984E3)
                                    'C' -> Color(0xFF2ECC71)
                                    else -> Color(0xFFE056FD)
                                },
                                start = Offset(leftX, leftY),
                                end = Offset(rightX, rightY),
                                strokeWidth = 8f,
                                cap = StrokeCap.Round
                            )

                            // Draw matching tick indicator in the center
                            drawCircle(
                                color = Color(0xFF2ECC71),
                                radius = 10f,
                                center = Offset((leftX + rightX) / 2, (leftY + rightY) / 2)
                            )
                        }
                    }

                    // Draw draft line if kid selected left node
                    if (selectedLeftChar != null) {
                        val activeIdx = sourceNodes.indexOfFirst { it.char == selectedLeftChar }
                        val leftY = (activeIdx * 82 + 50) * density
                        val leftX = 72 * density

                        // Draw moving dots or custom dotted path to guide
                        drawCircle(
                            color = Color(0xFFFF7675),
                            radius = 6f,
                            center = Offset(leftX, leftY)
                        )
                    }
                }

                // Render Left Column
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {
                    sourceNodes.forEach { item ->
                        val isMatched = linksCompleted[item.char] == true
                        val isSelected = selectedLeftChar == item.char

                        Box(
                            modifier = Modifier
                                .size(width = 80.dp, height = 58.dp)
                                .shadow(2.dp, RoundedCornerShape(14.dp))
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isMatched) Color(0xFFE8F5E9)
                                    else if (isSelected) Color(0xFFFFEAA7)
                                    else Color.White
                                )
                                .border(
                                    width = if (isSelected) 3.dp else 1.5.dp,
                                    color = if (isMatched) Color(0xFF2ECC71) else if (isSelected) Color(0xFFF1C40F) else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    if (!isMatched) {
                                        selectedLeftChar = item.char
                                        onSpeak("Letter ${item.char}. Tap its matching object on the right!")
                                        // Auto matches if right side is already selected!
                                        if (selectedRightChar != null) {
                                            if (selectedLeftChar == selectedRightChar) {
                                                linksCompleted[item.char] = true
                                                onSpeak("Correct! ${item.char} is for ${item.targetLabel}!")
                                                selectedLeftChar = null
                                                selectedRightChar = null
                                            } else {
                                                onSpeak("Oops! Try again!")
                                                selectedLeftChar = null
                                                selectedRightChar = null
                                            }
                                        }
                                    } else {
                                        onSpeak("This is already matched!")
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(item.char.toString(), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2C3E50))
                                Text(if (isMatched) "Matched ✓" else "Tap 🎯", fontSize = 9.sp, color = if (isMatched) Color(0xFF2ECC71) else Color.Gray)
                            }
                        }
                    }
                }

                // Render Right Column
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {
                    rightNodes.forEach { item ->
                        val isMatched = linksCompleted[item.char] == true
                        val isSelected = selectedRightChar == item.char

                        Box(
                            modifier = Modifier
                                .size(width = 96.dp, height = 58.dp)
                                .shadow(2.dp, RoundedCornerShape(14.dp))
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isMatched) Color(0xFFE8F5E9)
                                    else if (isSelected) Color(0xFFFFEAA7)
                                    else Color.White
                                )
                                .border(
                                    width = if (isSelected) 3.dp else 1.5.dp,
                                    color = if (isMatched) Color(0xFF2ECC71) else if (isSelected) Color(0xFFF1C40F) else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    if (!isMatched) {
                                        selectedRightChar = item.char
                                        onSpeak("Matching picture ${item.targetLabel}!")
                                        if (selectedLeftChar != null) {
                                            if (selectedLeftChar == selectedRightChar) {
                                                linksCompleted[item.char] = true
                                                onSpeak("Excellent! Match made: ${item.char} is for ${item.targetLabel}!")
                                                selectedLeftChar = null
                                                selectedRightChar = null
                                            } else {
                                                onSpeak("That's not matching! Try again!")
                                                selectedLeftChar = null
                                                selectedRightChar = null
                                            }
                                        }
                                    } else {
                                        onSpeak("This is already completed!")
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(item.emoji + " " + item.targetLabel, fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFF2C3E50))
                                Text(if (isMatched) "Correct ✓" else "Match 🧩", fontSize = 9.sp, color = if (isMatched) Color(0xFF2ECC71) else Color.Gray)
                            }
                        }
                    }
                }

                // Celebrative HUD Confetti Overlay if completes
                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.92f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text("🌟 EXCELLENT! 🌟", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF9F43))
                            Text("You completed all matches!", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text("🏆 Genius Kid Badge Unlocked!", fontSize = 13.sp, color = Color(0xFF2ECC71), fontWeight = FontWeight.Black)

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    linksCompleted.clear()
                                    selectedLeftChar = null
                                    selectedRightChar = null
                                    showConfetti = false
                                    onSpeak("Let's play again! Match letters to objects!")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Play Again 🔄", fontSize = 13.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}
