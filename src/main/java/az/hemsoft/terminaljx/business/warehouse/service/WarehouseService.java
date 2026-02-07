package az.hemsoft.terminaljx.business.warehouse.service;

import az.hemsoft.terminaljx.business.core.annotation.Service;
import az.hemsoft.terminaljx.business.warehouse.model.Warehouse;
import az.hemsoft.terminaljx.business.warehouse.repository.WarehouseRepository;
import java.util.List;

@Service
public class WarehouseService {
    private final WarehouseRepository repository;

    public WarehouseService() {
        this.repository = new WarehouseRepository();
    }

    public List<Warehouse> getAll() {
        return repository.findAll();
    }

    public Warehouse getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public Warehouse create(Warehouse warehouse) {
        return repository.save(warehouse);
    }

    public void update(Warehouse warehouse) {
        repository.update(warehouse);
    }

    public void delete(Integer id) {
        repository.delete(id);
    }
}
