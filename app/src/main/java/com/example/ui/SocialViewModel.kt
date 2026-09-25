package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AccountEntity
import com.example.data.AppDatabase
import com.example.data.NotificationEntity
import com.example.data.PostEntity
import com.example.data.PostWithDetails
import com.example.data.SocialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavTab {
    HOME, SEARCH, CREATE, NOTIFICATIONS, PROFILE, SAVED
}

data class SocialUiState(
    val currentTab: NavTab = NavTab.HOME,
    val currentAccount: AccountEntity? = null,
    val allAccounts: List<AccountEntity> = emptyList(),
    val feedPosts: List<PostWithDetails> = emptyList(),
    val savedPosts: List<PostEntity> = emptyList(),
    val notifications: List<NotificationEntity> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<AccountEntity> = emptyList(),
    val isDarkMode: Boolean = false,
    val isBengali: Boolean = true,
    // Dialogs
    val showCreatePostDialog: Boolean = false,
    val showEditProfileDialog: Boolean = false,
    val showSwitchAccountDialog: Boolean = false,
    val showNewAccountDialog: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val selectedPostForMenu: PostEntity? = null,
    val activeCommentsPostId: String? = null
)

class SocialViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SocialRepository

    private val _uiState = MutableStateFlow(SocialUiState())
    val uiState: StateFlow<SocialUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = SocialRepository(database.socialDao())

        viewModelScope.launch {
            repository.ensureDefaultData()
        }

        // Collect repository flows and update UI state
        viewModelScope.launch {
            combine(
                repository.currentAccount,
                repository.allAccounts,
                repository.feedPosts,
                repository.savedPosts,
                repository.allNotifications
            ) { current, accounts, feed, saved, notifications ->
                _uiState.value.copy(
                    currentAccount = current ?: accounts.firstOrNull { it.isCurrent } ?: accounts.firstOrNull(),
                    allAccounts = accounts,
                    feedPosts = feed,
                    savedPosts = saved,
                    notifications = notifications
                )
            }.collect { newState ->
                _uiState.value = newState.copy(
                    searchResults = filterAccounts(newState.allAccounts, _uiState.value.searchQuery)
                )
            }
        }
    }

    private fun filterAccounts(accounts: List<AccountEntity>, query: String): List<AccountEntity> {
        if (query.isBlank()) return accounts
        val q = query.trim().lowercase()
        return accounts.filter {
            it.name.lowercase().contains(q) || it.username.lowercase().contains(q) || it.bio.lowercase().contains(q)
        }
    }

    fun setNavTab(tab: NavTab) {
        if (tab == NavTab.CREATE) {
            _uiState.value = _uiState.value.copy(showCreatePostDialog = true)
        } else {
            _uiState.value = _uiState.value.copy(currentTab = tab)
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            searchResults = filterAccounts(_uiState.value.allAccounts, query)
        )
    }

    fun createPost(text: String, mediaUri: String = "", mediaType: String = "") {
        val current = _uiState.value.currentAccount ?: return
        viewModelScope.launch {
            repository.createPost(
                userId = current.id,
                text = text,
                mediaUri = mediaUri,
                mediaType = mediaType
            )
            _uiState.value = _uiState.value.copy(showCreatePostDialog = false)
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            repository.toggleLike(postId)
        }
    }

    fun toggleSave(postId: String) {
        viewModelScope.launch {
            repository.toggleSave(postId)
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            repository.deletePost(postId)
            _uiState.value = _uiState.value.copy(selectedPostForMenu = null)
        }
    }

    fun addComment(postId: String, text: String) {
        val current = _uiState.value.currentAccount ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(postId, current, text.trim())
        }
    }

    fun toggleFollow(account: AccountEntity) {
        viewModelScope.launch {
            repository.toggleFollow(account)
        }
    }

    fun switchAccount(accountId: String) {
        viewModelScope.launch {
            repository.switchAccount(accountId)
            _uiState.value = _uiState.value.copy(showSwitchAccountDialog = false)
        }
    }

    fun createNewAccount(name: String, username: String, bio: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.createAccount(name, username, bio)
            _uiState.value = _uiState.value.copy(
                showNewAccountDialog = false,
                showSwitchAccountDialog = false
            )
        }
    }

    fun updateProfile(name: String, username: String, bio: String, avatarUrl: String) {
        val current = _uiState.value.currentAccount ?: return
        viewModelScope.launch {
            repository.updateProfile(
                id = current.id,
                name = name,
                username = username,
                bio = bio,
                avatarUrl = avatarUrl
            )
            _uiState.value = _uiState.value.copy(showEditProfileDialog = false)
        }
    }

    fun clearNotifications() {
        viewModelScope.launch {
            repository.clearNotifications()
        }
    }

    fun resetDatabase() {
        viewModelScope.launch {
            repository.resetDatabase()
            _uiState.value = _uiState.value.copy(showSettingsDialog = false)
        }
    }

    fun toggleDarkMode() {
        _uiState.value = _uiState.value.copy(isDarkMode = !_uiState.value.isDarkMode)
    }

    fun toggleLanguage() {
        _uiState.value = _uiState.value.copy(isBengali = !_uiState.value.isBengali)
    }

    // Dialog controls
    fun setShowCreatePost(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCreatePostDialog = show)
    }

    fun setShowEditProfile(show: Boolean) {
        _uiState.value = _uiState.value.copy(showEditProfileDialog = show)
    }

    fun setShowSwitchAccount(show: Boolean) {
        _uiState.value = _uiState.value.copy(showSwitchAccountDialog = show)
    }

    fun setShowNewAccount(show: Boolean) {
        _uiState.value = _uiState.value.copy(showNewAccountDialog = show)
    }

    fun setShowSettings(show: Boolean) {
        _uiState.value = _uiState.value.copy(showSettingsDialog = show)
    }

    fun setSelectedPostForMenu(post: PostEntity?) {
        _uiState.value = _uiState.value.copy(selectedPostForMenu = post)
    }

    fun setActiveCommentsPostId(postId: String?) {
        _uiState.value = _uiState.value.copy(activeCommentsPostId = postId)
    }
}
