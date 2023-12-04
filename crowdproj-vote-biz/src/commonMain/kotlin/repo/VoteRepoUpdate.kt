package com.crowdproj.vote.biz.repo

import com.crowdproj.vote.common.CwpVoteContext
import com.crowdproj.vote.common.models.CwpVoteState
import com.crowdproj.vote.common.repo.DbVoteRequest
import com.crowdproj.vote.lib.cor.ICorChainDsl
import com.crowdproj.vote.lib.cor.worker

fun ICorChainDsl<CwpVoteContext>.repoUpdate(title: String) = worker {
    this.title = title
    on { state == CwpVoteState.RUNNING }
    handle {
        val request = DbVoteRequest(voteRepoPrepare)
        val result = voteRepo.updateVote(request)
        val resultVote = result.data
        if (result.isSuccess && resultVote != null) {
            voteRepoDone = resultVote
        } else {
            state = CwpVoteState.FAILING
            errors.addAll(result.errors)
            voteRepoDone
        }
    }
}
