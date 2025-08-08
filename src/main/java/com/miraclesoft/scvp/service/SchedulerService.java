package com.miraclesoft.scvp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.scvp.model.Scheduler;
import com.miraclesoft.scvp.service.impl.SchedulerServiceImpl;

/**
 * The Interface SchedulerService.
 *
 * @author Priyanka Kolla
 */
@Service
public class SchedulerService {

    /** The scheduler service impl. */
    @Autowired
    private SchedulerServiceImpl schedulerServiceImpl;

    /**
     * Find all.
     *
     * @param scheduler the scheduler
     * @return the list
     */
    public List<Scheduler> findAll(final Scheduler scheduler) {
        return schedulerServiceImpl.findAll(scheduler);
    }

    /**
     * Save.
     *
     * @param scheduler the scheduler
     * @return the string
     * @throws Exception the exception
     */
    public String save(final Scheduler scheduler) throws Exception {
        return schedulerServiceImpl.save(scheduler);
    }

    /**
     * Find one.
     *
     * @param id the id
     * @return the scheduler
     */
    public Scheduler findOne(final Long id) {
        return schedulerServiceImpl.findOne(id);
    }

    /**
     * Update.
     *
     * @param scheduler the scheduler
     * @return the string
     * @throws Exception the exception
     */
    public String update(final Scheduler scheduler) throws Exception {
        return schedulerServiceImpl.update(scheduler);
    }

    /**
     * Delete.
     *
     * @param id the id
     * @return the string
     * @throws Exception the exception
     */
    public String delete(final Long id) throws Exception {
        return schedulerServiceImpl.delete(id);
    }

}
