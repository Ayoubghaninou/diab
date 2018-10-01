package it.diab.glucose.list

import androidx.lifecycle.ViewModelProviders
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import it.diab.MainActivity
import it.diab.db.AppDatabase
import it.diab.db.entities.Glucose
import it.diab.db.entities.Insulin
import it.diab.test.random
import it.diab.util.extensions.asTimeFrame
import it.diab.util.extensions.glucose
import it.diab.util.extensions.insulin
import it.diab.util.timeFrame.TimeFrame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar

@RunWith(AndroidJUnit4::class)
class GlucoseListViewModelTest {
    private var mViewModel: GlucoseListViewModel? = null

    private val mGlucoseValues = arrayOf(89, 149, 201, 100)
    private val mData = Array(mGlucoseValues.size) { Glucose() }
    private var mTestTimeFrame: TimeFrame? = null
    private var mInsulin: Insulin? = null

    @Suppress("MemberVisibilityCanBePrivate")
    @get:Rule
    val testRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Before
    fun setup() {
        mViewModel = ViewModelProviders.of(testRule.activity)[GlucoseListViewModel::class.java]

        AppDatabase.TEST_MODE = true
        val db = AppDatabase.getInstance(testRule.activity)

        val calendar = Calendar.getInstance()
        mTestTimeFrame = calendar.time.asTimeFrame()

        for (i in 0..(mGlucoseValues.size - 1)) {
            calendar[Calendar.DAY_OF_YEAR] -= i
            mData[i] = glucose {
                uid = i.toLong()
                value = mGlucoseValues[i]
                date = calendar.time
                insulinId0 = (-1..0).random().toLong()
                insulinValue0 = (0..10).random().toFloat()
                insulinId1 = (-1..0).random().toLong()
                insulinValue1 = (0..5).random().toFloat()
                eatLevel = (0..2).random()
                timeFrame = TimeFrame.MORNING
            }

            db.glucose().insert(mData[i])
        }

        mInsulin = insulin {
            uid = 0
            name = "FooBar"
            timeFrame = mTestTimeFrame!!
        }
        db.insulin().insert(mInsulin!!)
    }

    @Test
    fun getInsulin() {
        val test = mViewModel!!.getInsulin(mInsulin!!.uid)
        assert(test.uid == mInsulin!!.uid)
        assert(test == mInsulin)
    }
}