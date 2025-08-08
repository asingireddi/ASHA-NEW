package com.miraclesoft.scvp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.scvp.model.ArchivePurge;
import com.miraclesoft.scvp.service.ArchivePurgeService;

/**
 * The Class ArchivePurgeController.
 *
 * @author Narendar Geesidi
 */
@RestController
@RequestMapping("/archivePurge")
public class ArchivePurgeController {

    /** The archive purge service. */
    @Autowired
    private ArchivePurgeService archivePurgeService;

    /**
     * Save.
     *
     * @param archivePurge the archive purge
     * @return the string
     * @throws Exception the exception
     */
    @PostMapping("/add")
    public String save(@RequestBody final ArchivePurge archivePurge) throws Exception {
        return archivePurgeService.save(archivePurge);
    }

    /**
     * Update.
     *
     * @param archivePurge the archive purge
     * @return the string
     * @throws DataAccessException the data access exception
     * @throws Exception           the exception
     */
    @PostMapping("/update")
    public String update(@RequestBody final ArchivePurge archivePurge) throws DataAccessException, Exception {
        return archivePurgeService.update(archivePurge);
    }

    /**
     * Find one.
     *
     * @param id the id
     * @return the archive purge
     * @throws Exception the exception
     */
    @GetMapping("/{id}")
    public ArchivePurge findOne(@PathVariable final int id) throws Exception {
        return archivePurgeService.findOne(id);
    }

    /**
     * Find all.
     *
     * @param transaction the transaction
     * @param archiveDays the archiveDays
     * @param purgeDays   the purgeDays
     * @return the list
     * @throws Exception the exception
     */
    @GetMapping("/search/{transaction}")
    public List<ArchivePurge> findAll(@PathVariable final String transaction,
            @RequestParam(required = false, defaultValue = "-1") final int archiveDays,
            @RequestParam(required = false, defaultValue = "-1") final int purgeDays) throws Exception {
        return archivePurgeService.findAll(transaction, archiveDays, purgeDays);
    }
}
