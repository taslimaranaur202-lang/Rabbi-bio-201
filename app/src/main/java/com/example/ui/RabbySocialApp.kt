package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.UserAvatar
import com.example.ui.dialogs.CreatePostDialog
import com.example.ui.dialogs.EditProfileDialog
import com.example.ui.dialogs.NewAccountDialog
import com.example.ui.dialogs.PostMenuDialog
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.dialogs.SwitchAccountDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SavedScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.BrandPink
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RabbySocialApp(
    viewModel: SocialViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    MyApplicationTheme(darkTheme = uiState.isDarkMode) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(310.dp),
                    drawerContainerColor = MaterialTheme.colorScheme.surface
                ) {
                    // Drawer Header
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                UserAvatar(
                                    avatarUrl = uiState.currentAccount?.avatarUrl,
                                    name = uiState.currentAccount?.name ?: "User",
                                    size = 52.dp
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = uiState.currentAccount?.name ?: "Rabby",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "@${uiState.currentAccount?.username ?: "user"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = if (uiState.isBengali) "আমার অ্যাকাউন্ট" else "My Account",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (uiState.isBengali) "মেন্যু" else "Menu",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )

                    // Menu items
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text(if (uiState.isBengali) "👤 প্রোফাইল" else "👤 Profile") },
                        selected = uiState.currentTab == NavTab.PROFILE,
                        onClick = {
                            viewModel.setNavTab(NavTab.PROFILE)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        label = { Text(if (uiState.isBengali) "✏️ প্রোফাইল এডিট" else "✏️ Edit Profile") },
                        selected = false,
                        onClick = {
                            viewModel.setShowEditProfile(true)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                        label = { Text(if (uiState.isBengali) "🔖 সংরক্ষিত পোস্ট" else "🔖 Saved Posts") },
                        selected = uiState.currentTab == NavTab.SAVED,
                        onClick = {
                            viewModel.setNavTab(NavTab.SAVED)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                        label = { Text(if (uiState.isBengali) "🔔 নোটিফিকেশন" else "🔔 Notifications") },
                        selected = uiState.currentTab == NavTab.NOTIFICATIONS,
                        onClick = {
                            viewModel.setNavTab(NavTab.NOTIFICATIONS)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Image, contentDescription = null) },
                        label = { Text(if (uiState.isBengali) "📷 ছবি/ভিডিও পোস্ট" else "📷 Create Post") },
                        selected = false,
                        onClick = {
                            viewModel.setShowCreatePost(true)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.SwitchAccount, contentDescription = null) },
                        label = { Text(if (uiState.isBengali) "👥 অ্যাকাউন্ট পরিবর্তন" else "👥 Switch Account") },
                        selected = false,
                        onClick = {
                            viewModel.setShowSwitchAccount(true)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text(if (uiState.isBengali) "⚙️ সেটিংস" else "⚙️ Settings") },
                        selected = false,
                        onClick = {
                            viewModel.setShowSettings(true)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                        label = { Text(if (uiState.isBengali) "🚪 লগআউট" else "🚪 Logout") },
                        selected = false,
                        onClick = {
                            showLogoutDialog = true
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("main_scaffold"),
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Rabby Social",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = BrandBluePrimary
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                    }
                                },
                                modifier = Modifier.testTag("top_menu_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        actions = {
                            // Saved bookmark quick button
                            IconButton(
                                onClick = { viewModel.setNavTab(NavTab.SAVED) },
                                modifier = Modifier.testTag("top_saved_button")
                            ) {
                                Icon(
                                    imageVector = if (uiState.currentTab == NavTab.SAVED) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Saved Posts",
                                    tint = if (uiState.currentTab == NavTab.SAVED) BrandBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Quick create post button (＋)
                            IconButton(
                                onClick = { viewModel.setShowCreatePost(true) },
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .size(38.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                    .testTag("top_create_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Create Post",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        // Home
                        NavigationBarItem(
                            selected = uiState.currentTab == NavTab.HOME,
                            onClick = { viewModel.setNavTab(NavTab.HOME) },
                            icon = {
                                Icon(
                                    imageVector = if (uiState.currentTab == NavTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text(if (uiState.isBengali) "হোম" else "Home") },
                            modifier = Modifier.testTag("nav_home")
                        )

                        // Search
                        NavigationBarItem(
                            selected = uiState.currentTab == NavTab.SEARCH,
                            onClick = { viewModel.setNavTab(NavTab.SEARCH) },
                            icon = {
                                Icon(
                                    imageVector = if (uiState.currentTab == NavTab.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                                    contentDescription = "Search"
                                )
                            },
                            label = { Text(if (uiState.isBengali) "খুঁজুন" else "Search") },
                            modifier = Modifier.testTag("nav_search")
                        )

                        // Create Post
                        NavigationBarItem(
                            selected = false,
                            onClick = { viewModel.setShowCreatePost(true) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Create Post"
                                )
                            },
                            label = { Text(if (uiState.isBengali) "পোস্ট" else "Post") },
                            modifier = Modifier.testTag("nav_create")
                        )

                        // Notifications
                        val unreadCount = uiState.notifications.count { !it.isRead }
                        NavigationBarItem(
                            selected = uiState.currentTab == NavTab.NOTIFICATIONS,
                            onClick = { viewModel.setNavTab(NavTab.NOTIFICATIONS) },
                            icon = {
                                if (unreadCount > 0) {
                                    BadgedBox(badge = { Badge { Text("$unreadCount") } }) {
                                        Icon(
                                            imageVector = if (uiState.currentTab == NavTab.NOTIFICATIONS) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                            contentDescription = "Notifications"
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = if (uiState.currentTab == NavTab.NOTIFICATIONS) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Notifications"
                                    )
                                }
                            },
                            label = { Text(if (uiState.isBengali) "নোটিফিকেশন" else "Alerts") },
                            modifier = Modifier.testTag("nav_notifications")
                        )

                        // Profile
                        NavigationBarItem(
                            selected = uiState.currentTab == NavTab.PROFILE,
                            onClick = { viewModel.setNavTab(NavTab.PROFILE) },
                            icon = {
                                Icon(
                                    imageVector = if (uiState.currentTab == NavTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "Profile"
                                )
                            },
                            label = { Text(if (uiState.isBengali) "প্রোফাইল" else "Profile") },
                            modifier = Modifier.testTag("nav_profile")
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Crossfade(
                        targetState = uiState.currentTab,
                        label = "ScreenTransition"
                    ) { tab ->
                        when (tab) {
                            NavTab.HOME -> HomeScreen(
                                currentAccount = uiState.currentAccount,
                                allAccounts = uiState.allAccounts,
                                feedPosts = uiState.feedPosts,
                                isBengali = uiState.isBengali,
                                onQuickPost = { text, mediaUri ->
                                    viewModel.createPost(text, mediaUri, if (mediaUri.isNotBlank()) "image" else "")
                                },
                                onOpenFullComposer = { viewModel.setShowCreatePost(true) },
                                onLikeClick = { viewModel.toggleLike(it) },
                                onSaveClick = { viewModel.toggleSave(it) },
                                onMenuClick = { viewModel.setSelectedPostForMenu(it) },
                                onAddComment = { postId, text -> viewModel.addComment(postId, text) }
                            )

                            NavTab.SEARCH -> SearchScreen(
                                searchQuery = uiState.searchQuery,
                                searchResults = uiState.searchResults,
                                currentAccount = uiState.currentAccount,
                                isBengali = uiState.isBengali,
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                onToggleFollow = { viewModel.toggleFollow(it) }
                            )

                            NavTab.PROFILE -> ProfileScreen(
                                currentAccount = uiState.currentAccount,
                                myPosts = uiState.feedPosts.filter { it.post.userId == uiState.currentAccount?.id },
                                isBengali = uiState.isBengali,
                                onEditProfileClick = { viewModel.setShowEditProfile(true) },
                                onSwitchAccountClick = { viewModel.setShowSwitchAccount(true) },
                                onLikeClick = { viewModel.toggleLike(it) },
                                onSaveClick = { viewModel.toggleSave(it) },
                                onMenuClick = { viewModel.setSelectedPostForMenu(it) },
                                onAddComment = { postId, text -> viewModel.addComment(postId, text) }
                            )

                            NavTab.NOTIFICATIONS -> NotificationsScreen(
                                notifications = uiState.notifications,
                                isBengali = uiState.isBengali,
                                onClearAll = { viewModel.clearNotifications() }
                            )

                            NavTab.SAVED -> SavedScreen(
                                savedPosts = uiState.feedPosts.filter { it.post.isSaved },
                                isBengali = uiState.isBengali,
                                onLikeClick = { viewModel.toggleLike(it) },
                                onSaveClick = { viewModel.toggleSave(it) },
                                onMenuClick = { viewModel.setSelectedPostForMenu(it) },
                                onAddComment = { postId, text -> viewModel.addComment(postId, text) }
                            )

                            NavTab.CREATE -> {} // Handled via dialog
                        }
                    }
                }
            }

            // Dialogs
            if (uiState.showCreatePostDialog) {
                CreatePostDialog(
                    isBengali = uiState.isBengali,
                    onDismiss = { viewModel.setShowCreatePost(false) },
                    onSubmit = { text, mediaUri ->
                        viewModel.createPost(text, mediaUri, if (mediaUri.isNotBlank()) "image" else "")
                    }
                )
            }

            if (uiState.showEditProfileDialog) {
                EditProfileDialog(
                    currentAccount = uiState.currentAccount,
                    isBengali = uiState.isBengali,
                    onDismiss = { viewModel.setShowEditProfile(false) },
                    onSave = { name, username, bio, avatarUrl ->
                        viewModel.updateProfile(name, username, bio, avatarUrl)
                    }
                )
            }

            if (uiState.showSwitchAccountDialog) {
                SwitchAccountDialog(
                    accounts = uiState.allAccounts,
                    currentAccountId = uiState.currentAccount?.id,
                    isBengali = uiState.isBengali,
                    onDismiss = { viewModel.setShowSwitchAccount(false) },
                    onSwitch = { viewModel.switchAccount(it) },
                    onOpenNewAccount = { viewModel.setShowNewAccount(true) }
                )
            }

            if (uiState.showNewAccountDialog) {
                NewAccountDialog(
                    isBengali = uiState.isBengali,
                    onDismiss = { viewModel.setShowNewAccount(false) },
                    onCreate = { name, username, bio ->
                        viewModel.createNewAccount(name, username, bio)
                    }
                )
            }

            if (uiState.showSettingsDialog) {
                SettingsDialog(
                    isDarkMode = uiState.isDarkMode,
                    isBengali = uiState.isBengali,
                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onResetData = { viewModel.resetDatabase() },
                    onDismiss = { viewModel.setShowSettings(false) }
                )
            }

            uiState.selectedPostForMenu?.let { selectedPost ->
                PostMenuDialog(
                    post = selectedPost,
                    isBengali = uiState.isBengali,
                    onDismiss = { viewModel.setSelectedPostForMenu(null) },
                    onCopyText = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Post", selectedPost.text)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(
                            context,
                            if (uiState.isBengali) "কপি হয়েছে!" else "Copied!",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onToggleSave = { viewModel.toggleSave(selectedPost.id) },
                    onDelete = { viewModel.deletePost(selectedPost.id) }
                )
            }

            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text(if (uiState.isBengali) "লগআউট" else "Logout") },
                    text = {
                        Text(
                            if (uiState.isBengali)
                                "এটি Rabby Social-এর ডেমো সংস্করণ। আপনি যেকোনো সময় অন্য অ্যাকাউন্টে পরিবর্তন করতে পারেন।"
                            else
                                "This is the demo version of Rabby Social. You can switch between accounts at any time."
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text(if (uiState.isBengali) "ঠিক আছে" else "OK")
                        }
                    }
                )
            }
        }
    }
}
