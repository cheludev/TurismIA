import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class PlaceAutocompleteField extends StatefulWidget {
  final Function(String) onPlaceSelected;

  const PlaceAutocompleteField({super.key, required this.onPlaceSelected});

  @override
  _PlaceAutocompleteFieldState createState() => _PlaceAutocompleteFieldState();
}

class _PlaceAutocompleteFieldState extends State<PlaceAutocompleteField> {
  final TextEditingController _controller = TextEditingController();
  List<dynamic> _predictions = [];

  final String apiKey = 'AIzaSyDWB8Fm91HCndVfyvTO63p2vK9yMgrOsBY';

  void _onChanged(String value) async {
    if (value.isEmpty) {
      setState(() {
        _predictions = [];
      });
      return;
    }

    final url = Uri.parse(
        'https://maps.googleapis.com/maps/api/place/autocomplete/json?input=$value&key=$apiKey&language=es');

    final response = await http.get(url);

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      setState(() {
        _predictions = data['predictions'];
      });
    } else {
      print('Error en la solicitud: ${response.statusCode}');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        TextField(
          controller: _controller,
          decoration: const InputDecoration(
            labelText: 'Buscar lugar',
          ),
          onChanged: _onChanged,
        ),
        const SizedBox(height: 10),
        // CAMBIO IMPORTANTE: aquí limitamos la altura del ListView
        SizedBox(
          height: 200,
          child: _predictions.isNotEmpty
              ? ListView.builder(
            itemCount: _predictions.length,
            itemBuilder: (context, index) {
              final prediction = _predictions[index];
              return ListTile(
                title: Text(prediction['description']),
                onTap: () {
                  widget.onPlaceSelected(prediction['description']);
                },
              );
            },
          )
              : const Center(child: Text('Sin resultados')),
        ),
      ],
    );
  }
}
