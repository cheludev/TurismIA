import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../models/user_model.dart';

class UserService {
  final Dio _dio = Dio(
    BaseOptions(
      baseUrl: 'http://192.168.1.136:8080/api',
    ),
  );

  Future<UserModel> getUserById(int id) async {
    const storage = FlutterSecureStorage();
    final token = await storage.read(key: 'jwt');

    if (token == null) {
      throw Exception('No hay token guardado.');
    }

    final response = await _dio.get(
      '/users/$id',
      options: Options(
        headers: {'Authorization': 'Bearer $token'},
      ),
    );

    final userJson = response.data['body'];
    return UserModel.fromJson(userJson);
  }
}
