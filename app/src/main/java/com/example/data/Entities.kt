package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val username: String,
    val bio: String,
    val avatarUrl: String = "",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isCurrent: Boolean = false,
    val isFollowing: Boolean = false
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val text: String,
    val mediaUri: String = "",
    val mediaType: String = "", // "image", "video", or ""
    val timestamp: Long = System.currentTimeMillis(),
    val likeCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val isSaved: Boolean = false
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val authorId: String = "",
    val authorName: String,
    val authorUsername: String = "",
    val authorAvatar: String = "",
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: String = "info", // "like", "follow", "comment", "welcome"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

// Combined model for UI rendering
data class PostWithDetails(
    val post: PostEntity,
    val author: AccountEntity?,
    val comments: List<CommentEntity> = emptyList()
)
