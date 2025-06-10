package com.messamraza.rockpaperscissor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.messamraza.rockpaperscissor.R
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    // Game choices enum
    enum class Choice(val displayName: String, val emoji: String) {
        ROCK("Rock", "🪨"),
        PAPER("Paper", "📄"),
        SCISSORS("Scissors", "✂️")
    }

    enum class GameResult {
        WIN, LOSE, TIE
    }

    // UI Components - using lateinit with null checks
    private lateinit var playerScoreText: TextView
    private lateinit var computerScoreText: TextView
    private lateinit var playerChoiceEmoji: TextView
    private lateinit var computerChoiceEmoji: TextView
    private lateinit var playerChoiceLabel: TextView
    private lateinit var computerChoiceLabel: TextView
    private lateinit var resultText: TextView
    private lateinit var resultDescription: TextView
    private lateinit var rockButton: Button
    private lateinit var paperButton: Button
    private lateinit var scissorsButton: Button
    private lateinit var playAgainButton: Button
    private lateinit var resetGameButton: Button
    private lateinit var instructionsText: TextView
    private lateinit var gameArea: View

    // Game State
    private var playerScore = 0
    private var computerScore = 0
    private var isPlaying = false
    private var currentPlayerChoice: Choice? = null
    private var currentComputerChoice: Choice? = null
    private var currentResult: GameResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            initializeViews()
            setupClickListeners()
            resetGameState()
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle initialization error gracefully
        }
    }

    private fun initializeViews() {
        // Initialize views with null safety
        playerScoreText = findViewById(R.id.playerScore)
        computerScoreText = findViewById(R.id.computerScore)
        playerChoiceEmoji = findViewById(R.id.playerChoiceEmoji)
        computerChoiceEmoji = findViewById(R.id.computerChoiceEmoji)
        playerChoiceLabel = findViewById(R.id.playerChoiceLabel)
        computerChoiceLabel = findViewById(R.id.computerChoiceLabel)
        resultText = findViewById(R.id.resultText)
        resultDescription = findViewById(R.id.resultDescription)
        rockButton = findViewById(R.id.rockButton)
        paperButton = findViewById(R.id.paperButton)
        scissorsButton = findViewById(R.id.scissorsButton)
        playAgainButton = findViewById(R.id.playAgainButton)
        resetGameButton = findViewById(R.id.resetGameButton)
        instructionsText = findViewById(R.id.instructionsText)
        gameArea = findViewById(R.id.gameArea)
    }

    private fun setupClickListeners() {
        rockButton.setOnClickListener { handlePlayerChoice(Choice.ROCK) }
        paperButton.setOnClickListener { handlePlayerChoice(Choice.PAPER) }
        scissorsButton.setOnClickListener { handlePlayerChoice(Choice.SCISSORS) }
        playAgainButton.setOnClickListener { playAgain() }
        resetGameButton.setOnClickListener { resetGame() }
    }

    private fun handlePlayerChoice(choice: Choice) {
        if (isPlaying) return

        isPlaying = true
        currentPlayerChoice = choice

        // Update UI to show player choice
        updatePlayerChoice(choice)
        showComputerThinking()
        disableChoiceButtons()
        hideInstructions()

        // Simulate computer thinking with delay
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                val computerChoice = generateComputerChoice()
                val result = determineWinner(choice, computerChoice)

                currentComputerChoice = computerChoice
                currentResult = result

                updateComputerChoice(computerChoice)
                updateGameResult(result, choice, computerChoice)
                updateScores(result)
                showActionButtons()

                isPlaying = false
                enableChoiceButtons()
            } catch (e: Exception) {
                e.printStackTrace()
                isPlaying = false
                enableChoiceButtons()
            }
        }, 1500)
    }

    private fun generateComputerChoice(): Choice {
        val choices = Choice.values()
        return choices[Random.nextInt(choices.size)]
    }

    private fun determineWinner(playerChoice: Choice, computerChoice: Choice): GameResult {
        if (playerChoice == computerChoice) return GameResult.TIE

        return when (playerChoice) {
            Choice.ROCK -> if (computerChoice == Choice.SCISSORS) GameResult.WIN else GameResult.LOSE
            Choice.PAPER -> if (computerChoice == Choice.ROCK) GameResult.WIN else GameResult.LOSE
            Choice.SCISSORS -> if (computerChoice == Choice.PAPER) GameResult.WIN else GameResult.LOSE
        }
    }

    private fun updatePlayerChoice(choice: Choice) {
        playerChoiceEmoji.text = choice.emoji
        playerChoiceLabel.text = choice.displayName
        gameArea.visibility = View.VISIBLE
    }

    private fun showComputerThinking() {
        computerChoiceEmoji.text = "🤔"
        computerChoiceLabel.text = "Thinking..."
    }

    private fun updateComputerChoice(choice: Choice) {
        computerChoiceEmoji.text = choice.emoji
        computerChoiceLabel.text = choice.displayName
    }

    private fun updateGameResult(result: GameResult, playerChoice: Choice, computerChoice: Choice) {
        val (resultMessage, resultColor) = when (result) {
            GameResult.WIN -> Pair("🎉 You Win!", android.R.color.holo_green_light)
            GameResult.LOSE -> Pair("😔 You Lose!", android.R.color.holo_red_light)
            GameResult.TIE -> Pair("🤝 It's a Tie!", android.R.color.holo_orange_light)
        }

        resultText.text = resultMessage
        resultText.setTextColor(ContextCompat.getColor(this, resultColor))
        resultText.visibility = View.VISIBLE

        resultDescription.text = "${playerChoice.displayName} vs ${computerChoice.displayName}"
        resultDescription.visibility = View.VISIBLE
    }

    private fun updateScores(result: GameResult) {
        when (result) {
            GameResult.WIN -> playerScore++
            GameResult.LOSE -> computerScore++
            GameResult.TIE -> { /* No score change */ }
        }

        playerScoreText.text = playerScore.toString()
        computerScoreText.text = computerScore.toString()
    }

    private fun showActionButtons() {
        playAgainButton.visibility = View.VISIBLE
        if (playerScore > 0 || computerScore > 0) {
            resetGameButton.visibility = View.VISIBLE
        }
    }

    private fun disableChoiceButtons() {
        rockButton.isEnabled = false
        paperButton.isEnabled = false
        scissorsButton.isEnabled = false

        // Add visual feedback for disabled state
        val disabledAlpha = 0.5f
        rockButton.alpha = disabledAlpha
        paperButton.alpha = disabledAlpha
        scissorsButton.alpha = disabledAlpha
    }

    private fun enableChoiceButtons() {
        rockButton.isEnabled = true
        paperButton.isEnabled = true
        scissorsButton.isEnabled = true

        // Restore full opacity
        rockButton.alpha = 1.0f
        paperButton.alpha = 1.0f
        scissorsButton.alpha = 1.0f
    }

    private fun hideInstructions() {
        instructionsText.visibility = View.GONE
    }

    private fun showInstructions() {
        instructionsText.visibility = View.VISIBLE
    }

    private fun playAgain() {
        currentPlayerChoice = null
        currentComputerChoice = null
        currentResult = null

        // Hide game area and results
        gameArea.visibility = View.GONE
        resultText.visibility = View.GONE
        resultDescription.visibility = View.GONE
        playAgainButton.visibility = View.GONE

        // Show instructions if no games played yet
        if (playerScore == 0 && computerScore == 0) {
            showInstructions()
        }

        // Hide reset button if scores are zero
        if (playerScore == 0 && computerScore == 0) {
            resetGameButton.visibility = View.GONE
        }
    }

    private fun resetGame() {
        playerScore = 0
        computerScore = 0
        resetGameState()
    }

    private fun resetGameState() {
        currentPlayerChoice = null
        currentComputerChoice = null
        currentResult = null
        isPlaying = false

        // Update score display
        playerScoreText.text = "0"
        computerScoreText.text = "0"

        // Hide all game elements
        gameArea.visibility = View.GONE
        resultText.visibility = View.GONE
        resultDescription.visibility = View.GONE
        playAgainButton.visibility = View.GONE
        resetGameButton.visibility = View.GONE

        // Show instructions
        showInstructions()

        // Enable choice buttons
        enableChoiceButtons()
    }
}