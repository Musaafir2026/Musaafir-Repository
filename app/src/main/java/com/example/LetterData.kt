package com.example

data class LetterPoint(val x: Float, val y: Float)

data class LetterStroke(val points: List<LetterPoint>) {
    constructor(vararg pts: LetterPoint) : this(pts.toList())
    constructor(vararg pairs: Pair<Float, Float>) : this(pairs.map { LetterPoint(it.first, it.second) })
}

data class WritingStyle(
    val name: String,
    val strokes: List<LetterStroke>
)

data class LetterInfo(
    val char: Char,
    val word: String,
    val emoji: String,
    val phonics: String,
    val styles: List<WritingStyle>
)

object LetterRepository {
    private val rawLetters: List<LetterInfo> = listOf(
        // A
        LetterInfo(
            char = 'A',
            word = "APPLE",
            emoji = "🍎",
            phonics = "Ah as in Apple",
            styles = listOf(
                WritingStyle(
                    name = "Classic Print",
                    strokes = listOf(
                        LetterStroke(0.5f to 0.1f, 0.15f to 0.9f), // Left diagonal
                        LetterStroke(0.5f to 0.1f, 0.85f to 0.9f), // Right diagonal
                        LetterStroke(0.3f to 0.6f, 0.7f to 0.6f)   // Crossbar
                    )
                ),
                WritingStyle(
                    name = "Alternative (Flat Top)",
                    strokes = listOf(
                        LetterStroke(0.40f to 0.1f, 0.60f to 0.1f), // Top horizontal flat
                        LetterStroke(0.40f to 0.1f, 0.15f to 0.9f), // Left diagonal
                        LetterStroke(0.60f to 0.1f, 0.85f to 0.9f), // Right diagonal
                        LetterStroke(0.31f to 0.55f, 0.69f to 0.55f) // Crossbar
                    )
                )
            )
        ),
        // B
        LetterInfo(
            char = 'B',
            word = "BEAR",
            emoji = "🧸",
            phonics = "Buh as in Bear",
            styles = listOf(
                WritingStyle(
                    name = "Classic Print",
                    strokes = listOf(
                        LetterStroke(0.3f to 0.1f, 0.3f to 0.9f), // Vertical stem
                        // Top hump
                        LetterStroke(0.3f to 0.1f, 0.65f to 0.1f, 0.65f to 0.5f, 0.3f to 0.5f),
                        // Bottom hump
                        LetterStroke(0.3f to 0.5f, 0.72f to 0.5f, 0.72f to 0.9f, 0.3f to 0.9f)
                    )
                ),
                WritingStyle(
                    name = "Alternative (Modern Round)",
                    strokes = listOf(
                        LetterStroke(0.3f to 0.1f, 0.3f to 0.9f), // Vertical stem
                        // Curved top hump
                        LetterStroke(0.3f to 0.12f, 0.58f to 0.15f, 0.55f to 0.45f, 0.3f to 0.45f),
                        // Big bottom hump
                        LetterStroke(0.3f to 0.45f, 0.78f to 0.5f, 0.75f to 0.85f, 0.3f to 0.9f)
                    )
                )
            )
        ),
        // C
        LetterInfo(
            char = 'C',
            word = "CAT",
            emoji = "🐱",
            phonics = "Cuh as in Cat",
            styles = listOf(
                WritingStyle(
                    name = "Smooth Arc",
                    strokes = listOf(
                        LetterStroke(0.75f to 0.25f, 0.45f to 0.15f, 0.25f to 0.5f, 0.45f to 0.85f, 0.75f to 0.75f)
                    )
                ),
                WritingStyle(
                    name = "Alternative (Block C)",
                    strokes = listOf(
                        LetterStroke(0.7f to 0.15f, 0.3f to 0.15f), // Top
                        LetterStroke(0.3f to 0.15f, 0.3f to 0.85f), // Side
                        LetterStroke(0.3f to 0.85f, 0.7f to 0.85f)  // Bottom
                    )
                )
            )
        ),
        // D
        LetterInfo(
            char = 'D',
            word = "DOG",
            emoji = "🐶",
            phonics = "Duh as in Dog",
            styles = listOf(
                WritingStyle(
                    name = "Classic Print",
                    strokes = listOf(
                        LetterStroke(0.3f to 0.1f, 0.3f to 0.9f), // Stem
                        LetterStroke(0.3f to 0.1f, 0.75f to 0.1f, 0.75f to 0.9f, 0.3f to 0.9f) // Loop
                    )
                ),
                WritingStyle(
                    name = "Alternative (Blocky D)",
                    strokes = listOf(
                        LetterStroke(0.3f to 0.1f, 0.3f to 0.9f),
                        LetterStroke(0.3f to 0.1f, 0.6f to 0.1f),
                        LetterStroke(0.6f to 0.1f, 0.75f to 0.35f, 0.75f to 0.65f, 0.6f to 0.9f),
                        LetterStroke(0.6f to 0.9f, 0.3f to 0.9f)
                    )
                )
            )
        ),
        // E
        LetterInfo(
            char = 'E',
            word = "ELEPHANT",
            emoji = "🐘",
            phonics = "Eh as in Elephant",
            styles = listOf(
                WritingStyle(
                    name = "Classic 4-Stroke",
                    strokes = listOf(
                        LetterStroke(0.3f to 0.1f, 0.3f to 0.9f), // Stem
                        LetterStroke(0.3f to 0.1f, 0.75f to 0.1f), // Top
                        LetterStroke(0.3f to 0.5f, 0.65f to 0.5f), // Mid
                        LetterStroke(0.3f to 0.9f, 0.75f to 0.9f)  // Bottom
                    )
                ),
                WritingStyle(
                    name = "Alternative (Continuous Arc)",
                    strokes = listOf(
                        LetterStroke(0.75f to 0.15f, 0.3f to 0.15f, 0.3f to 0.85f, 0.75f to 0.85f),
                        LetterStroke(0.3f to 0.5f, 0.65f to 0.5f)
                    )
                )
            )
        ),
        // F
        LetterInfo(
            char = 'F',
            word = "FISH",
            emoji = "🐟",
            phonics = "Fuh as in Fish",
            styles = listOf(
                WritingStyle(
                    name = "Classic Print",
                    strokes = listOf(
                        LetterStroke(0.3f to 0.1f, 0.3f to 0.9f), // Main
                        LetterStroke(0.3f to 0.1f, 0.75f to 0.1f), // Top
                        LetterStroke(0.3f to 0.5f, 0.65f to 0.5f)  // Middle
                    )
                ),
                WritingStyle(
                    name = "Alternative (Slanted F)",
                    strokes = listOf(
                        LetterStroke(0.38f to 0.1f, 0.22f to 0.9f), // Slanted stem
                        LetterStroke(0.38f to 0.1f, 0.78f to 0.1f), // Top
                        LetterStroke(0.33f to 0.5f, 0.68f to 0.5f)  // Mid
                    )
                )
            )
        ),
        // G
        LetterInfo(
            char = 'G',
            word = "GIRAFFE",
            emoji = "🦒",
            phonics = "Guh as in Giraffe",
            styles = listOf(
                WritingStyle(
                    name = "Classic Print",
                    strokes = listOf(
                        LetterStroke(0.75f to 0.25f, 0.45f to 0.15f, 0.25f to 0.5f, 0.45f to 0.85f, 0.75f to 0.85f),
                        LetterStroke(0.75f to 0.85f, 0.75f to 0.55f, 0.55f to 0.55f)
                    )
                ),
                WritingStyle(
                    name = "Alternative (Simple G)",
                    strokes = listOf(
                        LetterStroke(0.75f to 0.25f, 0.45f to 0.15f, 0.25f to 0.5f, 0.45f to 0.85f, 0.75f to 0.85f),
                        LetterStroke(0.75f to 0.85f, 0.75f to 0.6f)
                    )
                )
            )
        ),
        // H
        LetterInfo(
            char = 'H',
            word = "HELICOPTER",
            emoji = "🛸",
            phonics = "Huh as in Helicopter",
            styles = listOf(
                WritingStyle(
                    name = "Classic Print",
                    strokes = listOf(
                        LetterStroke(0.25f to 0.1f, 0.25f to 0.9f), // Left
                        LetterStroke(0.75f to 0.1f, 0.75f to 0.9f), // Right
                        LetterStroke(0.25f to 0.5f, 0.75f to 0.5f)  // Middle
                    )
                ),
                WritingStyle(
                    name = "Alternative (Curved MidBar)",
                    strokes = listOf(
                        LetterStroke(0.25f to 0.1f, 0.25f to 0.9f),
                        LetterStroke(0.75f to 0.1f, 0.75f to 0.9f),
                        LetterStroke(0.25f to 0.45f, 0.5f to 0.55f, 0.75f to 0.45f) // Kid style bar
                    )
                )
            )
        ),
        // I
        LetterInfo(
            char = 'I',
            word = "ICE-CREAM",
            emoji = "🍦",
            phonics = "Eye as in Ice Cream",
            styles = listOf(
                WritingStyle(
                    name = "Classic Print (Simple)",
                    strokes = listOf(
                        LetterStroke(0.5f to 0.1f, 0.5f to 0.9f)
                    )
                ),
                WritingStyle(
                    name = "Traditional (With Bars)",
                    strokes = listOf(
                        LetterStroke(0.5f to 0.1f, 0.5f to 0.9f), // Shaft
                        LetterStroke(0.3f to 0.1f, 0.7f to 0.1f), // Top
                        LetterStroke(0.3f to 0.9f, 0.7f to 0.9f)  // Bottom
                    )
                )
            )
        ),
        // J
        LetterInfo(
            char = 'J',
            word = "JUNGLE",
            emoji = "🐯",
            phonics = "Juh as in Jungle",
            styles = listOf(
                WritingStyle(
                    name = "Classic Print",
                    strokes = listOf(
                        LetterStroke(0.55f to 0.1f, 0.55f to 0.75f, 0.45f to 0.9f, 0.25f to 0.75f)
                    )
                ),
                WritingStyle(
                    name = "Alternative (With Top Bar)",
                    strokes = listOf(
                        LetterStroke(0.35f to 0.1f, 0.75f to 0.1f), // Top,
                        LetterStroke(0.55f to 0.1f, 0.55f to 0.75f, 0.45f to 0.9f, 0.25f to 0.75f)
                    )
                )
            )
        ),
        // K
        LetterInfo(
            char = 'K',
            word = "KITE",
            emoji = "🪁",
            phonics = "Kuh as in Kite",
            styles = listOf(
                WritingStyle(
                    name = "Classic Print",
                    strokes = listOf(
                        LetterStroke(0.3f to 0.1f, 0.3f to 0.9f), // Shaft
                        LetterStroke(0.7f to 0.1f, 0.3f to 0.5f), // Top diagonal
                        LetterStroke(0.3f to 0.5f, 0.75f to 0.9f) // Bottom diagonal
                    )
                ),
                WritingStyle(
                    name = "Alternative (High Cross)",
                    strokes = listOf(
                        LetterStroke(0.3f to 0.1f, 0.3f to 0.9f),
                        LetterStroke(0.7f to 0.1f, 0.35f to 0.4f),
                        LetterStroke(0.35f to 0.4f, 0.75f to 0.9f)
                    )
                )
            )
        ),
        // L
        LetterInfo(
            char = 'L',
            word = "LION",
            emoji = "🦁",
            phonics = "Luh as in Lion",
            styles = listOf(
                WritingStyle(
                    name = "Classic Print",
                    strokes = listOf(
                        LetterStroke(0.35f to 0.1f, 0.35f to 0.9f), // Shaft
                        LetterStroke(0.35f to 0.9f, 0.75f to 0.9f)  // Foot
                    )
                ),
                WritingStyle(
                    name = "Alternative (Soft Curve)",
                    strokes = listOf(
                        LetterStroke(0.38f to 0.1f, 0.32f to 0.8f, 0.45f to 0.9f, 0.75f to 0.9f)
                    )
                )
            )
        ),
        // M
        LetterInfo(
            char = 'M',
            word = "MONKEY",
            emoji = "🐒",
            phonics = "Muh as in Monkey",
            styles = listOf(
                WritingStyle(
                    name = "Classic Straight",
                    strokes = listOf(
                        LetterStroke(0.2f to 0.9f, 0.2f to 0.1f), // Left
                        LetterStroke(0.2f to 0.1f, 0.5f to 0.6f), // Mid left diagonal
                        LetterStroke(0.5f to 0.6f, 0.8f to 0.1f), // Mid right diagonal
                        LetterStroke(0.8f to 0.1f, 0.8f to 0.9f)  // Right
                    )
                ),
                WritingStyle(
                    name = "Alternative (Spaced Angle)",
                    strokes = listOf(
                        LetterStroke(0.15f to 0.9f, 0.25f to 0.1f), // Left slanting
                        LetterStroke(0.25f to 0.1f, 0.5f to 0.8f),  // Deep middle
                        LetterStroke(0.5f to 0.8f, 0.75f to 0.1f),  // Right middle
                        LetterStroke(0.75f to 0.1f, 0.85f to 0.9f)  // Right slanting
                    )
                )
            )
        ),
        // N
        LetterInfo(
            char = 'N',
            word = "NUT",
            emoji = "🥜",
            phonics = "Nuh as in Nut",
            styles = listOf(
                WritingStyle(
                    name = "Classic Print",
                    strokes = listOf(
                        LetterStroke(0.25f to 0.9f, 0.25f to 0.1f), // Linkleft
                        LetterStroke(0.25f to 0.1f, 0.75f to 0.9f), // Center diagonal
                        LetterStroke(0.75f to 0.9f, 0.75f to 0.1f)  // Linkright
                    )
                ),
                WritingStyle(
                    name = "Alternative (Wide Draw)",
                    strokes = listOf(
                        LetterStroke(0.2f to 0.9f, 0.2f to 0.1f),
                        LetterStroke(0.2f to 0.1f, 0.8f to 0.9f),
                        LetterStroke(0.8f to 0.9f, 0.8f to 0.1f)
                    )
                )
            )
        ),
        // O
        LetterInfo(
            char = 'O',
            word = "OWL",
            emoji = "🦉",
            phonics = "Oh as in Owl",
            styles = listOf(
                WritingStyle(
                    name = "One Loop Draw",
                    strokes = listOf(
                        LetterStroke(0.5f to 0.1f, 0.23f to 0.35f, 0.23f to 0.65f, 0.5f to 0.9f, 0.77f to 0.65f, 0.77f to 0.35f, 0.5f to 0.1f)
                    )
                ),
                WritingStyle(
                    name = "Alternative (2-Half Circle)",
                    strokes = listOf(
                        LetterStroke(0.5f to 0.1f, 0.23f to 0.35f, 0.23f to 0.65f, 0.5f to 0.9f), // Left side
                        LetterStroke(0.5f to 0.1f, 0.77f to 0.35f, 0.77f to 0.65f, 0.5f to 0.9f)  // Right side
                    )
                )
            )
        ),
        // P
        LetterInfo(
            char = 'P',
            word = "PANDA",
            emoji = "🐼",
            phonics = "Puh as in Panda",
            styles = listOf(
                WritingStyle(
                    name = "Classic Print",
                    strokes = listOf(
                        LetterStroke(0.3f to 0.1f, 0.3f to 0.9f), // Vertical
                        LetterStroke(0.3f to 0.1f, 0.68f to 0.1f, 0.68f to 0.5f, 0.3f to 0.5f) // Head
                    )
                ),
                WritingStyle(
                    name = "Alternative (Curved Oval P)",
                    strokes = listOf(
                        LetterStroke(0.3f to 0.1f, 0.3f to 0.9f),
                        LetterStroke(0.3f to 0.12f, 0.62f to 0.18f, 0.6f to 0.42f, 0.3f to 0.48f)
                    )
                )
            )
        ),
        // Q
        LetterInfo(
            char = 'Q',
            word = "QUEEN",
            emoji = "👑",
            phonics = "Kwuh as in Queen",
            styles = listOf(
                WritingStyle(
                    name = "Classic Loop & Slash",
                    strokes = listOf(
                        LetterStroke(0.5f to 0.1f, 0.23f to 0.35f, 0.23f to 0.65f, 0.5f to 0.9f, 0.77f to 0.65f, 0.77f to 0.35f, 0.5f to 0.1f),
                        LetterStroke(0.58f to 0.58f, 0.82f to 0.82f) // Slash
                    )
                ),
                WritingStyle(
                    name = "Alternative (Curly Tail)",
                    strokes = listOf(
                        LetterStroke(0.5f to 0.1f, 0.23f to 0.35f, 0.23f to 0.65f, 0.5f to 0.9f, 0.77f to 0.65f, 0.77f to 0.35f, 0.5f to 0.1f),
                        LetterStroke(0.52f to 0.68f, 0.67f to 0.65f, 0.8f to 0.85f) // S-curly tail
                    )
                )
            )
        ),
        // R
        LetterInfo(
            char = 'R',
            word = "ROBOT",
            emoji = "🤖",
            phonics = "Ruh as in Robot",
            styles = listOf(
                WritingStyle(
                    name = "Classic Print",
                    strokes = listOf(
                        LetterStroke(0.3f to 0.1f, 0.3f to 0.9f), // Stem
                        LetterStroke(0.3f to 0.1f, 0.68f to 0.1f, 0.68f to 0.5f, 0.3f to 0.5f), // Hump
                        LetterStroke(0.3f to 0.5f, 0.72f to 0.9f) // Foot
                    )
                ),
                WritingStyle(
                    name = "Alternative (Curved Foot)",
                    strokes = listOf(
                        LetterStroke(0.3f to 0.1f, 0.3f to 0.9f),
                        LetterStroke(0.3f to 0.12f, 0.64f to 0.18f, 0.62f to 0.42f, 0.3f to 0.48f),
                        LetterStroke(0.3f to 0.48f, 0.48f to 0.65f, 0.72f to 0.9f) // Curved leg
                    )
                )
            )
        ),
        // S
        LetterInfo(
            char = 'S',
            word = "SUN",
            emoji = "☀️",
            phonics = "Suh as in Sun",
            styles = listOf(
                WritingStyle(
                    name = "Smooth Snake Curve",
                    strokes = listOf(
                        LetterStroke(0.72f to 0.22f, 0.5f to 0.12f, 0.28f to 0.28f, 0.5f to 0.5f, 0.72f to 0.72f, 0.5f to 0.88f, 0.28f to 0.78f)
                    )
                ),
                WritingStyle(
                    name = "Alternative (Soft Angle)",
                    strokes = listOf(
                        LetterStroke(0.7f to 0.25f, 0.5f to 0.18f, 0.32f to 0.32f, 0.5f to 0.5f, 0.68f to 0.68f, 0.5f to 0.82f, 0.3f to 0.75f)
                    )
                )
            )
        ),
        // T
        LetterInfo(
            char = 'T',
            word = "TRAIN",
            emoji = "🚂",
            phonics = "Tuh as in Train",
            styles = listOf(
                WritingStyle(
                    name = "Classic Print",
                    strokes = listOf(
                        LetterStroke(0.2f to 0.1f, 0.8f to 0.1f), // Cap
                        LetterStroke(0.5f to 0.1f, 0.5f to 0.9f)  // Stem
                    )
                ),
                WritingStyle(
                    name = "Alternative (Slanted Helper)",
                    strokes = listOf(
                        LetterStroke(0.2f to 0.1f, 0.8f to 0.1f),
                        LetterStroke(0.52f to 0.1f, 0.48f to 0.9f) // Toddler slanted line focus
                    )
                )
            )
        ),
        // U
        LetterInfo(
            char = 'U',
            word = "UNICORN",
            emoji = "🦄",
            phonics = "Yoo as in Unicorn",
            styles = listOf(
                WritingStyle(
                    name = "Classic Curve",
                    strokes = listOf(
                        LetterStroke(0.25f to 0.1f, 0.25f to 0.72f, 0.5f to 0.9f, 0.75f to 0.72f, 0.75f to 0.1f)
                    )
                ),
                WritingStyle(
                    name = "Alternative (Tail U)",
                    strokes = listOf(
                        LetterStroke(0.25f to 0.1f, 0.25f to 0.72f, 0.5f to 0.9f, 0.75f to 0.72f, 0.75f to 0.1f),
                        LetterStroke(0.75f to 0.1f, 0.75f to 0.9f) // Add vertical support line with tail
                    )
                )
            )
        ),
        // V
        LetterInfo(
            char = 'V',
            word = "VIOLIN",
            emoji = "🎻",
            phonics = "Vuh as in Violin",
            styles = listOf(
                WritingStyle(
                    name = "Classic Sharp Point",
                    strokes = listOf(
                        LetterStroke(0.2f to 0.1f, 0.5f to 0.9f),
                        LetterStroke(0.8f to 0.1f, 0.5f to 0.9f)
                    )
                ),
                WritingStyle(
                    name = "Alternative (Curved Base)",
                    strokes = listOf(
                        LetterStroke(0.2f to 0.1f, 0.28f to 0.72f, 0.5f to 0.9f, 0.72f to 0.72f, 0.8f to 0.1f)
                    )
                )
            )
        ),
        // W
        LetterInfo(
            char = 'W',
            word = "WATERMELON",
            emoji = "🍉",
            phonics = "Wuh as in Watermelon",
            styles = listOf(
                WritingStyle(
                    name = "Classic Double Wave",
                    strokes = listOf(
                        LetterStroke(0.15f to 0.1f, 0.3f to 0.9f),
                        LetterStroke(0.3f to 0.9f, 0.5f to 0.38f),
                        LetterStroke(0.5f to 0.38f, 0.7f to 0.9f),
                        LetterStroke(0.7f to 0.9f, 0.85f to 0.1f)
                    )
                ),
                WritingStyle(
                    name = "Alternative (Sharp Zigzag)",
                    strokes = listOf(
                        LetterStroke(0.1f to 0.1f, 0.3f to 0.9f),
                        LetterStroke(0.3f to 0.9f, 0.5f to 0.1f),
                        LetterStroke(0.5f to 0.1f, 0.7f to 0.9f),
                        LetterStroke(0.7f to 0.9f, 0.9f to 0.1f)
                    )
                )
            )
        ),
        // X
        LetterInfo(
            char = 'X',
            word = "XYLOPHONE",
            emoji = "🪵",
            phonics = "Zz as in Xylophone",
            styles = listOf(
                WritingStyle(
                    name = "Classic Cross",
                    strokes = listOf(
                        LetterStroke(0.2f to 0.1f, 0.8f to 0.9f),
                        LetterStroke(0.8f to 0.1f, 0.2f to 0.9f)
                    )
                ),
                WritingStyle(
                    name = "Alternative (Steep Cross)",
                    strokes = listOf(
                        LetterStroke(0.28f to 0.1f, 0.72f to 0.9f),
                        LetterStroke(0.72f to 0.1f, 0.28f to 0.9f)
                    )
                )
            )
        ),
        // Y
        LetterInfo(
            char = 'Y',
            word = "YAK",
            emoji = "🦆",
            phonics = "Yuh as in Yak",
            styles = listOf(
                WritingStyle(
                    name = "Classic Cup & Stem",
                    strokes = listOf(
                        LetterStroke(0.2f to 0.1f, 0.5f to 0.5f), // Cup left
                        LetterStroke(0.8f to 0.1f, 0.5f to 0.5f), // Cup right
                        LetterStroke(0.5f to 0.5f, 0.5f to 0.9f)  // Trunk
                    )
                ),
                WritingStyle(
                    name = "Alternative (Single Slant)",
                    strokes = listOf(
                        LetterStroke(0.2f to 0.1f, 0.8f to 0.9f), // Main slash
                        LetterStroke(0.8f to 0.1f, 0.5f to 0.5f)  // Branch
                    )
                )
            )
        ),
        // Z
        LetterInfo(
            char = 'Z',
            word = "ZEBRA",
            emoji = "🦓",
            phonics = "Zuh as in Zebra",
            styles = listOf(
                WritingStyle(
                    name = "Classic Zigzag",
                    strokes = listOf(
                        LetterStroke(0.22f to 0.1f, 0.78f to 0.1f), // Top
                        LetterStroke(0.78f to 0.1f, 0.22f to 0.9f), // Diagonal
                        LetterStroke(0.22f to 0.9f, 0.78f to 0.9f)  // Bottom
                    )
                ),
                WritingStyle(
                    name = "Alternative (With MidBar)",
                    strokes = listOf(
                        LetterStroke(0.22f to 0.1f, 0.78f to 0.1f), // Top
                        LetterStroke(0.78f to 0.1f, 0.22f to 0.9f), // Diagonal
                        LetterStroke(0.22f to 0.9f, 0.78f to 0.9f), // Bottom
                        LetterStroke(0.40f to 0.5f, 0.60f to 0.5f)  // Mini midbar
                    )
                )
            )
        )
    )

