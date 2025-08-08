package com.miraclesoft.scvp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.miraclesoft.scvp.model.ArchivePurge;
import com.miraclesoft.scvp.service.impl.ArchivePurgeServiceImpl;

/**
 *
 * The Class ArchivePurgeService.
 *
 * @author Narendar Geesidi
 */
@Service
public class ArchivePurgeService {

    /** The archive purge service impl. */
    @Autowired
    private ArchivePurgeServiceImpl archivePurgeServiceImpl;

    /**
     * Save.
     *
     * @param archivePurge the archive purge
     * @return the string
     * @throws Exception the exception
     */
    public String save(final ArchivePurge archivePurge) throws Exception {
        return archivePurgeServiceImpl.save(archivePurge);
    }

    /**
     * Update.
     *
     * @param archivePurge the archive purge
     * @return the string
     * @throws DataAccessException the data access exception
     * @throws Exception           the exception
     */
    public String update(final ArchivePurge archivePurge) throws DataAccessException, Exception {
        return archivePurgeServiceImpl.update(archivePurge);
    }

    /**
     * Find one.
     *
     * @param id the id
     * @return the archive purge
     * @throws Exception the exception
     */
    public ArchivePurge findOne(final int id) throws Exception {
        return archivePurgeServiceImpl.findOne(id);
    }

    /**
     * Find all.
     *
     * @param transaction the transaction
     * @param archiveDays and archiveDays
     * @param purgeDays   and purgeDays
     * @return the list
     * @throws Exception the exception
     */
    public List<ArchivePurge> findAll(final String transaction, final int archiveDays, final int purgeDays)
            throws Exception {
        return archivePurgeServiceImpl.findAll(transaction, archiveDays, purgeDays);
    }
}
