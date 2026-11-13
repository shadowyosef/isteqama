package com.example

import com.example.data.*
import com.example.model.SurahInfo
import com.example.model.VoluntaryFastType
import com.example.util.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NewFeaturesUnitTest {

    @Test
    fun zakatCalculatorRespectsNisabAndRate() {
        val below = ZakatCalculatorHelper.calculateZakat(
            cashAmount = 500.0, goldGrams24 = 0.0, goldGrams21 = 0.0,
            silverGrams = 0.0, tradeGoodsValue = 0.0, goldPricePerGram24 = 60.0
        )
        assertFalse(below.isZakatEligible)
        assertEquals(0.0, below.zakatAmountDue, 0.001)

        val above = ZakatCalculatorHelper.calculateZakat(
            cashAmount = 10000.0, goldGrams24 = 0.0, goldGrams21 = 0.0,
            silverGrams = 0.0, tradeGoodsValue = 0.0, goldPricePerGram24 = 60.0
        )
        assertTrue(above.isZakatEligible)
        assertEquals(250.0, above.zakatAmountDue, 0.01)
    }

    @Test
    fun quizAndHifzChallengeGenerateValidItems() {
        val quiz = IslamicQuizCatalog.getRandomQuestion()
        assertTrue(quiz.questionAr.isNotBlank())
        assertTrue(quiz.correctIndex in quiz.optionsAr.indices)

        val hifz = HifzChallengeHelper.buildChallenge(listOf(1, 112))
        assertTrue(hifz.words.size >= 4)
        assertTrue(hifz.blankIndices.isNotEmpty())
        hifz.blankIndices.forEach { idx ->
            assertTrue(hifz.candidates.contains(hifz.words[idx]))
        }
    }

    @Test
    fun tafseerSourcesSearchAndSurahVirtuesWork() {
        val bundle = TafseerDataProvider.getRichTafseer(2, 255)
        assertTrue(bundle.muyassar.isNotBlank())
        assertTrue(bundle.saadi.isNotBlank())
        assertTrue(bundle.ibnKathir.isNotBlank())
        assertTrue(bundle.irabAndWords.isNotBlank())

        val results = TafseerDataProvider.searchEntries("الكرسي")
        assertTrue(results.isNotEmpty())

        val info = SurahVirtuesCatalog.getInfo(SurahInfo(18, "الكهف", "Al-Kahf", 110, "Meccan", 293, 304))
        assertTrue(info.purposeAr.isNotBlank())
        assertTrue(info.virtueAr.isNotBlank())
        assertTrue(info.reasonAr.isNotBlank())
    }

    @Test
    fun smartTimeGradientAdaptsByHour() {
        val morning = SmartAutoLockHelper.buildSmartLockForHour(7)
        val noon = SmartAutoLockHelper.buildSmartLockForHour(13)
        val evening = SmartAutoLockHelper.buildSmartLockForHour(18)
        val night = SmartAutoLockHelper.buildSmartLockForHour(23)

        assertEquals("DHIKR", morning.lockType)
        assertEquals("TASBEEH", noon.lockType)
        assertEquals("DHIKR", evening.lockType)
        assertEquals("QURAN", night.lockType)
    }

    @Test
    fun routinePackageCodeAndQrMatrixWork() {
        val code = RoutineShareCodeHelper.exportPackageCode()
        assertTrue(code.startsWith("ISTAQAMA1:"))
        val imported = RoutineShareCodeHelper.importPackageCode(code)
        assertTrue(imported.isSuccess)

        val matrix = RoutineShareCodeHelper.buildQrMatrix(code)
        assertEquals(21, matrix.size)
        assertTrue(matrix.all { it.size == 21 })
        assertTrue(matrix[0][0] && matrix[0][6] && matrix[6][0])
    }

    @Test
    fun fastingRecommendationRecognizesMonday() {
        val cal = Calendar.getInstance().apply { set(2025, Calendar.FEBRUARY, 3) } // Monday
        val (type, msg) = FastingTrackerRepository.getTodayFastingRecommendation(cal)
        assertNotNull(type)
        assertTrue(msg.isNotBlank())
        if (type != VoluntaryFastType.WHITE_DAYS && type != VoluntaryFastType.ASHURA && type != VoluntaryFastType.ARAFAT) {
            assertEquals(VoluntaryFastType.MONDAY_THURSDAY, type)
        }
    }
}
