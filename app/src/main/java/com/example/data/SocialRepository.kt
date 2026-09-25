package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class SocialRepository(private val dao: SocialDao) {

    val allAccounts: Flow<List<AccountEntity>> = dao.getAllAccounts()
    val currentAccount: Flow<AccountEntity?> = dao.getCurrentAccount()
    val allNotifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()
    val savedPosts: Flow<List<PostEntity>> = dao.getSavedPosts()

    // Combine posts with their authors and comments for a complete feed
    val feedPosts: Flow<List<PostWithDetails>> = combine(
        dao.getAllPosts(),
        dao.getAllAccounts(),
        dao.getAllComments()
    ) { posts, accounts, comments ->
        val accountMap = accounts.associateBy { it.id }
        val commentsMap = comments.groupBy { it.postId }

        posts.map { post ->
            PostWithDetails(
                post = post,
                author = accountMap[post.userId] ?: AccountEntity(
                    id = post.userId,
                    name = "User",
                    username = "user",
                    bio = ""
                ),
                comments = commentsMap[post.id] ?: emptyList()
            )
        }
    }

    fun getPostsForUser(userId: String): Flow<List<PostEntity>> =
        dao.getPostsByUser(userId)

    suspend fun createPost(
        userId: String,
        text: String,
        mediaUri: String,
        mediaType: String
    ) {
        val newPost = PostEntity(
            id = "p_${System.currentTimeMillis()}_${(100..999).random()}",
            userId = userId,
            text = text,
            mediaUri = mediaUri,
            mediaType = mediaType,
            timestamp = System.currentTimeMillis(),
            likeCount = 0,
            isLikedByMe = false,
            isSaved = false
        )
        dao.insertPost(newPost)
    }

    suspend fun toggleLike(postId: String) {
        val post = dao.getPostById(postId) ?: return
        val newLiked = !post.isLikedByMe
        val newCount = if (newLiked) post.likeCount + 1 else (post.likeCount - 1).coerceAtLeast(0)
        dao.updateLike(postId, newLiked, newCount)

        if (newLiked) {
            val current = dao.getCurrentAccountDirect()
            val senderName = current?.name ?: "Someone"
            dao.insertNotification(
                NotificationEntity(
                    id = "n_${System.currentTimeMillis()}",
                    title = "New Like",
                    message = "$senderName আপনার পোস্টে লাইক দিয়েছেন",
                    type = "like",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun toggleSave(postId: String) {
        val post = dao.getPostById(postId) ?: return
        dao.updateSaved(postId, !post.isSaved)
    }

    suspend fun deletePost(postId: String) {
        dao.deletePostById(postId)
        dao.deleteCommentsForPost(postId)
    }

    suspend fun addComment(
        postId: String,
        author: AccountEntity,
        text: String
    ) {
        val comment = CommentEntity(
            id = "c_${System.currentTimeMillis()}_${(100..999).random()}",
            postId = postId,
            authorId = author.id,
            authorName = author.name,
            authorUsername = author.username,
            authorAvatar = author.avatarUrl,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        dao.insertComment(comment)

        dao.insertNotification(
            NotificationEntity(
                id = "n_${System.currentTimeMillis()}",
                title = "New Comment",
                message = "${author.name} কমেন্ট করেছেন: '$text'",
                type = "comment",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleFollow(account: AccountEntity) {
        val current = dao.getCurrentAccountDirect() ?: return
        if (account.id == current.id) return

        if (account.isFollowing) {
            dao.unfollowAccount(account.id)
            dao.decrementFollowing(current.id)
        } else {
            dao.followAccount(account.id)
            dao.incrementFollowing(current.id)

            dao.insertNotification(
                NotificationEntity(
                    id = "n_${System.currentTimeMillis()}",
                    title = "New Follow",
                    message = "আপনি ${account.name} কে ফলো করছেন",
                    type = "follow",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun switchAccount(accountId: String) {
        dao.clearCurrentAccount()
        dao.setCurrentAccount(accountId)
    }

    suspend fun createAccount(name: String, username: String, bio: String): AccountEntity {
        val newAccount = AccountEntity(
            id = "u_${System.currentTimeMillis()}",
            name = name,
            username = username.ifBlank { name.lowercase().replace(" ", "_") },
            bio = bio.ifBlank { "New Rabby Social member 🚀" },
            avatarUrl = "",
            followersCount = 0,
            followingCount = 0,
            isCurrent = true,
            isFollowing = false
        )
        dao.clearCurrentAccount()
        dao.insertAccount(newAccount)
        return newAccount
    }

    suspend fun updateProfile(
        id: String,
        name: String,
        username: String,
        bio: String,
        avatarUrl: String
    ) {
        val existing = dao.getAccountById(id) ?: return
        dao.updateAccount(
            existing.copy(
                name = name,
                username = username,
                bio = bio,
                avatarUrl = if (avatarUrl.isNotEmpty()) avatarUrl else existing.avatarUrl
            )
        )
    }

    suspend fun clearNotifications() {
        dao.clearNotifications()
    }

    suspend fun resetDatabase() {
        dao.clearAllPosts()
        dao.clearAllComments()
        dao.clearAllAccounts()
        dao.clearNotifications()
        AppDatabase.populateInitialData(dao)
    }

    suspend fun ensureDefaultData() {
        val current = dao.getCurrentAccountDirect()
        if (current == null) {
            AppDatabase.populateInitialData(dao)
        }
    }
}
