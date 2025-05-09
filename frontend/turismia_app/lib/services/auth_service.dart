import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../models/user_login_request.dart';
import '../models/user_model.dart';
import '../models/user_register_request.dart';

class AuthService {
  final Dio _dio = Dio(
    BaseOptions(
      baseUrl: 'http://192.168.1.136:8080/api',
      connectTimeout: const Duration(seconds: 10),
      receiveTimeout: const Duration(seconds: 10),
    ),
  );

  Future<String> login(UserLoginRequest request) async {
    try {
      final response = await _dio.post('/users/login', data: request.toJson());

      final token = response.data['body']?['tokenValue'];
      if (token == null) {
        throw Exception('No se recibió el token en la respuesta.');
      }

      return token;
    } on DioException catch (e) {
      throw Exception(e.response?.data['message'] ?? 'Error desconocido');
    }
  }

  Future<UserModel> getMyProfile() async {
    const storage = FlutterSecureStorage();
    final token = await storage.read(key: 'jwt');

    final response = await _dio.get(
      '/users/me',
      options: Options(headers: {'Authorization': 'Bearer $token'}),
    );

    return UserModel.fromJson(response.data['body']);
  }

  Future<void> register(UserRegisterRequest request) async {
    try {
      await _dio.post('/users/signup', data: request.toJson());
    } on DioException catch (e) {
      throw Exception(e.response?.data['message'] ?? 'Error desconocido en registro');
    }
  }
}
