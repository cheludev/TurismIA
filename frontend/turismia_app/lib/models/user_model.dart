class UserModel {
  final int id;
  final String username;
  final String? photo;

  UserModel({
    required this.id,
    required this.username,
    this.photo,
  });

  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      id: json['id'],
      username: json['username'],
      photo: json['photo'],
    );
  }
}
