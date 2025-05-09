import 'package:dio/dio.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

import '../models/generated_spots.dart';
import '../models/generated_route.dart';

class RouteGeneratorService {
  final Dio _dio = Dio(
    BaseOptions(baseUrl: 'http://192.168.1.136:8080/api'),
  );

  Future<GeneratedRoute> generateRoute({
    required LatLng from,
    required double destinationLat,
    required double destinationLng,
    required int maxDuration,
  }) async {
    const storage = FlutterSecureStorage();
    final token = await storage.read(key: 'jwt');

    if (token == null) {
      throw Exception(
          'No se encontró el token JWT. El usuario no está autenticado.');
    }

    _dio.options.headers['Authorization'] = 'Bearer $token';

    final payload = {
      'from': {
        'latitude': from.latitude.toDouble(),
        'longitude': from.longitude.toDouble(),
      },
      'to': {
        'latitude': destinationLat.toDouble(),
        'longitude': destinationLng.toDouble(),
      },
      'maxDuration': maxDuration.toInt() * 60,
    };

    print('📤 Payload enviado: $payload');

    final response = await _dio.post('/routes/new', data: payload);

    print('📥 RAW RESPONSE: ${response.data}');

    final body = response.data['body'];
    final spots = body['spots'] as List;
    final totalDuration = body['totalDuration'];

    print('✅ Parsed duration: $totalDuration');
    print('✅ Parsed spots count: ${spots.length}');

    return GeneratedRoute.fromJson(body);
  }
}

