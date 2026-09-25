package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AccountEntity::class,
        PostEntity::class,
        CommentEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun socialDao(): SocialDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rabby_social_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.socialDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: SocialDao) {
            val accounts = listOf(
                AccountEntity(
                    id = "u1",
                    name = "Rabby",
                    username = "rabby_9t4",
                    bio = "Welcome to Rabby Social 🚀 | Android & Web Enthusiast",
                    avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200",
                    followersCount = 142,
                    followingCount = 38,
                    isCurrent = true,
                    isFollowing = false
                ),
                AccountEntity(
                    id = "u2",
                    name = "Anik Rahman",
                    username = "anik_tech",
                    bio = "Tech enthusiast & photography lover 📸",
                    avatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=200",
                    followersCount = 89,
                    followingCount = 45,
                    isCurrent = false,
                    isFollowing = true
                ),
                AccountEntity(
                    id = "u3",
                    name = "Nusrat Jahan",
                    username = "nusrat_art",
                    bio = "UI/UX Designer & Creative Artist 🎨 Dhaka",
                    avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200",
                    followersCount = 320,
                    followingCount = 112,
                    isCurrent = false,
                    isFollowing = false
                ),
                AccountEntity(
                    id = "u4",
                    name = "Tanvir Ahmed",
                    username = "tanvir_code",
                    bio = "Kotlin & Modern Mobile Architecture 💻",
                    avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200",
                    followersCount = 115,
                    followingCount = 76,
                    isCurrent = false,
                    isFollowing = false
                )
            )
            dao.insertAccounts(accounts)

            val now = System.currentTimeMillis()
            val posts = listOf(
                PostEntity(
                    id = "p1",
                    userId = "u1",
                    text = "স্বাগতম Rabby Social অ্যাপে! 🚀\nএখানে ছবি ও মনের ভাবনা শেয়ার করুন, বন্ধুদের ফলো করুন আর কমেন্টে আলোচনা উপভোগ করুন।",
                    mediaUri = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800",
                    mediaType = "image",
                    timestamp = now - 1000 * 60 * 15,
                    likeCount = 18,
                    isLikedByMe = true,
                    isSaved = false
                ),
                PostEntity(
                    id = "p2",
                    userId = "u2",
                    text = "আজকের বিকেলটা অসাধারণ কাটল! মেঘলা আকাশ আর এক কাপ গরম চা ☕☁️\n#EveningVibes #Chill",
                    mediaUri = "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=800",
                    mediaType = "image",
                    timestamp = now - 1000 * 60 * 60 * 2,
                    likeCount = 24,
                    isLikedByMe = false,
                    isSaved = true
                ),
                PostEntity(
                    id = "p3",
                    userId = "u3",
                    text = "Exploring new modern UI patterns in Jetpack Compose! Smooth transitions, clean layouts, and rich Material 3 dynamic styling ✨",
                    mediaUri = "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800",
                    mediaType = "image",
                    timestamp = now - 1000 * 60 * 60 * 5,
                    likeCount = 42,
                    isLikedByMe = true,
                    isSaved = true
                ),
                PostEntity(
                    id = "p4",
                    userId = "u4",
                    text = "নতুন প্রজেক্টে Room Database আর Kotlin Flow ব্যবহার করছি। ডেটা হ্যান্ডলিং এখন অনেক দ্রুত ও নির্ভুল। 🚀💡",
                    mediaUri = "",
                    mediaType = "",
                    timestamp = now - 1000 * 60 * 60 * 12,
                    likeCount = 16,
                    isLikedByMe = false,
                    isSaved = false
                )
            )
            dao.insertPosts(posts)

            val comments = listOf(
                CommentEntity(
                    id = "c1",
                    postId = "p1",
                    authorName = "Anik Rahman",
                    authorUsername = "anik_tech",
                    authorAvatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=200",
                    text = "অভিনন্দন ভাই! ডিজাইনটি দেখতে বেশ আকর্ষণীয় হয়েছে। 🔥",
                    timestamp = now - 1000 * 60 * 10
                ),
                CommentEntity(
                    id = "c2",
                    postId = "p1",
                    authorName = "Nusrat Jahan",
                    authorUsername = "nusrat_art",
                    authorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200",
                    text = "Very clean and user-friendly interface! Loved the blue theme! 💙",
                    timestamp = now - 1000 * 60 * 5
                ),
                CommentEntity(
                    id = "c3",
                    postId = "p2",
                    authorName = "Rabby",
                    authorUsername = "rabby_9t4",
                    authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200",
                    text = "চা আর আড্ডা, দারুণ কম্বিনেশন! ☕👌",
                    timestamp = now - 1000 * 60 * 30
                )
            )
            dao.insertComments(comments)

            val notifications = listOf(
                NotificationEntity(
                    id = "n1",
                    title = "New Like",
                    message = "Anik Rahman আপনার পোস্টে লাইক দিয়েছেন",
                    type = "like",
                    timestamp = now - 1000 * 60 * 12,
                    isRead = false
                ),
                NotificationEntity(
                    id = "n2",
                    title = "New Comment",
                    message = "Nusrat Jahan আপনার পোস্টে কমেন্ট করেছেন: 'Very clean...'",
                    type = "comment",
                    timestamp = now - 1000 * 60 * 5,
                    isRead = false
                ),
                NotificationEntity(
                    id = "n3",
                    title = "New Follower",
                    message = "Tanvir Ahmed আপনাকে ফলো করা শুরু করেছেন",
                    type = "follow",
                    timestamp = now - 1000 * 60 * 60 * 3,
                    isRead = true
                ),
                NotificationEntity(
                    id = "n4",
                    title = "স্বাগতম",
                    message = "Rabby Social এ যোগ দেওয়ার জন্য ধন্যবাদ! আপনার প্রথম পোস্ট তৈরি করুন।",
                    type = "welcome",
                    timestamp = now - 1000 * 60 * 60 * 24,
                    isRead = true
                )
            )
            dao.insertNotifications(notifications)
        }
    }
}
