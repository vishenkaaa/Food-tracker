package com.example.data

import com.example.data.mapper.UserModelMapper
import com.example.data.mapper.UserModelMapper.userInfoToMap
import com.example.data.repository.UserRepositoryImpl
import com.example.domain.logger.ErrorLogger
import com.example.domain.model.user.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserRepositoryImplTest {
    @MockK(relaxed = true)
    private lateinit var mockFirestore: FirebaseFirestore
    @MockK(relaxed = true)
    private lateinit var mockCollectionRef: CollectionReference
    @MockK(relaxed = true)
    private lateinit var mockDocumentRef: DocumentReference
    @MockK(relaxed = true)
    lateinit var mockDocumentSnapshot: DocumentSnapshot

    @MockK(relaxed = true)
    private lateinit var mockErrorLogger: ErrorLogger

    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setup(){
        MockKAnnotations.init(this)
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")

        every { mockFirestore.collection(any()) } returns mockCollectionRef
        every { mockCollectionRef.document(any()) } returns mockDocumentRef

        coEvery { any<Task<Void>>().await() } returns mockk()
        coEvery { mockDocumentRef.set(any()) } returns mockk()

        repository = UserRepositoryImpl(mockFirestore, mockErrorLogger)
    }

    @Test
    fun `createUser should save user data to Firestore`() = runTest{
        val testUser = User()
        val result = repository.createUser(testUser)

        assertTrue(result.isSuccess)
        coVerify { mockDocumentRef.set(any()) }
    }

    @Test
    fun `getUser should map snapshot data to User object when document exists`() = runTest {
        val userId = "user1"
        val mockFirebaseData = mapOf(
            UserModelMapper.NAME_KEY to "New User",
            UserModelMapper.EMAIL_KEY to "new@example.com",
            UserModelMapper.GOAL_KEY to "LOSE",
            UserModelMapper.TARGET_WEIGHT_KEY to 75.0,
            UserModelMapper.CURRENT_WEIGHT_KEY to 80.0,
            UserModelMapper.HEIGHT_KEY to 180,
            UserModelMapper.GENDER_KEY to "MALE",
            UserModelMapper.USER_ACTIVITY_LEVEL_KEY to "ACTIVE",
            UserModelMapper.BIRTH_DATE_KEY to "1990-10-20",
            UserModelMapper.TARGET_CALORIES_KEY to 2000,
            UserModelMapper.IS_NEW_KEY to false
        )

        val mockTaskSnapshot = mockk<Task<DocumentSnapshot>>(relaxed = true)

        every { mockDocumentRef.get() } returns mockTaskSnapshot
        coEvery { mockTaskSnapshot.await() } returns mockDocumentSnapshot

        every { mockDocumentSnapshot.exists() } returns true
        every { mockDocumentSnapshot.data } returns mockFirebaseData

        val result = repository.getUser(userId)

        assertTrue(result.isSuccess)
        assertEquals(userId, result.getOrThrow().id)
        coVerify(exactly = 1) { mockDocumentRef.get() }
    }

    @Test
    fun `getTargetCalories should return calories when document exists`() = runTest{
        val userId = "user1"
        val expectedCalories = 2500

        val mockTaskSnapshot = mockk<Task<DocumentSnapshot>>(relaxed = true)

        every { mockDocumentRef.get() } returns mockTaskSnapshot
        coEvery { mockTaskSnapshot.await() } returns mockDocumentSnapshot

        every { mockDocumentSnapshot.exists() } returns true
        every { mockDocumentSnapshot["targetCalories"] } returns expectedCalories

        val result = repository.getTargetCalories(userId)

        assertTrue(result.isSuccess)
        assertEquals(expectedCalories, result.getOrThrow())

        coVerify(exactly = 1){ mockDocumentRef.get()}
    }

    @Test
    fun `updateUserInfo should call update with correct map`() = runTest{
        val testUser = User()
        val testUserMap = userInfoToMap(testUser)

        every { mockDocumentRef.update(any()) } returns mockk()

        val result = repository.updateUserInfo(testUser)
        assertTrue { result.isSuccess }
        coVerify(exactly = 1){ mockDocumentRef.update(testUserMap)}
    }

    @Test
    fun `isUserFullyRegistered returns false when user has isNew flag`() = runTest{
        val userId = "user1"
        val testUser = User(id = userId, isNew = true)
        val spyRepository = spyk(repository)

        coEvery { spyRepository.getUser(userId) } returns Result.success(testUser)

        val result = spyRepository.isUserFullyRegistered(userId)
        assertTrue(!result)
        coVerify(exactly = 1) { spyRepository.getUser(userId) }
    }
}