package com.example.data

import com.example.data.repository.MealRepositoryImpl
import com.example.domain.logger.ErrorLogger
import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import com.example.domain.repository.MealRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertTrue

class MealRepositoryImplTest {

    @MockK(relaxed = true)
    lateinit var mockFirestore: FirebaseFirestore
    @MockK(relaxed = true)
    lateinit var mockDocumentRef: DocumentReference
    @MockK(relaxed = true)
    lateinit var mockCollectionRef: CollectionReference
    @MockK(relaxed = true)
    lateinit var mockErrorLogger: ErrorLogger
    @MockK(relaxed = true)
    lateinit var mockQuery: Query
    @MockK(relaxed = true)
    lateinit var mockQuerySnapshot: QuerySnapshot

    private lateinit var repository: MealRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        mockkStatic("kotlinx.coroutines.tasks.TasksKt")

        every { mockFirestore.collection(any()) } returns mockCollectionRef
        every { mockCollectionRef.document(any()) } returns mockDocumentRef
        every { mockDocumentRef.collection(any()) } returns mockCollectionRef

        every { mockDocumentRef.set(any(), any<SetOptions>()) } returns mockk(relaxed = true)
        every { mockCollectionRef.add(any()) } returns mockk(relaxed = true)

        coEvery { any<Task<Void>>().await() } returns mockk()
        coEvery { any<Task<DocumentReference>>().await() } returns mockk(relaxed = true)

        repository = MealRepositoryImpl(mockFirestore, mockErrorLogger)
    }

    @Test(timeout = 5000)
    fun `addDishToMeal should call set for timestamp and add for dish`() = runTest {
        val testDish = Dish(id = "dish1", title = "Salad", kcal = 100)

        val result = repository.addDishToMeal("userX", "2025-11-28", MealType.LUNCH, testDish)

        assertTrue(result.isSuccess)

        coVerify {
            mockDocumentRef.set(any(), SetOptions.merge())
            mockCollectionRef.add(any())
        }
    }

    @Test(timeout = 5000)
    fun `removeDishFromMeal should call delete on the correct document path`() = runTest {
        val userId = "userX"
        val date = "2025-11-28"
        val mealType = MealType.LUNCH
        val dishId = "dishToRemove1"

        val mockFinalDishRef: DocumentReference = mockk()

        coEvery { mockFinalDishRef.delete() } returns mockk(relaxed = true)

        coEvery {
            mockFirestore.collection("users")
                .document(userId)
                .collection("diary")
                .document(date)
                .collection(mealType.value)
                .document(dishId)
        } returns mockFinalDishRef

        val result = repository.removeDishFromMeal(userId, date, mealType, dishId)

        assertTrue { result.isSuccess }
        coVerify(exactly = 1) {
            mockFinalDishRef.delete()
        }
    }

    @Test(timeout = 5000)
    fun `updateDishInMeal should use transaction when meal type changes`() = runTest {
        val userId = "userX"
        val date = "2025-11-29"
        val testDish = Dish(id = "d1", title = "Changed dish", kcal = 250)

        val mockTransactionTask = mockk<Task<Void>>(relaxed = true)

        every { mockFirestore.runTransaction<Void>(any()) } returns mockTransactionTask
        coEvery { mockTransactionTask.await() } returns mockk()

        val result = repository.updateDishInMeal(
            userId, date, MealType.LUNCH, MealType.DINNER, testDish
        )

        assertTrue { result.isSuccess }
        verify { mockFirestore.runTransaction<Void>(any()) }
    }

    @Test(timeout = 5000)
    fun `updateDishInMeal should use update when meal type stays same`() = runTest {
        val userId = "userX"
        val date = "2025-11-29"
        val testDish = Dish(id = "dish1", title = "Salad", kcal = 150)
        val mealType = MealType.LUNCH

        val mockUpdatedTask = mockk<Task<Void>>(relaxed = true)

        every { mockDocumentRef.update(any<Map<String, Any>>()) } returns mockUpdatedTask
        coEvery { mockUpdatedTask.await() } returns mockk()

        val result = repository.updateDishInMeal(
            userId, date, mealType, mealType, testDish
        )

        assertTrue { result.isSuccess }

        coVerify {
            mockDocumentRef.update(any<Map<String, Any>>())
        }

        verify(exactly = 0) { mockFirestore.runTransaction<Void>(any())}
    }

    @Test(timeout = 5000)
    fun `getMealsForDateRange should query correct date range and process all documents`() = runTest {
        val userId = "userX"
        val startDate = "2025-12-01"
        val endDate = "2025-12-07"

        val docId1 = "2025-12-01"
        val docId2 = "2025-12-02"

        val mockDoc1: DocumentSnapshot = mockk{
            every { id } returns docId1
        }
        val mockDoc2: DocumentSnapshot = mockk{
            every { id } returns docId2
        }
        val documents = listOf(mockDoc1, mockDoc2)

        val mockQueryTask = mockk<Task<QuerySnapshot>>(relaxed = true)

        every { mockCollectionRef.whereGreaterThanOrEqualTo(FieldPath.documentId(), startDate) } returns mockQuery
        every { mockQuery.whereLessThanOrEqualTo(FieldPath.documentId(), endDate) } returns mockQuery
        every { mockQuery.get() } returns mockQueryTask
        coEvery { mockQueryTask.await() } returns mockQuerySnapshot
        every { mockQuerySnapshot.documents } returns documents

        val emptyTask = mockk<Task<QuerySnapshot>>(relaxed = true)
        val emptyQuerySnapshot = mockk<QuerySnapshot>(relaxed = true)

        every { mockCollectionRef.get() } returns emptyTask
        coEvery { emptyTask.await() } returns emptyQuerySnapshot
        every { emptyQuerySnapshot.documents } returns emptyList()

        val result = repository.getMealsForDateRange(userId, startDate, endDate)
        assertTrue { result.isSuccess }

        val mealsMap = result.getOrNull()

        assertTrue { mealsMap?.containsKey(docId1) == true }
        assertTrue { mealsMap?.containsKey(docId2) == true }

        verify {
            mockCollectionRef.whereGreaterThanOrEqualTo(FieldPath.documentId(), startDate)
            mockQuery.whereLessThanOrEqualTo(FieldPath.documentId(), endDate)
        }
    }
}