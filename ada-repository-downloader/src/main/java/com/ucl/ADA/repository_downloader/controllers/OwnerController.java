package com.ucl.ADA.repository_downloader.controllers;

import com.ucl.ADA.model.branch.Branch;
import com.ucl.ADA.model.project_structure.ProjectStructure;
import com.ucl.ADA.model.repository.GitRepository;
import com.ucl.ADA.model.owner.Owner;
import com.ucl.ADA.model.snapshot.Snapshot;
import com.ucl.ADA.repository_downloader.services.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * Controller for listing and deleting users/owners of Git repos in the database.
 */


@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/users")
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    @DeleteMapping
    public void deleteAll() {
        ownerService.deleteAllOwners();
    }

    @DeleteMapping(value = "/{id}")
    public void deleteOwnerById(@PathVariable Long id) {
        ownerService.deleteOwner(id);
    }


    @GetMapping(value = "/{user_name}")
    public Owner getOwnerByName(@PathVariable String user_name) {
        return ownerService.getOwnerByName(user_name);
    }

    @GetMapping(value = "/{user_name}/repositories")
    public Set<GitRepository> getAllReposForUser(@PathVariable String user_name) {
        return getOwnerByName(user_name).getRepos();
    }


    @GetMapping(value = "/{user_name}/repositories/{repo_name}")
    public GitRepository getRepoByOwnerAndName(@PathVariable String user_name,
                                               @PathVariable String repo_name) {
        Set<GitRepository> repositories = getAllReposForUser(user_name);
        GitRepository repo = repositories.stream()
                .filter(r -> r.getRepoName().equals(repo_name)).findAny().orElse(null);

        return repo;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(value = "/{user_name}/repositories/{repo_name}/branches")
    public Set<Branch> getBranchesByOwnerAndRepo(@PathVariable String user_name,
                                                 @PathVariable String repo_name) {
        GitRepository repo = getRepoByOwnerAndName(user_name, repo_name);
        return repo.getBranches();
    }

    @GetMapping(value = "/{user_name}/repositories/{repo_name}/branches/{branch_name}")
    public Branch getBranchGivenOwnerRepoAndName(@PathVariable String user_name,
                                                 @PathVariable String repo_name,
                                                 @PathVariable String branch_name) {
        Set<Branch> branches = getBranchesByOwnerAndRepo(user_name, repo_name);
        Branch branch = branches.stream()
                .filter(b -> b.getBranchName().equals(branch_name)).findAny().orElse(null);

        return branch;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(value = "/{user_name}/repositories/{repo_name}/branches/{branch_name}/snapshots")
    public Set<Snapshot> getSnapshotsGivenOwnerRepoAndBranch(@PathVariable String user_name,
                                                             @PathVariable String repo_name,
                                                             @PathVariable String branch_name) {
        Branch branch = getBranchGivenOwnerRepoAndName(user_name, repo_name, branch_name);
        return branch.getSnapshots();
    }

    // not sure if last parameter should be id or timestamp, set it as id for now

    @GetMapping(value = "/{user_name}/repositories/{repo_name}/branches/{branch_name}/snapshots/{id}")
    public Snapshot getSnapshotGivenOwnerRepoBranchAndId(@PathVariable String user_name,
                                                         @PathVariable String repo_name,
                                                         @PathVariable String branch_name,
                                                         @PathVariable Long id) {

        Set<Snapshot> snapshots = getSnapshotsGivenOwnerRepoAndBranch(user_name, repo_name, branch_name);
        Snapshot snapshot = snapshots.stream()
                .filter(s -> s.getSnapshotID().equals(id)).findAny().orElse(null);

        return snapshot;
    }

    @GetMapping(value = "/{user_name}/repositories/{repo_name}/branches/{branch_name}/snapshots/{id}/project_structure}")
    public ProjectStructure getProjectStructureGivenSnapShot(@PathVariable String user_name,
                                                             @PathVariable String repo_name,
                                                             @PathVariable String branch_name,
                                                             @PathVariable Long id) {

        Snapshot snapshot = getSnapshotGivenOwnerRepoBranchAndId(user_name, repo_name, branch_name, id);
        ProjectStructure projectStructure = snapshot.getProjectStructure();

        return projectStructure;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public List<Owner> listAllUsers() {
        return ownerService.listOwners();
    }

    @GetMapping("/names")
    public List<String> listAllUserNames() {
        return ownerService.listUserNames();
    }
}
