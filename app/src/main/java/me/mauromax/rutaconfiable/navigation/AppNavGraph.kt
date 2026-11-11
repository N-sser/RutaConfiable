package me.mauromax.rutaconfiable.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
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

    // Estas pantallas reciben el rol en la ruta: "profile/driver" o "profile/client"
    object Registration : Screen("registration/{role}")
    object VerificationPending : Screen("verification_pending/{role}")
    object Profile : Screen("profile/{role}")
    object Trips : Screen("trips/{role}")
    object Loads : Screen("loads/{role}")

    /** Ruta con el rol ya puesto, por ejemplo "profile/driver". */
    fun withRole(role: RegistrationRole) =
        route.replace("{role}", if (role == RegistrationRole.CLIENT) "client" else "driver")
}

/** Declara una pantalla cuya ruta lleva el rol; `content` recibe el rol ya convertido. */
private fun NavGraphBuilder.roleComposable(screen: Screen, content: @Composable (RegistrationRole) -> Unit) {
    composable(
        route = screen.route,
        arguments = listOf(navArgument("role") {
            type = NavType.StringType
            defaultValue = "driver"
        })
    ) { entry ->
        val isClient = entry.arguments?.getString("role") == "client"
        content(if (isClient) RegistrationRole.CLIENT else RegistrationRole.DRIVER)
    }
}

/** Navegación entre pestañas de la barra inferior sin apilar pantallas repetidas. */
private fun NavHostController.goTab(screen: Screen, role: RegistrationRole) {
    navigate(screen.withRole(role)) {
        popUpTo(Screen.Profile.route) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    // Vuelve a la pantalla de bienvenida y borra todo el historial
    fun backToWelcome() = navController.navigate(Screen.Welcome.route) { popUpTo(0) }

    NavHost(navController = navController, startDestination = Screen.Welcome.route) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToRegister = {
                    navController.navigate(Screen.Registration.withRole(RegistrationRole.DRIVER))
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onLoginSuccess = { role ->
                    navController.navigate(Screen.Profile.withRole(role)) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onForgotPasswordClick = { navController.navigate(Screen.ForgotPasswordStep1.route) },
                onRegisterClick = {
                    navController.navigate(Screen.Registration.withRole(RegistrationRole.DRIVER))
                }
            )
        }

        composable(Screen.ForgotPasswordStep1.route) {
            ForgotPasswordStep1Screen(
                onBackClick = { navController.popBackStack() },
                onSendCodeSuccess = { navController.navigate(Screen.ForgotPasswordStep2.route) }
            )
        }

        composable(Screen.ForgotPasswordStep2.route) {
            ForgotPasswordStep2Screen(
                onBackClick = { navController.popBackStack() },
                onResetPasswordSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        roleComposable(Screen.Registration) { role ->
            RegistrationScreen(
                initialRole = role,
                onBackClick = { navController.popBackStack() },
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onAddPaymentClick = { navController.navigate(Screen.PaymentMethods.route) },
                onRegistrationSubmitted = { submittedRole ->
                    navController.navigate(Screen.VerificationPending.withRole(submittedRole)) {
                        popUpTo(Screen.Registration.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PaymentMethods.route) {
            PaymentMethodsScreen(onBackClick = { navController.popBackStack() })
        }

        roleComposable(Screen.VerificationPending) { role ->
            VerificationPendingScreen(
                role = role,
                onNavigateToProfile = { navController.navigate(Screen.Profile.withRole(role)) },
                onResetRegistration = { backToWelcome() }
            )
        }

        roleComposable(Screen.Profile) { role ->
            ProfileScreen(
                role = role,
                onLogout = { backToWelcome() },
                onNavigateToTrips = { navController.goTab(Screen.Trips, role) },
                onNavigateToLoads = { navController.goTab(Screen.Loads, role) }
            )
        }

        roleComposable(Screen.Trips) { role ->
            TripScreen(
                role = role,
                onNavigateToLoads = { navController.goTab(Screen.Loads, role) },
                onNavigateToProfile = { navController.goTab(Screen.Profile, role) }
            )
        }

        roleComposable(Screen.Loads) { role ->
            LoadsScreen(
                role = role,
                onPublished = { navController.goTab(Screen.Trips, role) },
                onNavigateToTrips = { navController.goTab(Screen.Trips, role) },
                onNavigateToProfile = { navController.goTab(Screen.Profile, role) }
            )
        }
    }
}
