package com.ucl.ADA.core.repository_analyser;

import com.ucl.ADA.metric_calculator.metrics.MetricServices;
import com.ucl.ADA.model.project_structure.ProjectStructure;
import com.ucl.ADA.model.project_structure.ProjectStructureService;
import com.ucl.ADA.model.snapshot.Snapshot;
import com.ucl.ADA.parser.ParserServices;
import com.ucl.ADA.repository_downloader.helpers.RepoDbPopulator;
import com.ucl.ADA.repository_downloader.services.RepoService;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;

@Service
public class RepositoryAnalyserServices {

    @Autowired
    ParserServices parserServices;

    @Autowired
    RepoService repoService;

    @Autowired
    MetricServices metricServices;

    @Autowired
    ProjectStructureService projectStructureService;

    /**
     * Handles the entire analysis of the repository and unifies the remaining three modules.
     *
     * @return ProjectMetrics object containing the resulting metric values between the objects, or null if there was
     * an error
     */

    public ProjectStructure analyseRepositoryService(String url, String branchName) {

        // Download repository and store metadata in DB
        // Also set the path to the downloaded directory, to be used by the parser
        RepoDbPopulator populator;
        try {
            populator = repoService.downloadAndStoreRepo(url, branchName);
        } catch (GitAPIException e) {
            populator = null;
        }

        if (populator == null) return null;

        // retrieve snapshot by id
        Snapshot snapshot = repoService.getSnapshotById(populator.getSnapshot_id());

        // Parse the downloaded repository.
        ProjectStructure parsedRepositoryProjectStructure;
        try {
            parsedRepositoryProjectStructure = parserServices.parseRepository(populator.getDirectoryPath());
            snapshot.setProjectStructure(parsedRepositoryProjectStructure);
            projectStructureService.save(parsedRepositoryProjectStructure);
        } catch (FileNotFoundException e) {
            parsedRepositoryProjectStructure = null;
        }

        // Calculate the metrics for the parsed repository.
        if (parsedRepositoryProjectStructure != null) {
            parsedRepositoryProjectStructure.computeAllMetrics();
        }

        // Delete downloaded repository since it's been parsed
        FileUtils.deleteQuietly(new File(populator.getDirectoryPath()));

        return parsedRepositoryProjectStructure;
    }
}
