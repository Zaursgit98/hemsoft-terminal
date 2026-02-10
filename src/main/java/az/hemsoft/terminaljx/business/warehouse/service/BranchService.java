package az.hemsoft.terminaljx.business.warehouse.service;


import az.hemsoft.terminaljx.business.core.annotation.Service;
import az.hemsoft.terminaljx.business.warehouse.model.Branch;
import az.hemsoft.terminaljx.business.warehouse.repository.BranchRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BranchService {
    private final BranchRepository branchRepsoitory;

    public BranchService() {
        this.branchRepsoitory = new BranchRepository();
    }

    public List<Branch> getAllBranch() {
        return branchRepsoitory.findAll();
    }

    public Branch getBranchById(Long id) {
        return branchRepsoitory.findById(id).orElse(null);
    }

    public void createBranch(Branch branch) {
        branchRepsoitory.save(branch);
    }

    public void updateBranch(Long id, Branch branchDetail) {
        Branch branch = branchRepsoitory.findById(id).get();
        branch.setBranchName(branch.getBranchName());
        branch.setCompanyId(branchDetail.getCompanyId());

        branchRepsoitory.save(branch);

    }

    public void deleteBranch(Long id) {
        branchRepsoitory.delete(id);
    }


}
