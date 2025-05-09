import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../models/route_detail_model.dart';
import '../models/route_model.dart';

class RouteService {
  final Dio _dio = Dio(
    BaseOptions(
      baseUrl: 'http://192.168.1.136:8080/api',
    ),
  );

  final _storage = const FlutterSecureStorage();

  Future<String?> _getToken() => _storage.read(key: 'jwt');

  Future<List<dynamic>> getPublicRoutes() async {
    final token = await _getToken();
    if (token == null) throw Exception('No hay token guardado.');

    final response = await _dio.get(
      '/routes/',
      options: Options(headers: {'Authorization': 'Bearer $token'}),
    );

    return response.data['body'] as List<dynamic>;
  }

  Future<int> createRouteDraft({
    required String name,
    required String description,
    required int cityId,
    required int ownerId,
    required List<int> spotIds,
  }) async {
    final token = await _getToken();
    if (token == null) throw Exception('No se encontró el token JWT.');

    final payload = {
      "name": name,
      "description": description,
      "cityId": cityId,
      "ownerId": ownerId,
      "spotIds": spotIds,
    };

    print('📤 Payload enviado al backend:\n$payload');

    final response = await _dio.post(
      "/routes/",
      data: payload,
      options: Options(
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
      ),
    );

    return response.data['body']['id'];
  }

  Future<void> publishRoute(int routeId) async {
    final token = await _getToken();
    if (token == null) throw Exception('No hay token');

    await _dio.put(
      '/routes/$routeId/publish',
      options: Options(headers: {'Authorization': 'Bearer $token'}),
    );
  }

  Future<void> createRoute({
    required String name,
    required String description,
    required List<int> spotIds,
    required int cityId,
    required int ownerId,
    required int duration,
  }) async {
    final token = await _getToken();
    if (token == null) throw Exception('No hay token guardado.');

    final response = await _dio.post(
      '/routes/',
      data: {
        'name': name,
        'description': description,
        'spotIds': spotIds,
        'cityId': cityId,
        'ownerId': ownerId,
        'duration': duration,
      },
      options: Options(headers: {'Authorization': 'Bearer $token'}),
    );

    if (response.statusCode != 200 && response.statusCode != 201) {
      throw Exception('Error al crear la ruta');
    }
  }

  Future<RouteModel> getRouteById(int id) async {
    final token = await _getToken();
    if (token == null) throw Exception('No hay token guardado.');

    final response = await _dio.get(
      '/routes/$id',
      options: Options(headers: {'Authorization': 'Bearer $token'}),
    );

    return RouteModel.fromJson(response.data['body']);
  }

  Future<RouteDetailModel> getRouteDetail(int id) async {
    final token = await _getToken();
    if (token == null) throw Exception('No hay token guardado.');

    final response = await _dio.get(
      '/routes/$id',
      options: Options(headers: {'Authorization': 'Bearer $token'}),
    );

    return RouteDetailModel.fromJson(response.data['body']);
  }


  Future<int> calculateDuration(List<int> spotIds) async {
    try {
      final response = await _dio.post('/calculate-duration', data: {
        'spotIds': spotIds,
      });

      if (response.statusCode == 200 && response.data['body'] != null) {
        return response.data['body']['totalDuration'];
      } else {
        throw Exception('Respuesta inválida de la API');
      }
    } catch (e) {
      throw Exception('Error al calcular duración: $e');
    }
  }
}
