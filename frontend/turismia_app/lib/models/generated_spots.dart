import 'package:turismia_app/models/spot_model.dart';

class GeneratedSpot {
  final int? id;
  final String name;
  final String? address;
  final double latitude;
  final double longitude;
  final double? rating;
  final int? averageTime;
  final bool validated;
  final String? info;
  final int? cityId;

  GeneratedSpot({
    required this.id,
    required this.name,
    this.address,
    required this.latitude,
    required this.longitude,
    this.rating,
    required this.averageTime,
    required this.validated,
    this.info,
    this.cityId,
  });

  factory GeneratedSpot.fromJson(Map<String, dynamic> json) {
    return GeneratedSpot(
      id: json['id'],
      name: json['name'],
      address: json['address'],
      latitude: json['latitude'].toDouble(),
      longitude: json['longitude'].toDouble(),
      rating: json['rating']?.toDouble(),
      averageTime: json['averageTime'],
      validated: json['validated'],
      info: json['info'],
      cityId: json['cityId'],
    );
  }

  factory GeneratedSpot.fromSpotModel(Spot spot) {
    return GeneratedSpot(
      id: spot.id,
      name: spot.name,
      address: spot.address,
      latitude: spot.latitude,
      longitude: spot.longitude,
      rating: spot.rating,
      averageTime: spot.averageTime,
      validated: spot.validated,
      info: spot.info,
    );
  }

}
