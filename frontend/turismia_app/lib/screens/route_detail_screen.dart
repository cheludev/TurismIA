import 'package:flutter/material.dart';
import '../models/route_model.dart';
import '../services/route_generator_service.dart';
import '../services/route_service.dart';
import '../services/user_service.dart';
import '../models/user_model.dart';
import '../services/spot_service.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import '../widgets/create_route_dialog.dart';
import '../widgets/turismia_fab_menu.dart';
import 'package:go_router/go_router.dart';

import 'generated_route_map_screen.dart';
class RouteDetailScreen extends StatelessWidget {
  final int routeId;

  const RouteDetailScreen({super.key, required this.routeId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Detalle de Ruta'),
      ),
      body: FutureBuilder<RouteModel>(
        future: RouteService().getRouteById(routeId),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else {
            final route = snapshot.data!;
            return _buildDetailContent(context, route);
          }
        },
      ),
      floatingActionButton: TurismiaFabMenu(
        onCreateRoute: () {
          showDialog(
            context: context,
            builder: (_) => const CreateRouteDialog(),
          );
        },
      ),
    );
  }

  Widget _buildDetailContent(BuildContext context, RouteModel route) {
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              route.name,
              style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 16),
            _buildUserInfo(route),
            const SizedBox(height: 16),
            Text('Duración: ${route.duration ~/ 60} minutos'),
            const SizedBox(height: 8),
            Text('Valoración: ${route.rating.toStringAsFixed(1)}'),
            const SizedBox(height: 8),
            const Text('Ciudad: (por añadir en backend)'),
            const SizedBox(height: 16),
            const Divider(),
            const Text(
              'Spots incluidos:',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            _buildSpotsList(route),
            const SizedBox(height: 16),
            const Divider(),
            const Text(
              'Mapa de la ruta:',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            SizedBox(
              height: 300,
              child: _buildGoogleMap(route),
            ),
            const SizedBox(height: 16),
            TextButton.icon(
              onPressed: () {
                context.go('/home');
              },
              icon: const Icon(Icons.arrow_back),
              label: const Text('Volver'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildUserInfo(RouteModel route) {
    return FutureBuilder<UserModel>(
      future: UserService().getUserById(route.ownerId),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const CircularProgressIndicator();
        } else if (snapshot.hasError) {
          return const Text('Error al cargar creador');
        } else {
          final user = snapshot.data!;
          return Row(
            children: [
              CircleAvatar(
                backgroundImage: user.photo != null
                    ? NetworkImage(user.photo!)
                    : null,
                child: user.photo == null
                    ? const Icon(Icons.person)
                    : null,
              ),
              const SizedBox(width: 10),
              Text('Creado por: ${user.username}'),
            ],
          );
        }
      },
    );
  }

  Widget _buildSpotsList(RouteModel route) {
    if (route.spotIds.isEmpty) {
      return const Text('Esta ruta no tiene spots.');
    }

    List<Widget> spotWidgets = [];

    for (int i = 0; i < route.spotIds.length; i++) {
      final spotId = route.spotIds[i];

      spotWidgets.add(
        FutureBuilder<Map<String, dynamic>>(
          future: SpotService().getSpotById(spotId),
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting) {
              return const ListTile(
                leading: CircularProgressIndicator(),
                title: Text('Cargando spot...'),
              );
            } else if (snapshot.hasError) {
              return const ListTile(
                leading: Icon(Icons.error),
                title: Text('Error al cargar spot'),
              );
            } else {
              final spot = snapshot.data!;
              return ListTile(
                leading: const Icon(Icons.place),
                title: Text(spot['name']),
                subtitle: Text(spot['address'] ?? 'Sin dirección'),
              );
            }
          },
        ),
      );

      // Añadir flecha solo si no es el último
      if (i < route.spotIds.length - 1) {
        spotWidgets.add(const ListTile(
          leading: Icon(Icons.arrow_downward, color: Colors.grey),
          title: SizedBox(),
        ));
      }
    }

    return Column(children: spotWidgets);
  }


  Widget _buildGoogleMap(RouteModel route) {
    return FutureBuilder<Map<String, dynamic>>(
      future: _loadMarkersAndPolyline(route),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        } else if (snapshot.hasError) {
          return const Center(child: Text('Error al cargar el mapa'));
        } else {
          final data = snapshot.data!;
          final markers = data['markers'] as Set<Marker>;
          final polyline = data['polyline'] as Polyline;

          return GoogleMap(
            initialCameraPosition: CameraPosition(
              target: markers.isNotEmpty
                  ? markers.first.position
                  : const LatLng(37.2614, -6.9447), // Huelva centro
              zoom: 14,
            ),
            markers: markers,
            polylines: {polyline},
          );
        }
      },
    );
  }

  Future<Map<String, dynamic>> _loadMarkersAndPolyline(RouteModel route) async {
    Set<Marker> markers = {};
    List<LatLng> polylinePoints = [];

    for (int spotId in route.spotIds) {
      final spot = await SpotService().getSpotById(spotId);
      final lat = spot['latitude'];
      final lng = spot['longitude'];

      if (lat != null && lng != null) {
        final position = LatLng(lat, lng);
        markers.add(Marker(
          markerId: MarkerId('spot_$spotId'),
          position: position,
          infoWindow: InfoWindow(title: spot['name']),
        ));
      }
    }

    // Ahora sí: usar polyline de la ruta
    polylinePoints = route.polyline;

    final polyline = Polyline(
      polylineId: const PolylineId('route_polyline'),
      color: Colors.blue,
      width: 4,
      points: polylinePoints,
    );

    print('DEBUG markers: ${markers.length}');
    print('DEBUG polyline points: ${polylinePoints.length}');

    return {
      'markers': markers,
      'polyline': polyline,
    };
  }
}
