package me.mauromax.rutaconfiable.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import me.mauromax.rutaconfiable.ui.screens.*

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object ForgotPasswordStep1 : Screen("forgot_password_step_1")
    object ForgotPasswordStep2 : Screen("forgot_password_step_2")

    object Registration : Screen("registration/{role}") {
        fun createRoute(role: String = "driver") = "registration/$role"
    }

    object VerificationPending : Screen("verification_pending/{role}") {
        fun createRoute(role: String = "driver") = "verification_pending/$role"
    }

    object Profile : Screen("profile/{role}") {
        fun createRoute(role: String = "driver") = "profile/$role"
    }
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        // 1. Welcome Screen
        composable(route = Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Registration.createRoute("driver"))
                }
            )
        }

        // 2. Login Screen
        composable(route = Screen.Login.route) {
            LoginScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Profile.createRoute("driver")) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate(Screen.ForgotPasswordStep1.route)
                },
                onRegisterClick = {
                    navController.navigate(Screen.Registration.createRoute("driver"))
                }
            )
        }

        // 3. Forgot Password Step 1
        composable(route = Screen.ForgotPasswordStep1.route) {
            ForgotPasswordStep1Screen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSendCodeSuccess = {
                    navController.navigate(Screen.ForgotPasswordStep2.route)
                }
            )
        }

        // 4. Forgot Password Step 2
        composable(route = Screen.ForgotPasswordStep2.route) {
            ForgotPasswordStep2Screen(
                onBackClick = {
                    navController.popBackStack()
                },
                onResetPasswordSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 5. Registration Screen
        composable(
            route = Screen.Registration.route,
            arguments = listOf(
                navArgument("role") {
                    type = NavType.StringType
                    defaultValue = "driver"
                }
            )
        ) { backStackEntry ->
            val roleArg = backStackEntry.arguments?.getString("role") ?: "driver"
            val initialRole = if (roleArg == "client") RegistrationRole.CLIENT else RegistrationRole.DRIVER

            RegistrationScreen(
                initialRole = initialRole,
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                },
                onRegistrationSubmitted = { submittedRole ->
                    val roleParam = if (submittedRole == RegistrationRole.CLIENT) "client" else "driver"
                    navController.navigate(Screen.VerificationPending.createRoute(roleParam)) {
                        popUpTo(Screen.Registration.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // 6. Verification Pending Screen
        composable(
            route = Screen.VerificationPending.route,
            arguments = listOf(
                navArgument("role") {
                    type = NavType.StringType
                    defaultValue = "driver"
                }
            )
        ) { backStackEntry ->
            val roleArg = backStackEntry.arguments?.getString("role") ?: "driver"
            val role = if (roleArg == "client") RegistrationRole.CLIENT else RegistrationRole.DRIVER

            VerificationPendingScreen(
                role = role,
                onNavigateToProfile = {
                    val roleParam = if (role == RegistrationRole.CLIENT) "client" else "driver"
                    navController.navigate(Screen.Profile.createRoute(roleParam))
                },
                onResetRegistration = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        // 7. Profile Screen
        composable(
            route = Screen.Profile.route,
            arguments = listOf(
                navArgument("role") {
                    type = NavType.StringType
                    defaultValue = "driver"
                }
            )
        ) { backStackEntry ->
            val roleArg = backStackEntry.arguments?.getString("role") ?: "driver"
            val role = if (roleArg == "client") RegistrationRole.CLIENT else RegistrationRole.DRIVER

            ProfileScreen(
                role = role,
                onBackToPending = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}
