import 'package:flutter/material.dart';
import '../models/route_detail_model.dart';
import '../services/route_service.dart';
import '../models/route_model.dart';

class RouteProvider extends ChangeNotifier {
  final RouteService _routeService = RouteService();

  List<RouteModel> _routes = [];
  bool _isLoading = false;
  String? _error;

  List<RouteModel> get routes => _routes;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> fetchRoutes() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final data = await _routeService.getPublicRoutes();
      _routes = data.map((json) => RouteModel.fromJson(json)).toList();
    } catch (e) {
      _error = e.toString();
    }

    _isLoading = false;
    notifyListeners();
  }

 /* Future<RouteModel?> fetchRouteDetail(int routeId) async {
    try {
      final json = await _routeService.getRouteById(routeId);
      return RouteModel.fromJson(json);
    } catch (e) {
      print('Error al obtener detalle de ruta: $e');
      return null;
    }
  } */

  Future<RouteDetailModel> fetchRouteDetail(int id) async {
    return await _routeService.getRouteDetail(id);
  }

}
