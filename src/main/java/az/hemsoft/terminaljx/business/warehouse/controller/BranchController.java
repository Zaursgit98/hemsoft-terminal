package az.hemsoft.terminaljx.business.warehouse.controller;


import az.hemsoft.terminaljx.business.core.annotation.*;
import az.hemsoft.terminaljx.business.warehouse.model.Branch;
import az.hemsoft.terminaljx.business.warehouse.service.BranchService;

import java.util.List;

@RestController()
public class BranchController {

    private final BranchService branchService;

    public BranchController() {
        this.branchService = new BranchService();
    }

    @GetMapping("/all")
    public List<Branch> getAllBranch() {
        return branchService.getAllBranch();
    }

    @GetMapping("/branch/{id}")
    public Branch getBranchById(@PathVariable Long id) {
        return branchService.getBranchById(id);
    }

    @PostMapping("/save")
    public void createBranch(@RequestBody Branch branch) {
        branchService.createBranch(branch);
    }

    @PutMapping("/update/{id}")
    public void updateBranch(@PathVariable Long id, @RequestBody Branch branchDetail) {
        branchService.updateBranch(id, branchDetail);
    }

    @DeleteMapping("delete/{id}")
    public void deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
    }

}
