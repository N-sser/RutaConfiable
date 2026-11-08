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

    object PaymentMethods : Screen("payment_methods")

    object Registration : Screen("registration/{role}") {
        fun createRoute(role: String = "driver") = "registration/$role"
    }

    object VerificationPending : Screen("verification_pending/{role}") {
        fun createRoute(role: String = "driver") = "verification_pending/$role"
    }

    object Profile : Screen("profile/{role}") {
        fun createRoute(role: String = "driver") = "profile/$role"
    }

    object Trips : Screen("trips/{role}") {
        fun createRoute(role: String = "driver") = "trips/$role"
    }

    object Loads : Screen("loads/{role}") {
        fun createRoute(role: String = "driver") = "loads/$role"
    }
}

private fun roleParam(role: RegistrationRole) =
    if (role == RegistrationRole.CLIENT) "client" else "driver"

private fun parseRole(arg: String?) =
    if (arg == "client") RegistrationRole.CLIENT else RegistrationRole.DRIVER

/** Navegación entre pestañas de la barra inferior sin apilar pantallas repetidas. */
private fun NavHostController.goTab(route: String) {
    navigate(route) {
        popUpTo(Screen.Profile.route) { saveState = true }
        launchSingleTop = true
        restoreState = true
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
            val initialRole = parseRole(backStackEntry.arguments?.getString("role"))

            RegistrationScreen(
                initialRole = initialRole,
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                },
                onAddPaymentClick = {
                    navController.navigate(Screen.PaymentMethods.route)
                },
                onRegistrationSubmitted = { submittedRole ->
                    navController.navigate(Screen.VerificationPending.createRoute(roleParam(submittedRole))) {
                        popUpTo(Screen.Registration.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // Payment Methods Screen (desde el registro de cliente)
        composable(route = Screen.PaymentMethods.route) {
            PaymentMethodsScreen(onBackClick = { navController.popBackStack() })
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
            val role = parseRole(backStackEntry.arguments?.getString("role"))

            VerificationPendingScreen(
                role = role,
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.createRoute(roleParam(role)))
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
            val role = parseRole(backStackEntry.arguments?.getString("role"))

            ProfileScreen(
                role = role,
                onLogout = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0)
                    }
                },
                onNavigateToTrips = {
                    navController.goTab(Screen.Trips.createRoute(roleParam(role)))
                },
                onNavigateToLoads = {
                    navController.goTab(Screen.Loads.createRoute(roleParam(role)))
                }
            )
        }

        // 8. Trips Screen
        composable(
            route = Screen.Trips.route,
            arguments = listOf(
                navArgument("role") {
                    type = NavType.StringType
                    defaultValue = "driver"
                }
            )
        ) { backStackEntry ->
            val role = parseRole(backStackEntry.arguments?.getString("role"))

            TripScreen(
                role = role,
                onNavigateToLoads = {
                    navController.goTab(Screen.Loads.createRoute(roleParam(role)))
                },
                onNavigateToProfile = {
                    navController.goTab(Screen.Profile.createRoute(roleParam(role)))
                }
            )
        }

        // 9. Loads Screen (publicar viaje)
        composable(
            route = Screen.Loads.route,
            arguments = listOf(
                navArgument("role") {
                    type = NavType.StringType
                    defaultValue = "driver"
                }
            )
        ) { backStackEntry ->
            val role = parseRole(backStackEntry.arguments?.getString("role"))

            LoadsScreen(
                role = role,
                onPublished = {
                    navController.goTab(Screen.Trips.createRoute(roleParam(role)))
                },
                onNavigateToTrips = {
                    navController.goTab(Screen.Trips.createRoute(roleParam(role)))
                },
                onNavigateToProfile = {
                    navController.goTab(Screen.Profile.createRoute(roleParam(role)))
                }
            )
        }
    }
}