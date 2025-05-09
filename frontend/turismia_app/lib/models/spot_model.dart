class Spot {
  final int id;
  final String name;
  final String? address;
  final double latitude;
  final double longitude;
  final double? rating;
  final int? averageTime;
  final bool validated;
  final String? info;
  final int? cityId;

  Spot({
    required this.id,
    required this.name,
    this.address,
    required this.latitude,
    required this.longitude,
    this.rating,
    required this.averageTime,
    required this.validated,
    this.info,
    required this.cityId,
  });

  factory Spot.fromJson(Map<String, dynamic> json) {
    return Spot(
      id: json['id'] ?? -1,
      name: json['name'] ?? '',
      address: json['address'] ?? '',
      latitude: (json['latitude'] ?? 0.0).toDouble(),
      longitude: (json['longitude'] ?? 0.0).toDouble(),
      rating: (json['rating'] ?? 0.0).toDouble(),
      averageTime: json['averageTime'] ?? 0,
      validated: json['validated'] ?? false,
      info: json['info'] ?? '',
      cityId: json['cityId'],
    );
  }


}
