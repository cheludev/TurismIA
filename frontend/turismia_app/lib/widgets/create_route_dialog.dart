import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:geolocator/geolocator.dart';
import 'package:go_router/go_router.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:google_maps_webservice/places.dart';
import 'package:provider/provider.dart';

import '../providers/route_provider.dart';
import '../services/route_generator_service.dart';

class CreateRouteDialog extends StatefulWidget {
  const CreateRouteDialog({Key? key}) : super(key: key);


  @override
  _CreateRouteDialogState createState() => _CreateRouteDialogState();
}

class _CreateRouteDialogState extends State<CreateRouteDialog> {
  final _formKey = GlobalKey<FormState>();
  final TextEditingController _destinationController = TextEditingController();
  int _durationMinutes = 60;
  String? _selectedPlaceId;

  final places = GoogleMapsPlaces(apiKey: dotenv.env['GOOGLE_MAPS_API_KEY'] ?? '');
  List<Prediction> predictions = [];

  void autoCompleteSearch(String input) async {
    if (input.isEmpty) {
      setState(() {
        predictions = [];
      });
      return;
    }

    final response = await places.autocomplete(
      input,
      language: "es",
      components: [Component(Component.country, "es")],
    );

    if (response.isOkay) {
      setState(() {
        predictions = response.predictions;
      });
    } else {
      setState(() {
        predictions = [];
      });
    }
  }

  Future<Position> _determinePosition() async {
    bool serviceEnabled;
    LocationPermission permission;

    serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      throw Exception('La ubicación no está habilitada.');
    }

    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        throw Exception('Permiso de ubicación denegado.');
      }
    }

    if (permission == LocationPermission.deniedForever) {
      throw Exception('Los permisos de ubicación están denegados permanentemente.');
    }

    return await Geolocator.getCurrentPosition();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Crear nueva ruta'),
      content: Form(
        key: _formKey,
        child: SizedBox(
          width: 300,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextFormField(
                controller: _destinationController,
                decoration: const InputDecoration(labelText: 'Destino'),
                validator: (value) =>
                value == null || value.isEmpty ? 'Introduce destino' : null,
                onChanged: autoCompleteSearch,
              ),
              const SizedBox(height: 8),
              if (predictions.isNotEmpty)
                ConstrainedBox(
                  constraints: const BoxConstraints(maxHeight: 200),
                  child: ListView.builder(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemCount: predictions.length,
                    itemBuilder: (context, index) {
                      return ListTile(
                        title: Text(predictions[index].description ?? ''),
                        onTap: () {
                          _destinationController.text =
                              predictions[index].description ?? '';
                          _selectedPlaceId = predictions[index].placeId;
                          setState(() {
                            predictions.clear();
                          });
                        },
                      );
                    },
                  ),
                ),
              const SizedBox(height: 16),
              Text('Duración máxima (minutos): $_durationMinutes'),
              Slider(
                value: _durationMinutes.toDouble(),
                min: 30,
                max: 300,
                divisions: 27,
                label: '$_durationMinutes min',
                onChanged: (value) {
                  setState(() {
                    _durationMinutes = value.round();
                  });
                },
              ),
            ],
          ),
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context, true),
          child: const Text('Cancelar'),
        ),
        ElevatedButton(
          onPressed: () async {
            if (_formKey.currentState!.validate()) {
              if (_selectedPlaceId == null) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Selecciona un destino de la lista')),
                );
                return;
              }

              try {
                Position position = await _determinePosition();
                LatLng from = LatLng(position.latitude, position.longitude);

                final placeDetails =
                await places.getDetailsByPlaceId(_selectedPlaceId!);
                final location = placeDetails.result.geometry?.location;

                if (location == null) {
                  throw Exception('No se pudieron obtener las coordenadas del destino.');
                }

                final route = await RouteGeneratorService().generateRoute(
                  from: from,
                  destinationLat: location.lat,
                  destinationLng: location.lng,
                  maxDuration: _durationMinutes,
                );

                if (context.mounted) {
                  final result = await context.push('/routePreview', extra: route);
                  if (context.mounted) Navigator.pop(context, result);
                }
              } catch (e) {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text(e.toString())),
                );
              }
            }
          },
          child: const Text('Generar ruta'),
        ),
      ],
    );
  }
}
