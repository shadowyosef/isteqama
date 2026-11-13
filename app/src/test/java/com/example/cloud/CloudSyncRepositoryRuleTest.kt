package com.example.cloud

import com.example.base.FirestoreEmulatorTestBase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CloudSyncRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun backupUploadAndObserve_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repo = CloudSyncRepository(firestore, auth)

        val snap = UserBackupSnapshot(
            ownerId = uid,
            label = "Phone Sync",
            totalTasbeeh = 350L,
            totalPagesRead = 12,
            currentStreak = 7,
            locksCount = 3
        )
        val res = withTimeout(TIMEOUT_MS) { repo.uploadBackupSnapshot(snap) }
        assertTrue(res.isSuccess)

        val observed = withTimeout(TIMEOUT_MS) {
            repo.observeMyBackups().first { it.isNotEmpty() }
        }
        assertEquals(350L, observed.first().totalTasbeeh)
    }

    @Test
    fun circleCreateAndJoin_crossUserProgress_succeeds() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repo = CloudSyncRepository(firestore, auth)
        val created = withTimeout(TIMEOUT_MS) { repo.createCircle("Ramadan Group", 30) }
        assertTrue("createCircle failed: ${created.exceptionOrNull()}", created.isSuccess)
        val code = created.getOrThrow().inviteCode

        signInTestUser(BOB_EMAIL)
        val joined = withTimeout(TIMEOUT_MS) { repo.joinCircleByCode(code) }
        assertTrue("joinCircleByCode failed: ${joined.exceptionOrNull()}", joined.isSuccess)

        val addRes = withTimeout(TIMEOUT_MS) { repo.addCircleMemberPages(code, 5) }
        assertTrue("addCircleMemberPages failed: ${addRes.exceptionOrNull()}", addRes.isSuccess)

        val members = withTimeout(TIMEOUT_MS) {
            repo.observeCircleMembers(code).first { list -> list.any { it.pagesRead == 5 } }
        }
        assertEquals(2, members.size)
    }

    @Test
    fun unauthenticated_backupUpload_failsGracefully() = runBlocking {
        auth.signOut()
        val repo = CloudSyncRepository(firestore, auth)
        val res = withTimeout(TIMEOUT_MS) {
            repo.uploadBackupSnapshot(UserBackupSnapshot(label = "NoAuth"))
        }
        assertTrue(res.isFailure)
    }

    private companion object {
        const val ALICE_EMAIL = "alice.istaqama@example.com"
        const val BOB_EMAIL = "bob.istaqama@example.com"
        const val TIMEOUT_MS = 10000L
    }
}
