package me.mauromax.rutaconfiable.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import me.mauromax.rutaconfiable.ui.data.AuthRepository
import me.mauromax.rutaconfiable.ui.data.ProfileData
import me.mauromax.rutaconfiable.ui.data.ProfileRepository
import me.mauromax.rutaconfiable.ui.screens.*

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object ForgotPasswordStep1 : Screen("forgot_password_step_1")
    object ForgotPasswordStep2 : Screen("forgot_password_step_2/{email}") {
        fun createRoute(email: String) = "forgot_password_step_2/${Uri.encode(email)}"
    }

    object PaymentMethods : Screen("payment_methods")

    object Registration : Screen("registration/{role}") {
        fun createRoute(role: String = "driver") = "registration/$role"
    }

    object VerificationPending : Screen("verification_pending/{role}") {
        fun createRoute(role: String = "driver") =
            "verification_pending/$role"
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
    if (arg == "client") RegistrationRole.CLIENT
    else RegistrationRole.DRIVER

private fun NavHostController.goTab(route: String) {
    navigate(route) {
        popUpTo(Screen.Profile.route) {
            saveState = true
        }
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

        composable(route = Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToRegister = {
                    navController.navigate(
                        Screen.Registration.createRoute("driver")
                    )
                },
                onGoogleSignedIn = { role, verified ->
                    // Los conductores entran siempre; los clientes esperan su verificación.
                    val destination = if (verified || role == RegistrationRole.DRIVER) {
                        Screen.Profile.createRoute(roleParam(role))
                    } else {
                        Screen.VerificationPending.createRoute(roleParam(role))
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Welcome.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(route = Screen.Login.route) {
            LoginScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginSuccess = { selectedRole, verified ->
                    val routeRole =
                        if (selectedRole == RegistrationRole.CLIENT) {
                            "client"
                        } else {
                            "driver"
                        }

                    // Los conductores entran siempre; un cliente sin aprobar queda en la
                    // pantalla de verificación.
                    val destination = if (verified || selectedRole == RegistrationRole.DRIVER) {
                        Screen.Profile.createRoute(routeRole)
                    } else {
                        Screen.VerificationPending.createRoute(routeRole)
                    }

                    navController.navigate(destination) {
                        popUpTo(Screen.Welcome.route) {
                            inclusive = true
                        }
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate(
                        Screen.ForgotPasswordStep1.route
                    )
                },
                onRegisterClick = { selectedRole ->
                    val routeRole =
                        if (selectedRole == RegistrationRole.CLIENT) {
                            "client"
                        } else {
                            "driver"
                        }

                    navController.navigate(
                        Screen.Registration.createRoute(routeRole)
                    )
                }
            )
        }

        composable(route = Screen.ForgotPasswordStep1.route) {
            ForgotPasswordStep1Screen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLinkSent = { email ->
                    navController.navigate(
                        Screen.ForgotPasswordStep2.createRoute(email)
                    )
                }
            )
        }

        composable(
            route = Screen.ForgotPasswordStep2.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            ForgotPasswordStep2Screen(
                email = backStackEntry.arguments?.getString("email").orEmpty(),
                onBackClick = {
                    navController.popBackStack()
                },
                onBackToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.Registration.route,
            arguments = listOf(
                navArgument("role") {
                    type = NavType.StringType
                    defaultValue = "driver"
                }
            )
        ) { backStackEntry ->

            val initialRole = parseRole(
                backStackEntry.arguments?.getString("role")
            )

            RegistrationScreen(
                initialRole = initialRole,
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                },
                onAddPaymentClick = {
                    navController.navigate(
                        Screen.PaymentMethods.route
                    )
                },
                onRegistrationSubmitted = { submittedRole ->
                    // Conductor: entra directo al perfil. Cliente: espera su verificación.
                    val destination = if (submittedRole == RegistrationRole.DRIVER) {
                        Screen.Profile.createRoute(roleParam(submittedRole))
                    } else {
                        Screen.VerificationPending.createRoute(roleParam(submittedRole))
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Registration.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(route = Screen.PaymentMethods.route) {
            PaymentMethodsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.VerificationPending.route,
            arguments = listOf(
                navArgument("role") {
                    type = NavType.StringType
                    defaultValue = "driver"
                }
            )
        ) { backStackEntry ->

            val role = parseRole(
                backStackEntry.arguments?.getString("role")
            )

            VerificationPendingScreen(
                role = role,
                // Se llama solo cuando un administrador aprobó la cuenta.
                onNavigateToProfile = {
                    navController.navigate(
                        Screen.Profile.createRoute(roleParam(role))
                    ) {
                        popUpTo(Screen.VerificationPending.route) {
                            inclusive = true
                        }
                    }
                },
                onResetRegistration = {
                    AuthRepository.logout()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(
            route = Screen.Profile.route,
            arguments = listOf(
                navArgument("role") {
                    type = NavType.StringType
                    defaultValue = "driver"
                }
            )
        ) { backStackEntry ->

            val role = parseRole(
                backStackEntry.arguments?.getString("role")
            )

            val profile by produceState(initialValue = ProfileData(), role) {
                value = ProfileRepository.load(role)
            }

            ProfileScreen(
                role = role,
                userName = profile.name,
                stats = profile.stats,
                trips = profile.trips,
                nextTrip = profile.nextTrip,
                onLogout = {
                    AuthRepository.logout()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0)
                    }
                },
                onNavigateToTrips = {
                    navController.goTab(
                        Screen.Trips.createRoute(roleParam(role))
                    )
                },
                onNavigateToLoads = {
                    navController.goTab(
                        Screen.Loads.createRoute(roleParam(role))
                    )
                }
            )
        }

        composable(
            route = Screen.Trips.route,
            arguments = listOf(
                navArgument("role") {
                    type = NavType.StringType
                    defaultValue = "driver"
                }
            )
        ) { backStackEntry ->

            val role = parseRole(
                backStackEntry.arguments?.getString("role")
            )

            TripScreen(
                role = role,
                onNavigateToLoads = {
                    navController.goTab(
                        Screen.Loads.createRoute(roleParam(role))
                    )
                },
                onNavigateToProfile = {
                    navController.goTab(
                        Screen.Profile.createRoute(roleParam(role))
                    )
                }
            )
        }

        composable(
            route = Screen.Loads.route,
            arguments = listOf(
                navArgument("role") {
                    type = NavType.StringType
                    defaultValue = "driver"
                }
            )
        ) { backStackEntry ->

            val role = parseRole(
                backStackEntry.arguments?.getString("role")
            )

            LoadsScreen(
                role = role,
                onPublished = {
                    navController.goTab(
                        Screen.Trips.createRoute(roleParam(role))
                    )
                },
                onNavigateToTrips = {
                    navController.goTab(
                        Screen.Trips.createRoute(roleParam(role))
                    )
                },
                onNavigateToProfile = {
                    navController.goTab(
                        Screen.Profile.createRoute(roleParam(role))
                    )
                }
            )
        }
    }
}