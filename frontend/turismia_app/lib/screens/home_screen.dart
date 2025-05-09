import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import '../providers/route_provider.dart';
import '../services/route_generator_service.dart';
import '../services/user_service.dart';
import '../models/user_model.dart';
//import '../widgets/create_route_dialog.dart';
import '../widgets/turismia_fab_menu.dart';
import '../widgets/create_route_dialog.dart';
import 'generated_route_map_screen.dart';
import '../widgets/turismia_fab_menu.dart';
import '../widgets/create_route_dialog.dart';


class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  bool _isMenuOpen = false;

  Future<void> _logout(BuildContext context) async {
    const storage = FlutterSecureStorage();
    await storage.delete(key: 'jwt');
    context.go('/login');
  }

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<RouteProvider>(context, listen: false).fetchRoutes();
    });
  }

  @override
  Widget build(BuildContext context) {
    final routeProvider = Provider.of<RouteProvider>(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Turismia'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () => _logout(context),
          ),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Bienvenido/a',
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            const Text(
              'Rutas públicas:',
              style: TextStyle(
                fontSize: 18,
              ),
            ),
            const SizedBox(height: 16),
            Expanded(
              child: _buildRouteList(routeProvider),
            ),
          ],
        ),
      ),
      floatingActionButton: TurismiaFabMenu(
        onCreateRoute: () async {
          final shouldRefresh = await showDialog<bool>(
            context: context,
            builder: (_) => const CreateRouteDialog(),
          );

          if (shouldRefresh == true && context.mounted) {
            Provider.of<RouteProvider>(context, listen: false).fetchRoutes();
          }
        },
      ),

    );
  }

  Widget _buildRouteList(RouteProvider provider) {
    if (provider.isLoading) {
      return const Center(child: CircularProgressIndicator());
    } else if (provider.error != null) {
      return Center(child: Text('Error: ${provider.error}'));
    } else if (provider.routes.isEmpty) {
      return const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.refresh, size: 72, color: Colors.grey),
            SizedBox(height: 16),
            Text(
              'Aún no existe ninguna ruta en la base de datos',
              style: TextStyle(color: Colors.grey),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      );
    } else {
      return ListView.builder(
        itemCount: provider.routes.length,
        itemBuilder: (context, index) {
          final route = provider.routes[index];
          return _buildRouteCard(route);
        },
      );
    }
  }

  Widget _buildRouteCard(route) {
    return Card(
      elevation: 3,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
      ),
      child: ListTile(
        leading: FutureBuilder<UserModel>(
          future: UserService().getUserById(route.ownerId),
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting) {
              return const CircleAvatar(
                child: CircularProgressIndicator(strokeWidth: 2),
              );
            } else if (snapshot.hasError) {
              return const CircleAvatar(
                child: Icon(Icons.error),
              );
            } else {
              final user = snapshot.data!;
              return CircleAvatar(
                backgroundImage: user.photo != null
                    ? NetworkImage(user.photo!) // cuando haya fotos reales
                    : null,
                child: user.photo == null
                    ? const Icon(Icons.person)
                    : null,
              );
            }
          },
        ),
        title: Text(route.name),
        subtitle: FutureBuilder<UserModel>(
          future: UserService().getUserById(route.ownerId),
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting) {
              return const Text('Cargando creador...');
            } else if (snapshot.hasError) {
              return const Text('Error al cargar usuario');
            } else {
              final user = snapshot.data!;
              return Text(
                  'Creado por: ${user.username} • Ciudad: (pendiente de añadir)');
            }
          },
        ),
        trailing: const Icon(Icons.arrow_forward_ios),
        onTap: () {
          context.go('/routeDetail', extra: route.id);
        },
      ),
    );
  }

}
