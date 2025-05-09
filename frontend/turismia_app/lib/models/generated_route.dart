
import 'generated_spots.dart';

class GeneratedRoute {
  final List<GeneratedSpot> spots;
  final int totalDuration;

  GeneratedRoute({
    required this.spots,
    required this.totalDuration
  });

  factory GeneratedRoute.fromJson(Map<String, dynamic> json) {
    return GeneratedRoute(
      spots: (json['spots'] as List)
          .where((e) => e['id'] != null)
          .map((e) => GeneratedSpot.fromJson(e))
          .toList(),
      totalDuration: json['totalDuration'] ?? 0,
    );
  }


}