    val letters: List<LetterInfo> = rawLetters.map { info ->
        when (info.char) {
            'A' -> info.copy(word = "Apple", emoji = "🍎", phonics = "A for Apple")
            'B' -> info.copy(word = "Ball", emoji = "⚽", phonics = "B for Ball")
            'C' -> info.copy(word = "Cat", emoji = "🐱", phonics = "C for Cat")
            'D' -> info.copy(word = "Dog", emoji = "🐶", phonics = "D for Dog")
            'E' -> info.copy(word = "Elephant", emoji = "🐘", phonics = "E for Elephant")
            'F' -> info.copy(word = "Fish", emoji = "🐟", phonics = "F for Fish")
            'G' -> info.copy(word = "Goat", emoji = "🐐", phonics = "G for Goat")
            'H' -> info.copy(word = "Horse", emoji = "🐴", phonics = "H for Horse")
            'I' -> info.copy(word = "Insect", emoji = "🐞", phonics = "I for Insect")
            'J' -> info.copy(word = "Joker", emoji = "🃏", phonics = "J for Joker")
            'K' -> info.copy(word = "Kite", emoji = "🪁", phonics = "K for Kite")
            'L' -> info.copy(word = "Lion", emoji = "🦁", phonics = "L for Lion")
            'M' -> info.copy(word = "Mango", emoji = "🥭", phonics = "M for Mango")
            'N' -> info.copy(word = "Nose", emoji = "👃", phonics = "N for Nose")
            'O' -> info.copy(word = "Owl", emoji = "🦉", phonics = "O for Owl")
            'P' -> info.copy(word = "Pencil", emoji = "✏️", phonics = "P for Pencil")
            'Q' -> info.copy(word = "Queen", emoji = "👸", phonics = "Q for Queen")
            'R' -> info.copy(word = "Rain", emoji = "🌧️", phonics = "R for Rain")
            'S' -> info.copy(word = "Sun", emoji = "☀️", phonics = "S for Sun")
            'T' -> info.copy(word = "Train", emoji = "🚂", phonics = "T for Train")
            'U' -> info.copy(word = "Umbrella", emoji = "☂️", phonics = "U for Umbrella")
            'V' -> info.copy(word = "Van", emoji = "🚐", phonics = "V for Van")
            'W' -> info.copy(word = "Watermelon", emoji = "🍉", phonics = "W for Watermelon")
            'X' -> info.copy(word = "Xray", emoji = "🩻", phonics = "X for Xray")
            'Y' -> info.copy(word = "Yak", emoji = "🐃", phonics = "Y for Yak")
            'Z' -> info.copy(word = "Zebra", emoji = "🦓", phonics = "Z for Zebra")
            else -> info
        }
    }
}
