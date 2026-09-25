package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SocialDao {
    // Accounts
    @Query("SELECT * FROM accounts ORDER BY name ASC")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE isCurrent = 1 LIMIT 1")
    fun getCurrentAccount(): Flow<AccountEntity?>

    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: String): AccountEntity?

    @Query("SELECT * FROM accounts WHERE isCurrent = 1 LIMIT 1")
    suspend fun getCurrentAccountDirect(): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<AccountEntity>)

    @Update
    suspend fun updateAccount(account: AccountEntity)

    @Query("UPDATE accounts SET isCurrent = 0")
    suspend fun clearCurrentAccount()

    @Query("UPDATE accounts SET isCurrent = 1 WHERE id = :id")
    suspend fun setCurrentAccount(id: String)

    @Query("UPDATE accounts SET followersCount = followersCount + 1, isFollowing = 1 WHERE id = :id")
    suspend fun followAccount(id: String)

    @Query("UPDATE accounts SET followersCount = CASE WHEN followersCount > 0 THEN followersCount - 1 ELSE 0 END, isFollowing = 0 WHERE id = :id")
    suspend fun unfollowAccount(id: String)

    @Query("UPDATE accounts SET followingCount = followingCount + 1 WHERE id = :currentUserId")
    suspend fun incrementFollowing(currentUserId: String)

    @Query("UPDATE accounts SET followingCount = CASE WHEN followingCount > 0 THEN followingCount - 1 ELSE 0 END WHERE id = :currentUserId")
    suspend fun decrementFollowing(currentUserId: String)

    // Posts
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY timestamp DESC")
    fun getPostsByUser(userId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isSaved = 1 ORDER BY timestamp DESC")
    fun getSavedPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    suspend fun getPostById(id: String): PostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun deletePostById(id: String)

    @Query("UPDATE posts SET isLikedByMe = :liked, likeCount = :count WHERE id = :id")
    suspend fun updateLike(id: String, liked: Boolean, count: Int)

    @Query("UPDATE posts SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSaved(id: String, isSaved: Boolean)

    // Comments
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: String): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments ORDER BY timestamp ASC")
    fun getAllComments(): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Query("DELETE FROM comments WHERE postId = :postId")
    suspend fun deleteCommentsForPost(postId: String)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("DELETE FROM notifications")
    suspend fun clearNotifications()

    @Query("DELETE FROM posts")
    suspend fun clearAllPosts()

    @Query("DELETE FROM comments")
    suspend fun clearAllComments()

    @Query("DELETE FROM accounts")
    suspend fun clearAllAccounts()
}
