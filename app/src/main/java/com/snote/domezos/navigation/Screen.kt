package com.snote.domezos.navigation

sealed class Screen(val route: String) {
    data object Language : Screen("language")
    data object Main     : Screen("main")
    data object Help     : Screen("help")
    data object Info     : Screen("info")
    data object TinyUrl  : Screen("tinyUrl")
}
