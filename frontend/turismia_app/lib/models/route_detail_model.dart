import 'package:google_maps_flutter/google_maps_flutter.dart';

class RouteDetailModel {
  final int id;
  final String name;
  final String description;
  final int cityId;
  final int duration;
  final double rating;
  final bool draft;
  final int ownerId;
  final List<int> spotIds;
  final List<LatLng> polyline;

  RouteDetailModel({
    required this.id,
    required this.name,
    required this.description,
    required this.cityId,
    required this.duration,
    required this.rating,
    required this.draft,
    required this.ownerId,
    required this.spotIds,
    required this.polyline,
  });

  factory RouteDetailModel.fromJson(Map<String, dynamic> json) {
    return RouteDetailModel(
      id: json['id'],
      name: json['name'],
      description: json['description'] ?? '',
      cityId: json['cityId'],
      duration: json['duration'],
      rating: (json['rating'] as num).toDouble(),
      draft: json['draft'] ?? false,
      ownerId: json['ownerId'],
      spotIds: List<int>.from(json['spotIds'] ?? []),
      polyline: (json['polyline'] as List<dynamic>)
          .map((p) => LatLng(p['latitude'], p['longitude']))
          .toList(),
    );
  }
}
