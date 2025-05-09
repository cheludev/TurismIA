import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import '../models/generated_spots.dart';
import '../models/generated_route.dart' as model;
import '../services/route_generator_service.dart';

import '../models/generated_spots.dart' as model;

class GeneratedRouteMapScreen extends StatelessWidget {
  final model.GeneratedRoute route;

  const GeneratedRouteMapScreen({super.key, required this.route});

  @override
  Widget build(BuildContext context) {
    final markers = route.spots.map((spot) => Marker(
      markerId: MarkerId(spot.name),
      position: LatLng(spot.latitude, spot.longitude),
      infoWindow: InfoWindow(title: spot.name),
    )).toSet();

    final polyline = Polyline(
      polylineId: const PolylineId('generated_route'),
      color: Colors.blue,
      width: 4,
      points: route.spots.map((s) => LatLng(s.latitude, s.longitude)).toList(),
    );

    return Scaffold(
      appBar: AppBar(title: const Text('Ruta generada')),
      body: GoogleMap(
        initialCameraPosition: CameraPosition(
          target: LatLng(route.spots.first.latitude, route.spots.first.longitude),
          zoom: 12,
        ),
        markers: markers,
        polylines: {polyline},
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          // Aquí pondremos la lógica de guardar la ruta en el futuro.
        },
        child: const Icon(Icons.save),
      ),
    );
  }
}
