import 'package:google_maps_flutter/google_maps_flutter.dart';

class RouteModel {
  final int id;
  final String name;
  final int ownerId;
  final int duration;
  final double rating;
  final List<int> spotIds;
  final List<LatLng> polyline;

  RouteModel({
    required this.id,
    required this.name,
    required this.ownerId,
    required this.duration,
    required this.rating,
    required this.spotIds,
    required this.polyline,

  });

  factory RouteModel.fromJson(Map<String, dynamic> json) {
    return RouteModel(
      id: json['id'],
      name: json['name'],
      ownerId: json['ownerId'] ?? json['owner'],
      duration: json['duration'],
      rating: (json['rating'] as num).toDouble(),
      spotIds: (json['spotIds'] ?? json['spots'] ?? []).cast<int>(),
      polyline: (json['polyline'] as List<dynamic>? ?? [])
          .map((p) => LatLng(p['latitude'], p['longitude']))
          .toList(),
    );
  }

}
