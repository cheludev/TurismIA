class UserRegisterRequest {
  final String username;
  final String email;
  final String password;
  final String role; // Siempre 'TOURIST' de momento

  UserRegisterRequest({
    required this.username,
    required this.email,
    required this.password,
    this.role = 'TOURIST',
  });

  Map<String, dynamic> toJson() => {
    'username': username,
    'email': email,
    'password': password,
    'role': role,
  };
}
