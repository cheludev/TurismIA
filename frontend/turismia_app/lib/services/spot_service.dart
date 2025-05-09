import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as _client;
import 'package:http/http.dart' as storage;
import 'package:turismia_app/services/route_generator_service.dart';

import '../models/spot_model.dart';

class SpotService {
  final Dio _dio = Dio(
    BaseOptions(
      baseUrl: 'http://192.168.1.136:8080/api',
    ),
  );

  Future<Map<String, dynamic>> getSpotById(int id) async {
    const storage = FlutterSecureStorage();
    final token = await storage.read(key: 'jwt');

    if (token == null) {
      throw Exception('No hay token guardado.');
    }

    final response = await _dio.get(
      '/spots/$id',
      options: Options(
        headers: {'Authorization': 'Bearer $token'},
      ),
    );

    return response.data['body'];
  }

  Future<List<Spot>> getAllSpotsInCity(String cityName) async {
    const storage = FlutterSecureStorage();
    final token = await storage.read(key: 'jwt');

    if (token == null) {
      throw Exception('No hay token guardado.');
    }

    final response = await _dio.get(
      '/spots/allIn/$cityName',
      options: Options(
        headers: {'Authorization': 'Bearer $token'},
      ),
    );

    final List<dynamic> body = response.data['body'];
    return body.map((e) => Spot.fromJson(e)).toList();
  }

  Future<List<Spot>> getAllSpotsFromUrl(String url) async {
    const storage = FlutterSecureStorage();
    final token = await storage.read(key: 'jwt');

    final response = await Dio().get(
      url,
      options: Options(headers: {
        'Authorization': 'Bearer $token',
      }),
    );

    final data = response.data['body'] as List;
    return data.map((json) => Spot.fromJson(json)).toList();
  }


}
