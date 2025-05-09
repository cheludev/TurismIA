import 'package:flutter/material.dart';

class TurismiaFabMenu extends StatefulWidget {
  final VoidCallback onCreateRoute;

  const TurismiaFabMenu({Key? key, required this.onCreateRoute}) : super(key: key);

  @override
  _TurismiaFabMenuState createState() => _TurismiaFabMenuState();
}

class _TurismiaFabMenuState extends State<TurismiaFabMenu> {
  bool _isOpen = false;

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        if (_isOpen) ...[
          const SizedBox(height: 12),
          FloatingActionButton(
            heroTag: "createRoute",
            backgroundColor: Colors.blue,
            mini: true,
            onPressed: () {
              widget.onCreateRoute();
              setState(() {
                _isOpen = false;
              });
            },
            child: const Icon(Icons.map),
          ),
          const SizedBox(height: 12),
          FloatingActionButton(
            heroTag: 'myRoutes',
            backgroundColor: Colors.blue,
            mini: true,
            onPressed: () {
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('Mis rutas guardadas (por hacer)')),
              );
            },
            child: const Icon(Icons.bookmark),
          ),
          const SizedBox(height: 12),
        ],
        FloatingActionButton(
          backgroundColor: Colors.blue,
          onPressed: () {
            setState(() {
              _isOpen = !_isOpen;
            });
          },
          child: Icon(_isOpen ? Icons.close : Icons.menu),
        ),
      ],
    );
  }
}
