package com.crowdproj.vote.biz.validation

import com.crowdproj.vote.biz.CwpVoteProcessor
import com.crowdproj.vote.common.CwpVoteContext
import com.crowdproj.vote.common.models.*
import com.crowdproj.vote.common.models.CwpVoteCommand
import com.crowdproj.vote.stubs.CwpVoteStub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

private val stub = CwpVoteStub.get()

@OptIn(ExperimentalCoroutinesApi::class)
fun validationRatingIdCorrect(command: CwpVoteCommand, processor: CwpVoteProcessor) = runTest {
    val ctx = CwpVoteContext(
        command = command,
        state = CwpVoteState.NONE,
        workMode = CwpVoteWorkMode.TEST,
        voteRequest = CwpVote(
            id = stub.id,
            ratingId = CwpVoteRatingId("123"),
            userId = stub.userId,
            score = stub.score
        ),
    )
    processor.exec(ctx)
    assertEquals(0, ctx.errors.size)
    assertNotEquals(CwpVoteState.FAILING, ctx.state)
    assertEquals("123", ctx.voteValidated.ratingId.asString())
}

@OptIn(ExperimentalCoroutinesApi::class)
fun validationRatingIdEmpty(command: CwpVoteCommand, processor: CwpVoteProcessor) = runTest {
    val ctx = CwpVoteContext(
        command = command,
        state = CwpVoteState.NONE,
        workMode = CwpVoteWorkMode.TEST,
        voteRequest = CwpVote(
            id = stub.id,
            ratingId = CwpVoteRatingId(""),
            userId = stub.userId,
            score = stub.score
        ),
    )
    processor.exec(ctx)
    assertEquals(1, ctx.errors.size)
    assertEquals(CwpVoteState.FAILING, ctx.state)
    val error = ctx.errors.firstOrNull()
    assertEquals("ratingId", error?.field)
    assertContains(error?.message ?: "", "ratingId")
}

@OptIn(ExperimentalCoroutinesApi::class)
fun validationRatingIdSymbols(command: CwpVoteCommand, processor: CwpVoteProcessor) = runTest {
    val ctx = CwpVoteContext(
        command = command,
        state = CwpVoteState.NONE,
        workMode = CwpVoteWorkMode.TEST,
        voteRequest = CwpVote(
            id = stub.id,
            ratingId = CwpVoteRatingId("wrongFormat"),
            userId = stub.userId,
            score = stub.score
        ),
    )
    processor.exec(ctx)
    assertEquals(1, ctx.errors.size)
    assertEquals(CwpVoteState.FAILING, ctx.state)
    val error = ctx.errors.firstOrNull()
    assertEquals("ratingId", error?.field)
    assertContains(error?.message ?: "", "ratingId")
}
