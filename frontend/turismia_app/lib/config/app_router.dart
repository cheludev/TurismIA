import 'package:go_router/go_router.dart';
import '../models/generated_route.dart';
import '../models/generated_route.dart' as service;
import '../models/route_model.dart';
import '../screens/route_detail_screen.dart';
import '../screens/home_screen.dart';
import '../screens/login_screen.dart';
import '../screens/register_screen.dart';
import '../screens/route_preview_screen.dart';

final GoRouter router = GoRouter(
  initialLocation: '/login',
  routes: [
    GoRoute(
      path: '/login',
      builder: (context, state) => const LoginScreen(),
    ),
    GoRoute(
      path: '/register',
      builder: (context, state) => const RegisterScreen(),
    ),
    GoRoute(
      path: '/home',
      builder: (context, state) => const HomeScreen(),
    ),
    GoRoute(
      path: '/routeDetail',
      builder: (context, state) {
        final routeId = state.extra as int;
        return RouteDetailScreen(routeId: routeId);
      },
    ),
    GoRoute(
      path: '/routePreview',
      builder: (context, state) {
        final route = state.extra as GeneratedRoute;
        return RoutePreviewScreen(route: route);
      },
    ),




  ],
);
