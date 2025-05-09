import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

import '../models/generated_spots.dart' as service;
import '../models/generated_route.dart' as service;
import '../models/spot_model.dart';
import '../services/route_service.dart';
import '../services/spot_service.dart';
import '../providers/auth_provider.dart';
import '../widgets/searchable_spot_dialog.dart';

class RoutePreviewScreen extends StatefulWidget {
  final service.GeneratedRoute route;

  const RoutePreviewScreen({super.key, required this.route});

  @override
  State<RoutePreviewScreen> createState() => _RoutePreviewScreenState();
}

class _RoutePreviewScreenState extends State<RoutePreviewScreen> {
  final TextEditingController _titleController = TextEditingController();
  late List<service.GeneratedSpot> _spots;
  int _totalDuration = 0;

  @override
  void initState() {
    super.initState();
    _spots = widget.route.spots;
    _totalDuration = widget.route.totalDuration;
  }


  void _removeSpot(int index) {
    setState(() {
      _spots.removeAt(index);
    });
    _updateDuration();
  }

  Future<void> _replaceSpot(int index) async {
    final selectedSpot = await showDialog<Spot>(
      context: context,
      builder: (_) => const SearchableSpotDialog(),
    );

    if (selectedSpot != null) {
      setState(() {
        _spots[index] = service.GeneratedSpot.fromSpotModel(selectedSpot);
      });
      _updateDuration();
    }
  }


  Future<void> _updateDuration() async {
    try {
      final spotIds = _spots.where((s) => s.id != null).map((s) => s.id!).toList();
      final newDuration = await RouteService().calculateDuration(spotIds);
      setState(() {
        _totalDuration = newDuration;
      });
    } catch (e) {
      print('❌ Error al recalcular duración: $e');
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Error al recalcular duración')),
      );
    }
  }



  void _saveRoute() async {
    final user = Provider.of<AuthProvider>(context, listen: false).user;
    if (user == null) return;

    final spotIds = _spots
        .where((s) => s.id != null)
        .map((s) => s.id!)
        .toList();

    final payload = {
      "name": _titleController.text.trim().isEmpty ? "Ruta sin título" : _titleController.text.trim(),
      "cityId": 1,
      "ownerId": user.id,
      "spotIds": spotIds,
      "description": "Descripción generada automáticamente",
      "duration": widget.route.totalDuration
    };

    print('📤 Payload enviado al backend:\n$payload');

    try {
      await RouteService().createRoute(
        name: payload["name"] as String,
        description: payload["description"] as String,
        spotIds: List<int>.from(payload["spotIds"] as List),
        cityId: payload["cityId"] as int,
        ownerId: payload["ownerId"] as int,
        duration: payload["duration"] as int,
      );

      if (context.mounted) {
        Navigator.pop(context, true);
      }
    } catch (e) {
      print('❌ Error al guardar la ruta: $e');
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Error al guardar la ruta')),
        );
      }
    }
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Previsualización de Ruta')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            TextFormField(
              controller: _titleController,
              decoration: const InputDecoration(labelText: 'Título de la ruta'),
            ),
            const SizedBox(height: 12),
            Text('Duración estimada: ${_totalDuration ~/ 60} minutos'),
            const Divider(),
            const Text('Spots incluidos:', style: TextStyle(fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            Expanded(
              child: ListView.builder(
                itemCount: _spots.length,
                itemBuilder: (context, index) {
                  final spot = _spots[index];
                  return Card(
                    margin: const EdgeInsets.symmetric(vertical: 6),
                    child: ListTile(
                      title: Text(spot.name),
                      subtitle: Text('Lat: ${spot.latitude}, Lng: ${spot.longitude}'),
                      trailing: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          IconButton(
                            icon: const Icon(Icons.edit),
                            onPressed: () => _replaceSpot(index),
                          ),
                          IconButton(
                            icon: const Icon(Icons.delete),
                            onPressed: () => _removeSpot(index),
                          ),
                        ],
                      ),
                    ),
                  );
                },
              ),
            ),
            const SizedBox(height: 12),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                OutlinedButton(
                  onPressed: () => Navigator.pop(context),
                  child: const Text('Cancelar'),
                ),
                ElevatedButton(
                  onPressed: _saveRoute,
                  child: const Text('Guardar Ruta'),
                ),
              ],
            )
          ],
        ),
      ),
    );
  }
}
