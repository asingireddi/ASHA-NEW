package com.miraclesoft.scvp.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.scvp.service.impl.CronJobServiceImpl;

/**
* The class CronJobService.
*
* @author Priyanka Kolla
*/
@Service
public class CronJobService {

    /** The scheduler service impl. */
    @Autowired
    private CronJobServiceImpl cronJobServiceImpl;

    /**
     * Gets the emails for report.
     *
     * @param type the type
     * @return the emails for report
     */
    public Set<String> getToEmailsForReport(final String type) {
        return cronJobServiceImpl.getToEmailsForReport(type);
    }

    /**
     * Gets the cc emails for report.
     *
     * @param type the type
     * @return the cc emails for report
     */
    public Set<String> getCcEmailsForReport(final String type) {
        return cronJobServiceImpl.getCcEmailsForReport(type);
    }

    /**
     * Gets the file.
     *
     * @param type the type
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String getFile(final String type) throws IOException {
        return cronJobServiceImpl.getFile(type);
    }

    /**
    * Archive purge scheduler.
    *
    * @return the string
    * @throws SQLException the SQL exception
    */
    public String archivePurgeScheduler() throws SQLException {
        return cronJobServiceImpl.archivePurgeScheduler();
    }

}
