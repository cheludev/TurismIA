import 'package:flutter/material.dart';
import '../models/spot_model.dart';
import '../services/spot_service.dart';

class SearchableSpotDialog extends StatefulWidget {
  const SearchableSpotDialog({super.key});

  @override
  State<SearchableSpotDialog> createState() => _SearchableSpotDialogState();
}

class _SearchableSpotDialogState extends State<SearchableSpotDialog> {
  List<Spot> _allSpots = [];
  List<Spot> _filteredSpots = [];
  final TextEditingController _controller = TextEditingController();

  @override
  void initState() {
    super.initState();
    _fetchSpots();
    _controller.addListener(_onSearchChanged);
  }

  Future<void> _fetchSpots() async {
    final spots = await SpotService().getAllSpotsInCity("Huelva");
    setState(() {
      _allSpots = spots;
      _filteredSpots = spots;
    });
  }

  void _onSearchChanged() {
    final query = _controller.text.toLowerCase();
    setState(() {
      _filteredSpots = _allSpots
          .where((spot) => spot.name.toLowerCase().contains(query))
          .toList();
    });
  }

  @override
  void dispose() {
    _controller.removeListener(_onSearchChanged);
    _controller.dispose();
    super.dispose();
  }
  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text("Buscar Spot"),
      content: SizedBox(
        width: 300,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: _controller,
              decoration: const InputDecoration(
                hintText: "Escribe para buscar...",
              ),
            ),
            const SizedBox(height: 12),
            if (_filteredSpots.isEmpty)
              const Text("No hay resultados")
            else
              ConstrainedBox(
                constraints: const BoxConstraints(maxHeight: 200),
                child: ListView.builder(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: _filteredSpots.length,
                  itemBuilder: (_, index) {
                    final spot = _filteredSpots[index];
                    return ListTile(
                      title: Text(spot.name),
                      onTap: () => Navigator.pop(context, spot),
                    );
                  },
                ),
              ),
          ],
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text("Cancelar"),
        ),
      ],
    );
  }

}
