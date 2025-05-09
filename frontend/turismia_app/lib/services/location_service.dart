import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class LocationService {
  static Future<LatLng> getCurrentLocation() async {
    bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      throw Exception('El servicio de ubicación no está habilitado.');
    }

    LocationPermission permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        throw Exception('Permiso de ubicación denegado.');
      }
    }

    if (permission == LocationPermission.deniedForever) {
      throw Exception('Permiso de ubicación denegado permanentemente.');
    }

    Position position = await Geolocator.getCurrentPosition();
    return LatLng(position.latitude, position.longitude);
  }

  static Future<LatLng> getLocationByName(String name) async {
    const storage = FlutterSecureStorage();
    final jwt = await storage.read(key: 'jwt');

    final dio = Dio();
    final response = await dio.get(
      'http://192.168.1.136:8080/api/routes/location/by-name',
      queryParameters: {'name': name},
      options: Options(
        headers: {
          'Authorization': 'Bearer $jwt',
          'Content-Type': 'application/json',
        },
      ),
    );

    if (response.statusCode == 200) {
      final body = response.data['body'];
      return LatLng(body['latitude'], body['longitude']);
    } else {
      throw Exception('No se pudo obtener la ubicación por nombre.');
    }
  }
}
